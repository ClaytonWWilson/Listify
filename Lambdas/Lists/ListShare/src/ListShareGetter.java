import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ListShareGetter implements CallHandler{
    private final Connection connection;
    private final String cognitoID;

    private final String GET_LISTS = "SELECT * FROM ListSharee WHERE listID = ?;";

    public ListShareGetter(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    @Override
    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryMap, String cognitoID) throws SQLException {
        Integer listID = Integer.parseInt(queryMap.get("id"));

        PreparedStatement getList = connection.prepareStatement(GET_LISTS);
        getList.setInt(1, listID);

        ResultSet getListResults = getList.executeQuery();
        getListResults.first();

        //ListShare object to hold the data values of the first row retrived
        ListShare first = new ListShare(getListResults); 

        //Insert the ListShare objects to hold the data of the remaining rows into first's ListShare list
        while (getListResults.next()) {
            first.addtoList(new ListShare(getListResults));
        }

        return first;
    }
}
