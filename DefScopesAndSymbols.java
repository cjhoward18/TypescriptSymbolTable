package cs345.typescript;

import org.antlr.symtab.*;

public class DefScopesAndSymbols extends TypeScriptBaseListener {
    public static final Type VOID_TYPE = new PrimitiveType("void");
    public static final Type NUMBER_TYPE = new PrimitiveType("number");
    public static final Type STRING_TYPE = new PrimitiveType("string");
    protected Scope currentScope;
    protected Scope globals;

    public Scope getGlobalScope() {
        return globals;
    }

    void popScope() {
        if (currentScope != null) {
            currentScope = currentScope.getEnclosingScope();
        }
    }

    public class TypeScriptClassSymbol extends ClassSymbol {
        public TypeScriptClassSymbol(String name) {
            super(name);
        }
    }

    public class TypeScriptVariableSymbol extends VariableSymbol {
        public TypeScriptVariableSymbol(String name) {
            super(name);
        }
    }

    public class TypeScriptFunctionSymbol extends MethodSymbol {
        public TypeScriptFunctionSymbol(String name) {
            super(name);
        }
    }

    @Override
    public void exitProgram(TypeScriptParser.ProgramContext ctx) {
        popScope();
    }

    @Override
    public void enterProgram(TypeScriptParser.ProgramContext ctx) {
        globals = currentScope = new GlobalScope(null);
        ctx.scope = currentScope;
    }

    @Override
    public void enterClassDeclaration(TypeScriptParser.ClassDeclarationContext ctx) {
        String className = ctx.name.getText();
        TypeScriptClassSymbol cs = new TypeScriptClassSymbol(className);
        currentScope.define(cs);

        if (ctx.superClassName != null) {
            String superClass = ctx.superClassName.getText();
            cs.setSuperClass(superClass);
        }
        currentScope = cs;
        ctx.scope = currentScope;
    }

    @Override
    public void exitClassDeclaration(TypeScriptParser.ClassDeclarationContext ctx) {
        popScope();
    }

    @Override
    public void enterField(TypeScriptParser.FieldContext ctx) {
        String id = ctx.ID().getText();
        FieldSymbol f = new FieldSymbol(id);
        String typename = ctx.type().getText();
        defineVar(f, typename);
    }

    @Override
    public void enterFunctionDeclaration(TypeScriptParser.FunctionDeclarationContext ctx) {
        String id = ctx.ID().getText();
        MethodSymbol f = new MethodSymbol(id);
        defineFunc(f, ctx.typename);
        ctx.scope = currentScope;
    }

    @Override
    public void exitFunctionDeclaration(TypeScriptParser.FunctionDeclarationContext ctx) {
        popScope();
    }

    @Override
    public void enterParameter(TypeScriptParser.ParameterContext ctx) {
        String id = ctx.ID().getText();
        VariableSymbol f = new VariableSymbol(id);
        String typename = ctx.type().getText();
        defineVar(f, typename);
    }

    @Override
    public void enterBlock(TypeScriptParser.BlockContext ctx) {
        LocalScope locals = new LocalScope(currentScope);
        currentScope.nest(locals);
        currentScope = locals;
        ctx.scope = currentScope;
    }

    @Override
    public void exitBlock(TypeScriptParser.BlockContext ctx) {
        popScope();
    }

    @Override
    public void enterVarref(TypeScriptParser.VarrefContext ctx) {
        VariableSymbol f = (VariableSymbol) currentScope.resolve(ctx.ID().getText());
        ctx.etype = f.getType();
    }

    @Override
    public void enterDecl(TypeScriptParser.DeclContext ctx) {
        String id = ctx.ID().getText();
        VariableSymbol f = new VariableSymbol(id);
        String typename = ctx.type().getText();
        defineVar(f, typename);
    }

    @Override
    public void enterVarDeclaration(TypeScriptParser.VarDeclarationContext ctx) {
        String id = ctx.ID().getText();
        VariableSymbol f = new VariableSymbol(id);
        String typename = ctx.type().getText();
        defineVar(f, typename);
    }

    public void defineVar(VariableSymbol s, String type) {
        Symbol typeSym = currentScope.resolve(type);
        if (typeSym instanceof Type) {
            s.setType(((Type) typeSym));
        } else if (type == null) {
            s.setType(VOID_TYPE);
        } else if (type.equals("number")) {
            s.setType(NUMBER_TYPE);
        } else if (type.equals("string")) {
            s.setType(STRING_TYPE);
        }
        currentScope.define(s);
    }

    public void defineFunc(MethodSymbol s, TypeScriptParser.TypeContext type) {
        if (type == null) {
            s.setType(VOID_TYPE);
        } else if (type.getText().equals("string")) {
            s.setType(STRING_TYPE);
        } else if (type.getText().equals("number")) {
            s.setType(NUMBER_TYPE);
        } else if (type != null) {
            String typename = type.getText();
            Symbol typeSym = currentScope.resolve(typename);
            if (typeSym instanceof Type) {
                s.setType((Type) typeSym);
            }
        }
        currentScope.define(s);
        s.setEnclosingScope(currentScope);
        currentScope = s;
    }
}
