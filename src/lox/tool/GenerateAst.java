
package lox.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
	public static void main(String[] args) throws IOException {
		String outputDir = "";
		
		if (args.length == 0) {
			outputDir = "../main/";
		} else if (args.length != 1) {
			System.err.println("Usage: generate_ast <output directory>");
			System.exit(1);
		} else outputDir = args[0];
		
		defineAst(outputDir, "Expr", Arrays.asList(
				"Assign     : Token name, Expr value",
				"Binary		: Expr left, Token operator, Expr right",
				"Grouping	: Expr expression",
				"Literal	: ILoxObject value",
				"Unary		: Token operator, Expr right",
				"Variable   : Token name"
			));

		defineAst(outputDir, "Stmt", Arrays.asList(
				"Block      : List<Stmt> statements",
				"Expression : Expr expression",
				"Print      : Expr expression",
				"Variable   : Token name, Expr initializer"
			));
	}
	
	private static void defineAst(
			String outputDir, String baseName, List<String> types) throws IOException {
		String path = outputDir + "/" + baseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		String[] typeSplit;
		
		writer.println();
		writer.println("package lox.main;");
		writer.println();
		writer.println("import lox.classes.ILoxObject;");
		writer.println("import java.util.List;");
		writer.println();
		writer.println("abstract class " + baseName + " {");
		
		defineVisitor(writer, baseName, types);
		
		// The AST classes
		for (String type : types) {
			typeSplit = type.split(":");
			String className = typeSplit[0].trim();
			String fields = typeSplit[1].trim();
			
			defineType(writer, baseName, className, fields);
		}
		
		// The base accept() method
		writer.println();
		writer.println("\tabstract <R> R accept(Visitor<R> visitor);");
		
		writer.println("}");
		writer.close();
	}
	
	private static void defineVisitor(
			PrintWriter writer, String baseName, List<String> types) {
		writer.println("\tinterface Visitor<R> {");
		
		for (String type : types) {
			String typeName = type.split(":")[0].trim();
			writer.println("\t\tR visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
		}
		
		writer.println("\t}\n");
	}
	
	private static void defineType(
			PrintWriter writer, String baseName,
			String className, String fieldList) {
		writer.println("\tstatic class " + className + " extends " + baseName + " {");
		
		// Fields
		String[] fields = fieldList.split(", ");
		for (String field : fields) {
			writer.println("\t\tfinal " + field + ";");
		}
		
		// Constructor
		writer.println();
		writer.println("\t\t" + className + "(" + fieldList + ") {");
		
		// Store parameters in fields
		for (String field : fields) {
			String name = field.split(" ")[1];
			writer.println("\t\t\tthis." + name + " = " + name + ";");
		}
		
		writer.println("\t\t}");
		
		// Visitor pattern
		writer.println();
		writer.println("\t\t<R> R accept(Visitor<R> visitor) {");
		writer.println("\t\t\treturn visitor.visit" + className + baseName + "(this);");
		writer.println("\t\t}");
		
		writer.println("\t}\n");
	}
}
