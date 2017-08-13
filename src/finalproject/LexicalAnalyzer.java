package finalproject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LexicalAnalyzer {
	

	private static Map<String, Lexeme> map;
	
	static {
		instantiateMap();
	}
	
	public static void main(String[] args) throws Exception {
		
		Program program = new Program();
		
		Token t;
		t = program.getToken();
		System.out.println("TOKEN\t\tLEXEME");
		System.out.println("---------------------------");
		while (t==null || t.getIdString() != "EOF")
		{
			
			if (t != null){
				System.out.println( t.getIdString() + "\t\t" + t.getLexeme() );
			}
			t = program.getToken();
		}
	}
	
	public static Token[] getTokenArray() throws Exception {
		Program program = new Program();
		List<Token> tokens = new ArrayList<>();
		
		Token t;
		t = program.getToken();
		while (t==null || t.getIdString() != "EOF")
		{
			if (t != null){
				tokens.add(t);
			}
			t = program.getToken();
		}
		tokens.add(new Token("EOF", "End of file"));
		return tokens.toArray(new Token[tokens.size()]);
	}
	
	public static class Program{
		private String input;
		private int index;
		
		public Program() throws FileNotFoundException {
			try (Scanner scnr = new Scanner(new File(ArithmeticParser.FILE_PATH))) {
				input = scnr.useDelimiter("\\Z").next()
				.replaceAll("\r", " ")
				.replaceAll("\t", " ");
			}
			index = 0;			
		}
		
		public void incremIndex(int value) {
			index += value;
		}
		
		public Token getToken() throws Exception{
			
			// Check if end of file already
			if (index >= input.length()) {
				return new Token("EOF", "End of File");
			}
			
			// Else get character
			char chr = input.charAt(index);
			String str = String.valueOf(chr);
			
			if(" ".equals(str) || "\n".equals(str)){
				index++;
				return null;
			}else if (Character.isAlphabetic(chr)){
				str="z";
			}
			else if(Character.isDigit(chr)){
				str="0";
			}
			else if(chr=='\'' || chr=='"'){
				str="'";
			}
			
			if (!map.containsKey(str)){
				index++;
				return new Token("Lexical error:", "illegal character");
			}
			else{
				return map.get(str).getToken(this, input, index);
			}
		}
	}

	//Token-Lexeme Hashmap
	private static void instantiateMap() {
		map = new HashMap<String, Lexeme>();
		map.put("+", Lexeme.getLexeme("PLUS"));
		map.put("-", Lexeme.getLexeme("MINUS"));
		map.put("*", new AsteriskLexeme());
		map.put("/", new SlashLexeme());
		map.put("%", Lexeme.getLexeme("MODULO"));
		map.put("=", new EqualsLexeme());
		map.put(">", new GreaterLexeme());
		map.put("<", new LesserLexeme());
		map.put("!", new NotLexeme());
		map.put("(", Lexeme.getLexeme("LPAREN"));
		map.put(")", Lexeme.getLexeme("RPAREN"));
		map.put(",", Lexeme.getLexeme("COMMA"));
		map.put(";", Lexeme.getLexeme("SEMICOLON"));
		map.put(".", Lexeme.getLexeme("PERIOD"));
		map.put("#", new SlashLexeme()); 
		map.put("0", new NumLexeme());
		map.put("z", new LetterLexeme());
		map.put("'", new StringLexeme());
	}
	
	public static class Token{
		private String l;
		private String s;
		
		public Token(String l, String s){
			this.l = l;
			this.s = s;
		}
		
		public String getIdString(){
			return l;
		}
		
		public String getLexeme(){
			return s;
		}
		
		public boolean equalsIdString(String idString) {
			return idString.equals(l);
		}
	}
	
	//Lexeme Superclass
	public static class Lexeme {
		private String lxm;
		
		public static Lexeme getLexeme() {
			return new Lexeme();
		}
		
		public static Lexeme getLexeme(String lxm) {
			Lexeme lx = new Lexeme();
			lx.setLexeme(lxm);
			return lx;
		}
		
		public Token getToken(Program program, String input, int i) throws Exception {
			program.incremIndex(1);
			return new Token(this.lxm, String.valueOf(input.charAt(i)));
		}
		
		private void setLexeme(String lxm) {
			this.lxm = lxm;
		}
	}
	
	//Lexeme Subclasses
	public static class AsteriskLexeme extends Lexeme {
		@Override
		public Token getToken(Program program, String input, int i) {
			if (i<input.length()-1 && input.charAt(i+1) == '*') {
				program.incremIndex(2);
				return new Token("EXP", "**");
				
			} else {
				program.incremIndex(1);
				return new Token ("MULT", "*");
			}
		}
	}
	
	public static class SlashLexeme extends Lexeme {
		@Override
		public Token getToken(Program program, String input, int i) {
			boolean isHashtag = input.charAt(i)=='#';
			
			if (isHashtag || (i<input.length()-1 && input.charAt(i+1) == '/')) {
				int comCount=0;
				int comSym=1;
				
				if (!isHashtag){
					comSym=2;
				}
				
				while(i<input.length()-comSym && input.charAt(i+comSym)!='\n'){
					comCount++;
					i++;
				}
								
				program.incremIndex(comCount+comSym);
				return null;
			} else {
				program.incremIndex(1);
				return new Token("DIVIDE", "/");
			}
		}
	}
	
	public static class EqualsLexeme extends Lexeme {
		@Override
		public Token getToken(Program program, String input, int i) {
			if (i<input.length()-1 && input.charAt(i+1) == '=') {
				program.incremIndex(2);
				return new Token("COMPARE", "==");
			} else {
				program.incremIndex(1);
				return new Token("ASSIGN", "=");
			}
		}
	}
	
	public static class GreaterLexeme extends Lexeme {
		@Override
		public Token getToken(Program program, String input, int i) {
			if (i<input.length()-1 && input.charAt(i+1) == '=') {
				program.incremIndex(2);
				return new Token("GREATER EQUAL", ">=");
			} else {
				program.incremIndex(1);
				return new Token("GREATER", ">");
			}
		}
	}
	
	public static class LesserLexeme extends Lexeme {
		@Override
		public Token getToken(Program program, String input, int i) {
			if (i<input.length()-1 && input.charAt(i+1) == '=') {
				program.incremIndex(2);
				return new Token("LESSER EQUAL", "<=");
			} else {
				program.incremIndex(1);
				return new Token("LESSER", "<");
			}
		}
	}
	
	public static class NotLexeme extends Lexeme {
		@Override
		public Token getToken(Program program, String input, int i) {
			if (i<input.length()-1 && input.charAt(i+1) == '=') {
				program.incremIndex(2);
				return new Token("NOT EQUAL", "!=");
			} else {
				program.incremIndex(1);
				return new Token("Lexical error:", "illegal character");
			}
		}
	}
	
	public static class LetterLexeme extends Lexeme {
		@Override
		public Token getToken(Program program, String input, int i) {
			String substring = String.valueOf(input.charAt(i));
			while (i<input.length()-1 && ((Character.isAlphabetic(input.charAt(i+1))) || Character.isDigit(input.charAt(i+1)))) {
				substring+=String.valueOf(input.charAt(i+1));
				i++;
			}
			
			if (substring.equals("IF")){
				program.incremIndex(substring.length());
				return new Token("IF", substring);
			}else if (substring.equals("SQRT")){
				program.incremIndex(substring.length());
				return new Token("SQRT", substring);
			}else if (substring.equals("PRINT")){
				program.incremIndex(substring.length());
				return new Token("PRINT", substring);
			}else{
				program.incremIndex(substring.length());
				return new Token("IDENT", substring);
			}
		}
	}
	
	public static class NumLexeme extends Lexeme {
		@Override
		public Token getToken(Program program, String input, int i) throws Exception {
			
			String substring = String.valueOf(input.charAt(i));
			int eCount = 0;
			int dotCount = 0;
			
			boolean badNum = false;
			
			while (i<input.length()-1 && !badNum && (Character.isDigit(input.charAt(i+1)) ||
					((input.charAt(i+1)=='e' || input.charAt(i+1)=='E') && eCount<1) ||
					((input.charAt(i+1)=='-' || input.charAt(i+1)=='+') && (input.charAt(i)=='e' || input.charAt(i)=='E')) ||
					input.charAt(i+1)=='.'
					)){
			
				if(input.charAt(i+1)=='.'){
					dotCount++;
				}else if (input.charAt(i+1)=='e' || input.charAt(i+1)=='E'){
					eCount++;
				}
				
				substring+=String.valueOf(input.charAt(i+1));
				i++;
				
				if(eCount>0 && (input.charAt(i+1) == 'e' || input.charAt(i+1) == 'E')){
					break;
				}
				
				if(i<input.length()-1 && 
						dotCount>1 ||
						(eCount>0 && input.charAt(i+1)=='.') ||
						((input.charAt(i)=='e'||input.charAt(i)=='E') && (!Character.isDigit(input.charAt(i+1)) && input.charAt(i+1)!='-' && input.charAt(i+1)!='+')) ||
						(input.charAt(i)=='-' && !Character.isDigit(input.charAt(i+1))) ||
						(input.charAt(i)=='+' && !Character.isDigit(input.charAt(i+1))) ||
						(input.charAt(i)=='.' && !Character.isDigit(input.charAt(i+1)))
						){
					
					substring+=String.valueOf(input.charAt(i+1));
					i++;
					
					badNum=true;
					break;
				}
			}
			
			program.incremIndex(substring.length());
			
			if (badNum){
				return new Token("Lexical error:", "badly formed number");
			}else{
				return new Token("NUMBER", substring);
			}
		}
	}
	
	public static class StringLexeme extends Lexeme {
		@Override
		public Token getToken(Program program, String input, int i) throws Exception {
			String substring = String.valueOf(input.charAt(i));
			char strClose = '\'';
			
			if (input.charAt(i)== '"'){
				strClose = '"';
			}
			i++;
			
			while(true){
				if (i>input.length()-1 || input.charAt(i)=='\n'){
					program.incremIndex(substring.length());
					return new Token("Lexical error:", "unterminated string ");
				}
				
				substring+=String.valueOf(input.charAt(i));
				
				if(input.charAt(i)==strClose){
					program.incremIndex(substring.length());
					return new Token("STRING", substring.replaceAll("\"", "").replaceAll("\'", ""));
				}
				i++;
			}
		}
	}
}