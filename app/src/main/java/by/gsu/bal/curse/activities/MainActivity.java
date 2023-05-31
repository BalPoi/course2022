package by.gsu.bal.curse.activities;

import static by.gsu.bal.curse.DB.postsRef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import by.gsu.bal.curse.MyRecyclerViewInterface;
import by.gsu.bal.curse.Post;
import by.gsu.bal.curse.R;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewInterface {
    private final String TAG = "MainActivity";
    private final ArrayList<Post> allPosts = new ArrayList<>();
    SearchView searchView;
    private TextView tvUserEmail;
    private RecyclerView rvPostsFeed;
    private PostsAdapter postsAdapter;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private ArrayList<Post> visiblePosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    @Override
    public void onClickOnItem(int position) {
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        intent.putExtra("post", visiblePosts.get(position));
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onCreateContextMenu(
            ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (user != null) {
            menu.setHeaderTitle("Профиль");
            menu.add(0, v.getId(), 0, "Выйти");
            menu.add(0, v.getId(), 0, "Мои объявления");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Выйти") {
            mAuth.signOut();
        } else if (item.getTitle() == "Мои объявления") {
            List<Post> myPosts = getMyPosts(mAuth.getUid());
            Intent intent = new Intent(MainActivity.this, MyPostsActivity.class);
            intent.putParcelableArrayListExtra("myPosts", (ArrayList<Post>) myPosts);
            startActivity(intent);
        }

        return true;
    }

    public void onClickNewPost(View view) {
        Class target = user != null ? EditPostActivity.class : LoginActivity.class;
        Intent i = new Intent(MainActivity.this, target);

        startActivity(i);
    }

    @Override
    public void deletePost(int position) {
        // not used
    }

    @Override
    public void editPost(int position) {
        // not used
    }

    private void init() {
        searchView = findViewById(R.id.searchView);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                tvUserEmail.setText(user.getEmail());
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                tvUserEmail.setText("Гость");
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
        tvUserEmail = findViewById(R.id.tvUserEmail);
        rvPostsFeed = findViewById(R.id.rvPostsFeed);

        registerForContextMenu(tvUserEmail);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPostsFeed.setLayoutManager(layoutManager);

        visiblePosts = allPosts;

        postsAdapter = new PostsAdapter(this, visiblePosts, this);
        rvPostsFeed.setAdapter(postsAdapter);

        //        ОБНОВЛЕНИЕ СПИСКА ПОСТОВ
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                allPosts.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    allPosts.add(0, post);
                }
                postsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                // not used
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    visiblePosts = allPosts;
                    postsAdapter.posts = allPosts;
                    postsAdapter.notifyDataSetChanged();
                }

                visiblePosts = new ArrayList<Post>();
                for (Post post : allPosts) {
                    String title = post.getTitle().toLowerCase();
                    String description = post.getDescription().toLowerCase();

                    if ((title + description).contains(newText.toLowerCase())) {
                        visiblePosts.add(post);
                    }
                }
                postsAdapter.posts = visiblePosts;
                postsAdapter.notifyDataSetChanged();

                return false;
            }
        });


    }

    private List<Post> getMyPosts(String uid) {
        List<Post> myPosts = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.getUserID().equals(uid)) {
                myPosts.add(post);
            }
        }

        return myPosts;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
