package Client;


import Bank.RejectedException;

import java.rmi.RemoteException;
import java.util.Scanner;

public class Main
{
    public static void main(String [] args){

        String input = "";
        Client client = new Client();
        System.out.println("Welcome to Plocket, type \"create\" to make a new account, or \"login\" to log in");
        Scanner scanner = new Scanner(System.in);
        input = scanner.nextLine();
        if(input.equals("create"))
        {
            System.out.println("Select username:");
            client.setClientname(scanner.nextLine());
            System.out.print("Set password:");
            client.setPassword(scanner.nextLine());

//            String h = Commands.BankCommandName.newAccount.toString();
//            client.
//            Client.Command c = new Client.Command(Client.BankCommandName.newAccount, client.getClientname(), 0);
            try
            {
                client.execute(new Command(Client.CommandName.newAccount, client.getClientname(), 0));
            } catch (RemoteException e)
            {
                e.printStackTrace();
            } catch (RejectedException e)
            {
                e.printStackTrace();
            }
        }
        else if(input.equals("login"))
        {
            System.out.println("Username:");
            client.setClientname(scanner.nextLine());
            System.out.print("Password:");
            client.setPassword(scanner.nextLine());

        }
        else
        {
            System.exit(0);
        }


        boolean loginSuccess = true;
        /* login*/
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
                        c = new Command(Client.CommandName.sell, client.getClientname(),Float.parseFloat(input.split(" ")[2]), input.split(" ")[1]);
                        break;
                    case "wish":
                        c = new Command(Client.CommandName.wish, client.getClientname(),Float.parseFloat(input.split(" ")[2]), input.split(" ")[1]);
                        break;
                    case "search":
                        c = new Command(Client.CommandName.search, client.getClientname(),(float)0.00, input.split(" ")[1]);
                        break;
                    case "list":
                        c = new Command(Client.CommandName.sell, client.getClientname(),(float)0.00, input.split(" ")[1]);
                        break;
                    case "logout":
                        c = new Command(Client.CommandName.quit, client.getClientname(), (float)0.00,"");
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
