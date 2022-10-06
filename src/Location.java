public class Location {
    int id; //будет равен номеру индекса в массиве
    String lat;
    String lng;
    String name;
    String country;
    String osm_key;
    String osm_value;

    public Location(int id, String la, String ln, String name, String c, String key, String val){
        this.id = id;
        this.lat = la;
        this.lng = ln;
        this.name = name;
        this.country = c;
        this.osm_key = key;
        this.osm_value = val;
    }

    public void printLocation(){
        System.out.println("------------------------------------");
        System.out.println("Location " + id);
        System.out.println("Name: " + name);
        System.out.println("Country: " + country);
        System.out.println("Type:" + osm_key);
        System.out.println("About:" + osm_value);
    }

    public String getLat(){ return lat; }

    public String getLng(){ return lng; }
}
