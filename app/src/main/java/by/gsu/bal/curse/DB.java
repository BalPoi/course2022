package by.gsu.bal.curse;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DB {
    static public FirebaseDatabase database;
    static public DatabaseReference postsRef;
    static public DatabaseReference usersRef;

    static public FirebaseStorage imgStorage;
    static public StorageReference imgRef;

    static {
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        postsRef = database.getReference(Constants.POSTS_KEY);
        usersRef = database.getReference(Constants.USERS_KEY);

        imgStorage = FirebaseStorage.getInstance();
        imgRef = imgStorage.getReference();
    }
}
