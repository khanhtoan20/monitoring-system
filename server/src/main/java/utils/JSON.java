package utils;


import org.json.JSONException;
import org.json.JSONObject;

public class JSON extends JSONObject {

    public JSON(String message) {
        super(message);
    }

    public String get(String key) {
        try {
            return super.get(key).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public JSON put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
