/*
 * 
 * Copyright 2017 | Sam Jakob Mearns (c) All Rights Reserved
 * 
 */

package com.cloutteam.jarcraftinator.config;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author samuel.mearns
 */
public class FileConfiguration {
    
    private final String filename;
    private Map map;
    
    /**
     * A custom class to assist in using YAML configuration files.
     * @param filename The filename of the YAML file.
     */
    public FileConfiguration(String filename){
        this.filename = filename;
    }
    
    /* FILE OPERATIONS */
    
    /**
     * Saves the default configuration (specified filename) in the folder with the jar.
     * You must provide this function with your default configuration as an InputStream.
     * Example: <pre>saveDefaultConfig(getClass().getClassLoader().getResourceAsStream("config.yml"));</pre>
     * This example will save a default configuration called config.yml to the folder containing the JAR.
     * @param internalFile The default configuration as an InputStream.
     * @throws java.io.IOException When the file couldn't be copied
    */
    public void saveDefaultConfig(InputStream internalFile) throws IOException {
        File file = new File(filename);
        if(!file.exists()){
            Files.copy(internalFile, file.getAbsoluteFile().toPath());
        }
        
        loadConfig();
    }
    
    /**
     * Loads the configuration from the specified filename.
     * @throws FileNotFoundException 
     */
    public void loadConfig() throws FileNotFoundException {
        File config = new File(filename);
        if(!config.exists()){
            throw new FileNotFoundException();
        }else{
            try {
                YamlReader reader = new YamlReader(new FileReader(filename));
                Object object = reader.read();
                Map data = (Map) object;
                this.map = data;
            }catch(YamlException ex){
                System.out.println("Unable to read config: " + filename);
                System.exit(1);
            }
        }
    }
    
    /**
     * Gets the name of the loaded configuration file.
     * @return The name of the file
     */
    public String getFilename(){
        return filename;
    }
    
    
    /* GETTERS */
    
    /**
     * Gets a value from the configuration as a string.
     * @param path The path as a string. (Examples: foo, foo.bar)
     * @return The value of the path
     */
    public String getString(String path){
        return (String) resolvePath(path);
    }
    
    /**
     * Gets a value from the configuration as an int.
     * @param path The path as a String. (Examples: foo, foo.bar)
     * @return The value of the path
     */
    public int getInt(String path){
        return Integer.parseInt((String) resolvePath(path));
    }
    
    /**
     * Gets a string list from the configuration as an ArrayList
     * @param path The path as a String. (Examples: foo, foo.bar)
     * @return The list as an ArrayList&lt;String&gt;
     */
    public List<String> getStringList(String path){
        return (ArrayList<String>) resolvePath(path);
    }
    
    /**
     * Gets all 'sub-paths' (children) of a path.
     * @param path The path as a String. (Examples: foo, foo.bar)
     * @return A list of children as a Set&lt;String&gt;
     */
    public Set<String> getChildren(String path){
        if(path.contains(".")){
            String[] segments = path.split("\\.");
            Map m = (Map) map.get(segments[0]);
            
            for(int i = 1; i < segments.length; i++){
                String segment = segments[i];
                m = (Map) m.get(segment);
            }
            
            return m.keySet();
        }else{
            Map m = (Map) map.get(path);
            return m.keySet();
        }
    }
    
    /* PRIVATE FUNCTIONS */
    private Object resolvePath(String path){
        if(path.contains(".")){
            String[] segments = path.split("\\.");
            Map m = null;
            
            for(int i = 0; i < segments.length - 1; i++){
                String segment = segments[i];
                m = (Map) map.get(segment);
            }
            
            if(m != null){
                return m.get(segments[segments.length - 1]);
            }else{
                return null;
            }
        }else{
            return map.get(path);
        }
    }
    
}
