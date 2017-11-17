package com.t2f.train2fit;

import java.util.Map;

/**
 * Created by chira on 15-11-2017.
 */

public class User {
    String userId;
    String display_name;
    String first_name;
    String last_name;
    String dob;
    Map<String, String> address;
    public User(String userId, String display_name, String first_name, String last_name, String dob, Map<String, String> address){
        this.address = address;
        this.userId = userId;
        this.display_name = display_name;
        this.first_name = first_name;
        this.last_name = last_name;
        this.dob = dob;
    }
}
