/*A class that unparses the constructed code, 
 * the "program" object from ProgramSynthesis.
 * Based off of AstUnparser assignment
 */

public class Unparse {
	

	private static int indentLevel; //Level the program is indented
	private static StringBuilder builder;
	
	public static String unparse(Program program){
		builder = new StringBuilder();
		indentLevel = 2; //One indent for inside class, another for inside main function
		
		for(Statement stmt : program.body){
			unparseStmt(stmt);
		}
		return builder.toString();
	}
	
	//Statement can only be an IfStatement or AssignStatement
	private static void unparseStmt(Statement stmt){
		if(stmt instanceof IfStatement){
			IfStatement ifstmt = (IfStatement) stmt;
			print("   " + "   ");//One indent for inside class, another for inside main function
			print("if(");
			unparseExpr(ifstmt.contingent);
			print("){\n");
			indentLevel++;
			for(Statement statement : ifstmt.statements){
				indent();
				unparseStmt(statement);
			}
			indentLevel--;
			indent();
			print("   " + "   "+"}\n");
		}else{
			AssignStatement assignStmt = (AssignStatement) stmt;
			print("   " + "   ");//One indent for inside class, another for inside main function
			//For first appeaarence of an id
			if(assignStmt.firstAppearence){
				print("int ");
			}
			unparseExpr(assignStmt.assign);
			print(" = ");
			unparseExpr(assignStmt.value);
			print(";\n");
		}
	}
	
	private static void unparseExpr(Expression expr){
		if(expr instanceof NumExpr){
			NumExpr numExpr = (NumExpr) expr;
			if(Math.signum(numExpr.number)==-1){
				print("(");
				print(String.valueOf(numExpr.number));
				print(")");
			}else{
				print(String.valueOf(numExpr.number));
			}
			
		}else if(expr instanceof IdExpr){
			IdExpr idExpr = (IdExpr) expr;
			print(idExpr.id);
		}else{//BinaryExpr
			BinaryExpr binaryExpr = (BinaryExpr) expr;
			unparseExpr(binaryExpr.left);
			print(binaryExpr.op.toString());
			unparseExpr(binaryExpr.right);
		}
	}
	
	//Adds indents before statements inside loops
	private static void indent(){
		String s="";
		for(int i = 0; i<indentLevel; i++){
			s += "  ";
		}
		builder.append(s);
	}
	
	private static void print(String s) {
		//One indent for inside class, another for inside main function
		builder.append(s);
	}
}
