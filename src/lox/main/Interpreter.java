package lox.main;

import lox.classes.ILoxObject;
import lox.classes.LoxObject;
import lox.errors.RuntimeError;

import java.util.List;

public class Interpreter implements Expr.Visitor<ILoxObject>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) { // TODO: not fuck it up
            Lox.runtimeError(error);
        }
    }

    @Override
    public ILoxObject visitBinaryExpr(Expr.Binary expr) {
        ILoxObject left = evaluate(expr.left);
        ILoxObject right = evaluate(expr.right);

        return LoxObject.calculate(expr.operator, new ILoxObject[]{left, right});
    }

    @Override
    public ILoxObject visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public ILoxObject visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public ILoxObject visitUnaryExpr(Expr.Unary expr) {
        ILoxObject right = evaluate(expr.right);

        return LoxObject.calculate(expr.operator, new ILoxObject[]{right});
    }

    @Override
    public ILoxObject visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        ILoxObject value = evaluate(stmt.expression);
        System.out.println(value.toString());
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitVariableStmt(Stmt.Variable stmt) {
        ILoxObject value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name, value);
        return null;
    }

    private ILoxObject evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }
}
