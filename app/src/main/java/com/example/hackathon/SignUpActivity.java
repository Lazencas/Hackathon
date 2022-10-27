package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.DialogTitle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Hashtable;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button signupBtn;
    EditText inputemail, inputpassword, inputname;
    String email, password, name;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    String defaultProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
         defaultProfile = "null";

        database = FirebaseDatabase.getInstance();

        //파베객체 초기화
        mAuth = FirebaseAuth.getInstance();
        signupBtn = findViewById(R.id.btnSignIn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });









    }

    private void signup() {

        //입력받은 이메일, 패스워드 에딧텍스트 연결
        inputemail = findViewById(R.id.editTextId);
        inputpassword = findViewById(R.id.editTextPassword);
        inputname = findViewById(R.id.editTextNickName);

        //에딧텍스트 스트링으로 전환
        email = inputemail.getText().toString();
        password = inputpassword.getText().toString();
        name = inputname.getText().toString();



        //회원가입파베로직
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공로직
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //회원가입하는 즉시 닉네임, 입력
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });
                            Toast.makeText(SignUpActivity.this, "회원가입성공!",
                                    Toast.LENGTH_SHORT).show();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("user").child(user.getUid());


                            Hashtable<String, String> profiles
                                    = new Hashtable<String, String>();



                            profiles.put("email", email);
                            profiles.put("nicname", name);
                            profiles.put("userUid", user.getUid());
                            profiles.put("profileImage", "");



                            myRef.setValue(profiles);







                            //회원가입하면서 입력된 값지워버리기
                            inputemail.setText(""); inputpassword.setText(""); inputname.setText("");
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));






                        } else {
                            // 회원가입 실패로직
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();


                        }

                        // ...
                    }
                });



    }
}