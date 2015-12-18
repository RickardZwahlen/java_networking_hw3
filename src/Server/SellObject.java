package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by martin on 2015-11-30.
 */
public class SellObject extends UnicastRemoteObject implements Product{
    private String productName;
    private double price;
    private String seller;

    public String getName() throws RemoteException{
        return productName;
    }

    public double getPrice() throws RemoteException {
        return price;
    }

    public String getSeller() throws RemoteException {
        return seller;
    }

    public SellObject(String n, Double p, String s) throws RemoteException{
        productName = n;
        price = p;
        seller = s;
    }

}
