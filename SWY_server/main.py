from fastapi import FastAPI, File, UploadFile
from fastapi.responses import FileResponse
from pydantic import BaseModel, Field # 🌟 Field 추가됨!
from motor.motor_asyncio import AsyncIOMotorClient
from typing import Optional
from rembg import remove
from PIL import Image
import google.generativeai as genai
import json
import io
import os
import uuid
from bson import ObjectId  

# 🌟 강력한 보안을 위한 암호화 라이브러리 추가
from passlib.context import CryptContext

app = FastAPI()

# ==========================================
# 0. 보안 및 AI 기본 세팅
# ==========================================
# 비밀번호 암호화 도구 세팅 (bcrypt 알고리즘 적용)
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# main.py의 get_password_hash 함수 수정
def get_password_hash(password):
    # 비밀번호가 72자를 넘을 경우, 앞에서부터 72자까지만 자릅니다 (bcrypt 제약 대응)
    if isinstance(password, str):
        password = password.encode('utf-8')
    
    # 72바이트까지만 잘라서 해싱 진행
    truncated_password = password[:72]
    return pwd_context.hash(truncated_password)

def verify_password(plain_password, hashed_password):
    """입력한 비밀번호와 DB의 해시 비밀번호가 일치하는지 검증하는 함수"""
    return pwd_context.verify(plain_password, hashed_password)

# Gemini AI 설정 (발급받은 API 키 유지)
GOOGLE_API_KEY = "여기에_키를_입력하세요"
genai.configure(api_key=GOOGLE_API_KEY)
model = genai.GenerativeModel('gemini-2.5-flash')

# ==========================================
# 1. MongoDB 연결 및 사진 저장 폴더 세팅
# ==========================================
MONGO_URL = "여기에_키를_입력하세요"
client = AsyncIOMotorClient(MONGO_URL)
db = client.virtual_fitting_db 

SAVE_DIR = "saved_clothes"
os.makedirs(SAVE_DIR, exist_ok=True)

# ==========================================
# 2. 데이터 규격 (Pydantic Models) - 🌟 Swagger UI 예쁘게 꾸미기 적용!
# ==========================================
class User(BaseModel):
    email: str = Field(..., description="사용자 이메일 (로그인 ID)", examples=["test@naver.com"])
    password: str = Field(..., description="비밀번호 (서버에는 암호화되어 저장됨)", examples=["1234"])
    name: str = Field(..., description="사용자 이름", examples=["홍길동"])
    age: str = Field(..., description="나이", examples=["25"])
    height: str = Field(..., description="키 (cm)", examples=["175"])
    weight: str = Field(..., description="몸무게 (kg)", examples=["70"])
    gender: str = Field(..., description="성별 (남/여)", examples=["남"])
    preferred_style: str = Field(default="캐주얼", description="선호하는 스타일 (예: 미니멀, 캐주얼, 스트릿)")
    preferred_color: str = Field(default="무채색", description="선호하는 색상톤")

class LoginRequest(BaseModel):
    email: str = Field(..., description="가입한 이메일", examples=["test@naver.com"])
    password: str = Field(..., description="비밀번호", examples=["1234"])

class Cloth(BaseModel):
    user_email: str = Field(..., description="옷 소유자의 이메일", examples=["test@naver.com"])
    season: str = Field(..., description="계절", examples=["여름"])
    part: str = Field(..., description="옷 종류 (상의, 하의 등)", examples=["상의"])
    thickness: str = Field(..., description="옷 두께", examples=["보통"])
    length: str = Field(..., description="기장", examples=["반팔"])
    image_url: str = Field(..., description="서버에 저장된 누끼 이미지 파일명", examples=["uuid-1234-abcd.png"])
    mapped_asset_id: Optional[str] = Field(None, description="유니티 AR 매핑용 ID", examples=["shirt_01"])

class ClothUpdate(BaseModel):
    season: str = Field(..., description="수정할 계절", examples=["가을"])
    part: str = Field(..., description="수정할 옷 종류", examples=["상의"])
    thickness: str = Field(..., description="수정할 두께", examples=["두꺼움"])
    length: str = Field(..., description="수정할 기장", examples=["긴팔"])    

# ==========================================
# 3. API 엔드포인트 - 🌟 태그(tags)와 설명(summary) 달기 완료!
# ==========================================

# 👤 1. 회원가입 API
@app.post("/users", tags=["1. 회원 관리"], summary="새로운 사용자 회원가입")
async def create_user(req: User):
    existing_user = await db.users.find_one({"email": req.email})
    if existing_user:
        return {"status": "fail", "message": "이미 가입된 이메일입니다."}
    
    try:
        user_dict = req.model_dump()
    except AttributeError:
        user_dict = req.dict()
    
    user_dict["password"] = get_password_hash(req.password)
    
    await db.users.insert_one(user_dict)
    return {"status": "success", "message": "회원가입 완료 및 정보 보호 적용됨!"}

# 🔐 2. 로그인 API
@app.post("/login", tags=["1. 회원 관리"], summary="사용자 로그인 (비밀번호 검증)")
async def login(req: LoginRequest):
    user = await db.users.find_one({"email": req.email})
    
    # 1. DB에 이메일 자체가 없을 때
    if not user:
        return {"status": "not_found", "message": "가입되지 않은 이메일입니다."}
    
    # 2. 이메일은 있는데 비밀번호가 맞을 때
    if verify_password(req.password, user["password"]):
        return {"status": "success", "message": "로그인 성공!"}
    # 3. 이메일은 있는데 비밀번호가 틀렸을 때
    else:
        return {"status": "wrong_password", "message": "비밀번호가 일치하지 않습니다."}
    
# 🔍 2-1. 전체 회원 목록 조회 API (GET) - 관리자용
@app.get("/users", tags=["1. 회원 관리"], summary="가입된 전체 회원 목록 조회 (관리자용)")
async def get_all_users():
    cursor = db.users.find({})
    users = await cursor.to_list(length=100) # 최대 100명까지 불러오기
    
    # MongoDB의 고유 ID를 문자로 바꾸고, 보안상 비밀번호는 화면에 안 보이게 가립니다!
    for user in users:
        user["_id"] = str(user["_id"])
        if "password" in user:
            del user["password"]
            
    return {"status": "success", "data": users}    

# 👕 3. AI 누끼 및 옷 속성 분석 API
@app.post("/upload-cloth/", tags=["2. AI 분석 및 옷 추가"], summary="사진 누끼 제거 및 AI 속성 분석")
async def upload_and_remove_background(file: UploadFile = File(...)):
    input_image = await file.read()
    output_image_bytes = remove(input_image)
    image = Image.open(io.BytesIO(output_image_bytes))
    file_name = f"{uuid.uuid4()}.png"
    file_path = os.path.join(SAVE_DIR, file_name)
    image.save(file_path, format="PNG")
    
    prompt = """
    너는 패션 전문가야. 제공된 옷 사진을 보고 속성을 분석해서 반드시 아래 JSON 형식으로만 대답해. 다른 설명은 절대 추가하지 마.
    {
        "season": "봄, 여름, 가을, 겨울 중 하나를 선택",
        "part": "상의, 하의, 아우터, 원피스 중 하나를 선택",
        "thickness": "얇음, 보통, 두꺼움 중 하나를 선택",
        "length": "반팔/반바지, 긴팔/긴바지, 민소매 중 하나를 선택"
    }
    """
    try:
        ai_ready_image = image.convert("RGB")
        response = model.generate_content([prompt, ai_ready_image])
        response_text = response.text.strip().replace('```json', '').replace('```', '')
        ai_data = json.loads(response_text)
    except Exception as e:
        print(f"🚨 AI 분석 에러 상세 내용: {e}")
        ai_data = {
            "season": "알 수 없음", "part": "알 수 없음", 
            "thickness": "알 수 없음", "length": "알 수 없음"
        }
    
    return {
        "status": "success", 
        "message": "누끼 제거 및 AI 분석 완료!", 
        "file_name": file_name,
        "ai_analysis": ai_data
    }

# 💾 4. 최종 옷 정보 DB 저장 API
@app.post("/clothes", tags=["2. AI 분석 및 옷 추가"], summary="새로운 옷 정보를 DB에 저장")
async def create_cloth(cloth: Cloth):
    await db.clothes.insert_one(cloth.model_dump())
    return {"status": "success", "message": "MongoDB에 의류 정보 저장 완료!"}

# 🖼️ 5. 저장된 누끼 이미지 불러오기 API
@app.get("/clothes-image/{image_name}", tags=["2. AI 분석 및 옷 추가"], summary="저장된 누끼 이미지 불러오기")
async def get_cloth_image(image_name: str):
    file_path = os.path.join(SAVE_DIR, image_name)
    if os.path.exists(file_path):
        return FileResponse(file_path)
    return {"error": "이미지를 찾을 수 없습니다."}

# ==========================================
# 👕 [나의 옷장] 관련 API 3종 세트
# ==========================================

# 1. 특정 사용자의 옷 목록 전체 가져오기 (GET)
@app.get("/clothes/{email}", tags=["3. 나의 옷장"], summary="내 옷장에 저장된 모든 옷 불러오기")
async def get_my_clothes(email: str):
    cursor = db.clothes.find({"user_email": email})
    clothes = await cursor.to_list(length=100)
    
    for cloth in clothes:
        cloth["_id"] = str(cloth["_id"]) 
        
    return {"status": "success", "data": clothes}

# 2. 옷 상세 정보 수정하기 (PUT)
@app.put("/clothes/{cloth_id}", tags=["3. 나의 옷장"], summary="저장된 옷의 상세 정보 수정(업데이트)")
async def update_cloth(cloth_id: str, update_data: ClothUpdate):
    try:
        try:
            update_dict = update_data.model_dump()
        except AttributeError:
            update_dict = update_data.dict()

        result = await db.clothes.update_one(
            {"_id": ObjectId(cloth_id)}, 
            {"$set": update_dict}
        )
        
        if result.matched_count == 1:
            return {"status": "success", "message": "옷 정보가 정상적으로 반영되었습니다."}
        
        return {"status": "fail", "message": "수정할 옷을 찾지 못했습니다."}
    except Exception as e:
        return {"status": "error", "message": f"수정 중 에러 발생: {str(e)}"}

# 3. 옷 삭제하기 (DELETE)
@app.delete("/clothes/{cloth_id}", tags=["3. 나의 옷장"], summary="옷장에서 특정 옷 영구 삭제")
async def delete_cloth(cloth_id: str):
    try:
        result = await db.clothes.delete_one({"_id": ObjectId(cloth_id)})
        
        if result.deleted_count == 1:
            return {"status": "success", "message": "옷이 성공적으로 삭제되었습니다."}
        return {"status": "fail", "message": "삭제할 옷을 찾지 못했습니다."}
    except Exception as e:
        return {"status": "error", "message": f"삭제 중 에러 발생: {str(e)}"}
