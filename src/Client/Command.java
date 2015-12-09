package Client;

/**
 * Created by Rickard on 2015-12-09.
 */
public class Command {
    private String userName;
    private float amount;
    private Client.BankCommandName bankCommandName;

    public String getUserName() {
        return userName;
    }

    public float getAmount() {
        return amount;
    }

    public Client.BankCommandName getBankCommandName() {
        return bankCommandName;
    }

    private Command(Client.BankCommandName bankCommandName, String userName, float amount) {
        this.bankCommandName = bankCommandName;
        this.userName = userName;
        this.amount = amount;
    }
}