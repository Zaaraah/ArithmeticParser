package finalproject;

import java.util.HashMap;
import java.util.Map;

import finalproject.LexicalAnalyzer.Lexeme;
import finalproject.LexicalAnalyzer.Token;


public class ArithmeticParser
{
	public static String FILE_PATH = "C:/Eclipse-Java-Luna/Workspace/ArithmeticParser/src/prog1.txt";	
	private static Token[] input;
	private static Token token;
	private static int position = 0;
	private static boolean ifIsMet;
	private static boolean errFlag = false;
	
	private static Map<String, Double> variables;
	
	public static void getNextToken()
	{
		token = input[position++]; 
	}
	
	public static void main( String args[] ) throws Exception 
	{	
		variables = new HashMap<String, Double>();
		//load text file
		input = LexicalAnalyzer.getTokenArray();
		
		/*System.out.println("TOKEN\t\tLEXEME");
		System.out.println("---------------------------");
		for (int i = 0; i < input.length; i++) {
			System.out.println( input[i].getIdString() + "\t\t" + input[i].getLexeme() );
		}
		System.out.println("---------------------------");*/

		getNextToken();
		String answer = S();
		System.out.println(answer);
	}
	
	// S -> T
	public static String S()
	{
        String ans ="";  
		while (!token.equalsIdString("EOF") && !errFlag )
		{
			ans = T(); 
		}
	    return ans;
	}
	
	// T -> US
	public static String T(){
		ifIsMet=true;
		String ans = U();
		/*if (errFlag)
		{
			return "";
		}*/
		return ans + "\n" + S();
	}
	
	// U -> A | P | I | eps
	public static String U(){
		String ans ="";
		String statement="";
		if (token.equalsIdString("IDENT")){
			statement = A();
			ans = "computation performed (" + statement + ")";
		}
		else if (token.equalsIdString("PRINT"))
		{
			statement = P();
			ans = "output (" + statement + ")";
		}
		else if (token.equalsIdString("IF"))
		{
			
			statement = I();
			ans = statement;
		}
		else
		{
			error ("INVALID LINE");
		}
		return ans;
	}
	
	// A -> ident = E ;
	public static String A(){
		String ident = token.getLexeme();
		double ans = 0;
		
		getNextToken();
		if (token.equalsIdString("ASSIGN"))
		{
			getNextToken();
			ans = E();
			if (token.equalsIdString("SEMICOLON"))
			{
				getNextToken();
				if(ifIsMet)
				{
					variables.put(ident, ans);
				}
			}
			else
			{
				error ("Missing semicolon");
			}
		}
		else
		{
			error ("Invalid assignment");
		}
		return ident+" = "+String.format("%1.2f", ans);
	}
	
	// P -> print ( Q ) ;
	public static String P(){
		getNextToken();
		String ans="";
		if (token.equalsIdString("LPAREN"))
        {        
			getNextToken();
			ans = Q();
            if (token.equalsIdString("RPAREN"))
            {
            	getNextToken();
            	if (token.equalsIdString("SEMICOLON"))
                {
            		getNextToken();
                }
            	else
            	{
            		error ("Missing semicolon");
            	}
            }
            else
            {
            	error ("Unclosed parenthesis");;
            }
        }
        else
        {
        	error( "Invalid print statement" );
        }
		return ans;
	}
	
	// Q -> E | str
	public static String Q(){
		String ans="";
		if (token.equalsIdString("STRING"))
        {  
			ans = token.getLexeme();
			getNextToken();
			
        }
		else
		{
			ans = String.format("%1.2f", E());
		}
		return ans;
	}
	
	// I -> if ( E R E ) J 
	public static String I(){
		getNextToken();
		String ans="";
		
		if (token.equalsIdString("LPAREN"))
        {        
			getNextToken();
            //if (token.equalsIdString("IDENT"))
            //{
            	//Double var = variables.get(token.getLexeme());
				Double var = E();
            	//getNextToken();
            	boolean comp = R(var);
            	//getNextToken();
            	if (token.equalsIdString("RPAREN"))
                { 
	            	if (comp){
	            		getNextToken();
	            		ans = "condition met, "+ J();
	            	}
	            	else
	            	{
	            		getNextToken();
	            		ifIsMet = false;
	            		J();
	            		ans = "condition not met";
	            		
	            	}
	            }
	            else
	            {
	            	error ("Unclosed parenthesis");
	            }
        }
        else
        {
        	error( "Invalid if statement" );
        }
		return ans;
	}
	
	// R ->  grtr | less | grtrthn | lsthn | eql | nteql
	public static boolean R(double var)
	{
		boolean ans = false;
		if (token.equalsIdString("COMPARE"))
		{
			getNextToken();
			if (var == E())
			{
				ans = true;
			}
					
		}
		else if (token.equalsIdString("NOT EQUAL"))
		{
			getNextToken();
			if (var != E())
			{
				ans = true;
			}
		}
		else if (token.equalsIdString("GREATER"))
		{
			getNextToken();
			if (var > E())
			{
				ans = true;
			}
		}
		else if (token.equalsIdString("GREATER EQUAL"))
		{
			getNextToken();
			if (var >= E())
			{
				ans = true;
			}
		}
		else if (token.equalsIdString("LESSER"))
		{
			getNextToken();
			if (var < E())
			{
				ans = true;
			}
		}
		else if (token.equalsIdString("LESSER EQUAL"))
		{
			getNextToken();
			if (var <= E())
			{
				ans = true;
			}
		}
		else
		{
			error ("Invalid comparator");
		}
		
		return ans;
	}
	
	// J -> S
	public static String J()
	{
		String ans = U();
		return ans;
	}
	
	// E -> BC
	public static double E()
	{
		double x = B();
        double y = C();
        return x + y;
	}
	
	// C -> +BC | -BC | eps
	public static double C()
	{
		if (token.equalsIdString("PLUS"))
		{
			getNextToken();
			double x = B();
            double y = C();
			return x + y;
		}
		else if (token.equalsIdString("MINUS"))
		{
			getNextToken();
			double x = -B();
            double y = C();
			return x + y;
		}
		else
		{
            return 0; // do nothing (eps)
		}
	}
	
		/*// B -> DF
		public static double B()
		{
			double x = D();
		    double y = F(x);
		    return y;
		}
			
		// F -> *DF | /DF | %DF | eps
		public static double F(double z)
		{	
			if (token.equalsIdString("MULT"))
			{
				getNextToken();
				double x = D();
				double y = F(x*z);
				return y;            
			}
			else if (token.equalsIdString("DIVIDE"))
			{
				getNextToken();
				double x = D();
				double y = F(1/(x/z));
				return y;	
			}
			else if (token.equalsIdString("MODULO"))
			{	
				getNextToken();
				double x = D();
				double y = F(z % x);
				return y;  
			}
			else
			{
				return z; // do nothing (eps)
			}
		}*/
		
		// B -> DF
		public static double B()
		{
	 			double x = D();
	            double y = F(x);
				return x * y;
		}
		
		// F -> *DF | /DF | %DF | eps
		public static double F(double z)
		{
			boolean isMod = false;
			boolean isLastMod = false;
			
			if (token.equalsIdString("MULT"))
			{
				getNextToken();
				double x = D();
	            double y = F(x);
				return x * y;
			}
			else if (token.equalsIdString("DIVIDE"))
			{
				getNextToken();
				double x = D();
	            double y = F(x);
				return 1/(x/y);
			}
			else if (token.equalsIdString("MODULO"))
			{
				getNextToken();	
				double x = D();
				boolean isNotLast = token.equalsIdString("MULT") || token.equalsIdString("DIVIDE") || token.equalsIdString("MODULO");
	            double y = F(z % x);
	            if (isNotLast) {
	            	return ((z%x)*y)/z;
	            } else {
	            	return (z%x)/z;
	            }
			}
			else
			{
				return 1; // do nothing (eps)
			}
		}
	
	// D -> GH
	public static double D()
	{
 			double x = H();
            double y = G();
			return Math.pow(x, y);
	}
	
	// G -> **GH | eps
	public static double G()
	{
		if (token.equalsIdString("EXP"))
		{
			getNextToken();
			double x = H();
            double y = G();
			return Math.pow(x, y);
		}
		else
		{
            return 1; // do nothing (eps)
		}
	}
	
	// H -> -X | X
	public static double H()
	{
		if (token.equalsIdString("MINUS"))
		{
			getNextToken();
			return -X();
		}
		else
		{
            return X();
		}
	}
	
	// X -> num | ( E ) | sqrt ( E ) | ident
	public static double X()
	{
		double ans = 0;
		if (token.equalsIdString("NUMBER"))            
		{
			ans = Double.parseDouble(token.getLexeme());
			getNextToken();
		}
		else if (token.equalsIdString("LPAREN"))
        {        
			getNextToken();    
            ans = E();
            if (token.equalsIdString("RPAREN"))
            {
            	getNextToken();
            }
            else
            {
            	error( "Unclosed parenthesis" );
            }
        }
		else if (token.equalsIdString("SQRT"))
        {        
			getNextToken();    
			if (token.equalsIdString("LPAREN"))
	        {        
				getNextToken();    
	            ans = Math.sqrt(E());
	            if (token.equalsIdString("RPAREN"))
	            {
	            	getNextToken();
	            }
	            else
	            {
	            	error( "Unclosed parenthesis" );
	            }
	        }
			else
			{
				error ("Invalid square");
			}
        }
		else if (token.equalsIdString("IDENT"))            
		{
			if(variables.containsKey(token.getLexeme()))
			{
				ans = variables.get(token.getLexeme());
			}
			else
			{
				variables.put(token.getLexeme(), 0.0);
			}
			getNextToken();
		}
        else
        {
        	error( "Lexical error" );
        }
                        
		return ans;
	}
	
	
	public static void error( String from )
	{
		System.out.printf( "PARSE ERROR: %s %d\n", from, position );
		System.out.println("---------------------------------------");
		errFlag = true;
	}
	
}