package com.example.myapplication_virtualfitting;

// 🌟 메모장 기능을 쓰기 위해 추가된 2줄

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import com.example.myapplication_virtualfitting.network.ApiService;
import com.example.myapplication_virtualfitting.network.LoginRequest;
import com.example.myapplication_virtualfitting.network.LoginResponse;
import com.example.myapplication_virtualfitting.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        EditText passwordEditText = view.findViewById(R.id.field_password);

        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                String email = emailEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(getContext(), "이메일을 먼저 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 칸이 숨겨져 있으면 보여주고 버튼 이름 변경
                if (passwordEditText.getVisibility() == View.GONE) {
                    passwordEditText.setVisibility(View.VISIBLE);
                    btnContinue.setText("로그인");
                    return;
                }

                String password = passwordEditText.getText().toString().trim();

                if (password.isEmpty()) {
                    Toast.makeText(getContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 서버로 로그인 요청 쏘기
                ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
                apiService.login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse result = response.body();

                            // 🌟 여기서부터 덮어쓰기! 🌟
                            if ("success".equals(result.status)) {
                                // 1. 완벽하게 성공!
                                Toast.makeText(getContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("USER_EMAIL", email);
                                editor.apply();

                                Log.d(TAG, "메모장에 이메일 저장 완벽 성공: " + email);
                                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainFragment);

                            } else if ("not_found".equals(result.status)) {
                                // 2. 이메일이 아예 없을 때 -> 회원가입으로 이동
                                Toast.makeText(getContext(), "계정 정보가 없습니다. 회원가입을 진행합니다.", Toast.LENGTH_SHORT).show();
                                Bundle bundle = new Bundle();
                                bundle.putString("userEmail", email);
                                bundle.putString("userPassword", password);
                                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_userInfoFragment, bundle);

                            } else if ("wrong_password".equals(result.status)) {
                                // 3. 🌟 우리가 원했던 기능! 비밀번호만 틀렸을 때 -> 안 넘어가고 토스트만 띄움!
                                Toast.makeText(getContext(), "비밀번호가 틀렸습니다. 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                            // 🌟 여기까지 덮어쓰기 끝! 🌟
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e(TAG, "서버 통신 실패", t);
                        Toast.makeText(getContext(), "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> Toast.makeText(getContext(), "구글 로그인 기능 구현 예정", Toast.LENGTH_SHORT).show());
        }
        if (btnNaver != null) {
            btnNaver.setOnClickListener(v -> Toast.makeText(getContext(), "네이버 로그인 기능 구현 예정", Toast.LENGTH_SHORT).show());
        }
    }
}