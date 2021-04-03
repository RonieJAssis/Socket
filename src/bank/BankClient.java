/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;

import java.io.*;
import java.net.*;

/**
 *
 * @author ronie
 */
public class BankClient extends Thread {

    private static boolean done = false;
    private Socket connection;

    public BankClient(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        try {
            BufferedReader startingPoint = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while (true) {

                line = startingPoint.readLine();

                if (line == null || line.equals("Bye!")) {
                    System.out.println("Connection End!");
                    break;
                }
                System.out.println();
                System.out.println(line);
            }
        } catch (IOException e) {

            System.out.println("IOException: " + e);
        }
        done = true;
    }

    public static void main(String[] args) {
        try {

            Socket connection = new Socket("127.0.0.1", 2222);

            PrintStream endPoint = new PrintStream(connection.getOutputStream());

            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("log in with your account: ");
            String myAccount = keyboard.readLine();
            endPoint.println(myAccount);

            Thread t = new BankClient(connection);
            t.start();

            String line="";
            while (true) {
                if (done || line.equals("CLOSE")) {
                    break;
                } else {
                    System.out.println("> Enter 1 to deposit");
                    System.out.println("> Enter 2 to take");
                    System.out.println("> Enter 3 to view");
                    System.out.println("> Enter 4 to close");
                    line = keyboard.readLine();
                    if (line.equals("1")) {
                        System.out.println("amount:");
                        String value = keyboard.readLine();
                        try {
                            line = ("DEPOSIT " + Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number");
                        }
                    } else if (line.equals("2")) {
                        System.out.println("amount:");
                        String value = keyboard.readLine();
                        try {
                            line = ("TAKE " + Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number");
                        }
                    } else if (line.equals("3")) {
                        line = ("VIEW");
                    } else if (line.equals("4")) {
                        line = ("CLOSE");
                    } else {
                        System.out.println("Invalid Operation");
                    }

                    endPoint.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

}
