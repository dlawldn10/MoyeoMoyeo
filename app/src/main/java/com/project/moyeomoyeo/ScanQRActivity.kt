package com.project.moyeomoyeo

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ScanQRActivity : AppCompatActivity() {

    var clubIdx = ""
    var jwt = ""
    val TAG = "QR스캐너"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_qractivity)

        if(intent.getStringExtra("clubIdx") != null){
            clubIdx = intent.getStringExtra("clubIdx")!!
        }
        else{
            Log.d(TAG, "clubIdx 값이 없습니다")
        }

        if(intent.getStringExtra("jwt") != null){
            jwt = intent.getStringExtra("jwt")!!
        }
        else{
            Log.d(TAG, "jwt 토큰이 없습니다")
        }

        initQRcodeScanner()
    }

    private fun initQRcodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(true)
        integrator.setPrompt("QR코드를 인식해주세요.")
        integrator.initiateScan()
    }

    private fun Attend() {
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.Default).async {

                val url = "https://moyeo.shop/clubs/$clubIdx/attendance"

                Log.d(TAG, "url : $url")

                val client = OkHttpClient.Builder().build()
                val formbody = FormBody.Builder()
                    .build()
                val req = Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", jwt)
                    .post(formbody)
                    .build()

                val response = client.newCall(req).execute()

                var jsonObject = JSONObject(response.body?.string())

                if(jsonObject.getBoolean("isSuccess")){
                    Log.d(TAG, jsonObject.getString("message"))
                    Log.d(TAG, jsonObject.getString("code"))
                }
            }.await()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result : IntentResult =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        val now = System.currentTimeMillis()
        val date = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(now)
        val datekor = SimpleDateFormat("MM월 dd일", Locale.KOREAN).format(now)

        val qrdate = (clubIdx + date).trim()

        if(result != null){
            if(result.contents == null){
                finish()
            }
            else {

                if(result.contents == qrdate){
                    val builder = AlertDialog.Builder(this)
                    val message = datekor + " 출석합니다."
                    builder.setTitle("출석체크")
                    builder.setMessage(message)
                    builder.setPositiveButton("확인"){dialog:DialogInterface, i: Int->
                        Attend()
                        finish()
                    }
                    builder.setNegativeButton("취소"){dialog:DialogInterface, i: Int->
                        finish()
                    }
                    builder.show()
                }

            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}