package by.gsu.bal.curse.activities;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import by.gsu.bal.curse.Constants;
import by.gsu.bal.curse.DB;
import by.gsu.bal.curse.Post;
import by.gsu.bal.curse.R;

public class EditPostActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_MULTIPLE = 0;
    private final String TAG = "EditPostActivity";
    private final HashSet<Uri> selectedImages = new HashSet<Uri>();
    Post oldPost;
    boolean oldImagesRemoved = false;
    int selectCount = 0;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        init();
    }

    public void onClickBtnPostSubmit(View view) {
        String postId = oldPost == null ? postsRef.push().getKey() : oldPost.getId();

        String title = etPostTitle.getText().toString();
        String description = etPostDescription.getText().toString();
        String cost = etPostCost.getText().toString();
        String contacts = etPostContacts.getText().toString();
        String city = etPostCity.getText().toString();

        String date = Constants.ISO8601.format(ZonedDateTime.now());

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        ArrayList<String> imagesURLs = oldPost == null ? new ArrayList<>() : new ArrayList<String>(
                oldPost.getImagesURLs());


        // ВАЛИДАЦИЯ
        if (selectedImages.isEmpty() && imagesURLs.isEmpty()) {
            makeText(EditPostActivity.this, "Должна быть хотябы 1 фотография",
                     Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.trim().isEmpty() ||
            description.trim().isEmpty() ||
            cost.trim().isEmpty() ||
            contacts.trim().isEmpty() ||
            city.trim().isEmpty()) {
            makeText(EditPostActivity.this, "Все поля должны быть заполнены",
                     Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.length() > 60) {
            makeText(EditPostActivity.this, "Название слишком большое. Максимум 60",
                     Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.length() > 1000) {
            makeText(EditPostActivity.this, "Описание слишком большое", Toast.LENGTH_SHORT).show();
            return;
        }
        if (contacts.length() > 200) {
            makeText(EditPostActivity.this, "Контактная информация слишком длинная",
                     Toast.LENGTH_SHORT).show();
            return;
        }

        if (oldImagesRemoved || oldPost == null || !selectedImages.isEmpty()) {
            if (oldImagesRemoved) DB.imgRef.child(postId).listAll().addOnCompleteListener(
                    task -> task.getResult().getItems().forEach(StorageReference::delete));
            makeText(this, "Загрузка фотографий", Toast.LENGTH_SHORT).show();
            for (Uri fileUri : selectedImages) {
                UploadTask uploadTask = imgRef.child(postId + '/' + fileUri.getLastPathSegment())
                        .putFile(fileUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            selectCount++;
                            imagesURLs.add(uri.toString());
                            if (selectCount == selectedImages.size()) {
                                Post post = new Post(postId, title, description, city, date, cost,
                                                     contacts, userId, imagesURLs);
                                postsRef.child(postId).setValue(post);
                                finish();
                            }
                        }));
            }
        } else {
            Post post = new Post(postId, title, description, city, date, cost, contacts, userId,
                                 imagesURLs);
            postsRef.child(postId).setValue(post);
            finish();
        }
    }

    public void onClickBtnResetAddedImages(View view) {
        if (!selectedImages.isEmpty() || !oldPost.getImagesURLs().isEmpty()) {
            selectedImages.clear();
            oldImagesRemoved = true;
            oldPost.imagesURLs.clear();
            tvImagesNumber.setText("0 загружено");
            makeText(this, "Выбранные фото сброшены", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickBtnAddImages(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберете фотографии"),
                               PICK_IMAGE_MULTIPLE);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    selectedImages.add(imageurl);
                }
                if (oldPost != null) {
                    tvImagesNumber.setText(count + oldPost.getImagesURLs().size() + " загружено");
                } else {
                    tvImagesNumber.setText(count + " загружено");
                }
                makeText(this, "Было выбрано фотографий: " + count, Toast.LENGTH_SHORT).show();
            } else {
                Uri imageurl = data.getData();
                selectedImages.add(imageurl);
                if (oldPost != null) {
                    tvImagesNumber.setText(1 + oldPost.getImagesURLs().size() + " загружено");
                } else {
                    tvImagesNumber.setText("1 загружено");
                }
                makeText(this, "Было выбрано фотографий: 1", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        tvImagesNumber = findViewById(R.id.tvImagesNumber);
        etPostTitle = findViewById(R.id.etPostTitle);
        etPostDescription = findViewById(R.id.etPostDescription);
        etPostCost = findViewById(R.id.etPostCost);
        etPostContacts = findViewById(R.id.etPostContacts);
        etPostCity = findViewById(R.id.etPostCity);
        btnPostSubmit = findViewById(R.id.btnPostSubmit);

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
}
