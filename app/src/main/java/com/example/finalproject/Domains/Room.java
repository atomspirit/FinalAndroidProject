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

/**
 * A class to represent a single room in the database.
 */
public class Room {
    // Fields -------------------------------------------------------------------------------------
    private String code, name, description, URL, creationDate;
    private ArrayList<String> participants;
    private User host;

    // Constructors -------------------------------------------------------------------------------
    /**
     * Constructs a new Room object with the specified attributes.
     *
     * @param name         The name of the room.
     * @param code         The code of the room.
     * @param host         The host of the room.
     * @param description  The description of the room.
     * @param URL          The URL of the room's profile picture.
     * @param creationDate The creation date of the room.
     */
    public Room(String name, String code, User host, String description, String URL, String creationDate) {
        this.code = code;
        this.name = name;
        this.participants = new ArrayList<>();
        addParticipant(host);
        this.host = host;
        this.description = description;
        this.URL = URL;
        this.creationDate = creationDate;
    }

    /**
     * Constructs a new Room object with the specified attributes.
     *
     * @param name         The name of the room.
     * @param code         The code of the room.
     * @param host         The host of the room.
     * @param description  The description of the room.
     * @param participants The list of participants in the room.
     * @param URL          The URL of the room's profile picture.
     * @param creationDate The creation date of the room.
     */
    public Room(String name, String code, User host, String description, ArrayList<String> participants, String URL, String creationDate) {
        this.code = code;
        this.name = name;
        this.participants = participants;
        this.host = host;
        this.description = description;
        this.URL = URL;
        this.creationDate = creationDate;
    }

    // Getters and setters ------------------------------------------------------------------------
    /**
     * Returns the name of the room.
     *
     * @return The name of the room.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the room.
     *
     * @param name The new name of the room.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the room.
     *
     * @return The description of the room.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the room.
     *
     * @param description The new description of the room.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the code of the room.
     *
     * @return The code of the room.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code of the room.
     *
     * @param code The new code of the room.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Loads the participants of the room and invokes the callback for each participant loaded.
     *
     * @param callback The callback to be invoked for each participant loaded.
     */
    public void loadParticipants(OnLoadParticipants callback) {
        ArrayList<User> users = new ArrayList<>();
        if (participants.size() == 0) return;

        Log.d("ROOM", participants.toString());
        for (String username : participants) {
            User.createUserFromUsername(username, new User.UserCallback() {
                @Override
                public void onUserReceived(User user) {
                    users.add(user);
                    callback.onLoadedUser(users, user);
                }
            });
        }
    }

    /**
     * Sets the participants of the room.
     *
     * @param participants The new participants of the room.
     */
    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    /**
     * Returns the host of the room.
     *
     * @return The host of the room.
     */
    public User getHost() {
        return host;
    }

    /**
     * Sets the host of the room.
     *
     * @param host The new host of the room.
     */
    public void setHost(User host) {
        this.host = host;
    }

    /**
     * Returns the URL of the room's profile picture.
     *
     * @return The URL of the room's profile picture.
     */
    public String getURL() {
        return URL;
    }

    /**
     * Sets the URL of the room's profile picture.
     *
     * @param URL The new URL of the room's profile picture.
     */
    public void setURL(String URL) {
        this.URL = URL;
    }

    /**
     * Returns the creation date of the room.
     *
     * @return The creation date of the room.
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the room.
     *
     * @param creationDate The new creation date of the room.
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    // Methods ------------------------------------------------------------------------------------
    /**
     * Adds a participant to the room.
     *
     * @param user The user to add as a participant.
     */
    public void addParticipant(User user) {
        participants.add(user.getUsername());
    }

    /**
     * Updates the list of participants in the room in the database.
     */
    public void updateParticipants() {
        String current_room = getCode();
        DatabaseReference reference = FirebaseManager.getReference("rooms");

        Query query = reference.orderByChild("code").equalTo(current_room);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.child(current_room).getRef().child("participants").setValue(participants);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });
    }

    /**
     * Checks if the room contains the specified participant.
     *
     * @param user The user to check.
     * @return True if the room contains the participant, false otherwise.
     */
    public boolean containsParticipant(User user) {
        return participants.contains(user.getUsername());
    }

    /**
     * Retrieves the current room from SharedPreferences and invokes the callback with the room.
     *
     * @param context  The application context.
     * @param callback The callback to be invoked with the retrieved room.
     */
    static public void getCurrentRoom(Context context, RoomCallback callback) {
        String current_room = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE).getString("current_room", "");

        createRoomFromCode(current_room, new RoomCallback() {
            @Override
            public void onRoomReceived(Room room) {
                callback.onRoomReceived(room);
            }
        });
    }

    /**
     * Creates a Room object from the database using the room code.
     *
     * @param code     The code of the room to retrieve.
     * @param callback The callback to be invoked with the retrieved room.
     */
    public static void createRoomFromCode(String code, RoomCallback callback) {
        DatabaseReference reference = FirebaseManager.getReference("rooms");

        Query query = reference.orderByChild("code").equalTo(code);
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

    /**
     * Allows a user to leave the room.
     *
     * @param user The user to remove from the room.
     */
    public void leave(User user) {
        // Remove this room from user->rooms
        user.removeRoom(this);

        // Remove this user from room->participants
        participants.remove(user.getUsername());
        updateParticipants();

        if (participants.isEmpty())
            delete();
    }

    /**
     * Deletes the room from the database.
     */
    public void delete() {
        DatabaseReference reference = FirebaseManager.getReference("rooms");
        if (reference == null) return;
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
    /**
     * Deserializes a DataSnapshot into a Room object.
     *
     * @param snapshot The DataSnapshot to deserialize.
     * @return The deserialized Room object.
     */
    public static Room fromSnapshot(DataSnapshot snapshot) {
        String code = snapshot.child("code").getValue(String.class);
        String name = snapshot.child("name").getValue(String.class);
        String description = snapshot.child("description").getValue(String.class);
        User host = User.fromSnapshot(snapshot.child("host"));
        String URL = snapshot.child("url").getValue(String.class);
        String creationDate = snapshot.child("creation date").getValue(String.class);
        ArrayList<String> participants = new ArrayList<>();

        for (DataSnapshot child : snapshot.child("participants").getChildren()) {
            participants.add(child.getValue(String.class));
        }

        return new Room(name, code, host, description, participants, URL, creationDate);
    }

    // Conversion methods -------------------------------------------------------------------------
    /**
     * Returns a string representation of the room.
     *
     * @return A string representation of the room.
     */
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

    /**
     * Converts the room to a map representation.
     *
     * @return A map representation of the room.
     */
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
    /**
     * A callback interface to handle room retrieval operations.
     */
    public interface RoomCallback {
        void onRoomReceived(Room room);
    }

    /**
     * A callback interface to handle loading participants.
     */
    public interface OnLoadParticipants {
        void onLoadedUser(ArrayList<User> usersSoFar, User currentUser);
    }
}
