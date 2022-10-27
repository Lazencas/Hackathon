package com.example.hackathon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class AddActivity extends AppCompatActivity {

    final String TAG = "AddActivity";
    LinearLayout linearLayoutHome, linearLayoutSearch, linearLayoutAdd, linearLayoutLike, linearLayoutProfile;

    EditText inputTubeUrl, inputText;
    String youTubeUrl, text, name, email, userUid;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser user;
    String thumbnail = null;
    String receiveMessage = null;
    String imageUrl = null;
    Bitmap thumbnailBitmap = null;
    String youtubeApi = "https://www.googleapis.com/youtube/v3/search?key=AIzaSyDbSVd5uPDIG_1G6xgmZX-qL0UHnvAanBE&part=snippet&q=";
    DatabaseReference imgRef;
    String myKey;
    String profileImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        linearLayoutHome = findViewById(R.id.linearLayoutHome);
        linearLayoutSearch = findViewById(R.id.linearLayoutSearch);
        linearLayoutAdd = findViewById(R.id.linearLayoutAdd);
        linearLayoutLike = findViewById(R.id.linearLayoutLike);
        linearLayoutProfile = findViewById(R.id.linearLayoutProfile);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 0) {





                    String thumbnailString = BitMapToString(thumbnailBitmap);
                    imgRef = FirebaseDatabase.getInstance().getReference();
                    imgRef = imgRef.child("write").child(myKey).child("thumbnail");
                    imgRef.setValue(thumbnailString);

                    SharedPreferences sharedPreferences = getSharedPreferences("test",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("img",thumbnailString);
                    editor.commit();


                }

            }
        };


        class imgRunnable implements Runnable {


            @Override
            public void run() {

                HttpURLConnection conn = null;

                try {


                    String youtubeUrl = youTubeUrl;

                    Log.d("유튭url", youtubeUrl);


                    InputStream inputStream;
                    String url = youtubeApi + youtubeUrl;

                    Log.d("합친거", url);
//                    Log.d("YoutubeActivity", "Runnable :" + url);
//                    Log.d("YoutubeActivity", "Runnable :" + youtubeUrl);
                    inputStream = new URL(url).openStream();

                    BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String str;
                    StringBuffer buffer = new StringBuffer();
                    while ((str = rd.readLine()) != null) {
                        buffer.append(str);
                    }

                    receiveMessage = buffer.toString();


                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(receiveMessage);

                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                    Log.d("확인", receiveMessage);

                    JSONObject jsonObjectItems = jsonArray.getJSONObject(0);
                    Log.d("제이슨", jsonObjectItems.toString());
//                        editor.putString("num" + i + "videoId", jsonObjectItems.getJSONObject("id").getString("videoId"));
//                        editor.putString("num" + i + "title", jsonObjectItems.getJSONObject("snippet").getString("title"));
//                        editor.putString("num" + i + "thumbnails", jsonObjectItems.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url"));
//                        editor.commit();

                    imageUrl = jsonObjectItems.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");

                    Log.d("유알엘", imageUrl);

                    URL fUrl = new URL(imageUrl);
                    conn = (HttpURLConnection) fUrl.openConnection();
                    conn.setDoInput(true);
                    conn.setConnectTimeout(8000);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    thumbnailBitmap = BitmapFactory.decodeStream(is);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.sendEmptyMessage(0);

            }
        }



        linearLayoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddActivity.this, Main.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
        mAuth = FirebaseAuth.getInstance();


          user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();
            userUid=user.getUid();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
        //데이터베이스에서 프로필이미지스트링 불러오기
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user").child(userUid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "데이터: "+dataSnapshot.getValue());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //글쓰기버튼
        Button btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //에딧텍스트 xml연결하고 스트링으로 변환
                inputTubeUrl=findViewById(R.id.editTextId);
                inputText = findViewById(R.id.editTextPassword);
                youTubeUrl = inputTubeUrl.getText().toString();
                text = inputText.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd/HH:mm");
                String currentDateandTime = sdf.format(new Date());




                //글쓰기버튼 눌럿을때 로직
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("write").push();

                myKey = myRef.getKey();


                Hashtable<String, String> profiles
                        = new Hashtable<String, String>();



                profiles.put("email", email);
                profiles.put("profileImage", profileImg);
                profiles.put("nickName", name);
                profiles.put("contents", text);
                profiles.put("youtubeUrl", youTubeUrl);
                profiles.put("thumbnail", "null");
                profiles.put("likeCount", "좋아요 0 회");
                profiles.put("nowTime", currentDateandTime);




                myRef.setValue(profiles);
                inputText.setText("");
                inputTubeUrl.setText("");

                imgRunnable imgRunnable = new imgRunnable();
                Thread imgThread = new Thread(imgRunnable);
                imgThread.start();



            }
        });










    

        linearLayoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddActivity.this, SearchActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

        linearLayoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddActivity.this, AddActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
        linearLayoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddActivity.this, LikeActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
        linearLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddActivity.this, ProfileActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
    }

    public String BitMapToString(Bitmap bitmap){

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);

        return temp;

    }



}
