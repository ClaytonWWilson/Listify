import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ListPermissions {
    private static final Map<Integer, String> keysToPerms;
    static {
        //All keys should be a prime number > 1
        //All keys need to be maintained here and in ListShare object in data on the client side
        HashMap<Integer, String> keysToPermsTemp = new HashMap<>();
        keysToPermsTemp.put(2, "read");
        keysToPermsTemp.put(3, "write");
        keysToPermsTemp.put(5, "delete");
        keysToPermsTemp.put(7, "share");
        keysToPerms = Collections.unmodifiableMap(keysToPermsTemp);
    }

    public static Integer getAll() {
        Integer toReturn = 1;
        for (Integer key : keysToPerms.keySet()) {
            toReturn *= key;
        }
        return toReturn;
    }

    public static boolean hasPermission(Integer level, String permission) {
        return level % getKeyForPermission(permission) == 0;
    }

    public static Integer getKeyForPermission(String permissionRaw) {
        String permission = permissionRaw.toLowerCase();
        for (Map.Entry<Integer, String> entry : keysToPerms.entrySet()) {
            if (entry.getValue().equals(permission)) {
                return entry.getKey();
            }
        }
        System.out.println("Tried to get key for invalid permission: " + permission);
        return -1;
    }

}
