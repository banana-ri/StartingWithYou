package com.example.myapplication_virtualfitting;

import android.content.Context;
import android.content.SharedPreferences;

//사용자 정보 및 로그인 상태 저장
//DB를 구축하면서 필요 없어진 내용입니다.
public class PreferenceHelper {
    /*
    private static final String PREF_NAME = "user_data";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor = sharedPreferences.edit();

    public PreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    //이메일 저장
    public void setUserEmail(String email) {
        editor.putString("email", email);
        editor.apply();
    }

    // 데이터 저장 (이름, 나이, 키, 몸무게 등)
    public void setUserData(String name, String age, String height, String weight, String gender) {
        editor.putString("name", name);
        editor.putString("age", age);
        editor.putString("height", height);
        editor.putString("weight", weight);
        editor.putString("gender", gender);
        editor.apply(); // 비동기로 저장
    }

    // 특정 데이터 하나씩 가져오기
    public String getName() { return sharedPreferences.getString("name", ""); }
    public String getHeight() { return sharedPreferences.getString("height", ""); }
    public String getAge() { return sharedPreferences.getString("age", ""); }
    public String getWeight() { return sharedPreferences.getString("weight", ""); }
    public String getGender() { return sharedPreferences.getString("gender", ""); }
    public String getEmail() { return sharedPreferences.getString("email", ""); }
     */
}