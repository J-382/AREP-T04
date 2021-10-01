package edu.escuelaing.web;

import static spark.Spark.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;
import org.eclipse.jetty.http.HttpStatus;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class LogServiceApp {

    private static final String COLLECTION_NAME = System.getProperty("COLLECTION_NAME");
    private static final String DATABASE_URI = System.getProperty("DATABASE_URI");
    private static final String DATABASE_NAME = System.getProperty("DATABASE_NAME");
    private static final int DATABASE_PORT = Integer.parseInt(System.getProperty("DATABASE_PORT"));

    /*private static final String COLLECTION_NAME = "logs";
    private static final String DATABASE_URI = "localhost";
    private static final String DATABASE_NAME = "app";
    private static final int DATABASE_PORT = 27017;*/

    public static void main(String... args){
        port(getPort());
        staticFiles.location("/");
        get("/", "application/json", ((req, res) -> {
            res.type("application/json");
            return getLogsFromDatabase();
        }));
        post("/", "application/json", ((req, res) -> {
            String message = req.queryParams("message");
            postLogInDatabase(message); 
            return HttpStatus.OK_200;    
        }));
    }

    public static JSONObject getLogsFromDatabase(){
        /*Database connection*/
        MongoClient mongoClient = new MongoClient(DATABASE_URI,DATABASE_PORT);
        MongoDatabase db = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> logsCollection = db.getCollection(COLLECTION_NAME);

        JSONObject response = new JSONObject();
        List<Document> logs = logsCollection.find().into(new ArrayList<>());
        int count = 0;
        for(Document log: logs){
            if( count >= 10) break;
            response.put(""+count, new JSONObject(log.toJson()));
            count++;
        }
        response = new JSONObject().put("Logs", response);
        mongoClient.close();
        return response;
    }

    public static void postLogInDatabase(String message){
        /*Database connection*/
        MongoClient mongoClient = new MongoClient(DATABASE_URI,DATABASE_PORT);
        MongoDatabase db = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> logsCollection = db.getCollection(COLLECTION_NAME);

        /*Date creation*/
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        Date date = new Date();

        /*Document for database*/
        Document document = new Document();
        document.append("message", message); document.append("date", formatter.format(date));
        logsCollection.insertOne(document);
        mongoClient.close();
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
