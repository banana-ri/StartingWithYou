package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class ClosetDetailFragment extends Fragment {

    private ImageView detailImageView;
    private ChipGroup groupSeason, groupPart, groupThickness, groupLength;

    // Fragment는 onCreateView에서 레이아웃을 연결합니다.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_closet_details_page, container, false);

        //1. 뷰 초기화
        detailImageView = view.findViewById(R.id.detail_image_view);
        groupSeason = view.findViewById(R.id.chipGroup_season);
        groupPart = view.findViewById(R.id.chipGroup_part);
        groupThickness = view.findViewById(R.id.chipGroup_thickness);
        groupLength = view.findViewById(R.id.chipGroup_length);

        //2. 필수 선택 로직 적용 (해제 시 첫 번째 칩 자동 선택)
        setupMandatorySelection(groupPart, groupSeason, groupThickness, groupLength);

        //3. 전달받은 데이터로 초기 상태 설정
        if (getArguments() != null) {
            int imageId = getArguments().getInt("selectedImage");
            detailImageView.setImageResource(imageId);

            checkChipByText(groupPart, getArguments().getString("part"));
            checkChipByText(groupSeason, getArguments().getString("season"));
            checkChipByText(groupThickness, getArguments().getString("thickness"));
            checkChipByText(groupLength, getArguments().getString("length"));
        }

        // 4. 버튼 클릭 리스너
        view.findViewById(R.id.button_save).setOnClickListener(v -> { //저장하기 버튼
            saveClothesData();
        });
        view.findViewById(R.id.button_recommend).setOnClickListener(v -> { //추천받기 버튼
            Navigation.findNavController(v).navigate(R.id.action_closetDetailFragment_to_mainFragment); //메인 화면으로 이동
        });
        view.findViewById(R.id.arrow_left_circle).setOnClickListener(v -> { //뒤로가기 버튼
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    // [확인용] 특정 텍스트에 맞는 칩을 찾아 체크해주는 메서드
    private void checkChipByText(ChipGroup group, String text) {
        if (text == null) return;
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            if (chip.getText().toString().equals(text)) {
                chip.setChecked(true);
                break;
            }
        }
    }

    // [수정용] 현재 선택된 칩들의 정보를 읽어오는 메서드
    private void saveClothesData() {
        String selectedPart = getSelectedChipText(groupPart);
        String selectedSeason = getSelectedChipText(groupSeason);
        String selectedThickness = getSelectedChipText(groupThickness);
        String selectedLength = getSelectedChipText(groupLength);
        
        // 여기서 DB에 저장하거나 서버로 보내는 로직을 작성합니다.
        // 로그나 토스트로 확인
        String result = String.format("저장됨: %s, %s, %s, %s",
                selectedPart, selectedSeason, selectedThickness, selectedLength);
        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
    }

    // 선택된 칩의 텍스트를 가져오는 편의 메서드
    private String getSelectedChipText(ChipGroup group) {
        int id = group.getCheckedChipId();
        if (id != View.NO_ID) {
            Chip chip = group.findViewById(id);
            return chip.getText().toString();
        }
        return "미선택";
    }

    // [필수 선택 로직] 선택 해제 시 첫 번째 칩을 강제로 체크
    private void setupMandatorySelection(ChipGroup... groups) {
        for (ChipGroup group : groups) {
            group.setOnCheckedStateChangeListener((parent, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    Chip first = (Chip) parent.getChildAt(0);
                    if (first != null) first.setChecked(true);
                }
            });
        }
    }
}