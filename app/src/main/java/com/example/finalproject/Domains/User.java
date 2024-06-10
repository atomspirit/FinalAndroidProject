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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * A class to represent a single user in the database.
 */
public class User {

    // Fields -------------------------------------------------------------------------------------
    private String username;
    private String password;
    private String email;
    private String bio;
    private String URL;

    // Constructors -------------------------------------------------------------------------------
    /**
     * Constructs a new User object with the specified attributes.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param email    The email address of the user.
     * @param bio      The biography of the user.
     * @param URL      The URL of the user's profile picture.
     */
    public User(String username, String password, String email, String bio, String URL) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.bio = bio;
        this.URL = URL;
    }

    // Methods ------------------------------------------------------------------------------------

    /**
     * Retrieves the current user from SharedPreferences and invokes the callback with the user.
     *
     * @param context  The application context.
     * @param callback The callback to be invoked with the retrieved user.
     */
    static public void getCurrentUser(Context context, UserCallback callback) {
        String current_username = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE).getString("current_username", "");
        createUserFromUsername(current_username, new UserCallback() {
            @Override
            public void onUserReceived(User user) {
                callback.onUserReceived(user);
            }
        });
    }

    /**
     * Deserializes a DataSnapshot into a User object.
     *
     * @param snapshot The DataSnapshot to deserialize.
     * @return The User object created from the DataSnapshot.
     */
    public static User fromSnapshot(DataSnapshot snapshot) {
        String username = snapshot.child("username").getValue(String.class);
        String password = snapshot.child("password").getValue(String.class);
        String email = snapshot.child("email").getValue(String.class);
        String bio = snapshot.child("bio").getValue(String.class);
        String URL = snapshot.child("url").getValue(String.class);

        return new User(username, password, email, bio, URL);
    }

    /**
     * Adds a room to the user's list of rooms in the database.
     *
     * @param username The username of the user.
     * @param roomCode The code of the room to add.
     */
    public static void addToRoom(String username, String roomCode) {
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
                    if (!rooms.contains(roomCode))
                        rooms.add(roomCode);
                } else {
                    rooms.add(roomCode);
                }
                reference.child(username).child("rooms").setValue(rooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

    /**
     * Removes a room from the user's list of rooms in the database.
     *
     * @param room The room to remove.
     */
    public void removeRoom(Room room) {
        DatabaseReference reference = FirebaseManager.getReference("users");
        if (reference == null)
            return;

        reference.child(this.getUsername()).child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getValue(String.class).equals(room.getCode())) {
                        child.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });
    }

    /**
     * Creates a User object from the database using the username.
     *
     * @param username The username of the user to retrieve.
     * @param callback The callback to be invoked with the retrieved user.
     */
    public static void createUserFromUsername(String username, UserCallback callback) {
        DatabaseReference reference = FirebaseManager.getReference("users");
        if (reference == null) {
            callback.onUserReceived(null);
            return;
        }

        Query query = reference.orderByChild("username").equalTo(username);
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

    /**
     * Returns the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The new username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The new password of the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the email address of the user.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The new email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the biography of the user.
     *
     * @return The biography of the user.
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets the biography of the user.
     *
     * @param bio The new biography of the user.
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Returns the URL of the user's profile picture.
     *
     * @return The URL of the user's profile picture.
     */
    public String getURL() {
        return URL;
    }

    /**
     * Sets the URL of the user's profile picture.
     *
     * @param URL The new URL of the user's profile picture.
     */
    public void setURL(String URL) {
        this.URL = URL;
    }

    // Conversion methods -------------------------------------------------------------------------

    /**
     * Returns a string representation of the user.
     *
     * @return A string representation of the user.
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", url='" + URL + '\'' +
                '}';
    }

    /**
     * Returns the hash code of the user based on the username.
     *
     * @return The hash code of the user.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username); // Use a unique attribute for hashing
    }

    /**
     * Compares this user to the specified object. The result is true if and only if the argument is not null and is a User object that has the same username as this object.
     *
     * @param obj The object to compare this user against.
     * @return true if the given object represents a User equivalent to this user, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User otherUser = (User) obj;
        return Objects.equals(username, otherUser.username); // Compare unique attributes
    }

    /**
     * Converts the user object to a map.
     *
     * @return A map representing the user object.
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("password", password);
        result.put("email", email);
        result.put("bio", bio);
        result.put("url", URL);
        return result;
    }

    // Interfaces ---------------------------------------------------------------------------------

    /**
     * A callback interface to handle user retrieval operations.
     */
    public interface UserCallback {
        void onUserReceived(User user);
    }
}
