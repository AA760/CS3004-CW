//Most of the code for this class was taken from CS3004 Tutorial 4 (Prof. Simon Taylor).
package cw;
import java.net.*;
import java.io.*;

public class BankServer {

	  public static void main(String[] args) throws IOException {

		ServerSocket BankServerSocket = null;
	    String BankServerName = "BankServer";
	    int BankServerNumber = 4546;
	    
	    double Client1Account = 1000;
	    double Client2Account = 1000;
	    double Client3Account = 1000;
	    
	    SharedBankState ourSharedBankStateObject = new SharedBankState(Client1Account,Client2Account,Client3Account);
	        
	    try {
	      BankServerSocket = new ServerSocket(BankServerNumber);
	    } catch (IOException e) {
	      System.err.println("Could not start " + BankServerName + " specified port.");
	      System.exit(-1);
	    }
	    System.out.println(BankServerName + " started");
	    
	    while (true){
	      new BankServerThread(BankServerSocket.accept(), "BankServerThread1", ourSharedBankStateObject).start();
	      new BankServerThread(BankServerSocket.accept(), "BankServerThread2", ourSharedBankStateObject).start();
	      new BankServerThread(BankServerSocket.accept(), "BankServerThread3", ourSharedBankStateObject).start();
	      System.out.println("All 3 " + BankServerName + " threads started.");
	    }
	 }
}
