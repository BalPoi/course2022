package by.gsu.bal.curse;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static by.gsu.bal.curse.DB.postsRef;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewInterface {
    private final String TAG = "MainActivity";
    private TextView tvUserEmail;
    private RecyclerView rvPostsFeed;
    private PostsAdapter postsAdapter;

    SearchView searchView;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    private ArrayList<Post> allPosts = new ArrayList<>();
    private ArrayList<Post> visiblePosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        searchView = findViewById(R.id.searchView);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    tvUserEmail.setText(user.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    tvUserEmail.setText("??????????");
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
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

//        ???????????????????? ???????????? ????????????
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
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (user != null) {
            menu.setHeaderTitle("??????????????");
            menu.add(0, v.getId(), 0, "??????????");
            menu.add(0, v.getId(), 0, "?????? ????????????????????");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "??????????") {
            mAuth.signOut();
        } else if (item.getTitle() == "?????? ????????????????????") {
            List<Post> myPosts = getMyPosts(mAuth.getUid());
            Intent intent = new Intent(MainActivity.this, MyPostsActivity.class);
            intent.putParcelableArrayListExtra("myPosts", (ArrayList<Post>) myPosts);
            startActivity(intent);
        }

        return true;
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

    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void onClickNewPost(View view) {
        Class target = user != null ? EditPostActivity.class : LoginActivity.class;
        Intent i = new Intent(MainActivity.this,  target);

        startActivity(i);
    }

    @Override
    public void deletePost(int position) {

    }

    @Override
    public void editPost(int position) {

    }
}