package Server;

/**
 * Created by Martin on 2015-12-02.
 */

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientTest {

    private ClientTest(){}

    public static void main(String[]args){

        try {
            Registry registry = LocateRegistry.getRegistry(1099);
            Server stub = (Server) registry.lookup("Server");

            String response = stub.hello();
            System.out.println(response);



        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }
}
