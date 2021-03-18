package net.app.civilix;

/**
 * Created by Belal on 9/30/2017.
 */

public class Project {
    int id;
    String name, joiningDate;

    public Project(int id, String name,String joiningDate) {
        this.id = id;
        this.name = name;

        this.joiningDate = joiningDate;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

   // public String getType() {
     //   return type;
   // }

    public String getJoiningDate() {
        return joiningDate;
    }

//    public double getEstimate() {
//        return estimate;
//    }
}
