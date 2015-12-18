package Client;


import Bank.Account;
import Server.Product;

import Bank.RejectedException;
import Server.SellObject;
import Server.Server;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;


public class Client extends UnicastRemoteObject implements ClientInterface{
    private final String BANKNAME = "Nordea";
    private Bank.Client bankClient;
    Account account;
    Bank.Bank bankobj;
    private String bankname = BANKNAME;
    String clientname;
    String password;
    Server server;


    public enum CommandName
    {
        sell, wish, buy, search, newAccount, getAccount, deleteAccount, deposit, withdraw, balance, quit, help, list;
    }


    @Override
    public void notifySale(Product product) throws RemoteException
    {
        System.out.println(clientname + " sold a " + product.getName() + " for " + product.getPrice() + ":-");
    }

    @Override
    public void notifyBuy(Product product) throws RemoteException
    {
        System.out.println(clientname + " bought a " + product.getName() + " for " + product.getPrice() + ":-");
    }

    @Override
    public void hello()
    {
        System.out.println("Server successfully contacted client.");
    }

    public void Setup() throws RemoteException
    {
        try {

            // Register the newly created object at rmiregistry.
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            Naming.rebind(clientname, this);
            System.out.println(this.toString() + " is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Registry registry = LocateRegistry.getRegistry(1099);
            server = (Server) registry.lookup("Server");

            System.out.println("will preform system call");
            server.hello(clientname);
            System.out.println("done with remote call. said hello :)");



        } catch (NotBoundException e)
        {
            e.printStackTrace();
        }
    }

    public Client(String clientname, String password) throws RemoteException
    {
        super();
        this.clientname = clientname;
        this.password = password;


        try {
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            System.out.println("Trying to connect to bank: " + bankname);
            bankobj = (Bank.Bank) Naming.lookup(bankname);
        } catch (Exception e) {
            System.out.println("The runtime failed: " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Connected to bank: " + bankname);
        try
        {
            account = bankobj.getAccount(clientname);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        Setup();
    }





    public void printCommandList()
    {
        System.out.println(java.util.Arrays.asList(Client.CommandName.values()));
    }

    public void execute(Command command) throws RemoteException, RejectedException
    {
        if (command == null) {
            return;
        }

        switch (command.getBankCommandName()) {
            case quit:
                System.exit(0);
            case help:
                for (CommandName commandName : CommandName.values()) {
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
                account = bankobj.newAccount(userName);
                return;
            case deleteAccount:
                clientname = userName;
                bankobj.deleteAccount(userName);
                account = null;
                return;
        }

        // all further commands require a Account reference
        Account acc = bankobj.getAccount(userName);
        if (acc == null) {
            System.out.println("No account for " + userName);
            System.exit(0);
        } else {
            account = acc;
            clientname = userName;
        }

//        sell, wish, search, productList, logout, newAccount, getAccount, deleteAccount, deposit, withdraw, balance, quit, help, list;

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
            case sell:
                System.out.println(command.getOther() + " put up for sale for " + command.getAmount());
                server.registerSellObject(command.getOther(), command.getAmount(), command.getUserName());
                break;
            case buy: //TODO
                server.buyObject(command.getOther(), command.getUserName());
                break;
            case wish:
                System.out.println(command.getOther() + " put up on wishlist for " + command.getAmount());
                server.registerBuyObject(command.getOther(), command.getAmount(), command.getUserName());
                break;
            case search:

                ArrayList<SellObject> products = server.findProduct(command.getOther());
                for(SellObject s: products)
                {
                    System.out.println(s.getName() + "\t" + s.getPrice());
                }
                break;
            case list:
//                System.out.println(command.getOther() + " put up on wishlist for " + command.getAmount());
                String[] productsList = server.listProducts();
//                products = server.listProducts();
                for(String s: productsList)
                {
                    System.out.println(s);
                }
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

    public String toString()
    {

        return "Client: " + this.getClientname();
    }
}


