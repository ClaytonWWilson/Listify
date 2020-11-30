import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Mockito.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestListGetter {

    @Test
    public void testListGetterValid() { conductListGetterTestMock(false); }

    @Test
    public void testListIDGetterValid() {
        conductListIDGetterTestMock(false);
    }

    @Test
    public void testListIDGetterError() {
        conductListIDGetterTestMock(false);
    }

    @Test
    public void testListGetterError() { conductListGetterTestMock(true); }

    public void conductListGetterTest(boolean shouldThrow) {
        Integer listID = 1;
        String name = "aname";
        String owner = "anowner";
        Timestamp lastUpdated = Timestamp.from(Instant.ofEpochMilli(1602192528688L));

        Integer productID = 2;
        Integer quantity = 3;
        Timestamp addedDate = Timestamp.from(Instant.ofEpochMilli(1602192528689L));;
        Boolean purchased = false;

        ArrayList<Object> rsReturns = new ArrayList<>();
        rsReturns.add(listID);
        rsReturns.add(name);
        rsReturns.add(owner);
        rsReturns.add(lastUpdated);
        rsReturns.add(productID);
        rsReturns.add(quantity);
        rsReturns.add(addedDate);
        rsReturns.add(purchased);
        StatementInjector injector = null;
        try {
            injector = new StatementInjector(null, rsReturns, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ListGetter getter = new ListGetter(injector, "id");
        Map<String, Object> ignore = new HashMap<>();
        HashMap<String, String> queryParams = TestInputUtils.addQueryParams(ignore);
        queryParams.put("id", "1");
        try {
            Object conductReturn = getter.conductAction(TestInputUtils.addBody(ignore), queryParams, "cognitoID");
            assert !shouldThrow;
            assert (conductReturn.getClass() == List.class);
            List listReturn = (List) conductReturn;
            assert (listReturn.toString().equals("List{itemID=1, name='aname', owner='anowner', lastUpdated=1602192528688, entries=[ItemEntry{listID=1, productID=2, quantity=3, addedDate=1602192528689, purchased=false}]}"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert shouldThrow;
        }
    }

    public void conductListIDGetterTest(boolean shouldThrow) {
        
        ArrayList<Object> rsReturns = new ArrayList<>();
        rsReturns.add(1);
        rsReturns.add(2);
        rsReturns.add(3);
        rsReturns.add(4);

        StatementInjector injector = null;
        try {
            injector = new StatementInjector(null, rsReturns, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ListGetter getter = new ListGetter(injector, "id");
        Map<String, Object> ignore = new HashMap<>();
        HashMap<String, String> queryParams = TestInputUtils.addQueryParams(ignore);
        queryParams.put("id", "-1");
        try {
            Object conductReturn = getter.conductAction(TestInputUtils.addBody(ignore), queryParams, "cognitoID");
            assert !shouldThrow;
            assert (conductReturn.getClass() == ArrayList.class);
            ArrayList<Integer> listIDsReturn = (ArrayList<Integer>) conductReturn;
            System.out.println(listIDsReturn.toString());
            assert (listIDsReturn.toString().equals("[1, 2, 3, 4]"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert shouldThrow;
        }
    }

    public void conductListGetterTestMock(boolean shouldThrow) {
        Integer listID = 1;
        String name = "aname";
        String owner = "anowner";
        Timestamp lastUpdated = Timestamp.from(Instant.ofEpochMilli(1602192528688L));

        Integer productID = 2;
        Integer quantity = 3;
        Timestamp addedDate = Timestamp.from(Instant.ofEpochMilli(1602192528689L));;
        Boolean purchased = false;

        ArrayList<Object> rsReturns = new ArrayList<>();
        rsReturns.add(listID);
        rsReturns.add(name);
        rsReturns.add(owner);
        rsReturns.add(lastUpdated);
        rsReturns.add(productID);
        rsReturns.add(quantity);
        rsReturns.add(addedDate);
        rsReturns.add(purchased);
        StatementInjector injector = null;
        try {
            injector = new StatementInjector(null, rsReturns, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ListGetter getter = Mockito.spy(new ListGetter(injector, "id"));
        Map<String, Object> ignore = new HashMap<>();
        HashMap<String, String> queryParams = TestInputUtils.addQueryParams(ignore);
        queryParams.put("id", "1");
        try {
            Object conductReturn = getter.conductAction(TestInputUtils.addBody(ignore), queryParams, "cognitoID");
            assert !shouldThrow;
            assert (conductReturn.getClass() == List.class);
            List listReturn = (List) conductReturn;
            assert (listReturn.toString().equals("List{itemID=1, name='aname', owner='anowner', lastUpdated=1602192528688, entries=[ItemEntry{listID=1, productID=2, quantity=3, addedDate=1602192528689, purchased=false}]}"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert shouldThrow;
        }
    }

    public void conductListIDGetterTestMock(boolean shouldThrow) {
        ArrayList<Object> rsReturns = new ArrayList<>();
        rsReturns.add(1);
        rsReturns.add(2);
        rsReturns.add(3);
        rsReturns.add(4);

        StatementInjector injector = null;
        try {
            injector = new StatementInjector(null, rsReturns, shouldThrow);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //ListGetter getter = new ListGetter(injector, "id");
        ListGetter getter = Mockito.spy(new ListGetter(injector, "id"));
        Map<String, Object> ignore = new HashMap<>();
        HashMap<String, String> queryParams = TestInputUtils.addQueryParams(ignore);
        queryParams.put("id", "-1");
        try {
            Object conductReturn = getter.conductAction(TestInputUtils.addBody(ignore), queryParams, "cognitoID");
            assert !shouldThrow;
            assert (conductReturn.getClass() == ArrayList.class);
            ArrayList<Integer> listIDsReturn = (ArrayList<Integer>) conductReturn;
            System.out.println(listIDsReturn.toString());
            assert (listIDsReturn.toString().equals("[1, 2, 3, 4]"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            assert shouldThrow;
        }
    }
}
