package com.bnpp.dsibcef.apim.alias;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.dalesbred.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    private static void createTables(Database database) throws Exception {

        var path = Paths.get(Main.class.getResource("/schema.sql").toURI());
        database.update(Files.readString(path));
    }
}