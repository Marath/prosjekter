import java.net.*;
import java.io.*;
import java.util.*;


public class Client {
    
    InetAddress address;
    Socket connection;
    String TimeStamp;
    String fromServer, toServer;

    PrintWriter out;
    BufferedReader in;

    public void runClient(String host, String port) {

	int portNumber = Integer.parseInt(port);
	//	Comms_protocol comms = new Comms_protocol();

	try {
	    address = InetAddress.getByName(host);
	    connection = new Socket(address, portNumber);
	    
	    out = new PrintWriter(connection.getOutputStream(), true);
	    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    
	    System.out.println("Client initialized");
	    
	    while((fromServer = in.readLine()) != null) {
		System.out.println("Server: " + fromServer);
		
		if(fromServer.equals("Bye."))
		    break;
		
		toServer = stdIn.readLine();
		if(toServer != null) {
		    System.our.println("Client: " + toServer);
		    out.println(toServer);
		}
	    }
	    

	} catch(IOException e) {
	    System.out.println("IOException: " + e);
	} catch(Exception g) {
	    System.out.println("Exception: " + g);
	}
	


	
    }

}
