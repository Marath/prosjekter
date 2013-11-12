public class tre_prosjekt {
    public static void main(String args[]) {

    }
}

// Node class
class Node {
    String contents;
    int tag;
    char color;
    private Node left, right, parent;
    
    Node(int i, char col, String s) 
    {
	contents = s;
	tag = i;
	color = col;
    }

    public Node getRight() 
    {
	return right;
    }
    
    public Node getLeft() 
    {
	return left;
    } 

    public void printContents() {
	System.out.println(tag + " " + color + " " + contents);
    }

    public void print() {
	left.print();
	printContents();
	right.print();
    }
}

// Tree class
class Tree {
    Node root;

    Tree() {

    }
    
    public void Insert(Node n, int tag, char color, String s) 
    {
	if(root == null) 
	    root = new Node(tag, color, s);
	    
     	if(tag <= n.getLeft().tag) 
	    Insert(n.getLeft(), tag, color, s);
	else 
	    Insert(n.getRight(), tag, color, s);
    }
    
    public void deleteAll() 
    {
	root = null;
    }

    public void mirrorTree() 
    {
	Node n = root;
	Node dummy;
	Node left = n.getLeft();
	Node right = n.getRight();
	dummy = left;
	left = right;
	right = dummy;
    }
    
    public void printTree() 
    {
	if(root == null) 
	    System.out.println("Empty tree");
	
	root.print();
    }
}
