/*Some of the code for this class was taken from CS3004 Tutorial 4 (Prof. Simon Taylor). 
 * The biggest exclusions for this are the add(), subtract(), transfer(), firstClient(), 
 * secondClient(),  inputMoney() methods, and most of the processInput() method. 
 */
package cw;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class SharedBankState{
	
	private double myClient1Account;
	private double myClient2Account;
	private double myClient3Account;
	private boolean accessing=false; // true a thread has a lock, false otherwise
	private int threadsWaiting=0; // number of waiting writers

// Constructor	
	
	SharedBankState(double Client1Account, double Client2Account, double Client3Account) {
		myClient1Account = Client1Account;
		myClient2Account = Client2Account;
		myClient3Account = Client3Account;
	}

//Attempt to acquire a lock
	
	  public synchronized void acquireLock() throws InterruptedException{
	        Thread me = Thread.currentThread(); // get a ref to the current thread
	        System.out.println(me.getName()+" is attempting to acquire a lock!");	
	        ++threadsWaiting;
		    while (accessing) {  // while someone else is accessing or threadsWaiting > 0
		      System.out.println(me.getName()+" waiting to get a lock as someone else is accessing...");
		      //wait for the lock to be released - see releaseLock() below
		      wait();
		    }
		    // nobody has got a lock so get one
		    --threadsWaiting;
		    accessing = true;
		    System.out.println(me.getName()+" got a lock!"); 
		  }

		  // Releases a lock to when a thread is finished
		  public synchronized void releaseLock() {
			  //release the lock and tell everyone
		      accessing = false;
		      notifyAll();
		      Thread me = Thread.currentThread(); // get a ref to the current thread
		      System.out.println(me.getName()+" released a lock!");
		  }
	
	//add method
	public void add(String account, double value) {
		if (account.matches("Client1")) {
			myClient1Account = myClient1Account + value;
		}
		else if (account.matches("Client2")) {
			myClient2Account = myClient2Account + value;
		}
		else if (account.matches("Client3")) {
			myClient3Account = myClient3Account + value;
		}
	}
	
	//subtract method
	public void subtract(String account, double value) {
		if (account.matches("Client1")) {
			myClient1Account = myClient1Account - value;
		}
		else if (account.matches("Client2")) {
			myClient2Account = myClient2Account - value;
		}
		else if (account.matches("Client3")) {
			myClient3Account = myClient3Account - value;
		}
	}
	
	//transfer method
	public void transfer(String account1, String account2, double value) {
		if (account1.matches("Client1") && account2.matches("Client2") ) {
			myClient1Account = myClient1Account - value;
			myClient2Account = myClient2Account + value;
		}
		else if (account1.matches("Client1") && account2.matches("Client3") ) {
			myClient1Account = myClient1Account - value;
			myClient3Account = myClient3Account + value;
		}
		else if (account1.matches("Client2") && account2.matches("Client1") ) {
			myClient2Account = myClient2Account - value;
			myClient1Account = myClient1Account + value;
		}
		else if (account1.matches("Client2") && account2.matches("Client3") ) {
			myClient2Account = myClient2Account - value;
			myClient3Account = myClient3Account + value;
		}
		else if (account1.matches("Client3") && account2.matches("Client1") ) {
			myClient3Account = myClient3Account - value;
			myClient1Account = myClient1Account + value;
		}
		else if (account1.matches("Client3") && account2.matches("Client2") ) {
			myClient3Account = myClient3Account - value;
			myClient2Account = myClient2Account + value;
		}	
	}
	
	//returns first client in string
	public static String firstClient(String input) {
		String[] inputArray = input.split("");
		int firstNum = -1;
		for (int i=0;i<inputArray.length;i++) {
			if (inputArray[i].matches("[0-9]+") && firstNum == -1) {
				firstNum = Integer.parseInt(inputArray[i]);
			}
		}
		return "Client" + firstNum;
	}
	
	
	//returns the second client in string
	public static String secondClient(String input) {
		String[] inputArray = input.split("");
		ArrayList<String> numList = new ArrayList<String>();
		
		for (int i=0;i<inputArray.length;i++) {
			if (inputArray[i].matches("[0-9]+")) {
				numList.add(inputArray[i]);
			}	
		}
		return "Client" + numList.get(1);
	}
	
	
	//returns all but first numbers in string
	public static double inputMoney(String input) {
		String[] inputArray = input.split("");
		ArrayList<String> numList = new ArrayList<String>();
		String numString = "";
		double inputMoney = -1;
		for (int i=0;i<inputArray.length;i++) {
			if (inputArray[i].matches("[0-9]+")) {
				numList.add(inputArray[i]);
			}
		}
		//if its for transfer money command, remove first two numbers
		if (input.matches("^(?i)Transfer_money\\(client\\s?[1-3],client\\s?[1-3],\\d+\\)")) {
			numList.remove(0);
			numList.remove(0);
		}
		else numList.remove(0);
		for (int i=0;i<numList.size();i++) {
			numString = numString + numList.get(i);
		}
		inputMoney = Double.parseDouble(numString);
		return inputMoney;
	}		  
	
	
    /* The processInput method */

	public synchronized String processInput(String myThreadName, String theInput) {
    		System.out.println(myThreadName + " received "+ theInput);
    		String theOutput = null;
    		
    		// Check if client uses add money command
    		if (theInput.matches("^(?i)Add_money\\(client\\s?[1-3],\\d+\\)")) {
    			//Correct request
    			if (myThreadName.equals("BankServerThread1")) {
    				if (firstClient(theInput).matches("Client1")) {
        				System.out.println(myThreadName + " made the variable " + myClient1Account);
        				add(firstClient(theInput), inputMoney(theInput));
    	    			theOutput = "Add action completed. Client 1 balance now = " + myClient1Account;
    				}
    				else {theOutput = "Unauthorised - can only add to your own account";}
    			}
    			else if (myThreadName.equals("BankServerThread2")) {
    				if (firstClient(theInput).matches("Client2")) {
        				System.out.println(myThreadName + " made the variable " + myClient2Account);
        				add(firstClient(theInput), inputMoney(theInput));
    	    			theOutput = "Add action completed. Client 2 balance now = " + myClient2Account;
    				}
    				else {theOutput = "Unauthorised - can only add to your own account";}
    			}
       			else if (myThreadName.equals("BankServerThread3")) {
       				
       				if (firstClient(theInput).matches("Client3")) {
        				System.out.println(myThreadName + " made the variable " + myClient3Account);
        				add(firstClient(theInput), inputMoney(theInput));
    	    			theOutput = "Add action completed. Client 3 balance now = " + myClient3Account;
    				}
    				else {theOutput = "Unauthorised - can only add to your own account";}
       			}
    		}
    		
    		// Check if client uses subtract money command
    		else if (theInput.matches("^(?i)Subtract_money\\(client\\s?[1-3],\\d+\\)")) {
    			//Correct request
    			if (myThreadName.equals("BankServerThread1")) {
    				if (firstClient(theInput).matches("Client1")) {
        				System.out.println(myThreadName + " made the variable " + myClient1Account);
        				subtract(firstClient(theInput), inputMoney(theInput));
    	    			theOutput = "Subtract action completed. Client 1 balance now = " + myClient1Account;
    				}
    				else {theOutput = "Unauthorised - can only subtract from your own account";}
    			}
    			else if (myThreadName.equals("BankServerThread2")) {
    				if (firstClient(theInput).matches("Client2")) {
        				System.out.println(myThreadName + " made the variable " + myClient2Account);
        				subtract(firstClient(theInput), inputMoney(theInput));
    	    			theOutput = "Subtract action completed. Client 2 balance now = " + myClient2Account;
    				}
    				else {theOutput = "Unauthorised - can only subtract from your own account";}
    			}
       			else if (myThreadName.equals("BankServerThread3")) {
       				
       				if (firstClient(theInput).matches("Client3")) {
        				System.out.println(myThreadName + " made the variable " + myClient3Account);
        				subtract(firstClient(theInput), inputMoney(theInput));
    	    			theOutput = "Subtract action completed. Client 3 balance now = " + myClient3Account;
    				}
    				else {theOutput = "Unauthorised - can only subtract from your own account";}
       			}
    		}
    		
    		// Check if client uses transfer money command
    		else if (theInput.matches("^(?i)Transfer_money\\(client\\s?[1-3],client\\s?[1-3],\\d+\\)")) {
    			//Correct request
    			if (myThreadName.equals("BankServerThread1")) {
    				if (firstClient(theInput).matches("Client1") && secondClient(theInput).matches("Client2")) {
        				System.out.println(myThreadName + " made the variables " + myClient1Account + ", " + myClient2Account);
        				transfer(firstClient(theInput), secondClient(theInput),inputMoney(theInput));
    	    			theOutput = "Transer action completed. Client 1 balance now = " + myClient1Account + ", Client 2 balance now = " + myClient2Account;
    				}
    				else if (firstClient(theInput).matches("Client1") && secondClient(theInput).matches("Client3")) {
    					System.out.println(myThreadName + " made the variables " + myClient1Account + ", " + myClient3Account);
        				transfer(firstClient(theInput), secondClient(theInput),inputMoney(theInput));
    	    			theOutput = "Transer action completed. Client 1 balance now = " + myClient1Account + ", Client 3 balance now = " + myClient3Account;
    				}
    				else {theOutput = "Unauthorised - can only transfer from your own account";}
    			}
    			else if (myThreadName.equals("BankServerThread2")) {
    				if (firstClient(theInput).matches("Client2") && secondClient(theInput).matches("Client1")) {
        				System.out.println(myThreadName + " made the variables " + myClient2Account + ", " + myClient1Account);
        				transfer(firstClient(theInput), secondClient(theInput),inputMoney(theInput));
    	    			theOutput = "Transer action completed. Client 2 balance now = " + myClient2Account + ", Client 1 balance now = " + myClient1Account;
    				}
    				else if (firstClient(theInput).matches("Client2") && secondClient(theInput).matches("Client3")) {
    					System.out.println(myThreadName + " made the variables " + myClient2Account + ", " + myClient3Account);
        				transfer(firstClient(theInput), secondClient(theInput),inputMoney(theInput));
    	    			theOutput = "Transer action completed. Client 2 balance now = " + myClient2Account + ", Client 3 balance now = " + myClient3Account;
    				}
    				else {theOutput = "Unauthorised - can only transfer from your own account";}
    			}
       			else if (myThreadName.equals("BankServerThread3")) {
       				
    				if (firstClient(theInput).matches("Client3") && secondClient(theInput).matches("Client1")) {
        				System.out.println(myThreadName + " made the variables " + myClient3Account + ", " + myClient1Account);
        				transfer(firstClient(theInput), secondClient(theInput),inputMoney(theInput));
    	    			theOutput = "Transer action completed. Client 3 balance now = " + myClient3Account + ", Client 1 balance now = " + myClient1Account;
    				}
    				else if (firstClient(theInput).matches("Client3") && secondClient(theInput).matches("Client2")) {
    					System.out.println(myThreadName + " made the variables " + myClient3Account + ", " + myClient2Account);
        				transfer(firstClient(theInput), secondClient(theInput),inputMoney(theInput));
    	    			theOutput = "Transer action completed. Client 3 balance now = " + myClient3Account + ", Client 2 balance now = " + myClient2Account;
    				}
    				else {theOutput = "Unauthorised - can only transfer from your own account";}
       			}
    		}
    		
    		//incorrect request
    		else {theOutput = myThreadName + " received incorrect request - only understand \"Add_money(account,value)\", \"Subtract_money(account,value)\", or \"Transfer_money(account1,account2,value)\"";}
    		
     		//Return the output message to the ActionServer
    		System.out.println(theOutput);
    		return theOutput;
    	}	
}

