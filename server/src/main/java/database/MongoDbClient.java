package database;

import java.util.*;

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
import com.mongodb.Block;
import com.mongodb.client.model.Filters;

import org.bson.Document;

public class MongoDbClient {

    MongoClient client = new MongoClient(new MongoClientURI("mongodb://tcs-admin:password1@ds231956.mlab.com:31956/heroku_z0fxw42s"));
    MongoDatabase db = client.getDatabase("heroku_z0fxw42s");

    //MongoCollection<Document> test = db.getCollection("test");
    MongoCollection<Document> readings = db.getCollection("readings");

    /**
     * Save the object into the database.
     * @param update - UpdateObject to be saved.
     * @throws Exception - If it fails to save the object to the database.
     */
    public void save(UpdateObject update) throws MongoException {
        Gson gson = new Gson();
        String json = gson.toJson(update);
        readings.insertOne(Document.parse(json));
    }

    public UpdateObject fetchLatestUpdate() {
        //request last inserted doc
        Document myDoc = readings.find().sort(new Document("_id", -1)).first();
        System.out.println(myDoc);

        Gson gson = new Gson();
        UpdateObject update = gson.fromJson(myDoc.toJson(), UpdateObject.class);

        return update;
    }

    public ArrayList<UpdateObject> queryHistory(int roomID) {
      System.out.println("Query:");

      ArrayList<UpdateObject> history = new ArrayList<>();
      Gson gson = new Gson();
      Block<Document> printBlock = new Block<Document>() {
          @Override
          public void apply(final Document document) {
              System.out.println(document.toJson());
              UpdateObject update = gson.fromJson(document.toJson(), UpdateObject.class);
              history.add(update);
          }
      };

        readings.find(Filters.eq("lab", roomID)).forEach(printBlock);

        return history;
    }
}
