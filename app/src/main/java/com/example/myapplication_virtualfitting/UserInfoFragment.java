package com.example.myapplication_virtualfitting;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class UserInfoFragment extends Fragment {

    private EditText etName, etAge, etHeight, etWeight;
    private Button btnWoman, btnMan, btnOther, btnContinue;
    private String selectedGender = ""; // 선택된 성별을 담을 변수
    private PreferenceHelper prefHelper;

    // Fragment는 onCreateView에서 레이아웃을 연결합니다.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. 레이아웃 인플레이트, PreferenceHelper 초기화
        View view = inflater.inflate(R.layout.user_info_page, container, false);
        prefHelper = new PreferenceHelper(getContext());

        // 2. 뷰 찾기
        etName = view.findViewById(R.id.field_name);
        etAge = view.findViewById(R.id.field_age);
        etHeight = view.findViewById(R.id.field_height);
        etWeight = view.findViewById(R.id.field_weight);

        btnWoman = view.findViewById(R.id.button_woman);
        btnMan = view.findViewById(R.id.button_man);
        btnOther = view.findViewById(R.id.button_other);
        btnContinue = view.findViewById(R.id.button_continue);

        // 3. 성별 버튼 클릭 이벤트 설정
        btnWoman.setOnClickListener(v -> selectGender("여성"));
        btnMan.setOnClickListener(v -> selectGender("남성"));
        btnOther.setOnClickListener(v -> selectGender("그 외"));

        // 4. 저장 및 이동 로직
        btnContinue.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String age = etAge.getText().toString();
            String height = etHeight.getText().toString();
            String weight = etWeight.getText().toString();

            if (selectedGender.isEmpty()) {
                Toast.makeText(getContext(), "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            // PreferenceHelper를 사용하여 내부 저장소(SharedPreferences)에 저장
            prefHelper.setUserData(name, age, height, weight, selectedGender);

            // 저장 완료 후 메인 화면으로 이동
            Navigation.findNavController(v).navigate(R.id.action_userInfoFragment_to_mainFragment);
            });

        return view;
    }

    // [핵심] 단일 선택 로직: 선택된 버튼의 색상을 바꾸고 변수에 저장합니다.
    private void selectGender(String gender) {
        selectedGender = gender;

        // 모든 버튼을 일단 기본 배경으로 초기화
        btnWoman.setSelected(false);
        btnMan.setSelected(false);
        btnOther.setSelected(false);

        // 선택된 성별에 따라 버튼 상태 활성화 (android:state_selected 활용)
        if (gender.equals("여성")) btnWoman.setSelected(true);
        else if (gender.equals("남성")) btnMan.setSelected(true);
        else if (gender.equals("그 외")) btnOther.setSelected(true);
        updateButtonColors();
    }

    private void updateButtonColors() {
        int selectedBg = R.drawable.bg_rounded_button_continue;
        int defaultBg = R.drawable.bg_rounded_color;

        int whiteColor = Color.WHITE;
        int grayColor = Color.parseColor("#FF828282");

        if (selectedGender.equals("여성")) {
            btnWoman.setBackgroundResource(selectedBg);
            btnWoman.setTextColor(whiteColor);
        } else {
            btnWoman.setBackgroundResource(defaultBg);
            btnWoman.setTextColor(grayColor);
        }

        if (selectedGender.equals("남성")) {
            btnMan.setBackgroundResource(selectedBg);
            btnMan.setTextColor(whiteColor);
        } else {
            btnMan.setBackgroundResource(defaultBg);
            btnMan.setTextColor(grayColor);
        }

        if (selectedGender.equals("그 외")) {
            btnOther.setBackgroundResource(selectedBg);
            btnOther.setTextColor(whiteColor);
        } else {
            btnOther.setBackgroundResource(defaultBg);
            btnOther.setTextColor(grayColor);
        }
    }
}
