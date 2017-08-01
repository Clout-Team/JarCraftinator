package com.cloutteam.jarcraftinator.api.json;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JSONObject {

    private HashMap<String, Object> map = new HashMap<>();

    public JSONObject add(String key, Object value){
        map.put(key, value);
        return this;
    }

    public JSONObject remove(String key) {
        map.remove(key);
        return this;
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public boolean containsValue(String key) {
        return map.containsValue(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        String[] contents = new String[map.size()];
        int i = 0;
        for(Map.Entry<String, Object> entry : map.entrySet())
            contents[i++] = "\"" + encode(entry.getKey()) + "\": " + encodeValue(entry.getValue());
        builder.append(String.join(", ", contents));
        builder.append("}");
        return builder.toString();
    }

    private String encodeValue(Object value) {
        if(value instanceof JSONObject || value instanceof  JSONArray || value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof Boolean)
            return value.toString();
        return "\"" + encode(value.toString()) + "\"";
    }

    private String encode(String str) {
        return StringEscapeUtils.escapeJson(str);
    }
}
