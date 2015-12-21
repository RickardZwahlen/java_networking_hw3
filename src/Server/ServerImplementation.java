package Server;

import Bank.Bank;
import Bank.Account;
import Bank.RejectedException;
import Client.ClientInterface;

import java.rmi.NotBoundException;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.sql.*;

/**
 * Created by martin on 2015-11-30.
 */
public class ServerImplementation extends UnicastRemoteObject implements Server {

    private static final String BANK = "rmi://localhost/Nordea";
    private static final String bankname = "Nordea";
    private Bank bankobj;

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/EMP";

    //  Database credentials
    static final String USER = "username";
    static final String PASS = "password";

    static Connection conn = null;
    static Statement stmt = null;

    ArrayList<BuyObject> buyObjects = new ArrayList<BuyObject>();
    ArrayList<SellObject> sellObjects = new ArrayList<SellObject>();

    private boolean checkClientExists(String clientName) throws RemoteException {
        Account a = bankobj.getAccount(clientName);
        if (a == null){
            return false;
        }
        return true;
    }

    private double checkClientBalance(String clientName) throws RemoteException {
        Account a = bankobj.getAccount(clientName);
        return a.getBalance();
    }

    private void checkIfSaleAvailable() throws RemoteException, NotBoundException
    {
        for(BuyObject s:buyObjects)
        {
            for(SellObject k:sellObjects)
            {
                if(s.getName().equalsIgnoreCase(k.getName()) && s.getPrice() >= k.getPrice())
                {
                    Registry registry = LocateRegistry.getRegistry(1099);
                    ClientInterface wishStub = (ClientInterface) registry.lookup(s.getBuyer());


                    wishStub.notifyWish(k);
                    System.out.println("Notifying wishlist");

                }
            }
        }
    }

    public ServerImplementation() throws RemoteException {
        try {
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            bankobj = (Bank) Naming.lookup(bankname);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("The runtime failed: " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Connected to bank: " + bankname);
        if(checkClientExists("martin"))
            System.out.println(checkClientBalance("martin"));

        //STEP 2: Register JDBC driver
        try
        {
            Class.forName(JDBC_DRIVER);
            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        //SELECT * FROM Customers WHERE ContactName='Thomas Hardy' OR Address='Mataderos 2312';


    }

    @Override
    public void registerSellObject(String name, double price, String seller) throws RemoteException {

        SellObject sell = null;
        try {
            sell = new SellObject(name, price, seller);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        sellObjects.add(sell);

        try
        {
            checkIfSaleAvailable();
        } catch (NotBoundException e)
        {
            e.printStackTrace();
        }
    }

    public void registerSellObjectSQL(String name, double price, String seller) throws RemoteException {
//        INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country)
//        VALUES ('Cardinal','Tom B. Erichsen','Skagen 21','Stavanger','4006','Norway');
        ResultSet rs = null;
        try
        {
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO SellObjects (name, price, seller) VALUES ('" + name + "', " + price + ",'" + seller +"')";
            rs = stmt.executeQuery(sql);
        } catch (SQLException e)
        {
            try
            {
                rs.close();
            } catch (SQLException e1)
            {
                e1.printStackTrace();
            }
            catch(NullPointerException e2)
            {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void performTransaction(String buyer, String seller, SellObject product) throws RemoteException {

        if(checkClientExists(buyer) && checkClientExists(seller) && checkClientBalance(buyer)>product.getPrice()){
            Account buyerAccount = bankobj.getAccount(buyer);
            Account sellerAccount = bankobj.getAccount(seller);
            try {
                buyerAccount.withdraw((float)product.getPrice());
                sellerAccount.deposit((float)product.getPrice());
            } catch (RejectedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Transaction cannot be performed. Check account balance.");
            return;
        }

        try {
            Registry registry = LocateRegistry.getRegistry(1099);

            ClientInterface buyerStub = (ClientInterface) registry.lookup(buyer);
            ClientInterface sellerStub = (ClientInterface) registry.lookup(seller);

            System.out.println("Will notify the seller");
            sellerStub.notifySale(product);
            System.out.println("Done notifying seller");

            System.out.println("Will notify the buyer");
            buyerStub.notifyBuy(product);
            System.out.println("Done notifying buyer");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }


    }




    @Override
    public void registerBuyObject(String name, double price, String buyer) throws RemoteException {
        BuyObject buy = null;
        try {
            buy = new BuyObject(name, price, buyer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        buyObjects.add(buy);

        try
        {
            checkIfSaleAvailable();
        } catch (NotBoundException e)
        {
            e.printStackTrace();
        }
    }

//    @Override
    public void registerBuyObjectSQL(String name, double price, String buyer) throws RemoteException {
//        INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country)
//        VALUES ('Cardinal','Tom B. Erichsen','Skagen 21','Stavanger','4006','Norway');
        ResultSet rs = null;
        try
        {
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO SellObjects (name, price, buyer) VALUES ('" + name + "', " + price + ",'" + buyer +"')";
            rs = stmt.executeQuery(sql);
        } catch (SQLException e)
        {
            try
            {
                rs.close();
            } catch (SQLException e1)
            {
                e1.printStackTrace();
            }
            catch(NullPointerException e2)
            {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void buyObject(String name, String buyer) throws RemoteException
    {
        SellObject s;
        for(int i=0; i<sellObjects.size(); i++)
        {
            s = sellObjects.get(i);
            if(s.getName().equalsIgnoreCase(name))
            {
                performTransaction(buyer, s.getSeller(), s);
                sellObjects.remove(s);
                return;
            }
        }
    }

    public synchronized void buyObjectSQL(String name, String buyer) throws RemoteException
    {
        SellObject s;
        ResultSet rs = null;
        try
        {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM SellObjects WHERE name='" + name + "' ORDER BY price ASC";
            rs = stmt.executeQuery(sql);

            rs.next();
            //Retrieve by column name
            float price  = rs.getFloat("price");
            String produtName = rs.getString("name");
            String seller = rs.getString("seller");
            s = new SellObject(produtName, (double)price, seller);

            performTransaction(buyer, seller, s);
            sql = "DELETE FROM SellObjects WHERE name='" + produtName + "' AND seller='" + seller + "'";
            rs = stmt.executeQuery(sql);

        } catch (SQLException e)
        {
            try
            {
                rs.close();
            } catch (SQLException e1)
            {
                e1.printStackTrace();
            }
            catch(NullPointerException e2)
            {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }


    }

    @Override
    public String[] findProduct(String name) throws RemoteException
    {
        ArrayList<String> list = new ArrayList<String>();

        for (SellObject s : sellObjects) {
            if(s.getName().equals(name)){
                list.add(s.getName() + "\t" + s.getPrice() + ":-");
            }
        }
        return list.toArray(new String[list.size()]);
    }

//    @Override
    public String[] findProductSQL(String name) throws RemoteException
    {
        ArrayList<String> list = new ArrayList<String>();
        //STEP 4: Execute a query
        System.out.println("Creating statement...");
        ResultSet rs = null;
        try
        {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM SellObjects WHERE name='" +name +  "' ORDER BY price ASC";
            rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                float price  = rs.getFloat("price");
                String produtName = rs.getString("name");
                String seller = rs.getString("seller");


                list.add(produtName + "\t" + price + ":- by " + seller);
                //Display values
//                System.out.print("Product: " + produtName);
//                System.out.print(", Price: " + price);
//                System.out.println(", Seller: " + seller);
            }

        } catch (SQLException e)
        {
            try
            {
                rs.close();
            } catch (SQLException e1)
            {
                e1.printStackTrace();
            }
            catch(NullPointerException e2)
            {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
        return list.toArray(new String[list.size()]);
    }


    public String[] listProductsSQL() throws RemoteException
    {
        ArrayList<String> list = new ArrayList<String>();
        //STEP 4: Execute a query
        System.out.println("Creating statement...");
        ResultSet rs = null;
        try
        {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM SellObjects ORDER BY seller ASC";
            rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                float price  = rs.getFloat("price");
                String produtName = rs.getString("name");
                String seller = rs.getString("seller");


                list.add(produtName + "\t" + price + ":- by " + seller);
                //Display values
//                System.out.print("Product: " + produtName);
//                System.out.print(", Price: " + price);
//                System.out.println(", Seller: " + seller);
            }

        } catch (SQLException e)
        {
            try
            {
                rs.close();
            } catch (SQLException e1)
            {
                e1.printStackTrace();
            }
            catch(NullPointerException e2)
            {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
        return list.toArray(new String[list.size()]);
    }


    @Override
    public String[] listProducts() throws RemoteException
    {
        ArrayList<String> returnList = new ArrayList<String>();
        for(SellObject k: sellObjects)
        {
            returnList.add(k.getName() + "\t" + k.getPrice() + ":-");
        }
        return returnList.toArray(new String[returnList.size()]);
    }

    public SellObject[] findProduct(String name, double maxPrice) throws RemoteException
    {

        ArrayList<SellObject> list = new ArrayList<SellObject>();

        for (SellObject s : sellObjects) {
            if(s.getName().equals(name) && s.getPrice()<=maxPrice){
                list.add(s);
            }
        }
        return (SellObject[]) list.toArray();
    }

    @Override
    public void hello(String name) throws RemoteException {
        System.out.println("client asks for a hello, Hello!");

        try {
            Registry registry = LocateRegistry.getRegistry(1099);

            ClientInterface stub = (ClientInterface) registry.lookup(name);
            System.out.println("will try to contact the client");
            stub.hello();
            System.out.println("Done notifying the client");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args ) throws RemoteException {
        try {
            ServerImplementation serverObject = new ServerImplementation();
            // Register the newly created object at rmiregistry.
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            Naming.rebind("Server", serverObject);
            System.out.println(serverObject + " is ready.");
        } catch (Exception e) {

            try
            {
                stmt.close();
                conn.close();
            } catch (SQLException e1)
            {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

    }
}
