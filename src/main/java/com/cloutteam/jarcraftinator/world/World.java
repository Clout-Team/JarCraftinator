package com.cloutteam.jarcraftinator.world;

import com.cloutteam.jarcraftinator.JARCraftinator;

import java.io.File;
import java.sql.*;

public class World {
    private WorldType type;
    private String fname;
    private String name;
    private Connection dbconn = null;

    public World(String fname) {
        this.fname = fname;
        this.connectDb();
        try {
            ResultSet rs = this.queryDb("SELECT name, type FROM meta");
            this.name = rs.getString("Name");
            this.type = WorldType.fromInt(rs.getInt("Type"));
            System.out.println("World " + this.name + " loaded! type: " + this.type);
        } catch (SQLException e) {
            JARCraftinator.err(fname + ".world is not a valid worldfile!");
        }
    }

    public World(String name, WorldType type, Connection dbconn){
        this.type = type;
        this.name = name;
        this.dbconn = dbconn;
    }

    private ResultSet queryDb(String statementString) throws SQLException {
        Statement statement = this.dbconn.createStatement();
        return statement.executeQuery(statementString);
    }

    private void connectDb() {
        Connection connection = null;
        String dbPath = "worlds/" + this.fname + ".world";
        File f = new File(dbPath);
        if (!f.exists()) {
            JARCraftinator.err("World file " + dbPath + " does not exist!");
            System.exit(1);
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            this.dbconn = connection;
        } catch (SQLException e) {
            JARCraftinator.err("World file does not exist!");
        }
    }

    public static World newWorld(String fname, String name, WorldType type) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + fname + ".world");
            Statement stmt = connection.createStatement();
            stmt.setQueryTimeout(30);
            stmt.executeUpdate("CREATE TABLE meta (Name CHAR(32), Type INT)");
            stmt.executeUpdate("CREATE TABLE chunks (X INT, Y INT, Z INT, data BLOB)");
            stmt.executeUpdate("CREATE TABLE entities(id INT)");

            //insert meta
            PreparedStatement pStmt = connection.prepareStatement("INSERT INTO meta (name, type) VALUES (?, ?)");
            pStmt.setString(1, name);
            pStmt.setInt(2, type.getId());
            pStmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e);
        }

        World world = new World(name, type, connection);
        return world;
    }

    public Chunk getChunk(int X, int Y)  {
        return null;
    }

    public WorldType getType() {
        return this.type;
    }

    public String getName() {
        return name;
    }

}
