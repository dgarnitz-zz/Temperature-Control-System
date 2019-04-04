package database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import org.bson.Document;

public class MongoDbClient {

    MongoClient client = new MongoClient();
    MongoDatabase db = client.getDatabase("tcs");

    MongoCollection<Document> labs = db.getCollection("labs");
    MongoCollection<Document> readings = db.getCollection("readings");

    /**
     * Save the object into the database.
     * @param o - Object to be saved.
     * @throws Exception - If it fails to save the object to the database.
     */
    public void save(Object o) throws Exception {
        //TODO save object into the database
    }

    public void queryAll() {
      System.out.println("Query:");
      System.out.println(labs.find().first());
    }
}
