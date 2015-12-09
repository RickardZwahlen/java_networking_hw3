package Client;


import Bank.Account;
import Bank.RejectedException;
import Server.SellObject;

import java.rmi.RemoteException;


public class Client implements ClientInterface{
    private final String BANKNAME = "Nordea";
    private Bank.Client bankClient;
    Account account;
    Bank.Bank bankobj;
    private String bankname;
    String clientname;
    String password;

    public static enum commands {
        sell, wish, search, list, logout;
    };

    public static enum BankCommandName
    {
        newAccount, getAccount, deleteAccount, deposit, withdraw, balance, quit, help, list;
    };

    @Override
    public void notifySale(SellObject product)
    {

    }

    @Override
    public void notifyBuy(SellObject product)
    {

    }


    public Client()
    {

    }

    public Client(String clientname, String password)
    {
        this.clientname = clientname;
        this.password = password;
    }





    public void printCommandList()
    {
        System.out.println(java.util.Arrays.asList(commands.values()));
    }

    public void execute(Command command) throws RemoteException, RejectedException
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




    public String getClientname()
    {
        return clientname;
    }

    public void setClientname(String clientname)
    {
        this.clientname = clientname;
    }

    public String getBankname()
    {
        return bankname;
    }

    public void setBankname(String bankname)
    {
        this.bankname = bankname;
    }

    public String getPassword()
    {
        return password;
    }


    public void setPassword(String password)
    {
        this.password = password;
    }
}


