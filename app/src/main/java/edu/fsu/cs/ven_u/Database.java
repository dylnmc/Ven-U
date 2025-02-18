package edu.fsu.cs.ven_u;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private final String EVENT_DB = "events";
    private final String USER_DB = "users";
    private final String TAG = "Database.java";
    public DatabaseReference eventDb;
    public DatabaseReference userDb;

    public Database(){
        //References for events and users database
        eventDb = FirebaseDatabase.getInstance().getReference(EVENT_DB);
        userDb = FirebaseDatabase.getInstance().getReference(USER_DB);
    }
    public void addEvent(final Event event){
        try{
            //Should check for proper start / end times here (or in create event code)

            //Search database by title first
            eventDb.orderByChild("title").equalTo(event.title)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //For all entries with given title
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                try {
                                    Event old_event = child.getValue(Event.class);
                                    //Check if existing entry is made by the same user and starts at
                                    //the same time. If so, do not insert into database
                                    if (event.creator.equals(old_event.creator) &&
                                            event.start_time.equals(old_event.start_time) &&
                                        event.latitude == old_event.latitude &&
                                        event.longitude == old_event.longitude) {
                                        Log.w(TAG, "addEvent: Event already exists");
                                        return;
                                    }
                                }
                                catch(Exception e){
                                    Log.e(TAG,"Exception in addEvent listener: "
                                            + e.getMessage());
                                }
                            }
                            //If entry does not already exist, create event with new key
                            String key = eventDb.push().getKey();
                            eventDb.child(key).setValue(event);
                            Log.v(TAG, "Event " + event.title + " added.");
                        }
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
        }
        catch(Exception e) {
            Log.e(TAG,"Error in addEvent: " + e.getMessage());
        }
    }

    public void removeEvent(final String title, final String creator){
        try{
            //Search database by title first
            eventDb.orderByChild("title").equalTo(title)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Event event = child.getValue(Event.class);
                        //If a database entry with a matching title and username
                        //is found, delete the entry
                        if(creator.equals(event.creator))
                            eventDb.child(child.getKey()).removeValue();
                    }
                }
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
        catch(Exception e){
            Log.e(TAG,"Error in removeEvent: " + e.getMessage());
        }
    }
    public void inviteUser(final String title, final String creator, final String username){
        try{
            //Search database by title first
            eventDb.orderByChild("title").equalTo(title)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                Event event = child.getValue(Event.class);
                                //If a database entry with a matching title and username
                                //is found, add user to invites(assume username is valid;
                                //check when used.
                                if(creator.equals(event.creator)) {
                                    StringBuilder s = new StringBuilder(event.invited);
                                    if(event.invited.length() == 0){
                                        s.append(username);
                                    }
                                    else{
                                        s.append(", ");
                                        s.append(username);
                                    }
                                    event.invited = s.toString();
                                    String key = child.getKey();
                                    eventDb.child(key).setValue(event);
                                }
                            }
                        }
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
        }
        catch(Exception e){
            Log.e(TAG,"Error in inviteUser: " + e.getMessage());
        }
    }
    public void addUser(final User user){
        try{

            //Search database by username (lowercase)
            userDb.orderByChild("username").equalTo(user.username.toLowerCase())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //If no entries found
                            if(dataSnapshot.getChildrenCount() == 0) {
                                String key = userDb.push().getKey();
                                userDb.child(key).setValue(user);
                                Log.v(TAG, "User " + user.username + " added.");
                            }
                        }
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
        }
        catch(Exception e) {
            Log.e(TAG,"Error in addUser: " + e.getMessage());
        }
    }

    public void removeUser(final String username){
        try{
            //Search database by username (lowercase)
            userDb.orderByChild("username").equalTo(username.toLowerCase())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                User user = child.getValue(User.class);
                                //If a database entry with a matching title and username
                                //is found, delete the entry
                                if(user.username.equals(username))
                                    userDb.child(child.getKey()).removeValue();
                            }
                        }
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
        }
        catch(Exception e){
            Log.e(TAG,"Error in removeEvent: " + e.getMessage());
        }
    }

    public static class Event {
        private String title;
        private String description;
        private String creator;
        private String start_time;
        private String end_time;
        private double latitude;
        private double longitude;
        private String visibility;
        private String invited;

        public Event() {
            this.title = null;
            this.description = null;
            this.creator = null;
            this.start_time = null;
            this.end_time = null;
            this.latitude = 0;
            this.longitude = 0;
            this.visibility = null;
            this.invited = "";
        }
        public Event(String title, String description, String creator, String start_time,
                     String end_time, double latitude, double longitude, String visibility){
            this.title = title;
            this.description = description;
            this.creator = creator;
            this.start_time = start_time;
            this.end_time = end_time;
            this.latitude = latitude;
            this.longitude = longitude;
            this.visibility = visibility;
            this.invited = "";
        }
        public Event(String title, String description, String creator,
                     double latitude, double longitude, String visibility){
            this.title = title;
            this.description = description;
            this.creator = creator;
            this.start_time = null;
            this.end_time = null;
            this.latitude = latitude;
            this.longitude = longitude;
            this.visibility = visibility;
            this.invited = "";
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getCreator() {
            return creator;
        }
        public void setCreator(String creator) {
            this.creator = creator;
        }
        public String getStartTime() {
            return start_time;
        }
        public void setStartTime(String start_time) {
            this.start_time = start_time;
        }
        public String getEndTime() {
            return end_time;
        }
        public void setEndTime(String end_time) {
            this.end_time = end_time;
        }
        public double getLatitude() {
            return latitude;
        }
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
        public double getLongitude() {
            return longitude;
        }
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
        public String getVisibility() {
            return visibility;
        }
        public void setVisibility(String visibility) {
            this.visibility = visibility;
        }
        public String getInvited(){
            return invited;
        }
        public void setInvited(String invited){
            this.invited = invited;
        }

        public String[] findInvitedUsers() {
            ArrayList<String> list = new ArrayList<>();
            int i = 0;
            while(invited.length() > i && i >= 0){
                if(invited.indexOf(",", i) >= 0) {
                    list.add(invited.substring(i, invited.indexOf(",", i)));
                }
                else {
                    list.add(invited.substring(invited.lastIndexOf(",") + 2));
                    break;
                }
                i = invited.indexOf(",", i) + 2;
            }

            return list.toArray(new String[0]);
        }

    }
    public static class User {
        private String name;
        private String username;
        private String bio;
        private String password;

        public User(){
            this.name = null;
            this.username = null;
            this. bio = null;
            this.password = null;
        }
        public User(String name, String username, String bio, String password){
            this.name = name;
            this.username = username;
            this. bio = bio;
            this.password = password;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getBio() {
            return bio;
        }
        public void setBio(String bio) {
            this.bio = bio;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
