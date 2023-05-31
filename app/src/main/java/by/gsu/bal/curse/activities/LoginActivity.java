package by.gsu.bal.curse.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import by.gsu.bal.curse.Constants;
import by.gsu.bal.curse.R;
import by.gsu.bal.curse.UserPosts;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private EditText etEmail;
    private EditText etPassword;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.DB_URL);
        usersRef = database.getReference(Constants.USERS_KEY);

        mAuth = FirebaseAuth.getInstance();
    }

    public void onClickBtnSignUp(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.equals("") || password.equals("")) {
            showToast("Email или пароль не введены");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
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
                            default:
                                showToast("Ошибка");
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
                .addOnCompleteListener(this, task -> {
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
                            default:
                                showToast("Ошибка");
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
