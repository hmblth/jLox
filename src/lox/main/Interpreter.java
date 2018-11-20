package lox.main;

import lox.classes.ILoxObject;

import java.util.List;

public class Interpreter implements Expr.Visitor<ILoxObject>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) { // could have a LoxError interface implemented by multiple errors
            Lox.runtimeError(error);
        }
    }

    @Override
    public ILoxObject visitBinaryExpr(Expr.Binary expr) {
        ILoxObject left = evaluate(expr.left);
        ILoxObject right = evaluate(expr.right);

        switch (expr.operator.type) {
            case PLUS:
                return left.add(expr.operator, right);
            case MINUS:
                return left.subtract(expr.operator, right);
            case SLASH:
                return left.divide(expr.operator, right);
            case STAR:
                return left.multiply(expr.operator, right);
            case STAR_STAR:
                return left.power(expr.operator, right);
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case BANG_EQUAL:
            case EQUAL_EQUAL:
                return left.compare(expr.operator, right);
        }

        // unreachable
        return null;
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

        switch (expr.operator.type) {
            case BANG:
                return right.bool(expr.operator);
            case MINUS:
                return right.minus(expr.operator);
        }

        // unreachable
        return null;
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
