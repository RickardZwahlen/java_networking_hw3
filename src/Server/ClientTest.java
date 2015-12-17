package Server;

/**
 * Created by Martin on 2015-12-02.
 */

import Client.ClientInterface;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientTest extends UnicastRemoteObject implements ClientInterface
{

    private ClientTest() throws RemoteException {
        super();
    }

    public static void main(String[] args){

        String name = "client";
        try {
            ClientTest client = new ClientTest();
            // Register the newly created object at rmiregistry.
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            Naming.rebind(name, client);
            System.out.println(client + " is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Registry registry = LocateRegistry.getRegistry(1099);
            Server stub = (Server) registry.lookup("Server");

            System.out.println("will preform system call");
            stub.hello("client");
            System.out.println("done with remote call. said hello :)");



        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notifySale(SellObject s) throws RemoteException {
        System.out.println("i managed to sell " + s.getName() +" for " + s.getPrice());
    }

    @Override
    public void notifyBuy(SellObject s) throws RemoteException {
        System.out.println("i managed to buy " + s.getName() +" for " + s.getPrice());
    }

    @Override
    public void hello() throws RemoteException {
        System.out.println("hello");
    }
}
