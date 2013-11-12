public class Comms_protocol {

    String processInput(String inputLine) {
	if(Integer.parseInt(inputLine) == -1) {
	    System.out.println("Shutting down");
	    System.exit(0);
	} 
	return inputLine;
	
    }

    String processOutput(String outputLine) {
	return outputLine;
    }
   
}

