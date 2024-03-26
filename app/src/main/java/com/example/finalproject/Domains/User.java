package com.example.finalproject.Domains;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * A class to represent a single user in the database
 */
public class User {

    // Fields -------------------------------------------------------------------------------------
    private String username;
    private String password;
    private String email;


    // Constructors -------------------------------------------------------------------------------
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }


    // Methods ------------------------------------------------------------------------------------
    static public void getCurrentUser(Context context, UserCallback callback){
        String current_username = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE).getString("current_username", "");
        DatabaseReference reference = FirebaseManager.getReference("users");
        if(reference == null){
            callback.onUserReceived(null);
            return;
        }

        Query query = reference.orderByChild("username").equalTo(current_username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = User.fromSnapshot(snapshot.child(current_username));
                    Log.d("TAG", "received user: " + user);
                    callback.onUserReceived(user);
                } else {
                    callback.onUserReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onUserReceived(null);
            }
        });
    }

    // Custom deserialization method
    public static User fromSnapshot(DataSnapshot snapshot) {
        String username = snapshot.child("username").getValue(String.class);
        String password = snapshot.child("password").getValue(String.class);
        String email = snapshot.child("email").getValue(String.class);


        return new User(username,password, email);
    }

    public static void addToRoom(String username, String roomCode){
        DatabaseReference reference = FirebaseManager.getReference("users");
        if (reference == null)
            return;

        reference.child(username).child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> rooms = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String roomName = snapshot.getValue(String.class);
                        rooms.add(roomName);
                    }
                    if(!rooms.contains(roomCode))
                        rooms.add(roomCode);

                } else {
                    // rooms list doesn't exist, create a new one with current_room
                    rooms.add(roomCode);
                    reference.child(username).child("rooms").setValue(rooms);
                }
                reference.child(username).child("rooms").setValue(rooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
    public void removeRoom(Room room)
    {
        // Remove this room from user->rooms

        DatabaseReference reference = FirebaseManager.getReference("users");
        if(reference == null)
            return;

        reference.child(this.getUsername()).child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    if(child.getValue(String.class).equals(room.getCode())){
                        child.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void createUserFromUsername(String username, UserCallback callback) {
        DatabaseReference reference = FirebaseManager.getReference("users");
        if (reference == null) return;

        Query query=reference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = User.fromSnapshot(snapshot.child(username));
                    callback.onUserReceived(user);
                } else {
                    callback.onUserReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onUserReceived(null);
            }
        });
    }


    // Getters and setters ------------------------------------------------------------------------
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    // Conversion methods -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    @Override
    public int hashCode() {
        return Objects.hash(username); // Use a unique attribute for hashing
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User otherUser = (User) obj;
        return Objects.equals(username, otherUser.username); // Compare unique attributes
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("password", password);
        result.put("email", email);
        return result;
    }


    // Interfaces ---------------------------------------------------------------------------------
    public interface UserCallback {
        void onUserReceived(User user);
    }
}
