import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BalancedBinaryTree
{
    private BalancedBinaryTreeNode root;
 
    public BalancedBinaryTree()
    {
        root = null;
    }
 
    public boolean isEmpty()
    {
        return root == null;
    }
 
    public void clear()
    {
        root = null;
    }
 
    public void insert(String term)
    {
        root = insert(term, root);
    }
 
    private int height(BalancedBinaryTreeNode t)
    {
 
        return t == null ? -1 : t.height;
    }
 
    private int max(int lhs, int rhs)
    {
        return lhs > rhs ? lhs : rhs;
    }
 
    private BalancedBinaryTreeNode insert(String x, BalancedBinaryTreeNode t)
    {
    	int compare = 0;
    	if(t != null) {
    		compare = x.compareTo(t.term);
    	}
    	
        if (t == null)
            t = new BalancedBinaryTreeNode(x);
        else if (compare < 0)
        {
            t.left = insert(x, t.left);
            if (height(t.left) - height(t.right) == 2) {
            	compare = x.compareTo(t.left.term);
                if (compare < 0)
                    t = rotateWithLeftChild(t);
                else
                    t = doubleWithLeftChild(t);
            }
        } else if (compare > 0)
        {
            t.right = insert(x, t.right);
            if (height(t.right) - height(t.left) == 2) {
            	compare = x.compareTo(t.right.term);
                if (compare > 0)
                    t = rotateWithRightChild(t);
                else
                    t = doubleWithRightChild(t);
            }
        } else
            ;
        t.height = max(height(t.left), height(t.right)) + 1;
        return t;
    }
 
    private BalancedBinaryTreeNode rotateWithLeftChild(BalancedBinaryTreeNode k2)
    {
    	BalancedBinaryTreeNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = max(height(k2.left), height(k2.right)) + 1;
        k1.height = max(height(k1.left), k2.height) + 1;
        return k1;
    }
 
    private BalancedBinaryTreeNode rotateWithRightChild(BalancedBinaryTreeNode k1)
    {
    	BalancedBinaryTreeNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = max(height(k1.left), height(k1.right)) + 1;
        k2.height = max(height(k2.right), k1.height) + 1;
        return k2;
    }
 
    private BalancedBinaryTreeNode doubleWithLeftChild(BalancedBinaryTreeNode k3)
    {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }
 
    private BalancedBinaryTreeNode doubleWithRightChild(BalancedBinaryTreeNode k1)
    {
        k1.right = rotateWithLeftChild(k1.right);
        return rotateWithRightChild(k1);
    }
 
    public int countNodes()
    {
        return countNodes(root);
    }
 
    private int countNodes(BalancedBinaryTreeNode r)
    {
        if (r == null)
            return 0;
        else
        {
            int l = 1;
            l += countNodes(r.left);
            l += countNodes(r.right);
            return l;
        }
    }
 
    public boolean search(String val)
    {
        return search(root, val);
    }
 
    private boolean search(BalancedBinaryTreeNode r, String val)
    {
        boolean found = false;
        while ((r != null) && !found)
        {
            String rval = r.term;
            
            int compare = val.compareTo(rval);
            
            if (compare < 0)
                r = r.left;
            else if (compare > 0)
                r = r.right;
            else
            {
                found = true;
                break;
            }
            found = search(r, val);
        }
        return found;
    }
    
    public BalancedBinaryTreeNode searchWordStartingWith(String val)
    {
        return searchWordStartingWith(root, val);
    }
    
    private BalancedBinaryTreeNode searchWordStartingWith(BalancedBinaryTreeNode r, String val)
    {
        boolean found = false;
        while ((r != null) && !found)
        {
            String rval = r.term;
            
            int compare = val.compareTo(rval);
            boolean gotIt = rval.startsWith(val);
            if(gotIt) {
            	System.out.println(r.term);
            	System.out.println(r.originalTerm);
            	return r;
            }
            
            if (compare < 0)
                r = r.left;
            else if (compare > 0)
                r = r.right;
            else
            {
                found = true;
                System.out.println(r.term);
                System.out.println(r.originalTerm);
                break;
            }
            found = search(r, val);
        }
        return r;
    }
 
    public void inorder()
    {
        inorder(root);
    }
 
    private void inorder(BalancedBinaryTreeNode r)
    {
        if (r != null)
        {
            inorder(r.left);
            System.out.println(r.term + " ");
            inorder(r.right);
        }
    }
 
    public void preorder()
    {
 
        preorder(root);
    }
 
    private void preorder(BalancedBinaryTreeNode r)
    {
        if (r != null)
        {
            System.out.print(r.term + " ");
            preorder(r.left);
            preorder(r.right);
        }
    }
 
    public void postorder()
    {
        postorder(root);
    }
 
    private void postorder(BalancedBinaryTreeNode r)
    {
        if (r != null)
        {
            postorder(r.left);
            postorder(r.right);
            System.out.print(r.term + " ");
        }
    }
    
    // Saves a balancedBinaryTree from a file
    public void write(String sFile) 
			throws IOException {
		FileOutputStream fout = new FileOutputStream(sFile, true);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		
		oos.writeObject(root);
		
		oos.close();
		fout.close();
	}
    
    // Loads a balancedBinaryTree from a file
    public void load(String sFile) throws ClassNotFoundException, IOException {
		FileInputStream fin = new FileInputStream(sFile);
		ObjectInputStream oin = new ObjectInputStream(fin);
		
		root = (BalancedBinaryTreeNode) oin.readObject();
		
		oin.close();
		fin.close();
	}
    
    // This method will search all the nodes between the given terms and return them in a String like so:
    // "term1 term2 term3 "
    // it will call a recursive "inorder" method for the root element
    private String inorder(String smallTerm, String bigTerm) {
    	return inorder(root, smallTerm, bigTerm);
    }
    
    // This method will search all the nodes between the given terms
    // This recursive method will only go inside the needed nodes to be as fast as possible 
    private String inorder(BalancedBinaryTreeNode r, String smallTerm, String bigTerm) {
    	String result = "";
    	if(r != null) {
    		int compareSmall = r.term.compareTo(smallTerm);
    		int compareBig = r.term.compareTo(bigTerm);
    		if(compareSmall < 0) {
    			result += inorder(r.right, smallTerm, bigTerm);
    		}
    		else if(compareBig > 0) {
    			result += inorder(r.left, smallTerm, bigTerm);
    		}
    		else {
    			result += inorder(r.left, smallTerm, bigTerm);
    			if(!r.term.startsWith(bigTerm)) {
    				// if searching for mon then i need mon <= x < moo
    				result += r.originalTerm + " ";
    			}
    			result += inorder(r.right, smallTerm, bigTerm);
    		}
    		
    	}
    	return result;
      }
    
    // uses a search to get the words between two terms (alphabeticaly)
    public String[] getAllWordsBetween(String smallTerm, String bigTerm) {
    	String InOrderWords = inorder(smallTerm, bigTerm);
    	String[] words = InOrderWords.split(" ");
    	
    	// Remove duplicates
    	HashMap<String, Integer> wordsHashMap = new HashMap<String, Integer>();
    	for(String word : words) {
    		if(!wordsHashMap.containsKey(word)) {
    			wordsHashMap.put(word, 1);
			}
			else {
				int cnt = wordsHashMap.get(word);
				wordsHashMap.put(word, ++cnt);
			}
    	}
    	
    	// Get the HashMap as an array of Strings
    	String[] resultWords = new String[wordsHashMap.size()];
    	int i=0;
    	for (HashMap.Entry<String, Integer> entry : wordsHashMap.entrySet()) {
    		resultWords[i++] = entry.getKey();
    	}
    	
    	return resultWords;
    }
}
