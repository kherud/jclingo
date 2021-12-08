package org.potassco.clingo.control;

import com.sun.jna.ptr.IntByReference;
import org.json.JSONArray;
import org.json.JSONObject;
import org.potassco.clingo.dtype.NativeSizeByReference;

/**
 * This class abstracts the json like structure of clingo's configuration and statistics,
 * where a map holds a tree structure and each key can point to another map, an array, or a value
 */
public abstract class JSONLike {

    // allocate these once for performance
    protected final IntByReference intByRef = new IntByReference();
    protected final NativeSizeByReference nativeSizeByRef = new NativeSizeByReference();
    protected final String[] stringByRef = new String[1];

    // abstract clingo's struct specific functions
    protected abstract long getRootKey();
    protected abstract int getKeyType(long key);
    protected abstract boolean isMap(int type);
    protected abstract boolean isArray(int type);
    protected abstract boolean isValue(int type);
    protected abstract long getMapSize(long key);
    protected abstract long getArraySize(long key);
    protected abstract String getNameAtMapIndex(long key, int index);
    protected abstract long getIdAtArrayIndex(long key, int index);
    protected abstract long getIdOfKeyName(long key, String name);
    protected abstract boolean checkValueAssigned(long key);
    protected abstract Object getValueByKey(long key);

    public JSONObject getJSON() {
        return (JSONObject) recurseNode(getRootKey());
    }

    private Object recurseNode(long key) {
        int keyType = getKeyType(key);
        if (isValue(keyType))
            return recurseValue(key);
        else if (isArray(keyType))
            return recurseArray(key);
        else if (isMap(keyType))
            return recurseMap(key);
        return null;
    }

    private Object recurseMap(long key) {
        JSONObject jsonObject = new JSONObject();

        long mapSize = getMapSize(key);
        for (int i = 0; i < mapSize; i++) {
            String keyName = getNameAtMapIndex(key, i);
            long keyId = getIdOfKeyName(key, keyName);
            Object value = recurseNode(keyId);

            if (value != null)
                jsonObject.put(keyName, value);
        }

        return jsonObject;
    }

    private Object recurseArray(long key) {
        JSONArray jsonArray = new JSONArray();

        long arraySize = getArraySize(key);
        for (int i = 0; i < arraySize; i++) {
            long keyId = getIdAtArrayIndex(key, i);
            Object value = recurseNode(keyId);

            if (value != null)
                jsonArray.put(value);
        }

        return jsonArray;
    }

    private Object recurseValue(long key) {
        return checkValueAssigned(key) ? getValueByKey(key) : null;
    }
}
