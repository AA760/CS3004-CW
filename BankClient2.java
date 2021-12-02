//Most of the code for this class was taken from CS3004 Tutorial 4 (Prof. Simon Taylor).
package cw;
import java.io.*;
import java.net.*;

public class BankClient2 {
    public static void main(String[] args) throws IOException {

        Socket BankClientSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        int BankSocketNumber = 4546;
        String BankServerName = "localhost";
        String BankClientID = "BankClient2";

        try {
            BankClientSocket = new Socket(BankServerName, BankSocketNumber);
            out = new PrintWriter(BankClientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(BankClientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: localhost ");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: "+ BankSocketNumber);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        System.out.println("Initialised " + BankClientID + " client and IO connections");

        while (true) {
            
            fromUser = stdIn.readLine();
            if (fromUser != null) {
                System.out.println(BankClientID + " sending " + fromUser + " to BankServer");
                out.println(fromUser);
            }
            fromServer = in.readLine();
            System.out.println(BankClientID + " received " + fromServer + " from BankServer");
        }

    }
}
