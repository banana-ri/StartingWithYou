package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ClosetFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClothAdapter adapter;
    private List<Cloth> allClothes = new ArrayList<>(); // 전체 데이터 보관용
    private List<Cloth> filteredList = new ArrayList<>(); // 화면에 보여줄 필터링된 데이터용
    private static final String TAG = "ClosetFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "화면 전환됨");
        return inflater.inflate(R.layout.my_closet_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDummyData(); // 테스트용 데이터 생성

        // 뒤로가기 버튼 연결
        ImageButton btnBack = view.findViewById(R.id.arrow_left_circle);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        }

        // 추가 버튼(FAB) 연결
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_icon_button);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                // 옷 등록 프래그먼트로 이동 로직 추가 가능
            });
        }

        // 리사이클러뷰 및 어댑터 연결
        recyclerView = view.findViewById(R.id.recycler_view_closet);
        filteredList.clear();
        filteredList.addAll(allClothes); // 전체 데이터 보여주기

        adapter = new ClothAdapter(filteredList, cloth -> {
            // 상세 페이지로 이동하는 로직
            // 번들 생성 및 옷 정보 삽입
            Bundle bundle = new Bundle();
            bundle.putInt("selectedImage", cloth.imageResId);
            bundle.putString("season", cloth.season);
            bundle.putString("part", cloth.part);
            bundle.putString("thickness", cloth.thickness);
            bundle.putString("length", cloth.length);

            //데이터 전달하며 이동
            Navigation.findNavController(view).navigate(
                    R.id.action_closetFragment_to_closetDetailFragment, bundle
            );
        });

        // 그리드 레이아웃 설정 (3열로 설정)
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        // 카테고리 필터링 칩 그룹 설정
        ChipGroup chipGroup = view.findViewById(R.id.chipGroup_category);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            String category = "";

            if (checkedId == R.id.chip_category_all) category = "전체";
            else if (checkedId == R.id.chip_category_top) category = "상의";
            else if (checkedId == R.id.chip_category_bottoms) category = "하의";
            else if (checkedId == R.id.chip_category_acc) category = "악세사리";
            else if (checkedId == R.id.chip_category_bag) category = "가방";
            else if (checkedId == R.id.chip_category_shoes) category = "신발";

            filterClothes(category);
        });
    }

    // 테스트용 데이터 생성
    private void initDummyData() {
        allClothes.clear();
        allClothes.add(new Cloth(R.drawable.cloth_tshirt, "가을", "상의", "보통", "긺"));
        allClothes.add(new Cloth(R.drawable.cloth_jeans, "봄", "하의", "얇음", "약간 긺"));
        allClothes.add(new Cloth(R.drawable.cloth_pants, "겨울", "하의", "두꺼움", "보통"));
    }


    // 필터링 로직
    private void filterClothes(String category) {
        filteredList.clear(); // 기존 목록 비우기

        for (Cloth cloth : allClothes) {
            if (category.equals("전체") || cloth.part.equals(category)) {
                filteredList.add(cloth);
            }
        }

        // 어댑터에 데이터가 바뀌었으니 새로 그리라고 명령함
        adapter.notifyDataSetChanged();
    }
}
