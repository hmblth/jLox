
package lox.main;

import lox.errors.RuntimeError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
	private static final Interpreter interpreter = new Interpreter();

	static boolean hadError = false;
	static boolean hadRuntimeError = false;

	public static void main(String[] args) throws IOException {
		if (args.length > 1) { // more than 1 arg, print usage info
			System.out.println("Usage: jlox [script]");
			System.exit(64); // exit code standard: https://www.freebsd.org/cgi/man.cgi?query=sysexits&apropos=0&sektion=0&manpath=FreeBSD+4.3-RELEASE&format=html
		} else if (args.length == 1) { // interpret the given file
			runFile(args[0]);
		} else { // run a prompt for live code input
			runPrompt();
		}
	}

	// run code from file
	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
		
		if (hadError) System.exit(65);
		if (hadRuntimeError) System.exit(70);
	}
	
	// run code prompt
	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
		String line;
		
		for (;;) {
			System.out.print("> ");
			line = reader.readLine();
			if (line.equals("exit")) break;
			
			run(line);
			hadError = false;
		}
	}
	
	// main run code method
	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();

		// Stop if we had a syntax error
		if (hadError) return;

		interpreter.interpret(statements);
	}
	
	// print error method
	// better practice to wrap it in a class
	static void error(int line, int column, String message) {
		report(line, column, "", message);
	}
	
	static void error(Token token, String message) {
		if (token.type == TokenType.EOF) {
			report(token.line, token.column, " at end", message);
		} else {
			report(token.line, token.column, " at '" + token.lexeme + "'", message);
		}
	}

	static void runtimeError(RuntimeError error) {
		System.err.println("[line " + error.token.line + ":" + error.token.column + "] " + error.getMessage());
		hadRuntimeError = true;
	}
	
	private static void report(int line, int column, String where, String message) {
		System.err.println("[line " + line + ":" + column + "] Error" + where + ": " + message);
		hadError = true;
	}
}
