package Client;

import Server.Product;
import Server.SellObject;

/**
 * Created by martin on 2015-11-30.
 */
public interface ClientInterface {
    public void notifySale(SellObject product);
    public void notifyBuy(SellObject product);
}
