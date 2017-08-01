package com.cloutteam.jarcraftinator.api.json;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

public class JSONArray {

    private ArrayList<Object> list = new ArrayList<>();

    public JSONArray add(Object value){
        list.add(value);
        return this;
    }

    public JSONArray remove(Object value) {
        list.remove(value);
        return this;
    }

    public boolean contains(Object value) {
        return list.contains(value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        String[] contents = new String[list.size()];
        int i = 0;
        for(Object value : list)
            contents[i++] = encodeValue(value);
        builder.append(String.join(", ", contents));
        builder.append("]");
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
