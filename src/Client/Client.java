package Client;


import Bank.Account;
import Bank.RejectedException;

import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Client {
    private final String BANKNAME = "Nordea";
    private Bank.Client bankClient;
    Account account;
    Bank.Bank bankobj;
    private String bankname;
    String clientname;

    static enum commands {
        sell, wish, search, list, logout;
    };

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
        System.out.println(java.util.Arrays.asList(commands.values()));
    }

    private Command parse(String command) {
        if (command == null) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(command);
        if (tokenizer.countTokens() == 0) {
            return null;
        }

        BankCommandName bankCommandName = null;
        String userName = null;
        float amount = 0;
        int userInputTokenNo = 1;

        while (tokenizer.hasMoreTokens()) {
            switch (userInputTokenNo) {
                case 1:
                    try {
                        String commandNameString = tokenizer.nextToken();
                        bankCommandName = BankCommandName.valueOf(BankCommandName.class, commandNameString);
                    } catch (IllegalArgumentException commandDoesNotExist) {
                        System.out.println("Illegal command");
                        return null;
                    }
                    break;
                case 2:
                    userName = tokenizer.nextToken();
                    break;
                case 3:
                    try {
                        amount = Float.parseFloat(tokenizer.nextToken());
                    } catch (NumberFormatException e) {
                        System.out.println("Illegal amount");
                        return null;
                    }
                    break;
                default:
                    System.out.println("Illegal command");
                    return null;
            }
            userInputTokenNo++;
        }
        return new Command(bankCommandName, userName, amount);
    }

    void execute(Command command) throws RemoteException, RejectedException
    {
        if (command == null) {
            return;
        }

        switch (command.getBankCommandName()) {
            case list:
                try {
                    for (String accountHolder : bankobj.listAccounts()) {
                        System.out.println(accountHolder);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                return;
            case quit:
                System.exit(0);
            case help:
                for (BankCommandName commandName : BankCommandName.values()) {
                    System.out.println(commandName);
                }
                return;

        }

        // all further commands require a name to be specified
        String userName = command.getUserName();
        if (userName == null) {
            userName = clientname;
        }

        if (userName == null) {
            System.out.println("name is not specified");
            return;
        }

        switch (command.getBankCommandName()) {
            case newAccount:
                clientname = userName;
                bankobj.newAccount(userName);
                return;
            case deleteAccount:
                clientname = userName;
                bankobj.deleteAccount(userName);
                return;
        }

        // all further commands require a Account reference
        Account acc = bankobj.getAccount(userName);
        if (acc == null) {
            System.out.println("No account for " + userName);
            return;
        } else {
            account = acc;
            clientname = userName;
        }

        switch (command.getBankCommandName()) {
            case getAccount:
                System.out.println(account);
                break;
            case deposit:
                account.deposit(command.getAmount());
                break;
            case withdraw:
                account.withdraw(command.getAmount());
                break;
            case balance:
                System.out.println("balance: $" + account.getBalance());
                break;
            default:
                System.out.println("Illegal command");
        }
    }

    static enum BankCommandName
    {
        newAccount, getAccount, deleteAccount, deposit, withdraw, balance, quit, help, list;
    };

    private class Command {
        private String userName;
        private float amount;
        private BankCommandName bankCommandName;

        private String getUserName() {
            return userName;
        }

        private float getAmount() {
            return amount;
        }

        private BankCommandName getBankCommandName() {
            return bankCommandName;
        }

        private Command(BankCommandName bankCommandName, String userName, float amount) {
            this.bankCommandName = bankCommandName;
            this.userName = userName;
            this.amount = amount;
        }
    }


}


