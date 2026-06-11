import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class ApiServer {

    public static void main(String[] args) throws IOException {
        // Start the server on port 8081
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/add-product", new ProductHandler());
        server.setExecutor(null);
        server.start();
        System.out.println(" Enterprise API Server is LIVE on http://localhost:8081");
        System.out.println("Ready to intercept Postman payloads and commit them to PostgreSQL...");
    }

    static class ProductHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // 1. Read the incoming JSON string from Postman
                InputStream is = exchange.getRequestBody();
                String json = new String(is.readAllBytes());
                System.out.println("\n Intercepted Payload from Postman: " + json);

                String response;
                int statusCode;

                try {
                    // 2. Extract values natively using our helper method
                    String sku = getJsonValue(json, "sku");
                    String productName = getJsonValue(json, "product_name");
                    int categoryId = Integer.parseInt(getJsonValue(json, "category_id"));
                    double price = Double.parseDouble(getJsonValue(json, "price"));
                    int stockQuantity = Integer.parseInt(getJsonValue(json, "stock_quantity"));
                    int threshold = Integer.parseInt(getJsonValue(json, "low_stock_threshold"));

                    // 3. Hand the data off directly to our Database Layer!
                    ProductDAO.addProduct(sku, productName, categoryId, price, stockQuantity, threshold);

                    response = " SUCCESS: Product '" + productName
                            + "' successfully parsed and written to PostgreSQL!";
                    statusCode = 200;

                } catch (Exception e) {
                    response = " ERROR: Failed to parse JSON or write to database. Check system logs.";
                    statusCode = 400;
                    e.printStackTrace();
                }

                // 4. Send response back to Postman
                exchange.sendResponseHeaders(statusCode, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                String response = " ERROR: Method Not Allowed.";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        // Native Helper Function to extract a value from a JSON string without external
        // libraries
        // Corrected Native Helper Function
        private static String getJsonValue(String json, String key) {
            String searchKey = "\"" + key + "\"";
            int keyIdx = json.indexOf(searchKey);
            if (keyIdx == -1) return "";

            int colonIdx = json.indexOf(":", keyIdx);
            if (colonIdx == -1) return "";

            int valueStart = colonIdx + 1;
            // Skip any spaces between the colon and the value
            while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
                valueStart++;
            }

            char firstChar = json.charAt(valueStart);
            if (firstChar == '"') { // It's a String value
                int valueEnd = json.indexOf('"', valueStart + 1);
                return json.substring(valueStart + 1, valueEnd);
            } else { // It's a Number value
                int valueEnd = valueStart;
                while (valueEnd < json.length() && (Character.isDigit(json.charAt(valueEnd)) || json.charAt(valueEnd) == '.' || json.charAt(valueEnd) == '-')) {
                    valueEnd++;
                }
                return json.substring(valueStart, valueEnd);
            }
        }
    }
}