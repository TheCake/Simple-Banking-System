package banking;

/**
 *
 * @author Filip Hajek
 */
public class Card {
    private long cardNumber;
    private int password;
    private long balance;

    /**
     * Constructor for card object
     *
     * @param cardNumber the id of the card
     * @param password the PIN for the card
     * @param balance amount of money in the account
     */
    public Card(long cardNumber, int password, long balance) {
        this.cardNumber = cardNumber;
        this.password = password;
        this.balance = balance;
    }


    public long getCardNumber() {
        return cardNumber;
    }

    public int getPassword() {
        return password;
    }

    public long getBalance() {
        return balance;
    }

    /**
     * Adds income into the account
     *
     * @param added the income amount added to the account
     * @param database the database we use
     */
    public void addIncome(Database database, int added) {
        this.balance += added;
        database.updateBalance("UPDATE card SET balance = balance +" + added +
                " where number = '" + Long.toString(cardNumber) + "';");
    }

    /**
     * Subtracts income into the account
     *
     * @param subtracted the income amount subtracted from the account
     * @param database the database we use
     */
    public void subtractIncome(Database database, int subtracted) {
        this.balance += subtracted;
        database.updateBalance("UPDATE card SET balance = balance -" + subtracted +
                " where number = '" + Long.toString(cardNumber) + "';");
    }

    /**
     * Deletes account/card
     *
     * @param database the database we use
     */
    public void deleteCard(Database database) {
        database.deleteCard("DELETE FROM card WHERE number = '" +
                                Long.toString(cardNumber) + "';");
    }
}

