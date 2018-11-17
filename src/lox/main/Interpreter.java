package lox.main;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
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
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case PLUS:
                if (left instanceof String || right instanceof String) {
                    return stringify(left) + stringify(right);
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double)right == 0 ) {
                    throw new RuntimeError(expr.operator, "Cannot divide by zero");
                }
                return (double)left / (double)right;
            case STAR:
                if (left instanceof String) {
                    checkNumberOperand(expr.operator, right);
                    return multiplyString(expr.operator, stringify(left), (double)right);
                }
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case STAR_STAR:
                checkNumberOperands(expr.operator, left, right);
                return Math.pow((double)left, (double)right);
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
                return compare(expr.operator, left, right);
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        // unreachable
        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
        }

        // unreachable
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        // work around Java adding .0 to integers casted to double
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }

            return text;
        }

        return object.toString();
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double ) return;
        throw new RuntimeError(operator, "Operands must be numbers");
    }

    private String multiplyString(Token operator, String value, double repetitions) {
        int rep = (int)repetitions;
        String ret = "";

        if (rep < 0) { // ensure we don't fuck up our loop
            throw new RuntimeError(operator, "Trying to multiply a string by a negative value");
        } else if (rep > 0) {
            for(int i = 0; i < rep; ++i) {
                ret += value;
            }
        }

        return ret;
    }

    private boolean compare(Token operator, Object left, Object right) {
        if (left instanceof String && right instanceof String) {
            int result = compareStrings((String)left, (String)right);

            switch (operator.type) {
                case GREATER:
                    if (result == 1) return true;
                    break;
                case GREATER_EQUAL:
                    if (result >= 0) return true;
                    break;
                case LESS:
                    if (result == -1) return true;
                    break;
                case LESS_EQUAL:
                    if (result <= 0) return true;
                    break;
            }

            return false;
        }

        if (left instanceof Double && right instanceof Double) {
            switch (operator.type) {
                case GREATER:
                    return (double)left > (double)right;
                case GREATER_EQUAL:
                    return (double)left >= (double)right;
                case LESS:
                    return (double)left < (double)right;
                case LESS_EQUAL:
                    return (double)left < (double)right;
            }
        }

        throw new RuntimeError(operator, "Comparison operands must be numbers or strings");
    }

    private int compareStrings(String left, String right) {
        if (left.length() < right.length()) {
            return -1;
        } else if (left.length() > right.length()) {
            return 1;
        }

        for(int i = 0; i < left.length(); ++i) {
            if (left.charAt(i) > right.charAt(i) ) {
                return -1;
            } else if (left.charAt(i) < right.charAt(i) ) {
                return 1;
            }
        }

        return 0;
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }
}
