package by.gsu.bal.curse;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSubmit;

    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

    }

    private void init() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnSignin);

        database = FirebaseDatabase.getInstance(Constants.DB_URL);
        usersRef = database.getReference(Constants.USERS_KEY);

        mAuth = FirebaseAuth.getInstance();
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnSignUp(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.equals("") || password.equals("")) {
            showToast("Email или пароль не введены");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            UserPosts posts = new UserPosts();

                            usersRef.child(uid).setValue(true);
                            showToast("Новый пользователь создан");

                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            switch (task.getException().getMessage()) {
                                case "The email address is badly formatted.":
                                    showToast("Невалидный email");
                                    break;
                                case "The given password is invalid. [ Password should be at least 6 characters ]":
                                    showToast("Пароль должен быть некороче 6");
                                    break;
                                case "The email address is already in use by another account.":
                                    showToast("Email уже занят");
                                    break;
                            }
                        }
                    }
                });
    }

    public void onClickBtnSignIn(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.equals("") || password.equals("")) {
            showToast("Email или пароль не введены");
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");

                            finish();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            switch (task.getException().getMessage()) {
                                case "The email address is badly formatted.":
                                    showToast("Невалидный email");
                                    break;
                                case "There is no user record corresponding to this identifier. The user may have been deleted.":
                                case "The password is invalid or the user does not have a password.":
                                    showToast("Неверный email или пароль");
                                    break;
                            }
                        }
                    }
                });
    }
}