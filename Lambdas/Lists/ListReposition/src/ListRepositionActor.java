import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ListRepositionActor implements CallHandler {

    private Connection connection;
    private String cognitoID;

    public ListRepositionActor(Connection connection, String cognitoID) {
        this.connection = connection;
        this.cognitoID = cognitoID;
    }

    final private String GET_PRIOR_POSITON = "SELECT uiPosition FROM ListSharee WHERE userID = ? AND listID = ?;";
    final private String SET_NEW_POSITON = "UPDATE ListSharee SET uiPosition = ? WHERE userID = ? AND listID = ?;";
    final private String DECREMENT_HIGHER_POSITIONS = "UPDATE ListSharee SET uiPosition = uiPosition - 1 WHERE uiPosition > ? AND userID = ?;";
    final private String INCREMENT_GEQ_POSITIONS = "UPDATE ListSharee SET uiPosition = uiPosition + 1 WHERE uiPosition >= ? AND userID = ?;";


    public Object conductAction(Map<String, Object> bodyMap, HashMap<String, String> queryString, String cognitoID) throws SQLException {
        Integer listID = (Integer) bodyMap.get("listID");
        Integer newPosition = (Integer) bodyMap.get("newPosition");

        PreparedStatement getPriorPosition = connection.prepareStatement(GET_PRIOR_POSITON);
        getPriorPosition.setString(1, cognitoID);
        getPriorPosition.setInt(2, listID);
        ResultSet priorPositionRS = getPriorPosition.executeQuery();
        if (!priorPositionRS.next()) {
            throw new IllegalArgumentException("Bad listID for user");
        }
        Integer priorPosition = priorPositionRS.getInt("uiPosition");

        PreparedStatement fillPriorPosition = connection.prepareStatement(DECREMENT_HIGHER_POSITIONS);
        fillPriorPosition.setInt(1, priorPosition);
        fillPriorPosition.setString(2, cognitoID);
        System.out.println(fillPriorPosition);
        fillPriorPosition.executeUpdate();

        PreparedStatement openNewPosition = connection.prepareStatement(INCREMENT_GEQ_POSITIONS);
        openNewPosition.setInt(1, priorPosition);
        openNewPosition.setString(2, cognitoID);
        System.out.println(openNewPosition);
        openNewPosition.executeUpdate();

        PreparedStatement setNewPosition = connection.prepareStatement(SET_NEW_POSITON);
        setNewPosition.setInt(1, newPosition);
        setNewPosition.setString(2, cognitoID);
        setNewPosition.setInt(3, listID);
        System.out.println(setNewPosition);
        setNewPosition.executeUpdate();

        connection.commit();
        return null;
    }

}