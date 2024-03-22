package com.example.finalproject.Domains;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Room {
    // Fields -------------------------------------------------------------------------------------
    private String code, name, description;
    private ArrayList<User> participants;
    private User host;

    private static final String DEFAULT_DESCRIPTION = "default description";


    // Constructors -------------------------------------------------------------------------------
    public Room(String name, String code, User host, String description) {
        this.code = code;
        this.name = name;
        this.participants = new ArrayList<>();
        addParticipant(host);
        this.host = host;
        this.description = description;
    }
    public Room(String name, String code, User host,String description, ArrayList<User> participants) {
        this.code = code;
        this.name = name;
        this.participants = participants;
        this.host = host;
        this.description = description;
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

    public ArrayList<User> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<User> participants) {
        this.participants = participants;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    // Methods ------------------------------------------------------------------------------------
    public void addParticipant(User user){
        participants.add(user);
    }

    public void updateParticipants(Context context){
        String current_room = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE).getString("current_room", "");

        DatabaseReference reference;
        try {
            reference = FirebaseDatabase.getInstance("https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("rooms");
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }

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
        return participants.contains(user);
    }

    static public void getCurrentRoom(Context context, RoomCallback callback) {
        String current_room = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE).getString("current_room", "");

        DatabaseReference reference;
        try {
            reference = FirebaseDatabase.getInstance("https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("rooms");
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }

        Query query=reference.orderByChild("code").equalTo(current_room);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Room room = Room.fromSnapshot(snapshot.child(current_room));
                    Log.d("TAG", "received room: " + room);
                    callback.onRoomReceived(room);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onRoomReceived(null);
            }
        });
    }
    public static void createRoomFromCode(String code, RoomCallback callback) {
        DatabaseReference reference;
        try {
            reference = FirebaseDatabase.getInstance("https://finalandroidproject-759f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference("rooms");
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }

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

    // Custom deserialization method
    public static Room fromSnapshot(DataSnapshot snapshot) {
        String code = snapshot.child("code").getValue(String.class);
        String name = snapshot.child("name").getValue(String.class);
        String description = snapshot.child("description").getValue(String.class);
        User host = User.fromSnapshot(snapshot.child("host"));
        ArrayList<User> participants = new ArrayList<>();

        for (DataSnapshot child : snapshot.child("participants").getChildren()) {
            participants.add(User.fromSnapshot(child));
        }


        Room room = new Room(name,code, host,description,participants);

        return room;
    }

    // Conversion methods -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Room{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", participants=" + participants +
                ", host=" + host +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("name", name);
        result.put("participants", participants);
        result.put("host", host.toMap());
        result.put("description", description);
        return result;
    }

    // Interfaces ---------------------------------------------------------------------------------
    public interface RoomCallback {
        void onRoomReceived(Room room);
    }
}
