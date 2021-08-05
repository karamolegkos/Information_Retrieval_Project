import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Runner{
	
	public static int nDocId = 0;
	public static int DocAmount = 0;

	public static void main(String[] args) {
		Scanner intScanner = new Scanner(System.in); 
		Scanner strScanner = new Scanner(System.in); 
		
		String pathSerializable1 = "myInvetedIndex";
		String pathSerializable2 = "myBalancedBinaryTree";
		String pathSerializable3 = "myDocAmount";
		String pathSerializable4 = "myMapedFiles";
		
		InvertedIndex invertedIndex = new InvertedIndex();
		BalancedBinaryTree bbtIndex = new BalancedBinaryTree();
		MapDocid2Files mapedFiles = new MapDocid2Files();
		
		int ans = -1;
		boolean invertedIndexGot = false;
		
		while(ans != 0) {
			System.out.println("[1] - load the invertedIndex.");
			System.out.println("[2] - Get the invertedIndex.");
			System.out.println("[3] - Make a query to the invertedIndex.");
			System.out.println("[0] - Exit.");
			
			ans = intScanner.nextInt();
			
			if(ans==1) {
				invertedIndex = new InvertedIndex();
				bbtIndex = new BalancedBinaryTree();
				nDocId = 0;
				DocAmount = 0;
				
				File f1 = new File(pathSerializable1); 
				File f2 = new File(pathSerializable2); 
				File f3 = new File(pathSerializable3); 
				File f4 = new File(pathSerializable4); 
				
				if (f1.exists()) f1.delete();
				if (f2.exists()) f2.delete();
				if (f3.exists()) f3.delete();
				if (f4.exists()) f4.delete();
				
				System.out.println("Give me the path of the directory to load the invertedIndex from.\n(my/path/directory)");
				String path = strScanner.nextLine();
				
				System.out.println("Given Path: "+path);
				
				Parser parser = new Parser();
				File directory = new File(path);
				getSubFilesCount(directory);
				
				
				System.out.println("Amount of Documents in the Given Path: "+DocAmount);
				mapedFiles = new MapDocid2Files(DocAmount+1);
				
				try {
					// I will hold the posting lists AND a balanced binary tree for wild card queries
					invertedIndex = parser.makeInvertedIndex(directory, invertedIndex, mapedFiles);
					bbtIndex = parser.makeBBTree(directory, bbtIndex);
				} catch (IOException e1) {
					System.out.println("Something went wrong! 1");
					e1.printStackTrace();
				}
				// Below i save to the hard disc my InvertedIndex and my Balanced Binary Tree
				try {
					invertedIndex.write(pathSerializable1);
					invertedIndex.writeMetadata(pathSerializable3);
				} catch (IOException e) {
					System.out.println("Something went wrong! 2");
					e.printStackTrace();
				}
				try {
					bbtIndex.write(pathSerializable2);
				} catch (IOException e) {
					System.out.println("Something went wrong! 3");
					e.printStackTrace();
				}
				// Below i save to the Maped DocIDs to the Files Paths
				try {
					mapedFiles.write(pathSerializable4);
				} catch (IOException e) {
					System.out.println("Something went wrong! 10");
					e.printStackTrace();
				}
				invertedIndexGot = true;
			}
			if(ans==2) {
				invertedIndex = new InvertedIndex();
				bbtIndex = new BalancedBinaryTree();
				nDocId = 0;
				DocAmount = 0;
				
				File f1 = new File(pathSerializable1); 
				
				if(!f1.exists()) {
					System.out.println("There is no Inverted Index saved!\nPlease save one!");
				}
				else {
					invertedIndexGot = true;
					try {
						invertedIndex.load(pathSerializable1);
						invertedIndex.loadMetadata(pathSerializable3);
						DocAmount = invertedIndex.DocAmount;
					} catch (ClassNotFoundException e) {
						System.out.println("Something went wrong! 4");
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println("Something went wrong! 5");
						e.printStackTrace();
					}
					System.out.println("Amount of Documents loaded: "+DocAmount);
					
					try {
						bbtIndex.load(pathSerializable2);
					} catch (ClassNotFoundException e) {
						System.out.println("Something went wrong! 6");
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println("Something went wrong! 7");
						e.printStackTrace();
					}

					try {
						mapedFiles.load(pathSerializable4);
					} catch (ClassNotFoundException e) {
						System.out.println("Something went wrong! 8");
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println("Something went wrong! 9");
						e.printStackTrace();
					}
				}
			}
			if(ans==3) {
				
				File f1 = new File(pathSerializable1); 
				
				if(!f1.exists()) {
					System.out.println("There is no Inverted Index to use!\nPlease load one!");
				}
				else if(!invertedIndexGot) {
					System.out.println("There is no Inverted Index to use!\nPlease load one!");
				}
				else {
					System.out.println("Give me a query");
					String query = strScanner.nextLine();
					
					Integer[] answer = Search.queryAnswer(query, invertedIndex, bbtIndex);
					System.out.println("Results:");
					for(int i=0; i<answer.length; i++) {
						System.out.println("DocID: "+answer[i]+" | It can be found here: "+mapedFiles.get(answer[i]));
					}
					
				}
			}
			System.out.println();
			System.out.println();
		}
		intScanner.close();
		strScanner.close();

	}
	
	// it will count the docs to map them properly in the main method
	private static void getSubFilesCount(File dir) {
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				getSubFilesCount(file);
			}
			else {
				if(!file.getName().endsWith(".txt")) {
					continue;
				}
				DocAmount++;
			}
		}
	}
}
