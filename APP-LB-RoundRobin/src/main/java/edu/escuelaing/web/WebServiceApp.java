package edu.escuelaing.web;

import static spark.Spark.*;

import java.util.ArrayList;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import spark.Request;
import spark.Response;

public class WebServiceApp {

    private static ArrayList<String> loginServiceURIList;
    private static int currentServiceIndex = 0;

    public static void main(String... args){
        port(getPort());
        scanLoginServices();
        staticFiles.location("/");
        get("/", "text/html", ((req, res) -> { res.redirect("page.html"); return null; }));
        get("/consult", "application/json", ((req, res) -> {
            res.type("application/json");
            return getLogs();
        }));
        post("/", ((req, res) -> { postLog(req, res); res.redirect("page.html"); return null; } ));
    }

    public static void scanLoginServices(){
        loginServiceURIList = new ArrayList<>();
        loginServiceURIList.add("http://sv1");
        loginServiceURIList.add("http://sv2");
        loginServiceURIList.add("http://sv3");
    }

    public static String getLoginServiceURIByRoundRobin(){
        String currentServiceURI = loginServiceURIList.get(currentServiceIndex);
        currentServiceIndex = (currentServiceIndex + 1) % loginServiceURIList.size(); 
        return currentServiceURI;
    }

    public static JSONObject getLogs() throws IOException{
        URL url = new URL(getLoginServiceURIByRoundRobin());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET"); con.setConnectTimeout(5000); con.setReadTimeout(5000);
        JSONObject response = getJSON(con.getResponseCode(), con.getInputStream());
        con.disconnect();
        return response;
    }

    private static JSONObject getJSON(int responseCode, InputStream inputStream) throws IOException{
        JSONObject json;
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            StringBuffer response = new StringBuffer();
    
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            json = new JSONObject(response.toString());

        } else throw new IOException();
        return json;
    }

    public static void postLog(Request req, Response res) throws IOException{
        URL url = new URL(getLoginServiceURIByRoundRobin());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST"); con.setConnectTimeout(5000); con.setReadTimeout(5000);
        con.setRequestProperty( "Content-Type", "application/json"); 
        con.setRequestProperty( "charset", "utf-8");
        
        con.disconnect();
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
