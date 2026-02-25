package com.example.myapplication_virtualfitting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 레이아웃 불러오기
        return inflater.inflate(R.layout.sign_in_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 뷰 찾기
        Button btnContinue = view.findViewById(R.id.button_continue);
        LinearLayout btnGoogle = view.findViewById(R.id.button_google);
        LinearLayout btnNaver = view.findViewById(R.id.naver_button);
        EditText emailEditText = view.findViewById(R.id.field_email);

        AppDatabase db = AppDatabase.getDatabase(getContext()); //DB 본체 불러오기
        UserDao userDao = db.userDao(); //Dao 불러오기

        // 테스트용 데이터 삽입
        new Thread(() -> {
            //테스트용 유저 생성
            User testUser = new User("test@naver.com", "홍길동", "25", "180", "75", "남성");
            userDao.insert(testUser); //삽입
        }).start();

        // 클릭 리스너
        // 계속 버튼: 사용자 정보 입력 화면 / 메인 화면으로 이동
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                String email = emailEditText.getText().toString().trim(); // 이메일 입력창에서 텍스트 가져오기

                if (email.isEmpty()) { //입력된 값이 없음
                    Toast.makeText(getContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = userDao.getUserByEmail(email); //이메일로 유저 조회

                if (user != null) {// 기존 사용자
                    //환영 메시지
                    String welcomeMsg = String.format("%s님 환영합니다!", user.name);
                    Toast.makeText(getContext(), welcomeMsg, Toast.LENGTH_SHORT).show();
                    //메인 화면으로 이동 (정보를 입력할 필요가 없음)
                    Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainFragment); //메인 화면으로
                } else { //신규 사용자
                    Bundle bundle = new Bundle(); //입력받은 이메일을 담을 번들 생성
                    bundle.putString("userEmail", email); //키와 값 저장
                    Navigation.findNavController(v).navigate(
                            R.id.action_loginFragment_to_userInfoFragment,
                            bundle); //번들과 함께 정보 입력 화면으로
                }
            });
        }
        //구글 로그인 버튼
        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> {
                Toast.makeText(getContext(), "구글 로그인 기능 구현 예정", Toast.LENGTH_SHORT).show();
                // 구글 로그인 SDK 호출 로직
            });
        }
        //네이버 로그인 버튼
        if (btnNaver != null) {
            btnNaver.setOnClickListener(v -> {
                Toast.makeText(getContext(), "네이버 로그인 기능 구현 예정", Toast.LENGTH_SHORT).show();
                // 네이버 로그인 SDK 호출 로직
            });
        }

    }
}
