package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by martin on 2015-11-30.
 */
public interface Product extends Remote
{
    public String getName() throws RemoteException;
    public double getPrice() throws RemoteException;

}
