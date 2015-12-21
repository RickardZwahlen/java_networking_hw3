package Client;


import Bank.RejectedException;

import java.rmi.RemoteException;
import java.util.Scanner;

public class Main
{
    public static void main(String [] args){
        boolean loginSuccess = false;
        String input = "";
        Client client = null;
        Scanner scanner = new Scanner(System.in);
        while(!loginSuccess)
        {
            System.out.println("Welcome to Plocket, type \"create\" to make a new account, or \"login\" to log in");

            input = scanner.nextLine();
            if (input.startsWith("quit"))
            {
                return;
            }

            if (input.equals("create"))
            {
                System.out.println("Select username:");
                String input1 = scanner.nextLine();
                System.out.print("Set password:");
                String input2 = scanner.nextLine();
                try
                {
                    client = new Client(input1, input2);
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                }
//            String h = Commands.BankCommandName.newAccount.toString();
//            client.
//            Client.Command c = new Client.Command(Client.BankCommandName.newAccount, client.getClientname(), 0);
                try
                {
                    loginSuccess = client.execute(new Command(Client.CommandName.newAccount, client.getClientname(), 0));
                    if(!loginSuccess)
                        System.out.println("Account creation failed. Username might be taken, or password too short.");
                    else
                        client.execute(new Command(Client.CommandName.deposit, client.getClientname(), 1000));
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                } catch (RejectedException e)
                {
                    e.printStackTrace();
                }
            } else if (input.equals("login"))
            {
                System.out.println("Username:");
                String input1 = scanner.nextLine();
                System.out.print("Password:");
                String input2 = scanner.nextLine();

                try
                {
                    client = new Client(input1, input2);
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                }
                Command c = new Command(Client.CommandName.getAccount, client.getClientname(), (float) 0.00, client.getPassword());
                try
                {
                    boolean result = client.execute(c);
                    loginSuccess = result;
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                } catch (RejectedException e)
                {
                    e.printStackTrace();
                }
            }
        }


        if(loginSuccess)
        {
            System.out.println("Login successful.");
//            client.execute(new Client.Command(Client.BankCommandName.getAccount, username, 0));
            while(true)
            {
                System.out.println("Available commands are: ");
                client.printCommandList();
                input = scanner.nextLine();
//                client.execute(new Command(input.split(" ")[0], client.getClientname(), Float.parseFloat(input.split(" ")[1])));
                Command c = null;
                switch(input.split(" ")[0])
                {
                    case "sell":
                        System.out.println("Please input: <item name> <price>");
                        input = scanner.nextLine();
                        c = new Command(Client.CommandName.sell, client.getClientname(),Float.parseFloat(input.split(" ")[1]), input.split(" ")[0]);
                        break;
                    case "wish":
                        System.out.println("Please input: <item name> <max price>");
                        input = scanner.nextLine();
                        c = new Command(Client.CommandName.wish, client.getClientname(),Float.parseFloat(input.split(" ")[1]), input.split(" ")[0]);
                        break;
                    case "buy":
                        System.out.println("Please input: <item name>");
                        input = scanner.nextLine();
                        c = new Command(Client.CommandName.buy, client.getClientname(),(float)0.00, input.split(" ")[0]);
                        break;
                    case "search":
                        System.out.println("Search for: ");
                        input = scanner.nextLine();
                        c = new Command(Client.CommandName.search, client.getClientname(),(float)0.00, input);
                        break;
                    case "list":
                        c = new Command(Client.CommandName.list, client.getClientname(),(float)0.00);
                        break;
                    case "logout":
                        c = new Command(Client.CommandName.logout, client.getClientname(), (float)0.00,"");
                        break;
                    case "deposit":
                        c = new Command(Client.CommandName.deposit, client.getClientname(), Float.parseFloat(input.split(" ")[1]),"");
                        break;
                    case "balance":
                        c = new Command(Client.CommandName.balance, client.getClientname(), (float)0.00,"");
                        break;
                }

                try
                {
                    client.execute(c);
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                } catch (RejectedException e)
                {
                    e.printStackTrace();
                }

                if(input.split(" ")[0].equalsIgnoreCase("logout"))
                    System.exit(0);
            }
        }
    }
}
