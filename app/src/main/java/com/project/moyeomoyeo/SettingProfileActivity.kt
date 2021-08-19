package com.project.moyeomoyeo

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.project.moyeomoyeo.DataClass.MyData
import com.project.moyeomoyeo.DataClass.UserData
import java.text.SimpleDateFormat
import java.util.*

class SettingProfileActivity : AppCompatActivity() {

    var TAG = "프로필"

    val REQUEST_GALLARY_PROFILE = 1

    lateinit var profilePhotoUri : Uri
    var profilePhotoURL  = ""

    val progressList = arrayListOf<Int>(
        R.drawable.img_setting_nickname,
        R.drawable.img_setting_name,
        R.drawable.img_setting_phone,
        R.drawable.img_setting_photo
    )

    var progressNum : Int = 0

    var myData = MyData(0, "", "", "", "", 0)

    var userData = UserData("", 0, "")

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_profile)

        if(intent.getSerializableExtra("userData") != null){
            userData = intent.getSerializableExtra("userData") as UserData

        }else{
            Log.d("리스트 ", "멤버 조회 실패")
        }


        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기

        var nextBtn = findViewById<Button>(R.id.SettingProfileNext_Button)
        var editText = findViewById<EditText>(R.id.SettingProfile_EditText)
        var progressBar = findViewById<ProgressBar>(R.id.SettingProfile_ProgressBar)
        var imageView = findViewById<ImageView>(R.id.SettingProfile_ImageView)
        var imageBtn = findViewById<ImageView>(R.id.SettingProfile_ImageButton)


        imageView.setImageResource(progressList[progressNum])
        progressBar.setProgress(15, true)

        //다음으로 버튼을 눌렀을 때
        nextBtn.setOnClickListener {
            //에딧 텍스트가 비어있지 않고 닉네임 입력 페이지라면
            if (!editText.text.isNullOrEmpty() && progressNum == 0){
                //정보를 입력하고
                myData.nickname = editText.text.toString()

                //다음 과정으로 넘어가기
                progressNum++
                setDirImageView(progressNum)
                progressBar.setProgress(30, true)

            }else if(!editText.text.isNullOrEmpty() && progressNum == 1){
                //에딧 텍스트가 비어있지 않고 이름 입력 페이지라면
                myData.name = editText.text.toString()

                progressNum++
                setDirImageView(progressNum)
                progressBar.setProgress(45, true)

            }else if(!editText.text.isNullOrEmpty() && progressNum == 2) {
                //에딧 텍스트가 비어있지 않고 전화번호 입력 페이지라면
                myData.phoneNumber = editText.text.toString()

                progressNum++
                setDirImageView(progressNum)
                progressBar.setProgress(60, true)

                //프사 설정 ui를 꺼낸다.
                imageBtn.visibility = View.VISIBLE
                editText.visibility = View.GONE
                imageBtn.setOnClickListener {
                    openGallary(REQUEST_GALLARY_PROFILE)
                }

            }else if(progressNum == 3) {
                progressBar.setProgress(75, true)
                if (profilePhotoUri.toString().isNullOrEmpty()){
                    //기본 사진으로 설정하고

                    //관심분야를 선택하기 위해 다음 액티비티로 이동한다.
                    val intent = Intent(this, SettingLikeSortActivity::class.java)
                    intent.putExtra("userData", userData)
                    intent.putExtra("myData", myData)
                    startActivity(intent)

                }else{
                    UploadLogoFirebase()
                }

            }


        }
    }

    private fun openGallary(requestCode : Int) {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, requestCode)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home->{
                if (progressNum == 0){
                    //초기 로그인 화면으로 돌아가기

                }else{
                    progressNum--
                    setDirImageView(progressNum)
                }


            }


        }
        return super.onOptionsItemSelected(item)

    }

    fun setDirImageView(progressNum : Int){
        //키보드와 연결해주는.
        var imm : InputMethodManager? = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        var editText = findViewById<EditText>(R.id.SettingProfile_EditText)
        imm?.hideSoftInputFromWindow(editText.windowToken, 0)
        editText.text.clear()
        editText.clearFocus()
        findViewById<ImageView>(R.id.SettingProfile_ImageView).setImageResource(progressList[progressNum])


        findViewById<ImageView>(R.id.SettingProfile_ImageButton).visibility = View.GONE
        editText.visibility = View.VISIBLE
    }

    //Firebase에 로고 업로드 후 url 받아오기
    private fun UploadLogoFirebase(){

        var pd = ProgressDialog(this)
        pd.setTitle("저장중 1/2")
        pd.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference =
            FirebaseStorage.getInstance().reference.child("profiles/$fileName .jpg")

        storageReference.putFile(profilePhotoUri).addOnSuccessListener {
            //Toast.makeText(this@CreateClub, "사진 저장 성공", Toast.LENGTH_SHORT).show()
            storageReference.downloadUrl.addOnSuccessListener {
                pd.dismiss()
                profilePhotoURL = it.toString()
                myData.profileImage = profilePhotoURL
                Log.d(TAG, "profile URL : $profilePhotoURL")



                //관심분야를 선택하기 위해 다음 액티비티로 이동한다.
                val intent = Intent(this, SettingLikeSortActivity::class.java)
                intent.putExtra("userData", userData)
                intent.putExtra("myData", myData)
                startActivity(intent)
            }
        }
            .addOnFailureListener { p0 ->
                pd.dismiss()
                Toast.makeText(this, p0.message, Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLARY_PROFILE) {

                    Log.d(TAG, data?.data.toString())
                    profilePhotoUri = data?.data!!
                    Glide.with(this).load(profilePhotoUri).into(findViewById<ImageView>(R.id.SettingProfile_ImageButton))


                }else{
                    Log.d(TAG, "로고 불러오기 실패")
                }
            }
        }
    }

}