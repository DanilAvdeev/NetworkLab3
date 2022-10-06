public class InterestingObjects {
    int id;
    String xid;
    String name;
    String about;

    public InterestingObjects(int id, String xid){
        this.id = id;
        this.xid = xid;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setName(String name){this.name = name;}

    public void printObject(){
            System.out.println("---INFORMATION ABOUT OBJECT---");
            if (name == ""){ System.out.println("Name: no name"); }
            else { System.out.println("Name: " + name); }
            System.out.println("About: " + about +"\n");
    }

}
