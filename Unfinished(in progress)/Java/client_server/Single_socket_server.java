import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Client server program, accept coordinates from Client
 * by the form of x,y,
 * sets an 'X' in the target location of the grid held by the 
 * Server, sends ack back to client
 *
 */

public class Single_socket_server 
{
    
    String grid[][]; //grid where to set the x
    ServerSocket serverSocket; 
    Socket clientSocket;

    PrintWriter out; //write input to screen
    BufferedReader in; //recieve from clientSocket
    
    public Single_socket_server(String a[][]) 
    {
	grid = a;
    }
    
    public void runServer(String port) 
    {
	int portNumber = Integer.parseInt(port);
	String inputLine, outputLine;
	Comms_protocol comms = new Comms_protocol();
	
	try {
	    //server inits
	    serverSocket = new ServerSocket(portNumber);
	    clientSocket = serverSocket.accept(); //accept client
	    out = new PrintWriter
		(clientSocket.getOutputStream(), true);
	    
	    in = new BufferedReader
		(new InputStreamReader(clientSocket.getInputStream()));
	    
	    //communication begins
	    outputLine = comms.processInput(null);
	    out.println("Connection established on port: " + port); //testprint to client
	    int counter = 0;
	    
	    //listening begins
	    while((inputLine = in.readLine()) != null) {
		if(inputLine.length() < 3) {//accepting string of 3 chars 
		    if(counter < 10) 
			return;
		    else
			break;
		}
		
		outputLine = comms.processInput(inputLine);
		out.println(outputLine);
		
		//set the X at given chars
		grid[(int) inputLine.charAt(0)][(int) inputLine.charAt(2)] = "X";
		//might be redundant with protocol
		if(inputLine.equals("-1")) {
		    out.println("Bye.");
		    break;
		
		}
	    }//END while
	    
	} catch(IOException e) {
	    System.out.println("IOexception: " + e);
	} catch(Exception g) {
	    System.out.println("Exception: " + g);
	}
	
    } //END runServer
    
}
