package by.gsu.bal.curse;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.*;

public class EditPostActivity extends AppCompatActivity {
    private final String TAG = "EditPostActivity";
    private TextView tvImagesNumber;
    private EditText etPostTitle;
    private EditText etPostDescription;
    private EditText etPostCost;
    private EditText etPostContacts;
    private EditText etPostCity;
    private Button btnPostSubmit;

    private FirebaseDatabase database;
    private DatabaseReference postsRef;
    private DatabaseReference usersRef;

    private FirebaseStorage imgStorage;
    private StorageReference imgRef;

    private HashSet<Uri> selectedImages = new HashSet<Uri>();

    Post oldPost;
    boolean oldImagesRemoved = false;
    int selectCount = 0;


    private FirebaseAuth mAuth;

    private final int PICK_IMAGE_MULTIPLE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        init();
    }
    private void init() {
        tvImagesNumber = findViewById(R.id.tvImagesNumber);
        etPostTitle = findViewById(R.id.etPostTitle);
        etPostDescription = findViewById(R.id.etPostDescription);
        etPostCost = findViewById(R.id.etPostCost);
        etPostContacts = findViewById(R.id.etPostContacts);
        etPostCity = findViewById(R.id.etPostCity);
        btnPostSubmit =findViewById(R.id.btnPostSubmit);

        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        postsRef = database.getReference(Constants.POSTS_KEY);
        usersRef = database.getReference(Constants.USERS_KEY);

        imgStorage = FirebaseStorage.getInstance(Constants.IMG_STORAGE_URL);
        imgRef = imgStorage.getReference();

        mAuth = FirebaseAuth.getInstance();

        Intent i = getIntent();
        if (i.hasExtra("post")) {
            oldPost = i.getParcelableExtra("post");
            String imgNum = oldPost.getImagesURLs().size() + " загружено";
            tvImagesNumber.setText(imgNum);
            extractOldPost();
        }

    }

    private void extractOldPost() {
        etPostTitle.setText(oldPost.getTitle());
        etPostCost.setText(String.valueOf(oldPost.getCost()));
        etPostDescription.setText(oldPost.getDescription());
        etPostCity.setText(oldPost.getCity());
        etPostContacts.setText(oldPost.getContacts());
    }

    public void onClickBtnPostSubmit(View view) {
        String postId = oldPost == null
                ? postsRef.push().getKey()
                : oldPost.getId();

        String title = etPostTitle.getText().toString();
        String description = etPostDescription.getText().toString();
        String cost = etPostCost.getText().toString();
        String contacts = etPostContacts.getText().toString();
        String city = etPostCity.getText().toString();

        String date = Constants.ISO8601.format(ZonedDateTime.now());

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        ArrayList<String> imagesURLs = oldPost == null
                ? new ArrayList<>()
                : new ArrayList<String>(oldPost.getImagesURLs());

//        ВАЛИДАЦИЯ
        if (selectedImages.size() == 0 && imagesURLs.size() == 0) {
            Toast.makeText(EditPostActivity.this, "Должна быть хотябы 1 фотография", Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.trim().isEmpty()
                || description.trim().isEmpty()
                || cost.trim().isEmpty()
                || contacts.trim().isEmpty()
                || city.trim().isEmpty()) {
            Toast.makeText(EditPostActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.length() > 60) {
            Toast.makeText(EditPostActivity.this, "Название слишком большое. Максимум 60", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.length() > 1000) {
            Toast.makeText(EditPostActivity.this, "Описание слишком большое", Toast.LENGTH_SHORT).show();
            return;
        }
        if (contacts.length() > 200) {
            Toast.makeText(EditPostActivity.this, "Контактная информация слишком длинная", Toast.LENGTH_SHORT).show();
            return;
        }

        if (oldImagesRemoved || oldPost == null || selectedImages.size() > 0) {
            if (oldImagesRemoved) DB.imgRef.child(postId).listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<ListResult> task) {
                    task.getResult().getItems().forEach(StorageReference::delete);
                }
            });
            Toast.makeText(this, "Загрузка фотографий", Toast.LENGTH_SHORT).show();
            for (Uri fileUri : selectedImages) {
                UploadTask uploadTask = imgRef.child(postId+'/'+fileUri.getLastPathSegment()).putFile(fileUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                selectCount++;
                                imagesURLs.add(uri.toString());
                                if (selectCount == selectedImages.size()) {
                                    Post post = new Post(postId, title, description, city, date, cost, contacts, userId, imagesURLs);
                                    postsRef.child(postId).setValue(post);
                                    finish();
                                }
                            }
                        });
                    }
                });
            }
        } else {
            Post post = new Post(postId, title, description, city, date, cost, contacts, userId, imagesURLs);
            postsRef.child(postId).setValue(post);
            finish();
        }
    }

    public void onClickBtnResetAddedImages(View view) {
        if (selectedImages.size() != 0 || oldPost.getImagesURLs().size() != 0) {
            selectedImages.clear();
            oldImagesRemoved = true;
            oldPost.imagesURLs.clear();
            tvImagesNumber.setText("0 загружено");
            Toast.makeText(this, "Выбранные фото сброшены", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickBtnAddImages(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберете фотографии"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    selectedImages.add(imageurl);
                }
                if (oldPost != null) {
                    tvImagesNumber.setText(count+oldPost.getImagesURLs().size() + " загружено");
                } else {
                    tvImagesNumber.setText(count + " загружено");
                }
                Toast.makeText(this, "Было выбрано фотографий: "+count, Toast.LENGTH_SHORT).show();
            } else {
                Uri imageurl = data.getData();
                selectedImages.add(imageurl);
                if (oldPost != null) {
                    tvImagesNumber.setText(1+oldPost.getImagesURLs().size() + " загружено");
                } else {
                    tvImagesNumber.setText("1 загружено");
                }
                Toast.makeText(this, "Было выбрано фотографий: 1", Toast.LENGTH_SHORT).show();
            }
        }
    }
}