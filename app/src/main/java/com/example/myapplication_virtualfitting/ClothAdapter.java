package com.example.myapplication_virtualfitting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClothAdapter extends RecyclerView.Adapter<ClothAdapter.ViewHolder> {

    private List<Cloth> clothList;
    private OnItemClickListener listener;

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
        // 우리가 만든 item_cloth_card.xml을 불러옵니다.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cloth_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cloth cloth = clothList.get(position);

        // 데이터(이미지) 연결
        holder.ivCloth.setImageResource(cloth.imageResId);

        // 카드 클릭 시 상세 페이지로 이동하기 위한 리스너 호출
        holder.itemView.setOnClickListener(v -> listener.onItemClick(cloth));
    }

    @Override
    public int getItemCount() {
        return clothList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton ivCloth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_cloth_card.xml 안의 ID와 일치해야 합니다.
            ivCloth = itemView.findViewById(R.id.cloth_item);
        }
    }
}