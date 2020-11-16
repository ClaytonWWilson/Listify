package com.example.listify.data;

import com.example.listify.BuildConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ListShare {
    Integer listID;
    String shareWithEmail;
    Integer permissionLevel;

    private static final Map<Integer, String> keysToPerms;
    static {
        //All keys should be a prime number > 1
        //All keys need to be maintained here and in List module->ListPermissions class on the Lambda side
        HashMap<Integer, String> keysToPermsTemp = new HashMap<>();
        keysToPermsTemp.put(2, "read");
        keysToPermsTemp.put(3, "write");
        keysToPermsTemp.put(5, "delete");
        keysToPermsTemp.put(7, "share");
        keysToPerms = Collections.unmodifiableMap(keysToPermsTemp);
    }

    public ListShare(Integer listID, String shareWithEmail, Integer permissionLevel) {
        this.listID = listID;
        this.shareWithEmail = shareWithEmail;
        this.permissionLevel = permissionLevel;
    }

    public ListShare(Integer listID, String shareWithEmail, String permissionsRaw) {
        String permissions = permissionsRaw.toLowerCase();
        this.listID = listID;
        this.shareWithEmail = shareWithEmail;
        permissionLevel = 1;
        for (Map.Entry<Integer, String> keytoPermEntry: keysToPerms.entrySet()) {
            if (permissions.contains(keytoPermEntry.getValue())) {
                permissionLevel *= keytoPermEntry.getKey();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder("ListShare{" +
                "listID=" + listID +
                ", shareWithEmail='" + shareWithEmail + '\'' +
                ", permissionLevel=" + permissionLevel +
                " [Permissions: ");

        int permissionLevelCopy = permissionLevel;
        for (Integer permissionObject : keysToPerms.keySet()) {
            Integer permissionInteger = permissionObject;
            if (permissionLevelCopy % permissionInteger == 0) {
                permissionLevelCopy /= permissionInteger;
                toReturn.append(keysToPerms.get(permissionInteger)).append(",");
            }
        }
        if (BuildConfig.DEBUG && permissionLevelCopy != 1) {
            throw new AssertionError("Assertion failed");
        }
        toReturn.append("]}");
        return toReturn.toString();
    }

    public Integer getListID() {
        return listID;
    }

    public void setListID(Integer listID) {
        this.listID = listID;
    }

    public String getShareWithEmail() {
        return shareWithEmail;
    }

    public void setShareWithEmail(String shareWithEmail) {
        this.shareWithEmail = shareWithEmail;
    }

    public Integer getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(Integer permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

}
