package banking;

import java.sql.*;

public class Database {
    public static Connection c;
    private static String fileName = "test.db";
    /**
     * Creates database
     *
     * @param fileName takes the name for the database
     * @return whether the table exists
     * @throws SQLException the sql exception
     */
    public boolean createDatabase(String fileName) throws SQLException {
        c = null;
        Statement stmt = null;
        this.fileName = fileName;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + fileName);

            stmt = c.createStatement();
            String sql = "CREATE TABLE card " +
                    "(id INT PRIMARY KEY     NOT NULL," +
                    " number           TEXT    NOT NULL, " +
                    " pin            TEXT     NOT NULL, " +
                    " balance        INTEGER DEFAULT 0)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            if (e.getMessage().contains("table card already exists")) {
                return false;
            }
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return true;
    }

    /**
     * Insert statement for the database
     *
     * @param sql the sql string for query
     * @throws SQLException the sql exception
     */
    public void insertStatement(String sql) {
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            c.setAutoCommit(false);

            stmt = c.createStatement();

            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Select query especially just for the Card object
     *
     * @param sql the sql string for query
     * @return Card the whole object
     * @throws SQLException the sql exception
     */
    public Card selectCardStatement(String sql) {
        Statement stmt = null;
        Card card = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String number = rs.getString("number");
                String pin = rs.getString("pin");
                int balance = rs.getInt("balance");

                card = new Card(Long.parseLong(number), Integer.parseInt(pin), balance);
            }
            rs.close();
            stmt.close();
            c.commit();
            c.close();
            return card;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return card;
    }

    /**
     * Update statement for the database
     *
     * @param sql the sql string for query
     * @throws SQLException the sql exception
     */
    public void updateBalance(String sql) {
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            c.setAutoCommit(false);

            stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Delete statement for the database
     *
     * @param sql the sql string for query
     * @throws SQLException the sql exception
     */
    public void deleteCard(String sql) {
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            c.setAutoCommit(false);

            stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

}