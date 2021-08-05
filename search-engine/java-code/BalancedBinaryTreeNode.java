import java.io.Serializable;

public class BalancedBinaryTreeNode implements Serializable
{
	BalancedBinaryTreeNode left, right;
    int height;
    
    String term;
    String originalTerm;
    
 
    public BalancedBinaryTreeNode()
    {
        left = null;
        right = null;
        term = "";
        height = 0;
        originalTerm = "";
    }
 
    public BalancedBinaryTreeNode(String term)
    {
        left = null;
        right = null;
        this.term = term;
        height = 0;
        this.setOriginalTerm(term);
    }
    
    public void setOriginalTerm(String term) {
    	String[] parts = term.split("€");	// this is my special character
    	this.originalTerm = "";
    	for(int i = 0; i < parts.length; i++) {
    		this.originalTerm+=parts[parts.length-1-i];	// I will have ready the word for speed reasons
    	}
    }
}
