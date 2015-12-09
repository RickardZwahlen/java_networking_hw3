package Client;


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
//            client.execute(new Client.Command(Commands.BankCommandName.newAccount, client.getClientname(), 0));
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
//            client.execute(new Client.Command(Client.BankCommandName.getAccount, username, 0));
            while(true)
            {
                System.out.println("Login successful. available commands are: ");
                client.printCommandList();
                input = scanner.nextLine();
                if(input.trim().equals("logout"))
                    break;
                //TODO logout?


            }
        }
    }
}
