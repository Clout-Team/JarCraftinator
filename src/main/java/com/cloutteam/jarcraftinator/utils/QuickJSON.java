package com.cloutteam.jarcraftinator.utils;

import java.util.HashMap;
import java.util.Map;

public class QuickJSON {

    public static Map<String, Object> getVersionMap(String name, int protocol){
        Map<String, Object> version = new HashMap<>();
        version.put("name", name);
        version.put("protocol", protocol);
        return version;
    }

    public static Map<String, Object> description(String text){
        Map<String, Object> description = new HashMap<>();
        description.put("text", text);
        return description;
    }

    public static Map<String, Object> players(int online, int max){
        Map<String, Object> description = new HashMap<>();
        description.put("online", online);
        description.put("max", max);
        return description;
    }

}
