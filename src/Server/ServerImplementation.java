package Server;

import Bank.Bank;
import Bank.Account;
import Bank.RejectedException;
import Client.ClientInterface;

import java.rmi.NotBoundException;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by martin on 2015-11-30.
 */
public class ServerImplementation extends UnicastRemoteObject implements Server {

    private static final String BANK = "rmi://localhost/Nordea";
    private static final String bankname = "Nordea";
    private Bank bankobj;

    ArrayList<BuyObject> buyObjects = new ArrayList<BuyObject>();
    ArrayList<SellObject> sellObjects = new ArrayList<SellObject>();

    private boolean checkClientExists(String clientName) throws RemoteException {
        Account a = bankobj.getAccount(clientName);
        if (a == null){
            return false;
        }
        return true;
    }

    private double checkClientBalance(String clientName) throws RemoteException {
        Account a = bankobj.getAccount(clientName);
        return a.getBalance();
    }

    private void checkIfSaleAvailable() throws RemoteException {
        for(BuyObject b : buyObjects){
            SellObject[] list = findProduct(b.getName(), b.getPrice());
            if(list.length != 0){
                performTransaction(b.getBuyer(), list[0].getSeller(), list[0]);
            }
        }
    }

    public ServerImplementation() throws RemoteException {
        try {
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            bankobj = (Bank) Naming.lookup(bankname);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("The runtime failed: " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Connected to bank: " + bankname);
        if(checkClientExists("martin"))
            System.out.println(checkClientBalance("martin"));
    }

    @Override
    public void registerSellObject(String name, double price, String seller) throws RemoteException {
        SellObject sell = null;
        try {
            sell = new SellObject(name, price, seller);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        sellObjects.add(sell);

        checkIfSaleAvailable();
    }

    public void performTransaction(String buyer, String seller, SellObject product) throws RemoteException {

        if(checkClientExists(buyer) && checkClientExists(seller) && checkClientBalance(buyer)>product.getPrice()){
            Account buyerAccount = bankobj.getAccount(buyer);
            Account sellerAccount = bankobj.getAccount(seller);
            try {
                buyerAccount.withdraw((float)product.getPrice());
                sellerAccount.deposit((float)product.getPrice());
            } catch (RejectedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Transaction cannot be performed. Check account balance.");
            return;
        }

        try {
            Registry registry = LocateRegistry.getRegistry(1099);

            ClientInterface buyerStub = (ClientInterface) registry.lookup(buyer);
            ClientInterface sellerStub = (ClientInterface) registry.lookup(seller);

            System.out.println("Will notify the seller");
            sellerStub.notifySale(product);
            System.out.println("Done notifying seller");

            System.out.println("Will notify the buyer");
            buyerStub.notifyBuy(product);
            System.out.println("Done notifying buyer");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }


    }




    @Override
    public void registerBuyObject(String name, double price, String buyer) throws RemoteException {
        BuyObject buy = null;
        try {
            buy = new BuyObject(name, price, buyer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        buyObjects.add(buy);

        checkIfSaleAvailable();
    }

    @Override
    public synchronized void buyObject(String name, String buyer) throws RemoteException
    {
        SellObject s;
        for(int i=0; i<sellObjects.size(); i++)
        {
            s = sellObjects.get(i);
            if(s.getName().equalsIgnoreCase(name))
            {
                performTransaction(buyer, s.getSeller(), s);
                sellObjects.remove(s);
                return;
            }
        }
    }

    @Override
    public String[] findProduct(String name) throws RemoteException
    {
        ArrayList<String> list = new ArrayList<String>();

        for (SellObject s : sellObjects) {
            if(s.getName().equals(name)){
                list.add(s.getName() + "\t" + s.getPrice() + ":-");
            }
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public String[] listProducts() throws RemoteException
    {
        ArrayList<String> returnList = new ArrayList<String>();
        for(SellObject k: sellObjects)
        {
            returnList.add(k.getName() + "\t" + k.getPrice() + ":-");
        }
        return returnList.toArray(new String[returnList.size()]);
    }

    public SellObject[] findProduct(String name, double maxPrice) throws RemoteException
    {

        ArrayList<SellObject> list = new ArrayList<SellObject>();

        for (SellObject s : sellObjects) {
            if(s.getName().equals(name) && s.getPrice()<=maxPrice){
                list.add(s);
            }
        }
        return (SellObject[]) list.toArray();
    }

    @Override
    public void hello(String name) throws RemoteException {
        System.out.println("client asks for a hello, Hello!");

        try {
            Registry registry = LocateRegistry.getRegistry(1099);

            ClientInterface stub = (ClientInterface) registry.lookup(name);
            System.out.println("will try to contact the client");
            stub.hello();
            System.out.println("Done notifying the client");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args ) throws RemoteException {
        try {
            ServerImplementation serverObject = new ServerImplementation();
            // Register the newly created object at rmiregistry.
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            Naming.rebind("Server", serverObject);
            System.out.println(serverObject + " is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
