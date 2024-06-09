package com.example.finalproject.Domains;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Room {
    // Fields -------------------------------------------------------------------------------------
    private String code, name, description, URL, creationDate;
    private ArrayList<String> participants;
    private User host;

    // Constructors -------------------------------------------------------------------------------
    public Room(String name, String code, User host, String description,String URL,
                String creationDate) {
        this.code = code;
        this.name = name;
        this.participants = new ArrayList<>();
        addParticipant(host);
        this.host = host;
        this.description = description;
        this.URL = URL;
        this.creationDate = creationDate;
    }
    public Room(String name, String code, User host,String description,
                ArrayList<String> participants, String URL, String creationDate) {
        this.code = code;
        this.name = name;
        this.participants = participants;
        this.host = host;
        this.description = description;
        this.URL = URL;
        this.creationDate = creationDate;
    }

    // Getters and setters ------------------------------------------------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void loadParticipants(OnLoadParticipants callback) {
        ArrayList<User> users = new ArrayList<>();
        if(participants.size() == 0) return;

        Log.d("ROOM", participants.toString());
        for (String username : participants)
        {

            User.createUserFromUsername(username, new User.UserCallback() {
                @Override
                public void onUserReceived(User user) {
                    users.add(user);
                    callback.onLoadedUser(users, user);
                }
            });
        }
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    // Methods ------------------------------------------------------------------------------------
    public void addParticipant(User user){
        participants.add(user.getUsername());
    }

    public void updateParticipants(){
        String current_room = getCode();
        DatabaseReference reference = FirebaseManager.getReference("rooms");


        Query query=reference.orderByChild("code").equalTo(current_room);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.child(current_room).getRef().child("participants").setValue(participants);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public boolean containsParticipant(User user){
        return participants.contains(user.getUsername());
    }

    static public void getCurrentRoom(Context context, RoomCallback callback) {
        String current_room = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE).getString("current_room", "");

        createRoomFromCode(current_room, new RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                callback.onRoomReceived(room);
            }
        });
    }
    public static void createRoomFromCode(String code, RoomCallback callback) {
        DatabaseReference reference = FirebaseManager.getReference("rooms");


        Query query=reference.orderByChild("code").equalTo(code);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Room room = Room.fromSnapshot(snapshot.child(code));
                    callback.onRoomReceived(room);
                } else {
                    callback.onRoomReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onRoomReceived(null);
            }
        });
    }

    public void leave(User user){

        // Remove this room from user->rooms
        user.removeRoom(this);


        // Remove this user from room->participants

        participants.remove(user.getUsername());
        updateParticipants();

        if(participants.isEmpty())
            delete();

    }
    public void delete()
    {
        DatabaseReference reference = FirebaseManager.getReference("rooms");
        if(reference == null) return;
        reference.child(code).removeValue();

        Room self = this;
        loadParticipants(new OnLoadParticipants() {
            @Override
            public void onLoadedUser(ArrayList<User> usersSoFar, User currentUser) {
                currentUser.removeRoom(self);
            }
        });
    }


    // Custom deserialization method
    public static Room fromSnapshot(DataSnapshot snapshot) {
        String code = snapshot.child("code").getValue(String.class);
        String name = snapshot.child("name").getValue(String.class);
        String description = snapshot.child("description").getValue(String.class);
        User host = User.fromSnapshot(snapshot.child("host"));
        String URL = snapshot.child("url").getValue(String.class);;
        String creationDate = snapshot.child("creation date").getValue(String.class);
        ArrayList<String> participants = new ArrayList<>();

        for (DataSnapshot child : snapshot.child("participants").getChildren()) {
            participants.add(child.getValue(String.class));
        }


        Room room = new Room(name,code, host,description,participants,URL,creationDate);

        return room;
    }

    // Conversion methods -------------------------------------------------------------------------
    @NonNull
    @Override
    public String toString() {
        return "Room{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", participants=" + participants +
                ", host=" + host +
                ", url=" + getURL() +
                ", creation date=" + getCreationDate() +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("name", name);
        result.put("participants", participants);
        result.put("host", host.toMap());
        result.put("description", description);
        result.put("url", getURL());
        result.put("creation date", getCreationDate());
        return result;
    }

    // Interfaces ---------------------------------------------------------------------------------
    public interface RoomCallback {
        void onRoomReceived(Room room);
    }

    public interface OnLoadParticipants {
        void onLoadedUser(ArrayList<User> usersSoFar, User currentUser);
    }
}
