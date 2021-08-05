import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MapDocid2Files implements Serializable{
	private String[] map;
	
	public MapDocid2Files(int size) {
		map = new String[size];
	}
	
	public MapDocid2Files() {
		
	}

	public String get(int pos) {
		return map[pos];
	}
	
	public void add(int pos, String filename) {
		map[pos] = filename;
	}
	
	public void load(String sFile) throws ClassNotFoundException, IOException {
		FileInputStream fin = new FileInputStream(sFile);
		ObjectInputStream oin = new ObjectInputStream(fin);
		
		map = (String[]) oin.readObject();
		
		oin.close();
		fin.close();
	}
	
	public void write(String sFile) 
			throws IOException {
		FileOutputStream fout = new FileOutputStream(sFile, true);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		
		oos.writeObject(map);
		
		oos.close();
		fout.close();
	}
}
