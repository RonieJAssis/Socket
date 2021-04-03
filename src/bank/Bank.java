/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ronie
 */
public class Bank extends Thread{
    
    private static List<Client> clients;
    private Client client;
    private Socket connection;
    private String clientAccount;

    public Bank(Client client) {
        this.client = client;
    }
    
    public String deposit(double amount){
        if(amount>=0){    
            client.setAmount(client.getAmount()+amount);
            return "Sucess";
        }else{
            return "invalid amount";
        }
    }
    public String take(double amount){
        if(amount<=client.getAmount()&& amount>=0){
            client.setAmount(client.getAmount()-amount);
            return "Sucess";
        }else{
            return "invalid amount";
        }
    }
    public Double view(){
        return client.getAmount();
    }
    
    public void run(){
        try{
            BufferedReader startingPoint  = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
            PrintStream endPoint = new PrintStream(client.getSocket().getOutputStream());
            client.setEndPoint(endPoint);
            clientAccount = startingPoint.readLine();

            if (clientAccount == null) {
                return;
            }
            client.setAccountNumber(clientAccount);
            client.setAmount(1000.0);
    
            String line = startingPoint.readLine();
            PrintStream result = (PrintStream) client.getEndPoint();
            
            while ((line != null && !(line.trim().equals("")))&&!line.equals("CLOSE")) {
                if(line.equals("VIEW")){
                    result.println(this.view());
                }else{
                    String[] splits = line.split(" ");
                    if(splits[0].equals("DEPOSIT")){
                        result.println(this.deposit(Double.parseDouble(splits[1])));
                    }
                    if(splits[0].equals("TAKE")){
                        result.println(this.take(Double.parseDouble(splits[1])));
                    }
                }
                
                line = startingPoint.readLine();
            }
            result.println("Bye!");
            clients.remove(endPoint);
            connection.close();
        }catch(Exception e){
            System.out.println("IOException: " + e);
        }
    }
    
    public static void main(String[] args) {
        clients = new ArrayList<Client>();
        try{
            ServerSocket s = new ServerSocket(2222);
            while (true) {
                System.out.print("waiting for connection...");
                Socket connection = s.accept();
                Client client = new Client();
               
                client.setSocket(connection);

                clients.add(client);

                System.out.println(" Connected!: " + connection.getRemoteSocketAddress());

                Thread t = new Bank(client);
                t.start();

            }
           
       }catch(Exception e){
            System.out.println(e);
       }
    }
    
}
