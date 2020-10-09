import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ListEntryAdder implements CallHandler {

    private Connection connection;
    private String cognitoID;


    private final String CHECK_ITEM_IN_LIST = "SELECT quantity from ListProduct WHERE productID = ? AND listID = ?;";
    private final String CLEAR_PAIRING = "DELETE from ListProduct WHERE productID = ? AND listID = ?;";
    private final String ITEM_TO_LIST = "INSERT INTO ListProduct (productID, listID, quantity, addedDate, purchased) VALUES (?, ?, ?, ?, ?)";

    public ListEntryAdder(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        PreparedStatement quantitiyStatement = connection.prepareStatement(CHECK_ITEM_IN_LIST);
        Integer productID =  (Integer) bodyMap.get("productID");
        Integer listID = (Integer) bodyMap.get("listID");
        quantitiyStatement.setInt(1, productID);
        quantitiyStatement.setInt(2, listID);
        ResultSet quanitityRS = quantitiyStatement.executeQuery();
        int priorQuanity = 0;
        if (quanitityRS.next()) {
            priorQuanity = quanitityRS.getInt(1);
        }
        PreparedStatement clearStatement = connection.prepareStatement(CLEAR_PAIRING);
        clearStatement.setInt(1, productID);
        clearStatement.setInt(2, listID);
        clearStatement.executeUpdate();
        PreparedStatement statement = connection.prepareStatement(ITEM_TO_LIST);
        statement.setInt(1, productID);
        statement.setInt(2, listID);
        statement.setInt(3, (Integer) bodyMap.get("quantity") + priorQuanity);
        statement.setTimestamp(4, Timestamp.from(Instant.now()));
        statement.setBoolean(5, (Boolean) bodyMap.get("purchased"));
        System.out.println(statement);
        statement.executeUpdate();
        connection.commit();
        return null;
    }
}
