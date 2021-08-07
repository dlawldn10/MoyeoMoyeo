package com.project.moyeomoyeo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*

//카카오톡 로그인 액티비티
class LoginActivity : AppCompatActivity() {


    //뒤로가기 타이머
    var backKeyPressedTime: Long = 0

    var TAG = "로그인"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        var loginBttn = findViewById<Button>(R.id.Login_Button)
        var kakaoLoginBttn = findViewById<Button>(R.id.Kakao_Login_Bttn)

        loginBttn.setOnClickListener {
            //기능만 넣음. 실제 로그인 안됨.
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

//        var keyHash = Utility.getKeyHash(this)
//        Log.d("로그인", "" + keyHash)


        //카카오톡 로그인
        KakaoSdk.init(this, "08bbd9429cd88ac8922553576fae5eaa")
        kakaoLoginBttn.setOnClickListener {
            // 로그인 공통 callback 구성
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->

                if (error != null) {
                    Log.d(TAG, "로그인 실패", error)
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
                else if (token != null) {
                    Log.d(TAG, "로그인 성공 ${token.accessToken}")

                    //사용자 정보 요청하기
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        }
                        else if (user != null) {
                            var scopes = mutableListOf<String>()

                            //필요한 동의 항목 체크 -> 하나라도 NeedsAgreement가 true이면 추가 동의가 필요함.
                            if (user.kakaoAccount?.emailNeedsAgreement == true) { scopes.add("account_email") }
                            if (user.kakaoAccount?.genderNeedsAgreement == true) { scopes.add("gender") }

                            //필요 항목 미충족 시 추가 동의 받기
                            if (scopes.count() > 0) {

                                Log.d(TAG, "사용자에게 추가 동의를 받아야 합니다.")

                                UserApiClient.instance.loginWithNewScopes(this, scopes) { token, error ->
                                    if (error != null) {
                                        Log.e(TAG, "사용자 추가 동의 실패", error)
                                    } else {
                                        Log.d(TAG, "사용자 추가 동의 성공. allowed scopes: ${token!!.scopes}")

                                        // 사용자 정보 재요청
                                        UserApiClient.instance.me { user, error ->
                                            if (error != null) {
                                                Log.e(TAG, "사용자 정보 요청 실패", error)
                                            }
                                            else if (user != null) {
                                                Log.i(TAG, "사용자 정보 요청 성공")
                                                //우리쪽 서버로 토큰 보내기
                                                CoroutineScope(IO).async {
                                                    PostUserToken(token)
                                                }
                                            }
                                        }
                                    }
                                }
                            }else{
                                //필요 항목 충족 시 바로 로그인.
                                CoroutineScope(IO).async {
                                    PostUserToken(token)
                                }

                            }
                        }
                    }



                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }


        }


    }

    //뒤로가기 두번 누르면 종료
    override fun onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish()
        }

    }

    //코루틴에서 호출
    //홈 액티비티로 전환
    fun gotoHome(userData : UserData){
        val intent = Intent(this, HomeActivity::class.java)
        //토큰 정보를 다음 액티비티(홈 액티비티)로 넘긴다
        intent.putExtra("userData", userData)
        startActivity(intent)
    }


    //코루틴에서 호출
    //okhttp 비동기 방식 - POST
    //서버로 토큰 보내기
    fun PostUserToken(token: OAuthToken?){
        //1.클라이언트 만들기
        val client = OkHttpClient.Builder().build()


        //2.요청
        val formBody: FormBody = FormBody.Builder()
            .add("accessToken", token!!.accessToken)     //!! <- 추후 수정 필요
            .build()
        val req = Request.Builder().url("https://moyeo.shop/kakao/sign").post(formBody).build()


        //3.응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                CoroutineScope(Main).launch {
                    Toast.makeText(applicationContext, "서버에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "다시 시도하세요.", Toast.LENGTH_SHORT).show()
                }
                Log.d("로그인 에러", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {

                try {
                    //주의* response.body!!.string() 값은 한번밖에 호출 못함.
                    var jsonString = response.body!!.string().toString()
                    var jsonObject = JSONObject(jsonString)

                    if(jsonObject.getBoolean("isSuccess")){
                        var jsonResultObject = JSONObject(jsonObject.get("result").toString())
                        var Data = UserData(
                            jsonResultObject.get("jwt") as String,
                            jsonResultObject.get("userIdx") as Int,
                            jsonResultObject.get("member") as String
                        )
                        Log.d(TAG, Data.jwt)
                        CoroutineScope(Main).launch {
                            Toast.makeText(applicationContext, "성공적으로 로그인 되었습니다", Toast.LENGTH_SHORT).show()
                        }
                        //우리쪽 서버에 저장하기까지 완료 되면 다음 액티비티로.
                        gotoHome(Data)

                        //로그인 액티비티는 끝낸다.
                        finish()

                    }else{

                        //작업 실패 했을때
                        Log.d(TAG, jsonObject.get("code").toString())
                        Log.d(TAG, jsonObject.get("message").toString())
                    }

                }catch (e : JSONException){
                    Log.d(TAG, "JSON 오류")
                }
                


            }

        })
    }



}