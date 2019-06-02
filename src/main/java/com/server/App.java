package com.server;

import java.net.UnknownHostException;

public class App 
{
	 
	
    public static void main( String[] args ) throws UnknownHostException
    {
    	Server server;
    	InteractDB connectionOfMariaDB = InteractMariaDB.getInstance();
    	int port = 12054;
    	if (args.length != 1) {
    		System.out.println("Usage to shange port: java -jar program.jar [port]");
    		server = new Server(port, connectionOfMariaDB);
    		// System.exit(1);
    	}else {
    		port = Integer.parseInt(args[0]);
    		server = new Server(port, connectionOfMariaDB);
    	}
    	
    	
    }
    
    
}
