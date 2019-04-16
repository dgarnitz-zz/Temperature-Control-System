package database;

import com.google.gson.Gson;

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
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;

import org.bson.Document;

public class MongoDbClient {

    MongoClient client = new MongoClient(new MongoClientURI("mongodb://tcs-admin:password1@ds231956.mlab.com:31956/heroku_z0fxw42s"));
    MongoDatabase db = client.getDatabase("heroku_z0fxw42s");

    MongoCollection<Document> test = db.getCollection("test");
    //MongoCollection<Document> readings = db.getCollection("readings");

    /**
     * Save the object into the database.
     * @param update - UpdateObject to be saved.
     * @throws Exception - If it fails to save the object to the database.
     */
    public void save(UpdateObject update) throws MongoException {
//        Gson gson = new Gson();
//        String json = gson.toJson(update);
//        test.insertOne(Document.parse(json));
    }

    public void queryAll() {
//      System.out.println("Query:");
//        MongoCursor<Document> cursor = test.find().iterator();
//        try {
//            while (cursor.hasNext()) {
//                System.out.println(cursor.next().toJson());
//            }
//        } finally {
//            cursor.close();
//        }
    }
}
