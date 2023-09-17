package com.h1r0sh1m4.radio.database;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.SqliteDatabaseType;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.plugin.java.JavaPlugin;

public class SQLiteDriver{

    public ConnectionSource getConnection(JavaPlugin plugin) {
        DatabaseType databaseType = new SqliteDatabaseType();
        databaseType.loadDriver();
        File pluginFolder = plugin.getDataFolder();
        createFolderIfNotExists(pluginFolder);
        try {
            String URL = "jdbc:sqlite:"+ pluginFolder + File.separator + "radio.db";

            return new JdbcConnectionSource(URL, databaseType);
        } catch (SQLException e) {
            System.err.println("Failed to establish connection to SQLite database");
            return null;
        }
    }

    private void createFolderIfNotExists(File pluginFolder) {
        if(!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
    }

}