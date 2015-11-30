package Server;

/**
 * Created by martin on 2015-11-30.
 */
public class BuyObject implements Product{
    private String productName;
    private double price;
    private String buyer;

    public String getName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public String getSeller() {
        return buyer;
    }

    public BuyObject(String n, Double p, String b){
        productName = n;
        price = p;
        buyer = b;
    }
}
