
package lox.main;

import lox.classes.ILoxObject;
import java.util.List;

abstract class Stmt {
	interface Visitor<R> {
		R visitBlockStmt(Block stmt);
		R visitExpressionStmt(Expression stmt);
		R visitPrintStmt(Print stmt);
		R visitVariableStmt(Variable stmt);
	}

	static class Block extends Stmt {
		final List<Stmt> statements;

		Block(List<Stmt> statements) {
			this.statements = statements;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBlockStmt(this);
		}
	}

	static class Expression extends Stmt {
		final Expr expression;

		Expression(Expr expression) {
			this.expression = expression;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}
	}

	static class Print extends Stmt {
		final Expr expression;

		Print(Expr expression) {
			this.expression = expression;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitPrintStmt(this);
		}
	}

	static class Variable extends Stmt {
		final Token name;
		final Expr initializer;

		Variable(Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariableStmt(this);
		}
	}


	abstract <R> R accept(Visitor<R> visitor);
}
