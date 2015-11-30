package Client;


import java.util.Scanner;

public class Client {
    private final String BANKNAME = "Nordea";
    private Bank.Client bankClient;

    public static void main(String [] args){

        System.out.println("Welcome to Skrocket, please login:");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        System.out.print("Password:");
        String password = scanner.nextLine();
        String input = "";

        boolean loginSuccess = true;
        /* login*/
        if(loginSuccess)
        {
            while(true)
            {
                System.out.println("Login successful. available commands are: ");
                commandList();
                input = scanner.nextLine();
                if(input.trim().equals("logout"))
                    break;
                    //TODO logout?
                executeCommand(input);

            }

        }

    }

    private static void executeCommand(String command)
    {

    }

    private static void commandList()
    {
        System.out.println(" sell \n list \n search \n wish \n logout ");
    }
}
