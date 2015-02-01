import java.util.ArrayList;
import java.util.List;


public abstract class Statement {}

class AssignStatement extends Statement {
	public IdExpr assign;
	public Expression value;
	//Notes if it's the first appearence of an id and should be printed with "int"
	public Boolean firstAppearence;
	
	public AssignStatement(IdExpr assign, Expression value){
		this.assign = assign;
		this.value = value;
		firstAppearence = false;
	}
	public AssignStatement(IdExpr assign, Expression value, Boolean firstAppearence){
		this.assign = assign;
		this.value = value;
		this.firstAppearence = firstAppearence;
	}
}

class IfStatement extends Statement {
	public Expression contingent;
	public List<Statement> statements;
	
	public IfStatement(Expression contingent, List<Statement> statements){
		this.contingent = contingent;
		this.statements = statements;
	}
}

class Program {
	public List<Statement> body;
	
	public Program (){
		body = new ArrayList<Statement>();
	}
}