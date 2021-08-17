package com.project.moyeomoyeo

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL

class CreateClub : AppCompatActivity() {


    val REQUEST_GALLARY_LOGO = 1
    val REQUEST_GALLARY_IMAGE = 2

    var logoPath = ""
    var ImagePath = ""

    var logoImage = ""

    lateinit var areaRadio1 : RadioGroup
    lateinit var areaRadio2 : RadioGroup
    lateinit var fieldRadio1 : RadioGroup
    lateinit var fieldRadio2 : RadioGroup
    var area = 0
    var field = 0

    val TAG = "모임생성"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_club)

        areaRadio1 = findViewById<RadioGroup>(R.id.Area_RadioGroup1)
        areaRadio2 = findViewById<RadioGroup>(R.id.Area_RadioGroup2)
        fieldRadio1 = findViewById<RadioGroup>(R.id.Field_RadioGroup1)
        fieldRadio2 = findViewById<RadioGroup>(R.id.Field_RadioGroup2)

        var jwt = "null"

        if(intent.getStringExtra("jwt") != null){
            jwt = intent.getStringExtra("jwt")!!

        }else{
            Log.d(TAG, "jwt 토큰이 없습니다")
        }

        var sort = 0

        val createBttn = findViewById<Button>(R.id.CreateClub_Bttn)
        val sortRadio = findViewById<RadioGroup>(R.id.Sort_radioGroup)
        val nameText = findViewById<EditText>(R.id.Name_EditText)
        val description = findViewById<EditText>(R.id.Description_EditText)
        val logoBttn = findViewById<Button>(R.id.LogoBtn)
        val imageBttn = findViewById<Button>(R.id.ImageBtn)

        val detailDescription = findViewById<EditText>(R.id.detailDescription_EditText)

        //종류 라디오 버튼
        sortRadio.setOnCheckedChangeListener{ group, checkId ->
            when(checkId){
                R.id.ClubRBtn -> sort = 1
                R.id.StudyRBtn -> sort = 2
                R.id.SpecRBtn -> sort = 3
                R.id.EtcRBtn -> sort = 4
            }
        }

        areaRadio1.clearCheck()
        areaRadio2.clearCheck()
        areaRadio1.setOnCheckedChangeListener(areaListener1)
        areaRadio1.setOnCheckedChangeListener(areaListener2)

        fieldRadio1.clearCheck()
        fieldRadio2.clearCheck()
        fieldRadio1.setOnCheckedChangeListener(fieldListener1)
        fieldRadio1.setOnCheckedChangeListener(fieldListener2)

        //모임 만들기 버튼
        createBttn.setOnClickListener {

            val client = OkHttpClient()

            val json = JSONObject()
            json.put("sortIdx",sort.toString())
            json.put("name",nameText.text)
            json.put("description",description.text)
            json.put("detailDescription",detailDescription.text)
            json.put("clubImage",ImagePath)
            json.put("areaIdx",area.toString())
            json.put("fieldIdx",field.toString())
            json.put("logoImage",logoPath)

            val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

            var url = "https://moyeo.shop/clubs"

            val request = Request.Builder()
                .header("x-access-token",jwt)
                .url(url)
                .post(body)
                .build()

            client.newCall(request).enqueue(object:Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d(TAG, "Fail")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d(TAG, "Success")

                    val result: String = response.body?.string() ?: "null"
                    var jsonObject = JSONObject(result)

                    Log.d(TAG, jsonObject.get("code").toString())
                    Log.d(TAG, jsonObject.get("message").toString())

                    if(jsonObject.getBoolean("isSuccess")){
                        showMessage("모임이 성공적으로 추가되었습니다")
                        finish()
                    }
                    else {
                        showMessage(jsonObject.get("message").toString())
                    }

                }
            })


        }

        logoBttn.setOnClickListener {
            openGallary(REQUEST_GALLARY_LOGO)
        }

        imageBttn.setOnClickListener {
            openGallary(REQUEST_GALLARY_IMAGE)
        }

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)      //뒤로가기 버튼 x
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기
    }

    //지역 라디오 버튼
    private val areaListener1 : RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { group, checkId ->
            when(checkId){
                R.id.Area1RBtn -> area = 1
                R.id.Area3RBtn -> area = 3
                R.id.Area5RBtn -> area = 5
                R.id.Area7RBtn -> area = 7
                R.id.Area9RBtn -> area = 9
            }
            if(checkId != -1){
                areaRadio2.setOnCheckedChangeListener(null)
                areaRadio2.clearCheck()
                areaRadio2.setOnCheckedChangeListener(areaListener2)
            }
        }

    private val areaListener2 : RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { group, checkId ->
            when(checkId){
                R.id.Area2RBtn -> area = 2
                R.id.Area4RBtn -> area = 4
                R.id.Area6RBtn -> area = 6
                R.id.Area8RBtn -> area = 8
                R.id.Area10RBtn -> area = 10
            }
            if(checkId != -1){
                areaRadio1.setOnCheckedChangeListener(null)
                areaRadio1.clearCheck()
                areaRadio1.setOnCheckedChangeListener(areaListener1)
            }
        }

    //분야 라디오 버튼
    private val fieldListener1 : RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { group, checkId ->
            when(checkId){
                R.id.Field1RBtn -> field = 1
                R.id.Field2RBtn -> field = 2
                R.id.Field3RBtn -> field = 3
                R.id.Field4RBtn -> field = 4
            }
            if(checkId != -1){
                fieldRadio2.setOnCheckedChangeListener(null)
                fieldRadio2.clearCheck()
                fieldRadio2.setOnCheckedChangeListener(fieldListener2)
            }
        }

    private val fieldListener2 : RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { group, checkId ->
            when(checkId){
                R.id.Field5RBtn -> field = 5
                R.id.Field6RBtn -> field = 6
                R.id.Field7RBtn -> field = 7
                R.id.Field8RBtn -> field = 8
            }
            if(checkId != -1){
                fieldRadio1.setOnCheckedChangeListener(null)
                fieldRadio1.clearCheck()
                fieldRadio1.setOnCheckedChangeListener(fieldListener1)
            }
        }

    private fun showMessage(message : String){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            Toast.makeText(
                this@CreateClub,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }, 0)

    }
    //갤러리 열기
    private fun openGallary(requestCode : Int) {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    //이미지 절대경로 반환
    fun absolutelyPath(path: Uri) : String{

        var proj : Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = contentResolver.query(path, proj, null, null, null)
        var index = c!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c.moveToFirst()

        var result = c.getString(index)

        return result
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode){
            1 -> {
                if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLARY_LOGO){

                    Log.d(TAG, data?.data.toString())

                    val uri = data?.data
                    logoPath = absolutelyPath(data?.data!!)

                    //Base 64 인코딩
/*
                    val ins : InputStream? = uri?.let{
                        applicationContext.contentResolver.openInputStream(
                            it
                        )
                    }

                    val img: Bitmap = BitmapFactory.decodeStream(ins)
                    ins?.close()
                    val resized = Bitmap.createScaledBitmap(img, 256,256, true)
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    resized.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
                    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                    val outStream = ByteArrayOutputStream()
                    val res : Resources = resources
                    logoImage = Base64.encodeToString(byteArray, NO_WRAP)


 */


                    Log.d(TAG, "로고 bitmap : $logoImage")

                    //textview에 로고 절대 경로 표시
                    findViewById<TextView>(R.id.Logo_text).text = logoPath

                }
                else{
                    Log.d(TAG, "로고 불러오기 실패")
                }
            }

            2 -> {
                if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLARY_IMAGE){
                    Log.d(TAG, data?.data.toString())
                    ImagePath = absolutelyPath(data?.data!!)
                    findViewById<TextView>(R.id.Image_text).text = ImagePath
                }
                else{
                    Log.d(TAG, "이미지 불러오기 실패")
                }
            }
        }
    }

    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }



    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //닫기 아이콘만 있는 툴바
        menuInflater.inflate(R.menu.closebttn_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Close ->
                finish()

        }
        return super.onOptionsItemSelected(item)

    }


}

