
package lox.main;

import lox.classes.LoxBool;
import lox.classes.LoxNum;
import lox.classes.LoxString;

import java.util.ArrayList;
import java.util.List;

import static lox.main.TokenType.*;

// same basic idea as scanner, only this time at a token level
public class Parser {
	// TODO: Create a ParseError class similar to RuntimeError and expand on it
	private static class ParseError extends RuntimeException {}

	private final List<Token> tokens;
	private final List<ParseError> errors;
	private int current = 0;
	
	Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.errors = new ArrayList<ParseError>();
	}

	List<Stmt> parse() {
	    List<Stmt> statements = new ArrayList<>();

	    try {
			while (!isAtEnd()) {
				statements.add(declaration());
			}
		} catch (ParseError ex) {
		}

		return statements;
    }

    private Stmt declaration() {
		try {
			if (match(VAR)) return varDeclaration();

			return statement();
		} catch (ParseError error) {
			synchronize();
			return null;
		}
	}

	private Stmt varDeclaration() {
		Token name = consume(IDENTIFIER, "Expected variable name");

		Expr initializer = null;
		if (match(EQUAL)) {
			initializer = expression();
		}

		consume(SEMICOLON, "Expected ';' after variable declaration");
		return new Stmt.Variable(name, initializer);
	}

    private Stmt statement() {
		if (match(PRINT)) return printStatement();
		if (match(LEFT_BRACE)) return new Stmt.Block(block());

		return expressionStatement();
	}

	private List<Stmt> block() {
		List<Stmt> statements = new ArrayList<>();

		while (!check(RIGHT_BRACE) && !isAtEnd()) {
			statements.add(declaration());
		}

		consume(RIGHT_BRACE, "Expected '}' after block");
		return statements;
	}

	private Stmt printStatement() {
		Expr value = expression();
		consume(SEMICOLON, "Expected ';' after value");
		return new Stmt.Print(value);
	}

	private Stmt expressionStatement() {
		Expr expr = expression();
		consume(SEMICOLON, "Expected ';' after value");
		return new Stmt.Expression(expr);
	}

	private Expr expression() {
		return comma();
	}

	private Expr comma() {
		Expr expr = assignment();

		while (match(COMMA)) {
			expr = assignment();
		}

		return expr;
	}

	private Expr assignment() {
		Expr expr = equality();

		if (match(EQUAL)) {
			Token equals = previous();
			Expr value = assignment();

			if (expr instanceof Expr.Variable) {
				Token name = ((Expr.Variable)expr).name;
				return new Expr.Assign(name, value);
			}

			error(equals, "Invalid assignment target");
		}
		 return expr;
	}

	private Expr equality() {
		// parse initial expression as comparison, then check if there are other comparisons to check
		Expr expr = comparison();
		
		// builds up a single expression for multiple operators
		while (match(BANG_EQUAL, EQUAL_EQUAL, EQUAL_EH)) {
			Token operator = previous();
			Expr right = comparison();
			expr = new Expr.Binary(expr, operator, right);
		}
		
		return expr;
	}

	private Expr comparison() {
		// nearly same as equality() with diff operators
		Expr expr = addition();
		
		while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = previous();
			Expr right = addition();
			expr = new Expr.Binary(expr, operator, right);
		}
		
		return expr;
	}

	private Expr addition() {
		Expr expr = multiplication();
		
		while (match(MINUS, PLUS)) {
			Token operator = previous();
			Expr right = multiplication();
			expr = new Expr.Binary(expr, operator, right);
		}
		
		return expr;
	}
	
	private Expr multiplication() {
		Expr expr = exponent();
		
		while (match(SLASH, STAR)) {
			Token operator = previous();
			Expr right = exponent();
			expr = new Expr.Binary(expr, operator, right);
		}
		
		return expr;
	}

	private Expr exponent() {
		Expr expr = unary();

		while (match(STAR_STAR)) {
			Token operator = previous();
			Expr right = unary();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr unary() {
		if (match(BANG, MINUS, PLUS_PLUS, MINUS_MINUS)) {
			Token operator = previous();
			Expr right = unary();
			return new Expr.Unary(operator, right);
		}

		return primary();
	}

	private Expr primary() {
		if (match(FALSE)) return new Expr.Literal(new LoxBool(false));
		if (match(TRUE)) return new Expr.Literal(new LoxBool(true));
		if (match(NIL)) return new Expr.Literal(null);
		
		if (match(STRING)) {
			return new Expr.Literal(new LoxString((String)previous().literal));
		}

		if (match(NUMBER)) {
			return new Expr.Literal(new LoxNum((double)previous().literal));
		}

		if (match(IDENTIFIER)) {
			return new Expr.Variable(previous());
		}
		
		if (match(LEFT_PAREN)) {
			Expr expr = expression();
			consume(RIGHT_PAREN, "Expected ')' after expression");
			return new Expr.Grouping(expr);
		}

		throw error(peek(), "Expecting expression.");
	}
	
	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}
		
		return false;
	}
	
	private Token consume(TokenType type, String message) {
		if (check(type)) return advance();
		
		throw error(peek(), message);
	}
	
	private boolean check(TokenType type) {
		if (isAtEnd()) return false;
		return peek().type == type;
	}
	
	private Token advance() {
		if (!isAtEnd()) ++current;
		return previous();
	}
	
	private boolean isAtEnd() {
		return peek().type == EOF;
	}
	
	private Token peek() {
		return tokens.get(current);
	}
	
	private Token previous() {
		return tokens.get(current - 1);
	}
	
	private ParseError error(Token token, String message) {
		Lox.error(token, message);
		return new ParseError();
	}

	private void synchronize() {
	    advance();

	    while (!isAtEnd()) {
	        if (previous().type == SEMICOLON) return;

	        switch(peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }
}
