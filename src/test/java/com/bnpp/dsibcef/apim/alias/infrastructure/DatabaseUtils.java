package com.bnpp.dsibcef.apim.alias.infrastructure;

import com.bnpp.dsibcef.apim.alias.Main;
import org.dalesbred.Database;
import org.h2.jdbcx.JdbcConnectionPool;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseUtils {

    public static void createDatabaseSchema() {
        DataSource adminDataSource= JdbcConnectionPool.create("jdbc:h2:mem:alias", "admin", "adminpwd");
        Database database=Database.forDataSource(adminDataSource);
        createTables(database);
    }

    public static void dropDatabaseSchema() {
        DataSource adminDataSource= JdbcConnectionPool.create("jdbc:h2:mem:alias", "admin", "adminpwd");
        Database database=Database.forDataSource(adminDataSource);
        dropTables(database);
    }

    public static void resetTableAlias(DataSource dataSource) {
        try(Connection conn=dataSource.getConnection()) {
            Statement st=conn.createStatement();
            st.executeUpdate("truncate table alias_partner");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTables(Database database)  {
        try {
            Path path = Paths.get(Main.class.getResource("/schema.sql").toURI());
            database.update(Files.readString(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void dropTables(Database database)  {
        try {
            Path path = Paths.get(Main.class.getResource("/drop_schema.sql").toURI());
            database.update(Files.readString(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}
