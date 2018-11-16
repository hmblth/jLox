
package lox.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.main.TokenType.*; // bad practice

public class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	
	private static final Map<String, TokenType> keywords;
	
	static {
		keywords = new HashMap<>();
		keywords.put("and", AND);
		keywords.put("class", CLASS);
		keywords.put("else", ELSE);
		keywords.put("false", FALSE);
		keywords.put("for", FOR);
		keywords.put("fun", FUN);
		keywords.put("if", IF);
		keywords.put("nil", NIL);
		keywords.put("or", OR);
		keywords.put("print", PRINT);
		keywords.put("return", RETURN);
		keywords.put("super", SUPER);
		keywords.put("this", THIS);
		keywords.put("true", TRUE);
		keywords.put("var", VAR);
		keywords.put("while", WHILE);
	}
	
	private int start = 0;
	private int current = 0;
	private int column = 0;
	private int line = 1;
	
	Scanner (String source) {
		this.source = source;
	}
	
	List<Token> scanTokens() {
		while (!isAtEnd()) {
			// we are at the beginning of the next lexeme
			start = current;
			scanToken();
		}
		
		tokens.add(new Token(EOF, "", null, line, column)); // add last token, EOF
		return tokens;
	}
	
	private void scanToken() {
		char c = advance();
		switch (c) {
			// characters to ignore
			case ' ':
			case '\r':
			case '\t':
				break;
				
			case '\n':
				++line;
				break;
		
			// single character tokens-
			case '(': addToken(LEFT_PAREN); break;
			case ')': addToken(RIGHT_PAREN); break;
			case '{': addToken(LEFT_BRACE); break;
			case '}': addToken(RIGHT_BRACE); break;
			case ',': addToken(COMMA); break;
			case '.': addToken(DOT); break;
			case '-': addToken(MINUS); break;
			case '+': addToken(PLUS); break;
			case ';': addToken(SEMICOLON); break;

			// possible double character tokens
			case '*': addToken(match('*') ? STAR_STAR : STAR ); break;
			case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
			case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
			case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
			case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
			
			// / character - possible comment
			case '/':
				if (match('/')) {
					// scan comment until the end of the line
					while (peek() != '\n' && !isAtEnd()) advance();
				} else if (match('*')) {
					int startColumn = column;
					while (peek() != '*' && peekNext() != '/' && !isAtEnd() ) {
						if (peek() == '\n') newLine();
						advance();
					}
					
					if (isAtEnd()) Lox.error(line, startColumn, "Unterminated comment");
					else {
						advance(); // get rid of the star
						advance(); // get rid of the second character
					}
				} else {
					addToken(SLASH);
				}
				break;
				
			// literals
			case '"': string(); break;
			
			// unexpected input
			default:
				if (isDigit(c)) { // scan number
					number();
				} else if (isAlpha(c)){ // scan identifier
					identifier();
				} else {
					Lox.error(line, column, "Unexpected character"); break; // keeps scanning, to catch other possible errors
				}
		}
	}
	
	// scan in a string
	private void string() {
		int startColumn = column;
		while (peek() != '"' && !isAtEnd()) {
			if (peek() == '\n') newLine();
			advance();
		}
		
		// unterminated string
		if (isAtEnd()) {
			Lox.error(line, startColumn, "Unterminated string");
			return;
		}
		
		// the closing "
		advance();
		
		// trim the surrounding quotes
		String value = source.substring(start + 1, current - 1); // if supported escape characters, they'd be unescaped here
		addToken(STRING, value);
	}
	
	private void number() {
		while (isDigit(peek())) advance();
		
		// Look for fractions
		if (peek() == '.' && isDigit(peekNext())) {
			advance();
			
			while (isDigit(peek())) advance();
		}
		
		addToken(NUMBER,
				Double.parseDouble(source.substring(start, current)));
	}
	
	private void identifier() {
		while (isAlphaNumeric(peek())) advance();
		
		// See if the identifier is a reserved word
		String text = source.substring(start, current);
		
		TokenType type = keywords.get(text);
		if (type == null) type = IDENTIFIER;
		addToken(type);
	}
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
			   (c >= 'A' && c <= 'Z') ||
			    c == '_';
	}
	
	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}
	
	private boolean match(char expected) {
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;
		
		// only advances if next character matches expected
		++current;
		++column;
		return true;
	}
	
	// check the next character without advancing - lookahead
	private char peek() {
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}
	
	private char peekNext() {
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current + 1);
	}
	
	// check if we're done
	private boolean isAtEnd() {
		return current >= source.length();
	}
	
	// get next char
	private char advance() {
		++column;
		return source.charAt(current++);
	}
	
	private void newLine() {
		++line;
		column = 0;
	}
	
	// add token with no literal
	private void addToken(TokenType type) {
		addToken(type, null);
	}
	
	// add token with literal
	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line, column));
	}
}
