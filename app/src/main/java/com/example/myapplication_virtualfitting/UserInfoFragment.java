package com.example.myapplication_virtualfitting;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class UserInfoFragment extends Fragment {

    private EditText etName, etAge, etHeight, etWeight;
    private Button btnWoman, btnMan, btnOther, btnContinue;
    private String selectedGender = ""; // 선택된 성별을 담을 변수
    private String receivedEmail = ""; // 넘어온 이메일을 담을 변수
    private TextView tvTitle;
    private static final String TAG = "UserInfoFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 레이아웃 불러오기
        Log.d(TAG, "화면 전환됨");
        return inflater.inflate(R.layout.user_info_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //이메일 받기
        if (getArguments() != null) { //전달받은 번들이 있는지 (이메일이 넘어왔는지) 확인
            receivedEmail = getArguments().getString("userEmail");
            Log.d(TAG, "전달받은 이메일: " + receivedEmail);}

        // 뷰 찾기
        etName = view.findViewById(R.id.field_name);
        etAge = view.findViewById(R.id.field_age);
        etHeight = view.findViewById(R.id.field_height);
        etWeight = view.findViewById(R.id.field_weight);
        tvTitle = view.findViewById(R.id.user_info_title);

        btnWoman = view.findViewById(R.id.button_woman);
        btnMan = view.findViewById(R.id.button_man);
        btnOther = view.findViewById(R.id.button_other);
        btnContinue = view.findViewById(R.id.button_continue);

        //DB에 정보가 있는지 조회
        if (!receivedEmail.isEmpty()) {
            AppDatabase db = AppDatabase.getDatabase(getContext());
            User existingUser = db.userDao().getUserByEmail(receivedEmail);

            // 있으면 메인 화면에서 넘어왔다는 뜻
            if (existingUser != null) { // 각 필드에 값 채움
                Log.d(TAG, "사용자 정보 찾음: " + receivedEmail + " " + existingUser.name);
                tvTitle.setText("사용자 정보를 수정해 주세요");
                etName.setText(existingUser.name);
                etAge.setText(existingUser.age);
                etHeight.setText(existingUser.height);
                etWeight.setText(existingUser.weight);
                selectGender(existingUser.gender);
            }
        }

        // 성별 버튼 클릭 이벤트
        btnWoman.setOnClickListener(v -> selectGender("여성"));
        btnMan.setOnClickListener(v -> selectGender("남성"));
        btnOther.setOnClickListener(v -> selectGender("그 외"));

        // 계속 버튼: 저장 및 이동 로직
        btnContinue.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String height = etHeight.getText().toString().trim();
            String weight = etWeight.getText().toString().trim();
            String email = receivedEmail;
            String gender = selectedGender;

            // 유효성 검사(빈칸 있는지)
            if (email.isEmpty()) {
                Toast.makeText(getContext(), "오류: 이메일 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.isEmpty() || age.isEmpty() || height.isEmpty()|| weight.isEmpty() || gender.isEmpty()) {
                Toast.makeText(getContext(), "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // DB에 저장
            AppDatabase db = AppDatabase.getDatabase(getContext());
            db.userDao().insert(new User(email, name, age, height, weight, gender));
            Log.d(TAG, "==사용자 정보 저장 중==");
            Log.d(TAG, "email: " + email);
            Log.d(TAG, "name: " + name);
            Log.d(TAG, "age: " + age);
            Log.d(TAG, "height: " + height);
            Log.d(TAG, "weight: " + weight);
            Log.d(TAG, "gender: " + gender);
            Log.d(TAG, "====================");
            // 저장 완료 후 메인 화면으로 이동
            Toast.makeText(getContext(), "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle(); //입력받은 이메일을 담을 번들 생성
            bundle.putString("userEmail", email); //키와 값 저장
            Navigation.findNavController(v).navigate(R.id.action_userInfoFragment_to_mainFragment, bundle);
            });

    }

    // 선택된 버튼 색상 변경 / 변수에 저장
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
