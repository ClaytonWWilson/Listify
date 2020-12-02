import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestItemGetter {

    @Test
    public void testItemGetterValid() {
        conductItemGetterTestMock(false);
    }

    @Test
    public void testItemGetterError() {
        conductItemGetterTestMock(true);
    }

    public void conductItemGetterTestMock(boolean shouldThrow) {
        ArrayList<Object> rsReturns = new ArrayList<>();
        rsReturns.add(1);//ProductID
        rsReturns.add(2);//chainID
        rsReturns.add("aupc");
        rsReturns.add("adescription");
        rsReturns.add(BigDecimal.valueOf(.03));//Price
        rsReturns.add("animageurl");
        rsReturns.add("adepartment");
        rsReturns.add(Timestamp.from(Instant.ofEpochMilli(1602192528688L)));//retrievedDate
        rsReturns.add(5); // fetchCounts
        StatementInjector injector = null;
        try {
            injector = new StatementInjector(null, rsReturns, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ItemGetter getter = Mockito.spy(new ItemGetter(injector, "id"));
        Map<String, Object> ignore = new HashMap<>();
        HashMap<String, String> queryParams = TestInputUtils.addQueryParams(ignore);
        queryParams.put("id", "1");
        try {
            Object conductReturn = getter.conductAction(TestInputUtils.addBody(ignore), queryParams, "cognitoID");
            assert !shouldThrow;
            assert (conductReturn.getClass() == Item.class);
            Item itemReturn = (Item) conductReturn;
            assert (itemReturn.toString().equals("Item{productID=1, chainID=2, upc='aupc', description='adescription', price=0.03, imageURL='animageurl', department='adepartment', retrievedDate=1602192528688, fetchCounts=5}"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert shouldThrow;
        }
    }
}
