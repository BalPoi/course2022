package by.gsu.bal.curse;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class DB {
    public static final FirebaseDatabase database;
    public static final DatabaseReference postsRef;
    public static final DatabaseReference usersRef;

    public static final FirebaseStorage imgStorage;
    public static final StorageReference imgRef;

    static {
        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        postsRef = database.getReference(Constants.POSTS_KEY);
        usersRef = database.getReference(Constants.USERS_KEY);

        imgStorage = FirebaseStorage.getInstance();
        imgRef = imgStorage.getReference();
    }

    private DB() {
    }
}
