import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductDAO {

    // Method to insert a new product into the database
    public static void addProduct(String sku, String productName, int categoryId, double price, int stockQuantity,
            int threshold) {
        String query = "INSERT INTO products (sku, product_name, category_id, price, stock_quantity, low_stock_threshold) VALUES (?, ?, ?, ?, ?, ?)";

        // Open the database connection automatically using try-with-resources
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Map the Java variables to the SQL '?' placeholders
            pstmt.setString(1, sku);
            pstmt.setString(2, productName);

            // Handle optional category (if 0, we set it to NULL in database)
            if (categoryId == 0) {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(3, categoryId);
            }

            pstmt.setDouble(4, price);
            pstmt.setInt(5, stockQuantity);
            pstmt.setInt(6, threshold);

            // Execute the query
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(" Product '" + productName + "' inserted successfully with SKU: " + sku);
            }

        } catch (SQLException e) {
            System.out.println(" Failed to insert product!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        
        // Threshold
        addProduct("TECH-LAP-001", "Enterprise ThinkPad Laptops", 0, 75000.00, 25, 5);
    }
}
