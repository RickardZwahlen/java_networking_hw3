package Server;

/**
 * Created by martin on 2015-11-26.
 */
import Bank.RejectedException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Server extends Remote {

    public void registerSellObject(String name, double price, String seller)throws RemoteException;
    public void registerBuyObject(String name, double price, String buyer)throws RemoteException;
    public void buyObject(String name, String buyer)throws RemoteException;
    public ArrayList<SellObject> findProduct(String name)throws RemoteException;
    public String[] listProducts() throws RemoteException;
    public void hello(String name) throws RemoteException;
}