package main.java.utils;

import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class DBSettings {

    public static final String HOST_NAME = "localhost";
    public static final Integer PORT = 27017;
    public static MongoClient mongoClient = null;
    public static DB mongoDB = null;
    public static DBCollection mongoCollection = null;
}
