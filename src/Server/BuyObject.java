package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by martin on 2015-11-30.
 */
public class BuyObject extends UnicastRemoteObject implements Product{
    private String productName;
    private double price;
    private String buyer;

    public String getName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public String getBuyer() {
        return buyer;
    }

    public BuyObject(String n, Double p, String b) throws RemoteException {
        productName = n;
        price = p;
        buyer = b;
    }
}
