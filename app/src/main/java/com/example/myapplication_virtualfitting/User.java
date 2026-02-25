package com.example.myapplication_virtualfitting;
//DB의 테이블 역할을 하는 자바 클래스
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 이 클래스가 DB의 테이블임을 명시
@Entity(tableName = "user_table")
public class User {

    // 이메일을 PrimaryKey(고유 키)로 설정
    @PrimaryKey
    @NonNull
    public String email; //고유 키
    public String name;
    public String age;
    public String height;
    public String weight;
    public String gender;

    // 생성자 (데이터를 생성할 때 사용)
    public User(@NonNull String email, String name, String age, String height, String weight, String gender) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
    }
}