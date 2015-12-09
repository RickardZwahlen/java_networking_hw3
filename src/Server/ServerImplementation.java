package Server;

import Bank.Bank;
import Bank.BankImpl;

import java.rmi.AlreadyBoundException;
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
    private Bank bankobj;

    ArrayList<BuyObject> buyObjects = new ArrayList<BuyObject>();
    ArrayList<SellObject> sellObjects = new ArrayList<SellObject>();

    public ServerImplementation() throws RemoteException {

    }

    @Override
    public void registerSellObject(String name, double price, String seller) {
        SellObject sell = null;
        try {
            sell = new SellObject(name, price, seller);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        sellObjects.add(sell);
        for (BuyObject b : buyObjects){
            if (b.getName().equals(name) && b.getPrice()<=price){
                performTransaction("", "", sell, b);
                buyObjects.remove(b);
            }
        }
    }

    public void performTransaction(String buyer, String Seller, SellObject product){

    }
    public void performTransaction(String buyer, String Seller, SellObject product, BuyObject buyObject){

    }

    @Override
    public void registerBuyObject(String name, double price, String buyer) {
        BuyObject buy = null;
        try {
            buy = new BuyObject(name, price, buyer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        buyObjects.add(buy);

        for (SellObject s : sellObjects){
            if (s.getName().equals(name) && s.getPrice()<=price){
                performTransaction("", "", s, buy);
                buyObjects.remove(buy);
            }
        }
    }

    @Override
    public SellObject findProduct(String name) {
        for (SellObject s : sellObjects) {
            if(s.getName().equals(name)){
                System.out.println(s.getName());
                return s;
            }
        }
        return null;
    }

    @Override
    public void hello(String name) throws RemoteException {
        System.out.println("client asks for a hello, Hello!");


        try {
            Registry registry = LocateRegistry.getRegistry(1099);

            ClientInterface stub = (ClientInterface) registry.lookup("client");
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
