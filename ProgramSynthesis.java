import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/*Program Synthesis Class is the class for synthesizing a program
 * based on an array of pixel values. 
 */
public class ProgramSynthesis{	
	
	/*
	 * If the end of the pixel array is reached, the synthesis may be deep in 
	 * creating a statement, so the second color is read in the array from the beginning,
	 * and endOfFile is set to false so that the 
	 * statement that's currently being worked on can finish from the beginning
	 * of the pixelValues array, but no new statements will be created
	 */
	
	/*"pixelNum" is what pixel number in the array the program is working on.
	 * In practice the number is incrememnted directly after a pixel 
	 * is read.
	 */
	private int pixelNum;
	private int[] pixelValues;
	private Program program; //The program we're synthesizing
	
	//Both expression and statement depth start at 0 and should never go below it
	private int statementDepth; //Keeps track how deep into the statement the program is
	private int expressionDepth; //Keeps track how deep into an expression the program is
	
	private List<IdExpr> idList;
	private Boolean endOfFile;
	private static final int PIXELLIMIT = 255; //The range of pixel (rgb) values from 0-255
	//private static final int PIXELLIMIT = 99; //For use with last two digits hash function
	private static final int IFSTATEMENTS = 10; // The program will synthesisize 1 - PROGRAMSTATMENTS statements

	
	//For random rather than pixel values
	//For testing or other purposes, uses random values rather than pixel values (inside choose function)
	private static boolean userRandomValues;
	/*The number of statements the program should synthesize when using random values
	 * rather than the pixel values (as the program usually terminates when it runs out of pixel values)
	 */
	private static int numOfStatements;
	private static final Random random = new Random();
	
	//Constructor
	public ProgramSynthesis(int[] pixelValues) {
		this.pixelValues = pixelValues;
		pixelNum = 0;
		idList = new ArrayList<IdExpr>();
		endOfFile = false;
		program = new Program();
		userRandomValues = false;
		statementDepth = 0;
		expressionDepth = 0;
	}
	
	//Constructor for using random values rather than pixel values
	public ProgramSynthesis(int numberOfStatements) {
		this.pixelValues = null; //Shouldn't ever be used
		numOfStatements = numberOfStatements;
		pixelNum = 0;
		idList = new ArrayList<IdExpr>();
		endOfFile = false;
		program = new Program();
		userRandomValues = true;
		statementDepth = 0;
		expressionDepth = 0;
	}

	
	public boolean pixelAvailable(){
		if(pixelNum<pixelValues.length){
			return true;
		}else{
			return false;
		}
	}
	
	//The main loop that does all of the conversion from pixels to program
	public Program synthesize(){
		if(userRandomValues){
			for(int i = 0; i<numOfStatements; i++){
				program.body.add(synthesizeStatement());
			}
		}else{
			while(!endOfFile){
				program.body.add(synthesizeStatement());
			}
		}
		return program;
	}
	
	public void testChoose(int chooseNumber, int numberOfTimes){
		for(int i=0; i<numberOfTimes; i++){
			System.out.println(choose(chooseNumber));
		}
	}
	
	
	/*Returns which choice to pick based on pixel value (unless USERANDOMVALUES=true)
	 * It is passed how many things to choose from as an arguement. Returns from one to that number.
	 * 
	*/

	private int choose(int choices){
		/*Reset to beginning of array so that statement synthesis can complete.
		* But the colour is set to the second tier so that values aren't repeated
		*/
		if(userRandomValues){
			int choice = random.nextInt(choices) + 1;
			return choice;
		}else{
			if(!pixelAvailable()){ 
				pixelNum=0;
				endOfFile = true; //Reached the end of the file. Finish the statement it's working on
			}
			double pixelValue = pixelValues[pixelNum];
			//double pixelValue = chooseHash(pixelValues[pixelNum]);
			pixelNum++;
			//System.out.printf("Pixel Value: %f\n", pixelValue);
			double ratio = pixelValue/PIXELLIMIT;
			//System.out.printf("Ratio: %f\n", ratio);
			double choice = ratio*choices;
			if(choice==0){ //without would return one more than choices (since choose starts at 1 and not 0)
				choice=1;
			}
			return (int)choice;
		}
	}
	
	/*
	private int chooseHash(int chooseInt){
		//Only last two digits - PIXELLIMIT should be 99
		int lastTwoDigits = chooseInt%100;
		return lastTwoDigits;
		
		//Reversed - PIXELLIMIT should be 942
//		int reversed = 0;
//        while(chooseInt != 0){
//            reversed = (reversed*10)+(chooseInt%10);
//            chooseInt = chooseInt/10;
//        } 
//        return reversed;
	}
	*/
	
	private Statement synthesizeStatement(){
		int toChooseBtwn = 2*(statementDepth+1); //The number of things to choose from times the depth (+1 because it starts at 0)
		int choice = choose(toChooseBtwn);//Normal 50% for each, 1/4 chance, 1/6, etc.
		if(choice ==1){
			//System.out.println("if");
			Expression expression = synthesizeBinaryBoolean();
			List<Statement> statements = new ArrayList<Statement>();
			choice = choose(IFSTATEMENTS); //How many statements to synthesize inside the if statement
			for(int i=0; i<choice; i++){
				statementDepth++;
				statements.add(synthesizeStatement());
				statementDepth--;
			}
			return new IfStatement(expression, statements);
		}else{
			//System.out.println("assign");
			IdExpr assign = chooseIdExpr();
			Expression value = synthesizeExpression();
			return new AssignStatement(assign, value);
		}
	}
	
	private Expression synthesizeExpression(){
		int toChooseBtwn = 3*(expressionDepth+1); //The number of things to choose from times the depth (+1 because it starts at 0)
		int choice = choose(toChooseBtwn);
		if(choice == 1){
			expressionDepth++;
			Expression left = synthesizeExpression();
			//expressionDepth-- and then ++
			Expression right = synthesizeExpression();
			expressionDepth--;
			//choose the operator
			Operator op;
			int opChoice = choose(5);
			if(opChoice == 1){
				op = Operator.Arithmetic.PLUS;
			}else if(opChoice == 2){
				op = Operator.Arithmetic.MINUS;
			}else if(opChoice == 3){
				op = Operator.Arithmetic.TIMES;
			}else if(opChoice == 4){
				op = Operator.Arithmetic.DIVIDE;
			}else{ //opChoice == 5
				op = Operator.Arithmetic.MOD;
			}
			return new BinaryExpr(left, op , right);
		}else if (choice < (toChooseBtwn/2)){ //There's two "safe" choices
			return chooseIdExpr();
		}else{
			return synthesizeNumber();
		}
	}
	
	private Expression synthesizeBinaryBoolean(){
		int leftChoice = choose(2);
		Expression left;
		Expression right;
		if(leftChoice == 1){
			left = synthesizeNumber();
		}else{
			left = chooseIdExpr();
		}
		int rightChoice = choose(2);
		if(rightChoice == 1){
			right = synthesizeNumber();
		}else{
			right = chooseIdExpr();
		}
		//choose the operator
		Operator op;
		int opChoice = choose(5);
		if(opChoice == 1){
			op = Operator.Boolean.LT;
		}else if(opChoice == 2){
			op = Operator.Boolean.LTE;
		}else if(opChoice == 3){
			op = Operator.Boolean.GT;
		}else if(opChoice == 4){
			op = Operator.Boolean.GTE;
		}else if(opChoice == 5){
			op = Operator.Boolean.EQ;
		}else{//choice == 6
			op = Operator.Boolean.NEQ;
		}
		return new BinaryExpr(left, op , right);
	}
	
	/*Chooses an id from all available in the program
	 *If one does not exist or a new one is chosen, then
	 *an id is created 
	 */
	private IdExpr chooseIdExpr(){
		int choice = choose(2); //Decide between a new id or an existing
		if(choice==1 || idList.isEmpty()){
			return newId();
		}else{
			//Subtract one less because choose() starts at 1
			//Subract one more because should be one less than size
			int idNumber = (choose(idList.size()));
			if(idNumber == idList.size()){
				idNumber = idList.size()-1;
			}
			//System.out.println(idNumber);
			return idList.get(idNumber); //Get a random Id from the list of ids
		}
	}
	
	/*Creates a new id, adss it to the list and adds an assignment
	 * statement to the program.
	 */
	private IdExpr newId(){
		IdExpr idExpr = new IdExpr();
		idList.add(idExpr);
		AssignStatement assignStatement = new AssignStatement(idExpr, synthesizeNumber(), true);
		program.body.add(assignStatement);
		return idExpr;
	}
	
	private NumExpr synthesizeNumber(){
		//Numbers can (initially) be between -1,000,000 and 1,000,000
		int numberOfDigits = choose(9);
		boolean negative;
		int isNegative = choose(2);
		if(isNegative==1){
			negative=true;
		}else{
			negative=false;
		}
		int number=0;
		//Choose the digits for the number
		for(int i=0; i<=numberOfDigits; i++){
			//Get the digit
			int chooseNumber = choose(10);
			//So that it can be 0
			if(chooseNumber==10){
				chooseNumber=0;
			}
			//Add the digit in its correct place to the number
			number += (chooseNumber*(int)Math.pow(10, i));
		}
		if(negative){
			number*=-1;
		}
		return new NumExpr(number);
	}
}
