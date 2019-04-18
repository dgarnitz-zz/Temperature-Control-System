package database;

import java.util.*;

import com.google.gson.Gson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.mongodb.MongoException;
import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.MongoCursor;

import org.bson.Document;

public class MongoDbClient {

    public static final String DB_URL = "mongodb://tcs-admin:password1@ds231956.mlab.com:31956/heroku_z0fxw42s";
    public static final String DB_NAME = "heroku_z0fxw42s";

    private MongoClient client;
    private MongoDatabase db;

    private MongoCollection<Document> readings;

    public MongoDbClient() {
        client = new MongoClient(new MongoClientURI(DB_URL));
        db = client.getDatabase(DB_NAME);

        readings = db.getCollection("readings");
    }

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

    /**
     * This method fetches the newest temperature reading stored in the database
     * @return An UpdateObject containing the latest temperature reading stored in the DB
     */
    public UpdateObject fetchLatestUpdate() {
        Document myDoc = readings.find().sort(new Document("_id", -1)).first();
        System.out.println(myDoc);

        Gson gson = new Gson();
        UpdateObject update = gson.fromJson(myDoc.toJson(), UpdateObject.class);

        return update;
    }

    /**
     * This method fetches the entire history of a given room/lab
     * @param roomID the id of the room/lab
     * @return an arraylist of UpdateObjects containing the temperature data
     */
    public ArrayList<UpdateObject> queryHistory(int roomID) {
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

    /**
     * This method returns a list of all labs/rooms that have readings stored in the DB
     * @return an ArrayList of integers containing lab/room IDs
     */
    public ArrayList<Integer> queryRooms() {
        System.out.println("query rooms");
        ArrayList<Integer> rooms = new ArrayList<>();
        Gson gson = new Gson();

        MongoCursor<Integer> cursor = null;
        try {
            cursor = readings.distinct("lab", Integer.class).iterator();

            System.out.println(cursor.hasNext());
            while (cursor.hasNext()) {
                rooms.add(cursor.next());
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return rooms;
    }
}
