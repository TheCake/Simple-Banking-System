package banking;

import java.util.Scanner;
import java.util.Random;
import java.sql.*;

/**
 *
 * @author Filip Hajek
 */
public class Main {
    private static Database database;
    private static Card loggedCard = null;
    private static Card transferCard = null;
    private static Scanner scanner;
    private static Random rand;
    private static String fileName = "test.db";
    private static final long BANK_PREFIX = 400000;

    public static void main(String[] args) throws SQLException {
        try {
            if (args[0].equals("-fileName") && !args[1].isEmpty()) {
                fileName = args[1];
            }
        } catch (Exception ignored) {
        }

        database = new Database();
        if (database.createDatabase(fileName)) {
            System.out.println("created");
        }

        scanner = new Scanner(System.in);
        rand = new Random();

        boolean running = true;
        while(running) {
            running = mainMenu();
        }

        System.out.println("Bye!");

    }

    /**
     * Main menu method with switch
     *
     * @return boolean flag whether the menu is still supposed to run or exit
     */
    public static boolean mainMenu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
        int chose = scanner.nextInt();

        switch(chose) {
            // exit
            case 0:
                return false;
            // create account
            case 1:
                createAccount();
                return true;
            // log account
            case 2:
                return logAccount();
            default:
                System.out.println("nope");
                return false;
        }
    }

    /**
     * Creates account/card and inserts it into the database
     *
     *
     */
    public static void createAccount() {
        Card newCard = new Card(createLuhnNum(), rand.nextInt(8999) + 1000 , 0);

        System.out.println("Your card has been created\n" +
                "Your card number:");
        System.out.println(newCard.getCardNumber());
        System.out.println("Your card PIN:");
        System.out.println(newCard.getPassword());


        database.insertStatement("INSERT INTO card(id , number , pin , balance) " +
                                  "VALUES (" + (int)(newCard.getCardNumber() - 4000000000000000L) + ", '" +
                                    Long.toString(newCard.getCardNumber()) + "', '" +
                                    Integer.toString(newCard.getPassword()) + "', " +
                                    newCard.getBalance() + " );");
    }

    /**
     * Method for the logging process
     *
     * @return boolean flag whether the menu is still supposed to run or exit
     */
    public static boolean logAccount() {
        boolean logged = false;

        System.out.println("Enter your card number:");
        long id = scanner.nextLong();
        System.out.println("Your card PIN:");
        int password = scanner.nextInt();


        loggedCard = database.selectCardStatement("SELECT * FROM card WHERE pin = " + password + " AND number = " + id + ";");
        if (loggedCard != null) {
            System.out.println("You have successfully logged in!");
            logged = true;
        } else {
            System.out.println("Wrong card number or PIN!");
        }


        if (logged) {
            return loggedMenu();
        } else {
            System.out.println("Wrong card number or PIN!");
        }

        return true;
    }

    /**
     * Menu for logged card
     *
     * @return boolean flag whether the menu is still supposed to run or exit
     */
    private static boolean loggedMenu() {
        while (true) {
            System.out.println("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
            int chose = scanner.nextInt();

            switch (chose) {
                // exit
                case 0:
                    loggedCard = null;
                    return false;
                // balance
                case 1:
                    System.out.println("Balance: " + loggedCard.getBalance());
                    break;
                // add income
                case 2:
                    System.out.println("Enter income:");
                    loggedCard.addIncome(database, scanner.nextInt());
                    System.out.println("Income was added!");
                    break;
                // do transfer
                case 3:
                    System.out.println("Transfer\n" +
                            "Enter card number:");
                    moneyTransfer();
                    break;
                // close account
                case 4:
                    loggedCard.deleteCard(database);
                    loggedCard = null;
                    System.out.println("The account has been closed!");
                    break;
                // log out
                case 5:
                    loggedCard = null;
                    System.out.println("You have successfully logged out!");
                    return true;
                default:
                    System.out.println("That's not a possible choice");
            }
        }
    }

    /**
     * Transfers the money between accounts and checks for any hiccups along the way
     *
     */
    private static void moneyTransfer() {
        long cardNum = scanner.nextLong();
        if (!isLuhnNum(cardNum)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }
        if (cardNum == loggedCard.getCardNumber()) {
            System.out.println("That's your card");
            return;
        }

        transferCard = database.selectCardStatement("SELECT * FROM card WHERE number = " + cardNum + ";");
        if (transferCard == null) {
            System.out.println("Such a card does not exist.");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        int moneyToTransfer = scanner.nextInt();
        if(loggedCard.getBalance() < moneyToTransfer) {
            System.out.println("Not enough money!");
            return;
        }
        loggedCard.subtractIncome(database, moneyToTransfer);
        transferCard.addIncome(database, moneyToTransfer);
        transferCard = null;
        System.out.println("Success!");
    }

    /**
     * Method for whether number is according to Luhn algorithm
     *
     * @param num the number to check through Luhn algo
     * @return boolean flag whether provided number checks out under Luhn algo
     */
    private static boolean isLuhnNum(Long num) {
        String numStr = Long.toString(num);
        int[] numArr = new int[16];
        int sum = 0;

        // takes the string made from long into array and computes the luhn sum for the check digit
        for(int i = 0; i < 16; i++) {
            numArr[i] = numStr.charAt(i) - 48;
            System.out.println(numArr[i]);
            if(i < 15) {
                if (i % 2 == 0) {
                    numArr[i] *= 2;
                }
                if (numArr[i] > 9) {
                    numArr[i] -= 9;
                }
                sum += numArr[i];
            }
        }

        // now compares whether the check digit fits into what it's supposed to be
        // and returns true or false
        if (sum % 10 == 0) {
            return numArr[15] == 0;
        } else {
            return numArr[15] == (10 - (sum % 10));
        }
    }

    /**
     * Creates Luhn number
     * Takes bank prefix, leaves 9 numbers as random and then computes the last check digit
     *
     * @return luhn number
     */
    private static long createLuhnNum() {
        //adding 6 numbers of bank prefix and the rest random
        int[] numArr = new int[16];
        int l = 5;
        for (int i = 0; i < numArr.length - 1; i++ ) {
            if (i < 6) {
                numArr[i] = ((int) (BANK_PREFIX / Math.pow(10, l))) % 10;
                l--;
            } else {
                numArr[i] = rand.nextInt(9);
            }
        }

        int[] numArr2 = numArr.clone();
        int sum = 0;
        // multiplying every second number in the cloned array to two
        for (int i = 0; i < numArr2.length - 1; i++ ) {
            if (i % 2 == 0) {
                numArr2[i] *= 2;
            }
            if (numArr2[i] > 9) {
                numArr2[i] -= 9;
            }
            sum += numArr2[i];
        }

        // getting the difference to the nearest floor decade
        if (sum % 10 == 0) {
            numArr[15] = 0;
        } else {
            numArr[15] = 10 - (sum % 10);
        }

        // now just creating the long number from the array of ints
        long luhn = 0;
        l = 15;
        for (int j : numArr) {
            luhn += j * Math.pow(10, l);
            l--;
        }

        return luhn;
    }

}