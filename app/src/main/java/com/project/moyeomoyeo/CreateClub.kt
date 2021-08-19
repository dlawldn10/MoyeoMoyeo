package com.project.moyeomoyeo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateClub : AppCompatActivity() {


    val REQUEST_GALLARY_LOGO = 1
    val REQUEST_GALLARY_IMAGE = 2

    var logoPath = ""
    var ImagePath = ""

    lateinit var logoUri : Uri
    lateinit var imageUri : Uri

    var logoURL  = ""
    var imageURL = ""

    lateinit var sortGroup : ArrayList<AppCompatButton>
    lateinit var fieldGroup : ArrayList<AppCompatButton>
    lateinit var areaGroup : ArrayList<AppCompatButton>

    lateinit var nameText : TextView
    lateinit var description : TextView
    lateinit var detailDescription : TextView

    var jwt = "null"

    var area = 0
    var field = 0
    var sort = 0

    val TAG = "모임생성"

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_club)


        if(intent.getStringExtra("jwt") != null){
            jwt = intent.getStringExtra("jwt")!!

        }else{
            Log.d(TAG, "jwt 토큰이 없습니다")
        }

        sortGroup = ArrayList()
        fieldGroup = ArrayList()
        areaGroup = ArrayList()

        val createBttn = findViewById<Button>(R.id.CreateClub_Bttn)
        nameText = findViewById<EditText>(R.id.Name_EditText)
        description = findViewById<EditText>(R.id.SubNameEditText)
        val logoBtn = findViewById<AppCompatImageButton>(R.id.LogoBttn)
        val imageBtn = findViewById<AppCompatImageButton>(R.id.ImageBttn)

        detailDescription = findViewById<EditText>(R.id.detailDescription_EditText)

        sortGroup.add(findViewById(R.id.sort1_btn))
        sortGroup.add(findViewById(R.id.sort2_btn))
        sortGroup.add(findViewById(R.id.sort3_btn))
        sortGroup.add(findViewById(R.id.sort4_btn))

        fieldGroup.add(findViewById(R.id.field1_btn))
        fieldGroup.add(findViewById(R.id.field2_btn))
        fieldGroup.add(findViewById(R.id.field3_btn))
        fieldGroup.add(findViewById(R.id.field4_btn))
        fieldGroup.add(findViewById(R.id.field5_btn))

        areaGroup.add(findViewById(R.id.area1_btn))
        areaGroup.add(findViewById(R.id.area2_btn))
        areaGroup.add(findViewById(R.id.area3_btn))
        areaGroup.add(findViewById(R.id.area4_btn))
        areaGroup.add(findViewById(R.id.area5_btn))
        areaGroup.add(findViewById(R.id.area6_btn))
        areaGroup.add(findViewById(R.id.area7_btn))
        areaGroup.add(findViewById(R.id.area8_btn))
        areaGroup.add(findViewById(R.id.area9_btn))
        areaGroup.add(findViewById(R.id.area10_btn))

        //모임 만들기 버튼
        createBttn.setOnClickListener {

            if(nameText.text.trim() == ""){
                Toast.makeText(this, "이름을 입력해주세요",Toast.LENGTH_SHORT).show()
            }
            else if(sort == 0){
                Toast.makeText(this, "카테고리를 선택해주세요",Toast.LENGTH_SHORT).show()
            }
            else if(description.text.trim() == ""){
                Toast.makeText(this, "설명을 입력해주세요",Toast.LENGTH_SHORT).show()
            }
            else if(detailDescription.text.trim() == ""){
                Toast.makeText(this, "상세 설명을 입력해주세요",Toast.LENGTH_SHORT).show()
            }
            else{
                UploadLogoFirebase()
            }




        }

        logoBtn.setOnClickListener {
            openGallary(REQUEST_GALLARY_LOGO)
        }

        imageBtn.setOnClickListener {
            openGallary(REQUEST_GALLARY_IMAGE)
        }

        for(i in 0 until sortGroup.size){
            sortGroup[i].setOnClickListener {
                for(j in 0 until sortGroup.size){
                    sortGroup[j].isSelected = false
                    sortGroup[j].setTextColor(Color.BLACK)
                }
                sortGroup[i].isSelected = true
                sortGroup[i].setTextColor(Color.WHITE)
                sort = i + 1
                Log.d(TAG, "sort : $sort")

            }
        }

        for(i in 0 until fieldGroup.size){
            fieldGroup[i].setOnClickListener {
                for(j in 0 until fieldGroup.size){
                    fieldGroup[j].isSelected = false
                    fieldGroup[j].setTextColor(Color.BLACK)
                }
                fieldGroup[i].isSelected = true
                fieldGroup[i].setTextColor(Color.WHITE)
                field = i + 1
                Log.d(TAG, "field : $field")

            }
        }

        for(i in 0 until areaGroup.size){
            areaGroup[i].setOnClickListener {
                for(j in 0 until areaGroup.size){
                    areaGroup[j].isSelected = false
                    areaGroup[j].setTextColor(Color.BLACK)
                }
                areaGroup[i].isSelected = true
                areaGroup[i].setTextColor(Color.WHITE)
                area = i + 1
                Log.d(TAG, "area : $area")

            }
        }

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)      //뒤로가기 버튼 x
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기
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

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
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

    //Firebase에 로고 업로드 후 url 받아오기
    private fun UploadLogoFirebase(){

            var pd = ProgressDialog(this@CreateClub)
            pd.setTitle("저장중 1/2")
            pd.show()

            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val fileName = formatter.format(now)
            val storageReference =
                FirebaseStorage.getInstance().reference.child("logos/$fileName .jpg")

            storageReference.putFile(logoUri).addOnSuccessListener {
                //Toast.makeText(this@CreateClub, "사진 저장 성공", Toast.LENGTH_SHORT).show()
                storageReference.downloadUrl.addOnSuccessListener {
                    pd.dismiss()
                    logoURL = it.toString()
                    UploadImageFirebase()
                    Log.d(TAG, "logo URL : $logoURL")
                }
            }
                .addOnFailureListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(this@CreateClub, p0.message, Toast.LENGTH_LONG).show()
                }
    }

    private fun UploadImageFirebase(){

        var pd = ProgressDialog(this@CreateClub)
        pd.setTitle("저장중 2/2")
        pd.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference =
            FirebaseStorage.getInstance().reference.child("images/$fileName .jpg")

        storageReference.putFile(imageUri).addOnSuccessListener {
            //Toast.makeText(this@CreateClub, "사진 저장 성공", Toast.LENGTH_SHORT).show()
            storageReference.downloadUrl.addOnSuccessListener {
                pd.dismiss()
                imageURL = it.toString()
                CreatClub()
                Log.d(TAG, "image URL : $imageURL")
            }
        }
            .addOnFailureListener { p0 ->
                pd.dismiss()
                Toast.makeText(this@CreateClub, p0.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun CreatClub(){

        val client = OkHttpClient()

        val json = JSONObject()
        json.put("sortIdx",sort.toString())
        json.put("name",nameText.text)
        json.put("description",description.text)
        json.put("detailDescription",detailDescription.text)
        json.put("clubImage",imageURL)
        json.put("areaIdx",area.toString())
        json.put("fieldIdx",field.toString())
        json.put("logoImage",logoURL)

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode){
            1 -> {
                if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLARY_LOGO){

                    Log.d(TAG, data?.data.toString())

                    logoUri = data?.data!!

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

                    imageUri = data?.data!!

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

