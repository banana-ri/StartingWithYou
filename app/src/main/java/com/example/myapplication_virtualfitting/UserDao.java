package com.example.myapplication_virtualfitting;
//Data Access Object
//DB에 직접 접근해 조회, 수정, 삽입, 삭제 작업을 전담하는 객체
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UserDao {
    // 유저 정보 저장 (이미 있는 이메일이면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    // 이메일로 특정 유저 찾기
    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    // 모든 유저 목록 가져오기 (테스트용)
    @Query("SELECT * FROM user_table")
    List<User> getAllUsers();
}
