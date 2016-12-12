package cs345.typescript;

import org.antlr.symtab.*;

public class ComputeTypes extends TypeScriptBaseVisitor<Type> {

    public static final Type VOID_TYPE = new PrimitiveType("void");
    public static final Type NUMBER_TYPE = new PrimitiveType("number");
    public static final Type STRING_TYPE = new PrimitiveType("string");
    protected StringBuilder buf = new StringBuilder();
    protected Scope currentScope;

    @Override
    public Type visitProgram(TypeScriptParser.ProgramContext ctx) {
        currentScope = ctx.scope;
        return visitChildren(ctx);
    }

    @Override
    public Type visitSourceElement(TypeScriptParser.SourceElementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitClassDeclaration(TypeScriptParser.ClassDeclarationContext ctx) {
        currentScope = ctx.scope;
        return visitChildren(ctx);
    }

    @Override
    public Type visitFunctionDeclaration(TypeScriptParser.FunctionDeclarationContext ctx) {
        currentScope = ctx.scope;
        return visitChildren(ctx);
    }

    @Override
    public Type visitBlock(TypeScriptParser.BlockContext ctx) {
        currentScope = ctx.scope;
        return super.visitBlock(ctx);
    }

    @Override
    public Type visitFunctioncalls(TypeScriptParser.FunctioncallsContext ctx) {
        if (ctx.funcCall().ID(1) != null) {
            buf.append(ctx.funcCall().arg.getText() + " is string\n");
        } else if (ctx.funcCall().INT() != null) {
            buf.append(ctx.funcCall().arg.getText() + " is number\n");
        }

        MethodSymbol b = (MethodSymbol) currentScope.resolve(ctx.funcCall().ID(0).toString());
        if (b.getType() != null) {
            if (b.getType().getName() == "void") {
                buf.append(ctx.funcCall().getText() + " is void" + "\n");
                return VOID_TYPE;
            }
        }
        return b.getType();
    }

    @Override
    public Type visitArgument(TypeScriptParser.ArgumentContext ctx) {
        VariableSymbol s = (VariableSymbol) ctx.ID().getSymbol();
        return NUMBER_TYPE;
    }

    @Override
    public Type visitAssignement(TypeScriptParser.AssignementContext ctx) {
        buf.append(ctx.expr(1).getText() + " is " + visit(ctx.expr(1)) + "\n");
        buf.append(ctx.expr(0).getText() + " is " + visit(ctx.expr(0)) + "\n");
        return ctx.expr(0).etype;
    }

    @Override
    public Type visitVarref(TypeScriptParser.VarrefContext ctx) {
        Symbol s = currentScope.resolve(ctx.ID().getText());
        ctx.etype = ((VariableSymbol) s).getType();
        return ctx.etype;
    }

    @Override
    public Type visitIntLiteral(TypeScriptParser.IntLiteralContext ctx) {
        ctx.etype = NUMBER_TYPE;
        return ctx.etype;
    }

    @Override
    public Type visitStringLiteral(TypeScriptParser.StringLiteralContext ctx) {
        ctx.etype = STRING_TYPE;
        return STRING_TYPE;
    }

    @Override
    public Type visitIsthis(TypeScriptParser.IsthisContext ctx) {
        Scope t;
        t = currentScope.getEnclosingScope();
        while (! (t instanceof ClassSymbol) ) {
            t = t.getEnclosingScope();
        }
        return (Type)t;
    }

    @Override
    public Type visitFieldreference(TypeScriptParser.FieldreferenceContext ctx) {
        Scope t = (Scope) visit(ctx.expr());
        buf.append(ctx.expr().getText() + " is " + t.getName() + "\n");
        VariableSymbol o = (VariableSymbol) t.resolve(ctx.ID().getText());
        ctx.etype = o.getType();
        return o.getType();
    }

    @Override
    public Type visitType(TypeScriptParser.TypeContext ctx) {
        return super.visitType(ctx);
    }

    public String getRefOutput() {
        return buf.toString();
    }
}
