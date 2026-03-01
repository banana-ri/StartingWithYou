package com.example.myapplication_virtualfitting;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClothAdapter extends RecyclerView.Adapter<ClothAdapter.ViewHolder> {

    private List<Cloth> clothList;
    private OnItemClickListener listener;
    private static final String TAG = "ClothAdapter";

    // 클릭 이벤트를 위한 인터페이스
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

        //이미지 연결
        holder.ivCloth.setImageResource(cloth.imageResId);

        //카드 클릭 시 리스너 호출(상세 페이지로 이동)
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