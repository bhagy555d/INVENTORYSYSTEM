import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnalyticsDAO {

    // Metric 1: Category-wise Asset Valuation (Multi-Table JOIN & GROUP BY)
    public static void getCategoryValuation() {
        String query = "SELECT c.category_name, SUM(p.price * p.stock_quantity) AS total_valuation " +
                "FROM products p " +
                "JOIN categories c ON p.category_id = c.category_id " +
                "GROUP BY c.category_name " +
                "ORDER BY total_valuation DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n --- LIVE BUSINESS METRIC: CATEGORY VALUATION ---");
            while (rs.next()) {
                String category = rs.getString("category_name");
                double valuation = rs.getDouble("total_valuation");
                System.out.printf("Category: %-18s | Total Value: ₹%,.2f\n", category, valuation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metric 2: Low-Stock Alerts (Conditional Filtering)
    public static void getLowStockAlerts() {
        String query = "SELECT product_name, stock_quantity, low_stock_threshold " +
                "FROM products " +
                "WHERE stock_quantity <= low_stock_threshold";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n --- LIVE BUSINESS METRIC: LOW-STOCK ALERTS ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                String name = rs.getString("product_name");
                int stock = rs.getInt("stock_quantity");
                int threshold = rs.getInt("low_stock_threshold");
                System.out.printf("ALERT: '%s' is running low! Current Stock: %d (Threshold: %d)\n", name, stock,
                        threshold);
            }
            if (!found) {
                System.out.println(" All assets are safely above thresholds.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Run both analytical live dashboards
        getCategoryValuation();
        getLowStockAlerts();
    }
}