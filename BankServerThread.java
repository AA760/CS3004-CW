//Most of the code for this class was taken from CS3004 Tutorial 4 (Prof. Simon Taylor).
package cw;
import java.net.*;
import java.io.*;


public class BankServerThread extends Thread {

	
  private Socket bankSocket = null;
  private SharedBankState mySharedBankStateObject;
  private String myBankServerThreadName;

  	public BankServerThread(Socket bankSocket, String BankServerThreadName, SharedBankState SharedObject) {
	
	  this.bankSocket = bankSocket;
	  mySharedBankStateObject = SharedObject;
	  myBankServerThreadName = BankServerThreadName;
	}

  public void run() {
    try {
      System.out.println(myBankServerThreadName + " initialising.");
      PrintWriter out = new PrintWriter(bankSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(bankSocket.getInputStream()));
      String inputLine, outputLine;

      while ((inputLine = in.readLine()) != null) {
    	  try { 
    		  mySharedBankStateObject.acquireLock();  
    		  outputLine = mySharedBankStateObject.processInput(myBankServerThreadName, inputLine);
    		  out.println(outputLine);
    		  mySharedBankStateObject.releaseLock();  
    	  } 
    	  catch(InterruptedException e) {
    		  System.err.println("Failed to get lock when reading:"+e);
    	  }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}