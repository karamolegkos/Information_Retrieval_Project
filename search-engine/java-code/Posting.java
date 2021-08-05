import java.io.Serializable;

public class Posting implements Serializable{
	private int docId;
	private long fScore;
	
	public Posting(int docId, long fScore) {
		this.docId = docId;
		this.fScore = fScore;
	}
	
	public int getDocId() {
		return docId;
	}
	
	public long getfScore() {
		return fScore;
	}
	
	@Override
	public String toString() {
		return "[docId: "+docId+", fscore: "+fScore+"]";
	}
}
