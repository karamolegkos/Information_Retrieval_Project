import java.util.ArrayList;

public class Search {
	
	static final String CONST_AND="AND";
    static final String CONST_OR="OR";
    static final String CONST_NOT="NOT";
    
    // Returns true if the given String is a boolean algebra operator
    private static boolean isOperator(String str) {
    	if(str.equals(CONST_AND) ||
    			str.equals(CONST_OR) ||
    			str.equals(CONST_NOT)) {
    		return true;
    	}
    	return false;
    }
	
	// Gets two Integer[] and finds the logical AND between them
	public static Integer[] AandB(Integer[] aArray, Integer[] bArray) {
		int sizeA = aArray.length;
		int sizeB = bArray.length;
		
		ArrayList<Integer> resultList = new ArrayList<>();
		int aDex = 0;
		int bDex = 0;
		
		while(aDex < sizeA && bDex < sizeB) {
			if(aArray[aDex] < bArray[bDex]) {
				aDex++;
			}
			else if(aArray[aDex] > bArray[bDex]) {
				bDex++;
			}
			else {
				resultList.add(aArray[aDex]);
				aDex++;
				bDex++;
			}
		}
		return resultList.toArray(new Integer[0]);
	}
	
	// Gets two Integer[] and finds the logical OR between them
	public static Integer[] AorB(Integer[] aArray, Integer[] bArray) {
		int sizeA = aArray.length;
		int sizeB = bArray.length;
		
		ArrayList<Integer> resultList = new ArrayList<>();
		int aDex = 0;
		int bDex = 0;
		
		while(aDex < sizeA && bDex < sizeB) {
			if(aArray[aDex] < bArray[bDex]) {
				resultList.add(aArray[aDex]);
				aDex++;
			}
			else if(aArray[aDex] > bArray[bDex]) {
				resultList.add(bArray[bDex]);
				bDex++;
			}
			else {
				resultList.add(aArray[aDex]);
				aDex++;
				bDex++;
			}
		}
		
		if(bDex == sizeB) {
			while(aDex < sizeA) {
				resultList.add(aArray[aDex]);
				aDex++;
			}
		}
		else if(aDex == sizeA) {
			while(bDex < sizeB) {
				resultList.add(bArray[bDex]);
				bDex++;
			}
		}
		return resultList.toArray(new Integer[0]);
	}
	
	// Gets one Integer[] and finds its logical NOT
	public static Integer[] notA(Integer[] aArray) {
		int sizeA = aArray.length;
		int aDex = 0;
		
		ArrayList<Integer> resultList = new ArrayList<>();
		
		for(int i=1; i<Runner.DocAmount+1; i++) {
			if(sizeA != aDex) {
				if(aArray[aDex] > i) {
					resultList.add(i);
				}
				else if(aArray[aDex] == i) {
					aDex++;
				}
			}
			else {
				resultList.add(i);
			}
		}
		
		return resultList.toArray(new Integer[0]);
	}
	
	// Gets two Integer[] and finds the logical A OR NOT B between them
	public static Integer[] AorNotB(Integer[] aArray, Integer[] bArray) {
		return AorB(aArray, notA(bArray));
	}
	
	// Gets two Integer[] and finds the logical A AND NOT B between them
	public static Integer[] AandNotB(Integer[] aArray, Integer[] bArray) {
		return AandB(aArray, notA(bArray));
	}
	
	// Gets one or more Integer[] and finds all the documents between them (A OR B OR C OR D OR ...)
	public static Integer[] manyOR(Integer[][] integers) {
		Integer[] results = new Integer[0];
		for(Integer[] integerArray : integers) {
			results = AorB(results, integerArray);
		}
		return results;
	}
	
	// A function to respond to the right action for each sub-query
	private static Integer[] makeAction(String op, Integer[] a, Integer[] b) {
		if(op.equals(CONST_AND)) {
			return AandB(a,b);
		}
		else if(op.equals(CONST_OR)) {
			return AorB(a,b);
		}
		else if(op.equals(CONST_AND+" "+CONST_NOT)) {
			return AandNotB(a,b);
		}
		else // op.equals(CONST_AND+" "+CONST_NOT)
		{
			return AorNotB(a,b);
		}
	}
	
	// Answers to queries
	public static Integer[] queryAnswer(String query, InvertedIndex invertedIndex, BalancedBinaryTree bbtIndex) {
		String operator = CONST_OR;
		String[] terms = query.split(" ");
		
		Integer[] results = new Integer[0];
		
		for(int i=0; i<terms.length; i++) {
			if(!isOperator(terms[i]) && !terms[i].contains("*")) {
				// Get the Document Array of the given word
				Integer[] myArray = getDocumentArrayOfWord(terms[i], invertedIndex);
				
				results = makeAction(operator, results, myArray);
				operator = "";
			}
			else if(!isOperator(terms[i]) && terms[i].contains("*")) {
				Integer[] myArray = getWildDocuments(terms[i].toLowerCase(), bbtIndex, invertedIndex);

				results = makeAction(operator, results, myArray);
				operator = "";
			}
			else if(!operator.equals("")){
				operator += " "+terms[i];
			}
			else {
				operator = terms[i];
			}
		}
		return results;
	}
	
	private static Integer[] getWildDocuments(String wordQuery, BalancedBinaryTree bbtIndex, InvertedIndex invertedIndex) {
		if(wordQuery.startsWith("*") && wordQuery.endsWith("*")) {
			String smallTerm = wordQuery.substring(1,wordQuery.length()-1);
			return getOrOfManyWords(smallTerm, bbtIndex, invertedIndex);
		}
		else if(wordQuery.endsWith("*")) {
			String smallTerm = "€"+wordQuery.substring(0,wordQuery.length()-1);
			return getOrOfManyWords(smallTerm, bbtIndex, invertedIndex);
		}
		else if(wordQuery.startsWith("*")) {
			String smallTerm = wordQuery.substring(1,wordQuery.length())+"€";
			return getOrOfManyWords(smallTerm, bbtIndex, invertedIndex);
		}
		else if(countCharTimesInString('*', wordQuery) == 1) {
			wordQuery = wordQuery.replace("*", " ");
			String wordToLookup1 = wordQuery.split(" ")[0];
			String wordToLookup2 = wordQuery.split(" ")[1];
			String smallTerm = wordToLookup2+"€"+wordToLookup1;
			return getOrOfManyWords(smallTerm, bbtIndex, invertedIndex);
		}
		else {
			return new Integer[0];
		}
	}
	
	private static Integer[] getDocumentArrayOfWord(String word, InvertedIndex invertedIndex) {
		ArrayList<Posting> aL = invertedIndex.get(word.toLowerCase());
		Posting[] arrayA = null;
		Integer[] myArray;
		if(aL == null) {
			myArray = new Integer[0];
		}
		else {
			arrayA = aL.toArray(new Posting[0]);
			myArray = new Integer[arrayA.length];
		}
		
		for(int z = 0; z<myArray.length; z++) {
			myArray[z] = arrayA[z].getDocId();
		}
		
		return myArray;
	}
	
	private static int countCharTimesInString(char c, String word) {
		int sum = 0;
		for(int i=0; i<word.length(); i++) {
			if(word.toCharArray()[i] == c) {
				sum++;
			}
		}
		return sum;
	}
	
	private static Integer[] getOrOfManyWords(String smallTerm, BalancedBinaryTree bbtIndex, InvertedIndex invertedIndex) {
		// make the bigTerm
		String value = smallTerm.toCharArray()[smallTerm.length()-1]+"";
		int charValue = value.charAt(0);
		String next = String.valueOf((char) (charValue+1));
		String bigTerm = smallTerm.substring(0, smallTerm.length()-1)+next;
		
		// Get the words from the word Query
		String[] resultedWords = bbtIndex.getAllWordsBetween(smallTerm, bigTerm);
		
		Integer[][] resultDocumentArrays = new Integer[resultedWords.length][];
		
		// For every word, hold its Documents Array
		for(int i=0; i<resultedWords.length; i++) {
			String word = resultedWords[i];
			Integer[] myArray = getDocumentArrayOfWord(word, invertedIndex);
			
			resultDocumentArrays[i] = myArray;
		}
		
		// Make an OR statement between all the words and return the result
		return manyOR(resultDocumentArrays);
	}

     
}
