package Client;

/**
 * Created by Rickard on 2015-12-09.
 */
public class Command {
    private String userName;
    private float amount;
    private Client.CommandName bankCommandName;
    private String other;

    public String getUserName() {
        return userName;
    }

    public float getAmount() {
        return amount;
    }

    public Client.CommandName getBankCommandName() {
        return bankCommandName;
    }

    public Command(Client.CommandName bankCommandName, String userName, float amount) {
        this.bankCommandName = bankCommandName;
        this.userName = userName;
        this.amount = amount;
    }

    public Command(Client.CommandName bankCommandName, String userName, float amount, String other) {
        this.bankCommandName = bankCommandName;
        this.userName = userName;
        this.amount = amount;
        this.other = other;
    }

    public String getOther()
    {
        return other;
    }
}