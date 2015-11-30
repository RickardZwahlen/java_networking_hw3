package Server;

/**
 * Created by martin on 2015-11-30.
 */
public class SellObject implements Product{
    private String productName;
    private double price;
    private String seller;

    public String getName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public String getSeller() {
        return seller;
    }

    public SellObject(String n, Double p, String s){
        productName = n;
        price = p;
        seller = s;
    }

}
