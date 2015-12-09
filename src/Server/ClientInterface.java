package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by martin on 2015-12-03.
 */
public interface ClientInterface extends Remote{
    public void notifySale(SellObject s) throws RemoteException;

    public void notifyBuy(SellObject s) throws RemoteException;

    public void hello() throws RemoteException;
}
