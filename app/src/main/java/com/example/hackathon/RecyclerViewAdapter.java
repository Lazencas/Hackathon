package com.example.hackathon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }



    DatabaseReference writeRef = null;
    DatabaseReference keyRef = null;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private ArrayList<RecyclerViewData> mData = null;
    Context context;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    RecyclerViewAdapter(ArrayList<RecyclerViewData> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_view_item_main, parent, false);
        RecyclerViewAdapter.ViewHolder vh = new RecyclerViewAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {

        RecyclerViewData item = mData.get(position);

        holder.recyclerViewUserProfileImage.setImageBitmap(item.getUserImage());
        holder.recyclerViewUserProfileNickName.setText(item.getUserNickName());
        holder.recyclerViewUserThumbnail.setImageBitmap(item.getYouTubeThumbnail());
        holder.recyclerViewCountLike.setText(item.getCountLike());
        holder.recyclerViewContents.setText(item.getContents());
        holder.recyclerViewLikeBtn.setBackgroundResource(item.getLikeBtn());


        final RecyclerViewAdapter.ViewHolder buttonHolder = (RecyclerViewAdapter.ViewHolder) holder;

        buttonHolder.recyclerViewLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String uid = user.getUid().toString();
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences(uid, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean likeCheck = sharedPreferences.getBoolean(mData.get(position).getKey(), false);


                if (!likeCheck) {
                    writeRef = FirebaseDatabase.getInstance().getReference();
                    writeRef = writeRef.child("write").child(mData.get(position).getKey()).child("likeCount");



                    Log.d("확인1",writeRef.toString());
                    String[] splitCountLike = mData.get(position).getCountLike().split(" ");


                    int intCountLike = Integer.valueOf(splitCountLike[1]);
                    intCountLike++;
                    String countLike = String.valueOf(intCountLike);

                    writeRef.setValue("좋아요 " + countLike + " 회");

                    mData.get(position).getKey();

                    mData.get(position).setCountLike("좋아요 " + countLike + " 회");
                    mData.get(position).setLikeBtn(R.drawable.like);
                    editor.putBoolean(mData.get(position).getKey(), true);
                    editor.commit();

                    notifyDataSetChanged();
                } else {
                    holder.recyclerViewLikeBtn.setBackgroundResource(R.drawable.like);
                    Toast.makeText(v.getContext(), "좋아요는 한번만 할 수 있습니다", Toast.LENGTH_SHORT).show();
                }


            }
        });

        buttonHolder.recyclerViewUserThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "되냐?"+position, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(v.getContext(),YoutubePlayerActivity.class);

                intent.putExtra("video",mData.get(position).getYoutubeUrl());

                v.getContext().startActivity(intent);

            }
        });


    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerViewUserProfileImage;
        TextView recyclerViewUserProfileNickName;
        ImageView recyclerViewUserThumbnail;
        ImageView recyclerViewLikeBtn;
        TextView recyclerViewCountLike;
        TextView recyclerViewContents;


        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)

            recyclerViewUserProfileImage = itemView.findViewById(R.id.recyclerViewUserProfileImage);
            recyclerViewUserProfileNickName = itemView.findViewById(R.id.recyclerViewUserProfileNickName);
            recyclerViewUserThumbnail = itemView.findViewById(R.id.recyclerViewUserThumbnail);
            recyclerViewLikeBtn = itemView.findViewById(R.id.recyclerViewLikeBtn);
            recyclerViewCountLike = itemView.findViewById(R.id.recyclerViewCountLike);
            recyclerViewContents = itemView.findViewById(R.id.recyclerViewContents);


        }
    }


}