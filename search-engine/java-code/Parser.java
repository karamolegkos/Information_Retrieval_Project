import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class Parser {

	public List<String> parseFile(String sFile) 
			throws IOException {
		
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = new BufferedReader(new FileReader(sFile));
		
		String sLine = "";
		
		while((sLine = br.readLine()) != null) {
			sb.append(sLine).append(" ");
		}
		
		return tokenize(sb.toString());
	}
	
	public List<String> tokenize(String sLine){
		List<String> a = new ArrayList<>();
		
		
		String DELIM = " .!,\t\n;()[]{}:\"-/'";
		StringTokenizer st = new StringTokenizer(sLine,DELIM);
		
		String sToken = "";
		while(st.hasMoreTokens()) {
			sToken = st.nextToken();
			sToken = sToken.toLowerCase();
			a.add(sToken);
		}
		
		return a;
	}
	
	public InvertedIndex makeInvertedIndex(File dir, InvertedIndex invertedIndex, MapDocid2Files m) throws IOException {
		//Parser parser = new Parser();
		
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				makeInvertedIndex(file, invertedIndex, m);
			}
			else {
				if(!file.getName().endsWith(".txt")) {
					continue;
				}
				Runner.nDocId++;
				List<String> a = this.parseFile(file.getCanonicalPath());
				
				HashMap<String, Long> hDocTF = new HashMap<String, Long>();
				for(int i=0; i<a.size(); i++) {
					String sTerm = a.get(i);
					if(!hDocTF.containsKey(sTerm)) {
						hDocTF.put(sTerm, 1L);
					}
					else {
						long cnt = hDocTF.get(sTerm);
						hDocTF.put(sTerm, ++cnt);
					}
				}
				
				hDocTF.forEach((key,value) -> {
					invertedIndex.insert(key, Runner.nDocId, value);
				});
				
				
				m.add(Runner.nDocId, file.getPath());
			}
		}
		
		return invertedIndex;
		
	}
	
	public BalancedBinaryTree makeBBTree(File dir, BalancedBinaryTree bbtInvIndex) throws IOException {
		//Parser parser = new Parser();
		
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				makeBBTree(file, bbtInvIndex);
			}
			else {
				if(!file.getName().endsWith(".txt")) {
					continue;
				}
				List<String> a = this.parseFile(file.getCanonicalPath());
				for(int i=0; i<a.size(); i++) {
					String sTerm = a.get(i)+"€";	// The special character
					// make all the possible forms
					String[] forms = getForms(sTerm);
					for(int j=0; j<forms.length; j++) {
						bbtInvIndex.insert(forms[j]);
					}
				}
			}
		}
		
		return bbtInvIndex;
		
	}
	
	/** Gets a word with the character € in the end and returns all the forms for the balanced binary tree**/
	private static String[] getForms(String word) {
		String[] forms = new String[word.length()];
		String str = word;
		for(int i = 0; i<word.length(); i++) {
			forms[i] = str;
			str = str.substring(1) + str.substring(0, 1);
		}
		return forms;
	}
}
