package com.example.myapplication_virtualfitting;

import android.os.Bundle;
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


    // Fragment는 onCreateView에서 레이아웃을 연결합니다.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_closet_page, container, false);

        // 1. 뒤로가기 버튼 연결
        ImageButton btnBack = view.findViewById(R.id.arrow_left_circle);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        }

        initDummyData(); // 테스트용 데이터 생성

        // 2. 추가 버튼(FAB) 연결
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_icon_button);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                // 옷 등록 프래그먼트로 이동 로직 추가 가능
            });
        }

        // 3. 리사이클러뷰 및 어댑터 연결
        recyclerView = view.findViewById(R.id.recycler_view_closet);
        filteredList.addAll(allClothes); // 처음엔 전체 데이터를 보여줌

        adapter = new ClothAdapter(filteredList, cloth -> {
            // 상세 페이지로 이동하는 로직
            // 바구니(Bundle) 만들기
            Bundle bundle = new Bundle();

            // 바구니에 클릭한 옷 정보 담기
            bundle.putInt("selectedImage", cloth.imageResId);
            bundle.putString("season", cloth.season);
            bundle.putString("part", cloth.part);
            bundle.putString("thickness", cloth.thickness);
            bundle.putString("length", cloth.length);

            // 네비게이션을 이용해 데이터 전달하며 이동
            Navigation.findNavController(view).navigate(
                    R.id.action_closetFragment_to_closetDetailFragment, bundle
            );
        });

        // 3) 그리드 레이아웃 설정 (3열로 설정)
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        // 4. 카테고리 필터링 칩 그룹 설정
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

        return view;
    }

    // 테스트용 데이터 생성
    private void initDummyData() {
        allClothes.clear();
        allClothes.add(new Cloth(R.drawable.image, "봄", "상의", "보통", "보통"));
        allClothes.add(new Cloth(R.drawable.image, "여름", "하의", "얇음", "짧음"));
        allClothes.add(new Cloth(R.drawable.image, "겨울", "신발", "두꺼움", "보통"));
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
