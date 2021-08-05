import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class InvertedIndex {
	
	private HashMap<String, ArrayList<Posting>> hashMap;
	public int DocAmount = 0;
	
	public InvertedIndex() {
		hashMap = new HashMap<>();
	}
	
	public void write(String sFile) 
			throws IOException {
		FileOutputStream fout = new FileOutputStream(sFile, true);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		
		oos.writeObject(hashMap);
		
		oos.close();
		fout.close();
	}
	
	public void writeMetadata(String sFile) 
			throws IOException {
		this.DocAmount = Runner.DocAmount;
		FileOutputStream fout = new FileOutputStream(sFile, true);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		
		oos.writeObject(DocAmount);
		
		oos.close();
		fout.close();
	}
	
	public void insert(String sTerm, int nDocID, long fscore) {
		if(hashMap.containsKey(sTerm)) {
			ArrayList<Posting> a = hashMap.get(sTerm);
			a.add(new Posting(nDocID, fscore));
		}
		else {
			ArrayList<Posting> a = new ArrayList<>();
			Posting p = new Posting(nDocID, fscore);
			a.add(p);
			hashMap.put(sTerm, a);
		}
	}
	
	public void load(String sFile) throws ClassNotFoundException, IOException {
		FileInputStream fin = new FileInputStream(sFile);
		ObjectInputStream oin = new ObjectInputStream(fin);
		
		hashMap = (HashMap<String, ArrayList<Posting>>) oin.readObject();
		
		oin.close();
		fin.close();
	}
	
	public void loadMetadata(String sFile) throws ClassNotFoundException, IOException{
		FileInputStream fin = new FileInputStream(sFile);
		ObjectInputStream oin = new ObjectInputStream(fin);
		
		DocAmount = (int) oin.readObject();
		
		oin.close();
		fin.close();
	}
	
	public void print() {
		Set<String> s = hashMap.keySet();
		Iterator<String> it = s.iterator();
		
		while(it.hasNext()) {
			String sTerm = it.next();
			ArrayList<Posting> a = hashMap.get(sTerm);
			
			System.out.println(sTerm + "("+a.size()+")"+"->");
			for(int i=0; i<a.size(); i++) {
				System.out.println(a.get(i).toString());
			}
		}
	}
	
	/** If term does not exists then returns null**/
	public ArrayList<Posting> get(String sTerm){
		return hashMap.get(sTerm);
	}

}
