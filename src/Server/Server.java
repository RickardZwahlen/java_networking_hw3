package Server;

/**
 * Created by martin on 2015-11-26.
 */
import Bank.RejectedException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {

    public void registerSellObject(String name, double price, String seller);
    public void registerBuyObject(String name, double price, String buyer);
    public SellObject findProduct(String name);
}