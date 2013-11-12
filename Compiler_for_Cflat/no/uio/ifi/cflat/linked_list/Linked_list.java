package no.uio.ifi.cflat.Linked_list;

import java.io.*;

public class Linked_list {
    
    /**
     * Indre Node klasse
     *
     */
    class Node {
   	Node first;
	Node next;
	String val;

	Node (String value) {
	    this.val = value;
	}
	
	public Node getFirst() {
	
	}
	
	public Node getNext() {
	    return next;
	}
	public void setNext(Node toSet) {
	    next = toSet;
	}
	/**
	 * Sette node verdi for en gitt node 
	 */
	public void setNodeVal(Node toChange, String newVal) {
	    toChange.val = newVal;
	}

      	public String getNodeVal(Node n) {
	    return n.val;
	}
    }/*--------------- Nodeclass END -------------------*/
    
    Node node;
    
    
    public boolean add(E e) {
	Node next;
	Node prev;
	Node curr = first;
	Node newNode = new Node(e);
	if (e == null) {
	    throw new NullPointerException();
	}
	if(contains(e)) { //hvis finns allerede
	    return false;
	}
	if (curr == null) { //hvis tom
	    first = newNode;
	    size++;
	    return true;
	}  else { //hvis ikke tom
	    if (e.compareTo(first.data) < 0) { //hvis første element 
		newNode.next = first;
		first = newNode;
		size++;
		return true;
	    } 
	    while(curr.next != null) {  //gamper gjennom
		if(e.compareTo(curr.next.data) < 0) {
		    newNode.next = curr.next;
		    curr.next = newNode; 
		    size++;
		    return true;
		}
		curr = curr.next;	
	    }
	    curr.next = newNode;
	    size++;
	    return true; 
	}
    }

  public boolean remove(E e) {
	Node curr = first;
	if (e == null) {
	    throw new NullPointerException();
	}
	if (curr == null) {
	    return false;	    
	}
	if(curr.data.compareTo(e) == 0) {
	    first = curr.next;
	    size--;
	    return true;
	}	
	while(curr.next != null) {
	    if(curr.next.data.compareTo(e) == 0) {
		curr.next = curr.next.next;
		size--;
		return true;
	    }
	    curr = curr.next;	    
	} //end if #3
	return false;
    }


     public E get(E e) {
	Node curr = first;
	if (e == null) {
	    throw new NullPointerException();
	}
	while (curr != null) {
	    if (curr.data.compareTo(e) == 0) {
		return curr.data;
	    }
	    curr = curr.next;
	}
	return null;
    }
  
    
    /**
     * Testmetoder for bruk sammen med java.util.linkedlist
     *
     *
     */
    
    public int size() {
	return size;
    }

    public boolean isEmpty() {
	return first == null && size == 0;
    }
    
    public void clear() {
	first = null;
	size = 0;
    }


}