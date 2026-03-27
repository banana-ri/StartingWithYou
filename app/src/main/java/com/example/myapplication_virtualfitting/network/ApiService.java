package com.example.myapplication_virtualfitting.network;

import com.example.myapplication_virtualfitting.Cloth;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // 1. 로그인
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // 2. 회원가입
    @POST("/users")
    Call<LoginResponse> signUpUser(@Body UserData userData);

    // 3. 내 옷장 목록 가져오기 (GET)
    @GET("/clothes/{email}")
    Call<ClothListResponse> getMyClothes(@Path("email") String email);

    // 4. 갤러리 사진을 서버로 보내 누끼 & AI 분석 요청 (파일 업로드)
    @Multipart
    @POST("/upload-cloth/")
    Call<UploadResponse> uploadClothImage(@Part MultipartBody.Part file);

    @DELETE("clothes/{id}")
    Call<Void> deleteCloth(@Path("id") String clothId);

    @PUT("clothes/{id}")
    Call<Void> updateCloth(@Path("id") String clothId, @Body Cloth cloth);

    // 5. AI 분석이 끝난 최종 옷 정보를 MongoDB에 저장
    @POST("/clothes")
    Call<Void> saveClothToDB(@Body Cloth cloth);
}