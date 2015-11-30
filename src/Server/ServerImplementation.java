package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 2015-11-30.
 */
public class ServerImplementation extends UnicastRemoteObject implements Server {

    ArrayList<BuyObject> buyObjects = new ArrayList<BuyObject>();
    ArrayList<SellObject> sellObjects = new ArrayList<SellObject>();

    protected ServerImplementation() throws RemoteException {
    }


    @Override
    public void registerSellObject(String name, double price, String seller) {
        sellObjects.add(new SellObject(name, price, seller));
        
    }

    @Override
    public void registerBuyObject(String name, double price, String buyer) {

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
}
