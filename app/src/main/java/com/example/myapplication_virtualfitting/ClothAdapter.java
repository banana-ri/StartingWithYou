package com.example.myapplication_virtualfitting;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // 🌟 Glide 임포트
import java.util.List;

public class ClothAdapter extends RecyclerView.Adapter<ClothAdapter.ViewHolder> {

    private List<Cloth> clothList;
    private OnItemClickListener listener;
    private static final String TAG = "ClothAdapter";

    // 🌟 우리 데스크탑 서버의 이미지 폴더 주소
    private static final String IMAGE_BASE_URL = "http://10.0.2.2:8000/clothes-image/";

    public interface OnItemClickListener {
        void onItemClick(Cloth cloth);
    }

    public ClothAdapter(List<Cloth> clothList, OnItemClickListener listener) {
        this.clothList = clothList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cloth_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cloth cloth = clothList.get(position);

        // 🌟 마법의 Glide 코드: 서버 주소 + 파일 이름으로 이미지를 불러와서 ImageView에 쏙!
        String fullImageUrl = IMAGE_BASE_URL + cloth.image_url;
        Glide.with(holder.itemView.getContext())
                .load(fullImageUrl)
                .placeholder(R.drawable.cloth_tshirt) // 서버에서 사진을 불러오는 동안 잠깐 보여줄 로딩용 티셔츠 이미지! // 로딩 중에 보여줄 기본 이미지 (팀원분이 만든 회색 배경)
                .into(holder.ivCloth);

        // 카드 클릭 시 리스너 호출
        holder.ivCloth.setOnClickListener(v -> {
            Log.d(TAG, "아이템 클릭됨: " + cloth.part);
            listener.onItemClick(cloth);
        });
    }

    @Override
    public int getItemCount() {
        return clothList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCloth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCloth = itemView.findViewById(R.id.cloth_item);
        }
    }
}