package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.OutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class LoginActivity : AppCompatActivity() {
    var TAG = "로그"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var loginBttn = findViewById<Button>(R.id.Login_Button)
        var kakaoLoginBttn = findViewById<Button>(R.id.Kakao_Login_Bttn)

        loginBttn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }


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
//                    Toast.makeText(this, "성공적으로 로그인 되었습니다", Toast.LENGTH_SHORT).show()

                    //서버로 로그인한 사용자 토큰 보내기
                    CoroutineScope(IO).launch {
                        val resultStr = withTimeoutOrNull(10000) {
                            sendToken(token)
                        }

                        if (resultStr != null) {
                            withContext(Main) {
                                Toast.makeText(applicationContext, "응답 성공", Toast.LENGTH_LONG)

                            }
                        }else{
                            Toast.makeText(applicationContext, "실패", Toast.LENGTH_LONG)

                        }
                    }

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)


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

    val RESULT_OK = "ok"
    val RESULT_FAIL = "fail"

    suspend fun sendToken(token: OAuthToken?): String {
        val testServer = URL("https://moyeo.shop/kakao/sign")
        var result = " "
        val myConnection = testServer.openConnection() as HttpsURLConnection
        myConnection.requestMethod = "POST"
        myConnection.setRequestProperty("X-ACCESS-TOKEN", token?.accessToken)

        myConnection.doOutput = true

        if(myConnection.responseCode == 100) {
            // Success
            // Further processing here
            Log.d("응답 ","성공")
            result = RESULT_OK
        } else {
            // Error handling code goes here
            Log.d("응답 ","실패")
            Log.d("응답 ",myConnection.responseCode.toString())
            result = RESULT_FAIL
        }
        return result
    }
}