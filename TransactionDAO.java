import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {

    // Method to process a stock transaction safely
    public static void processStockTransaction(int productId, String type, int quantity) {
        String updateStockQuery = type.equals("STOCK_IN")
                ? "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?"
                : "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";

        String insertLogQuery = "INSERT INTO inventory_transactions (product_id, transaction_type, quantity) VALUES (?, ?, ?)";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();

            //  ENTERPRISE RULE: Turn off auto-commit to start a manual transaction block
            conn.setAutoCommit(false);

            // 1. Update the Products table (Change the stock number)
            try (PreparedStatement updateStmt = conn.prepareStatement(updateStockQuery)) {
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, productId);
                updateStmt.executeUpdate();
            }

            // 2. Insert into the Ledger (Create the Audit Log)
            try (PreparedStatement logStmt = conn.prepareStatement(insertLogQuery)) {
                logStmt.setInt(1, productId);
                logStmt.setString(2, type);
                logStmt.setInt(3, quantity);
                logStmt.executeUpdate();
            }

            //  Both operations succeeded? Commit the transaction to the database
            // permanently
            conn.commit();
            System.out.println(
                    " Transaction Successful: " + type + " of " + quantity + " units for Product ID: " + productId);

        } catch (SQLException e) {
            //  Something failed (e.g., tried to deduct more stock than available)!
            System.out.println(" Transaction Failed! Rolling back changes...");
            if (conn != null) {
                try {
                    conn.rollback(); // Undo everything so no partial data is saved
                    System.out.println("Rollback complete. No partial data saved.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Reset auto-commit and safely close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Test 1: Let's add 10 more laptops to the warehouse (STOCK_IN)
        // Product ID 1 (our ThinkPad), Type: STOCK_IN, Quantity: 10
        processStockTransaction(1, "STOCK_IN", 10);
        // Test 2: Let's test our database constraints!
        

        processStockTransaction(1, "STOCK_OUT", 100);

        
    }
}
