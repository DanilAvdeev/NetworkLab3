import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class Main {
    public static String url_1;
    public static String url_2;
    public static String url_3;

    public static String[] urls_4;
    public static String[] abouts;
    public static Location[] locations;
    public static InterestingObjects[] interestingObjects;

    public static void main(String[] args) {
        try {
            camapaletabalFucha();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String header(String str, String key, String value){
        str = str.concat(key + "=" + value);
        return str;
    }

    public static String addHeader(String str, String key, String value){
        str = str.concat("&" + key + "=" + value);
        return str;
    }

    public static String addPath(String str, String path){
        str = str.concat("/" + path + "?");
        return str;
    }

    public static void buildURL_1(String value){
        url_1 = "https://graphhopper.com";
        url_1 = addPath(url_1, "api/1/geocode");
        url_1 = header(url_1, "q", value);
        url_1 = addHeader(url_1, "key", "f0e9716f-e5c0-43a9-ba76-0688a22db8db");
    }

    public static void buildURL_2(String lat, String lng){
        //https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
        url_2 = "https://api.openweathermap.org";
        url_2 = addPath(url_2, "data/2.5/weather");
        url_2 = header(url_2, "lat", lat);
        url_2 = addHeader(url_2, "lon", lng);
        url_2 = addHeader(url_2, "appid", "1aca95b4093c53a125640b802c6d52d5");
        url_2 = addHeader(url_2, "units", "metric");
    }

    public static void buildURL_3(String lng, String lat){
        //https://api.opentripmap.com/0.1/en/places/radius?radius=200&lon=13.391&lat=52.518&apikey=5ae2e3f221c38a28845f05b64064217e622087d0601de80287c65722
        url_3 = "https://api.opentripmap.com";
        url_3 = addPath(url_3, "0.1/en/places/radius");
        url_3 = header(url_3, "radius", "200"); //настройка радиуса интересных мест
        url_3 = addHeader(url_3, "lon", lng);
        url_3 = addHeader(url_3, "lat", lat);
        url_3 = addHeader(url_3, "apikey", "5ae2e3f221c38a28845f05b64064217e622087d0601de80287c65722");
    }

    public static String buildURL_4(String xid){
        String url;
        url = "https://api.opentripmap.com";
        url = url.concat("/0.1/en/places/xid");
        url = url.concat("/" + xid);
        url = url.concat( "?apikey=5ae2e3f221c38a28845f05b64064217e622087d0601de80287c65722");
        return url;
    }

    //парсер списка локаций
    public static void parse_1(String responseBody){
        //JSONArray hits = new JSONArray(responseBody);
        JSONObject response = new JSONObject(responseBody);
        JSONArray hits = response.getJSONArray("hits");
        locations = new Location[hits.length()];
        for (int i = 0; i < hits.length(); i++){
            JSONObject jsonLocation = hits.getJSONObject(i);
            String name = jsonLocation.getString("name");
            String country = jsonLocation.getString("country");
            String osm_key = jsonLocation.getString("osm_key");
            String osm_value = jsonLocation.getString("osm_value");
            double lat = jsonLocation.getJSONObject("point").getDouble("lat");
            String latStr = Double.toString(lat);
            double lng = jsonLocation.getJSONObject("point").getDouble("lng");
            String lngStr = Double.toString(lng);
            locations[i] = new Location(i, latStr, lngStr, name, country, osm_key, osm_value);
        }
    }

    //парсер погоды
    public static void parse_2(String responseBody) {
        JSONObject response = new JSONObject(responseBody);
        String main = response.getJSONArray("weather")
                .getJSONObject(0).getString("main");
        String description = response.getJSONArray("weather")
                .getJSONObject(0).getString("description");
        double temp = response.getJSONObject("main").getDouble("temp");
        double windSpeed = response.getJSONObject("wind").getDouble("speed");
        System.out.println("__WEATHER FORECAST__\n" +
                           "Type: " + main + ", " + description + "\n" +
                           "Temperature is " + temp + " Celsius" + "\n" +
                           "Wind speed is " + windSpeed + " m/sec");
    }

    public static void parse_3(String responseBody){
        JSONArray features = new JSONObject(responseBody).getJSONArray("features");
        interestingObjects = new InterestingObjects[features.length()];
        urls_4 = new String[features.length()];
        for(int i = 0; i < features.length(); i++){
            JSONObject jsonLocation = features.getJSONObject(i);
            String xid = jsonLocation.getJSONObject("properties").getString("xid");
//            String name = jsonLocation.getJSONObject("properties").getString("name");

            interestingObjects[i] = new InterestingObjects(i, xid);
//            urls_4 = new String[features.length()];
            urls_4[i] = buildURL_4(xid);
//            System.out.println(urls_4[i]);
        }
    }

    public static void parse_4(int i, String responseBody) throws org.json.JSONException{
        String text;
        String name;
        try {
            text = new JSONObject(responseBody).getJSONObject("wikipedia_extracts").getString("text");
        } catch (JSONException e) {
            text = "no info";
        }
        try {
            name = new JSONObject(responseBody).getString("name");
        } catch (JSONException e){
            name = "no name";
        }
        interestingObjects[i].setAbout(text);
        interestingObjects[i].setName(name);
//        System.out.println(name);
    }

    public static void camapaletabalFucha() throws ExecutionException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
            System.out.println("Write location you want to know info about: ");
            InputStream inputStream = System.in;
            BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));
            String place = null;
            try {
                place = buf.readLine();
            } catch (IOException e) {
                System.out.println("Can't read");
            }
//            String place = "Berlin";//scanf
            buildURL_1(place);
            HttpRequest request_1 = HttpRequest.newBuilder()
                    .uri(URI.create(url_1))
                    .build();
            client.sendAsync(request_1, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(Main::parse_1)
                    .join();
            for (Location location : locations) {
                location.printLocation();
            }
//            int i = 0;//scanf
            int i = 0;
            System.out.println("\nWrite location number to know more info about it:");
            try {
                i = Integer.parseInt(buf.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i < 0 || i > locations.length-1) {
                System.out.println("You can't count, right? I'll give you info about 1st location");
                i = 1;
            }
            String lat = locations[i].getLat();
            String lng = locations[i].getLng();
            buildURL_2(lat, lng);
            buildURL_3(lng, lat);
            return null;
        }).thenApply(result->{
            HttpRequest request_2 = HttpRequest.newBuilder()
                    .uri(URI.create(url_2))
                    .build();
            client.sendAsync(request_2, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(Main::parse_2)
                    .join();
            return null;
        }).thenApplyAsync(result->{
            HttpRequest request_3 = HttpRequest.newBuilder()
                    .uri(URI.create(url_3))
                    .build();
            client.sendAsync(request_3, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(Main::parse_3)
                    .join();
            return null;
        }).thenApply(result->{
            abouts = new String[urls_4.length];
            for (int i = 0; i < urls_4.length; i++) {
                HttpRequest request_4 = HttpRequest.newBuilder()
                        .uri(URI.create(urls_4[i]))
                        .build();
                try {
                    HttpResponse<String> response = client.send(request_4, HttpResponse.BodyHandlers.ofString());
                    String responseString = response.body();
                    parse_4(i, responseString);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
        future.get();
        for (InterestingObjects interestingObject : interestingObjects) {
            interestingObject.printObject();
        }
    }
}
