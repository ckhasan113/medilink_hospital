package com.example.hospitalapp.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hospitalapp.LoadingDialog;
import com.example.hospitalapp.MainActivity;
import com.example.hospitalapp.R;
import com.example.hospitalapp.pojo.HospitalDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;

    private EditText editTextEmail;

    private EditText editTextPassword;

    private CardView mLogin;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        if (user != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        rootRef = FirebaseDatabase.getInstance().getReference();

        editTextEmail = findViewById(R.id.email_log_in);
        editTextPassword = findViewById(R.id.password_log_in);

        loadingDialog = new LoadingDialog(this, "Loading...");

        mLogin = findViewById(R.id.login_master);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleLogin();
            }
        });
    }

    private void simpleLogin() {
        if (editTextEmail.getText().toString().isEmpty()) {
            editTextEmail.setError(getString(R.string.required_field));
            return;
        }
        if (editTextPassword.getText().toString().isEmpty()) {
            editTextPassword.setError(getString(R.string.required_field));
            return;
        }
        loadingDialog.show();
        if (!editTextEmail.getText().toString().isEmpty() && !editTextPassword.getText().toString().isEmpty()) {
            auth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString()+"hospital")
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loadingDialog.dismiss();
                            if (task.isSuccessful()) {
                                user = auth.getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                String token = FirebaseInstanceId.getInstance().getToken();
                                Map<String, Object> tokenMap = new HashMap<>();
                                tokenMap.put("device_token", token);

                                rootRef.child("Users").child(user.getUid()).child("Token").setValue(tokenMap);

                                loadingDialog.dismiss();
                                startActivity(intent);
                                finish();
                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void goregister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }
}
