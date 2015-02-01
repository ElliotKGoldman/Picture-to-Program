
public abstract class Expression {}

class NumExpr extends Expression {
	public int number;
	public NumExpr (int number){
		this.number = number;
	}
}

class IdExpr extends Expression {
	public static int _varNumber = 0;
	public String id;
	public IdExpr (){
		id = String.format("X%d", _varNumber);
		_varNumber++;
	}
}

class BinaryExpr extends Expression {
	public Expression left, right;
	public Operator op;
	public BinaryExpr(Expression left, Operator op, Expression right){
		this.left = left;
		this.right = right;
		this.op = op;
	}
}
