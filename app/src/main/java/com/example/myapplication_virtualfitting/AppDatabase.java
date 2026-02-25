package com.example.myapplication_virtualfitting;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// 어떤 테이블을 쓸지 정의
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // UserDao 추상 메서드
    public abstract UserDao userDao();

    // 싱글턴 패턴(앱 전체에서 하나만 생성해야하기 때문에)
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "user_database") // DB 파일 이름
                            // 테스트를 위해 메인 스레드에서 DB 접근을 허용
                            // (원래는 비동기 처리가 원칙이지만, 확인을 위해 잠시 허용)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}