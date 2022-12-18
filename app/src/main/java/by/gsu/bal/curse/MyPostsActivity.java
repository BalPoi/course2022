package by.gsu.bal.curse;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyPostsActivity extends AppCompatActivity implements MyRecyclerViewInterface {
    private final String TAG = "MyPostsActivity";
    private RecyclerView rvPostsFeed;
    private static PostsAdapter postsAdapter;
    SearchView searchView;

    ArrayList<Post> myPosts;
    ArrayList<Post> visiblePosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        init();
    }

    private void init() {
        myPosts = getIntent().getParcelableArrayListExtra("myPosts");

        searchView = findViewById(R.id.searchView);
        rvPostsFeed = findViewById(R.id.rvPostsFeed);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPostsFeed.setLayoutManager(layoutManager);

        visiblePosts = myPosts;

        postsAdapter = new PostsAdapter(this, visiblePosts, this);
        rvPostsFeed.setAdapter(postsAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    visiblePosts = myPosts;
                    postsAdapter.posts = myPosts;
                    postsAdapter.notifyDataSetChanged();
                }

                visiblePosts = new ArrayList<Post>();
                for (Post post : myPosts) {
                    String title = post.getTitle().toLowerCase();
                    String description = post.getDescription().toLowerCase();

                    if ((title+description).contains(newText.toLowerCase())) {
                        visiblePosts.add(post);
                    }
                }
                postsAdapter.posts = visiblePosts;
                postsAdapter.notifyDataSetChanged();
                return false;
            }
        });

    }

    @Override
    public void onClickOnItem(int position) {
        Intent intent = new Intent(MyPostsActivity.this, PostActivity.class);
        intent.putExtra("post", visiblePosts.get(position));
        startActivity(intent);
    }

    @Override
    public void deletePost(int position) {
        Post post = myPosts.get(position);
        Log.i("TAG", "Delete post: " + post);
        DB.postsRef.child(post.getId()).removeValue();
        DB.imgRef.child(post.getId()).listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<ListResult> task) {
                task.getResult().getItems().forEach(StorageReference::delete);
            }
        });
        myPosts.remove(position);
        postsAdapter.notifyDataSetChanged();

    }

    @Override
    public void editPost(int position) {
        Log.i("TAG", "Edit post: " + myPosts.get(position));
        Post post = myPosts.get(position);
        Intent intent = new Intent(MyPostsActivity.this, EditPostActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }
}