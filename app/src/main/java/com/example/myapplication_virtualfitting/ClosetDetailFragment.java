package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class ClosetDetailFragment extends Fragment {

    private ImageView detailImageView;
    private ChipGroup groupSeason, groupPart, groupThickness, groupLength;
    private static final String TAG = "ClosetDetailFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "화면 전환됨");
        return inflater.inflate(R.layout.my_closet_details_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //뷰 찾기
        detailImageView = view.findViewById(R.id.detail_image_view);
        groupSeason = view.findViewById(R.id.chipGroup_season);
        groupPart = view.findViewById(R.id.chipGroup_part);
        groupThickness = view.findViewById(R.id.chipGroup_thickness);
        groupLength = view.findViewById(R.id.chipGroup_length);

        // 필수 선택 로직 (해제 시 첫 번째 칩 자동 선택)
        setupMandatorySelection(groupPart, groupSeason, groupThickness, groupLength);

        // 전달받은 데이터로 초기 상태 설정
        if (getArguments() != null) {
            int imageId = getArguments().getInt("selectedImage");
            detailImageView.setImageResource(imageId);

            checkChipByText(groupPart, getArguments().getString("part"));
            checkChipByText(groupSeason, getArguments().getString("season"));
            checkChipByText(groupThickness, getArguments().getString("thickness"));
            checkChipByText(groupLength, getArguments().getString("length"));
            Log.d(TAG, "칩 상태 초기화 완료");
        }

        // 버튼 클릭 리스너
        view.findViewById(R.id.button_save).setOnClickListener(v -> { //저장하기 버튼
            Log.d(TAG, "저장하기 버튼 누름");
            saveClothesData();
        });
        view.findViewById(R.id.button_recommend).setOnClickListener(v -> { //추천받기 버튼
            Log.d(TAG, "추천받기 버튼 누름");
            Navigation.findNavController(v).navigate(R.id.action_closetDetailFragment_to_mainFragment); //메인 화면으로 이동
        });
        view.findViewById(R.id.arrow_left_circle).setOnClickListener(v -> { //뒤로가기 버튼
            Log.d(TAG, "뒤로가기 버튼 누름");
            Navigation.findNavController(v).popBackStack();
        });
    }

    // 정보 확인: 텍스트에 맞는 칩을 찾아 체크
    private void checkChipByText(ChipGroup group, String text) {
        if (text == null) {
            Log.e(TAG, "전달된 텍스트가 비어있습니다.");
            return;
        }
        boolean found = false;
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);

            if (chip.getText().toString().equals(text)){
                chip.setChecked(true);
                found =true;
                Log.d(TAG, "대상 칩 찾음: " + text);
                break;
            }

        }
        if (!found) {
            Log.e(TAG, "일치하는 칩을 찾지 못함: " + text);
            // 만약 못 찾았다면 첫 번째 칩 강제 선택
            if (group.getChildCount() > 0) {
                ((Chip) group.getChildAt(0)).setChecked(true);
            }
        }
    }

    // 정보 수정: 현재 선택된 칩들의 정보를 읽어오는 메서드
    private void saveClothesData() {
        String selectedPart = getSelectedChipText(groupPart);
        String selectedSeason = getSelectedChipText(groupSeason);
        String selectedThickness = getSelectedChipText(groupThickness);
        String selectedLength = getSelectedChipText(groupLength);
        
        // 여기서 DB에 저장하거나 서버로 보내는 로직을 작성
        // 로그나 토스트로 확인
        Log.d(TAG, "==== 옷 정보 저장 시도 ====");
        Log.d(TAG, "Part: " + selectedPart);
        Log.d(TAG, "Season: " + selectedSeason);
        Log.d(TAG, "Thickness: " + selectedThickness);
        Log.d(TAG, "Length: " + selectedLength);
        Log.d(TAG, "========================");

        if (selectedPart.equals("미선택") || selectedSeason.equals("미선택") || selectedLength.equals("미선택") || selectedLength.equals("미선택")) {
            Log.e(TAG, "저장 실패: 항목이 선택되지 않음");
            Toast.makeText(getContext(), "모든 항목을 선택해주세요", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "저장 성공: 데이터 전달 준비 완료");
            String result = String.format("저장됨: %s, %s, %s, %s", selectedSeason, selectedPart, selectedThickness, selectedLength);
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
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

    //필수 선택 로직: 선택 해제 시 첫 번째 칩을 강제로 체크
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