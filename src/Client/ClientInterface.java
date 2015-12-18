package Client;

import Server.Product;
import Server.SellObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by martin on 2015-11-30.
 */
public interface ClientInterface extends Remote{
    public void notifySale(Product product) throws RemoteException;
    public void notifyBuy(Product product) throws RemoteException;
    public void hello() throws RemoteException;
    public void notifyWish(Product product) throws RemoteException;
}
