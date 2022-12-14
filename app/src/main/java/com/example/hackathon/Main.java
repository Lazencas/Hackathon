package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Main extends AppCompatActivity {

    final String TAG = "MainActivity";

    FirebaseUser user;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;

    BufferedInputStream bis = null;
    String imageUrl = null;
    String receiveMessage;
    String youtubeApi = "https://www.googleapis.com/youtube/v3/search?key=AIzaSyAqfCsB6-fYapJKVCYNZIbVUZ1kNsDeRV4&part=snippet&q=";
    WriteData writeData;

    Bitmap thumbnail = null;


    LinearLayout linearLayoutHome, linearLayoutSearch, linearLayoutAdd, linearLayoutLike, linearLayoutProfile;
    Bitmap bitmapUserImage = null;

    RecyclerViewAdapter mAdapter = null;
    ArrayList<RecyclerViewData> mList = new ArrayList<RecyclerViewData>();
    RecyclerView mRecyclerView = null;
    DatabaseReference writeRef = null;
    Handler handler = null;

    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    File localFile = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String userEmail = user.getEmail();

        Log.d("???????????????",userEmail);

        writeRef = FirebaseDatabase.getInstance().getReference();
        writeRef = writeRef.child("write").getRef();


        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new RecyclerViewAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        try {
            localFile = File.createTempFile("images", "jpg");
            StorageReference riversRef = mStorageRef.child("users").child(userEmail).child("profileImage.jpg");
            final File finalLocalFile = localFile;
            riversRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    Log.d("?????????????????????","?????????????????????");
                    bitmapUserImage = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                    Log.d("?????????",bitmapUserImage.toString());
//                            bitmapUserImage = bitmap;

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("????????????",e.toString());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


        writeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {


                writeData = dataSnapshot.getValue(WriteData.class);


                String key = dataSnapshot.getKey();
                String nickName = writeData.getNickName();
                String countLike = writeData.getLikeCount();
                String contents = writeData.getContents();
                String youtubeUrl = writeData.getYoutubeUrl();
                String stringThumbnail = writeData.getThumbnail();


                Bitmap bitmap = StringToBitMap(stringThumbnail);


//                Log.d("???????????????2",bitmapUserImage.toString());
                addItem(bitmapUserImage, nickName, bitmap, countLike, contents, youtubeUrl, key);

                mAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







        mAdapter.notifyDataSetChanged();


        linearLayoutHome =

                findViewById(R.id.linearLayoutHome);

        linearLayoutSearch =

                findViewById(R.id.linearLayoutSearch);

        linearLayoutAdd =

                findViewById(R.id.linearLayoutAdd);

        linearLayoutLike =

                findViewById(R.id.linearLayoutLike);

        linearLayoutProfile =

                findViewById(R.id.linearLayoutProfile);

        linearLayoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main.this, Main.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

        linearLayoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main.this, SearchActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

        linearLayoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main.this, AddActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
        linearLayoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main.this, LikeActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });
        linearLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main.this, ProfileActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });


    }


    public void addItem(Bitmap userProfileImage, String userProfileNickName, Bitmap
            userThumbnail, String countLike, String contents, String youtubeUrl, String key) {

        //???????????? ????????? ??? ??????????????? ????????? ?????????????????????. ??? ???????????? ????????? ?????????????????? ????????????

        RecyclerViewData item = new RecyclerViewData();
        item.setUserImage(userProfileImage);
        item.setUserNickName(userProfileNickName);
        item.setYouTubeThumbnail(userThumbnail);
        item.setCountLike(countLike);
        item.setContents(contents);
        item.setYoutubeUrl(youtubeUrl);
        item.setKey(key);
        mList.add(0, item);
    }

    @Override
    public void onBackPressed() {
        // AlertDialog ????????? ????????? ????????? ???????????? ?????? ?????????
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("?????????????????????????");

        // "???" ????????? ????????? ???????????? ?????????
        alBuilder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity(); // ?????? ??????????????? ????????????
            }
        });
        // "?????????" ????????? ????????? ???????????? ?????????
        alBuilder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // ????????? ????????? ?????? ?????? ????????????
            }
        });
        alBuilder.setTitle("???????????? ??????");
        alBuilder.show(); // AlertDialog.Bulider??? ?????? AlertDialog??? ????????????.
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


}
