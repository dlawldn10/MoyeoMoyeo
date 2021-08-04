package com.project.moyeomoyeo

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.ClubPreviewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class ClubListActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    //최신 동아리 모집 공고(임시)
    val ArrangedClubList = ArrayList<ClubPreviewData>()

    //사용자 토큰
    var jwt = "null"

    //필터 선택항목 넣기
    var selectedItems = arrayListOf<Int>(0, 1)

    var keyword : String? = null


    var fieldFilterValues = arrayListOf<String>("전체", "문화/예술/공연", "봉사/사회활동", "학술/교양", "창업/취업", "어학", "운동", "친목", "기타")

    //서버가 1번부터 시작하므로 0번 비워둠
    var areaFilterValues = arrayListOf<String>( "" ,"전국", "수도권", "충북/충남/대전", "전북", "전남/광주", "경북/대구", "경남/부산/울산", "강원", "제주", "기타")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_list)


        //이전 인텐트로 전달받은 jwt가 있다면 저장하고, 추후 다음 액티비티로 넘어갈때 전달한다.
        if(intent.getStringExtra("jwt") != null){
            jwt = intent.getStringExtra("jwt")!!
            findViewById<TextView>(R.id.ClubListTitle_TextView).text = "최신 동아리 모집 공고"
            getArrangedList(jwt, "1")

        }else{
            Log.d("리스트 ", "멤버 조회 실패")
        }

        
        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기



        findViewById<Button>(R.id.arrangeType_Bttn).setOnClickListener {
            BuildArrangeDialog()
        }

        findViewById<Button>(R.id.ClubListFilter_Bttn).setOnClickListener {
            BuildFilterDialog()
        }

        findViewById<ImageButton>(R.id.search_imageButton).setOnClickListener {
            keyword = findViewById<EditText>(R.id.searchBar_EditText).text.toString()
            if(keyword == null || keyword == ""){
                Toast.makeText(applicationContext, "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show()
            }else{
                //분야(field), 지역(area)
                getFilteredList(selectedItems[0], selectedItems[1])
            }

        }


    }

    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //mypage만 있는 툴바
        menuInflater.inflate(R.menu.mypage_toolbar, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            //마이페이지 아이콘 클릭
            R.id.MyPage->{
                Toast.makeText(applicationContext, "마이페이지", Toast.LENGTH_SHORT).show()
                intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }

            //뒤로가기 클릭
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }

    //코루틴에서 호출
    //okhttp 비동기 방식 - GET
    //서버에서 동아리 목록 가져오기
    private fun getArrangedList(jwt: String, idx: String){

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                //동아리 목록 정보를 받아온다.
                try {
                    //리스트 초기화
                    ArrangedClubList.clear()
                    
                    val client = OkHttpClient.Builder().build()

                    val req = Request.Builder()
                        .url("https://moyeo.shop/clubs?choice=$idx")
                        .addHeader("x-access-token", jwt)
                        .build()

                    val response: Response = client.newCall(req).execute()

                    val result: String = response.body?.string() ?: "null"
                    var jsonObject = JSONObject(result)

                    if(jsonObject.getBoolean("isSuccess")){
                        var jsonArray = jsonObject.getJSONArray("result")
                        for (i in 0 until jsonArray.length()) {

                            val entry: JSONObject = jsonArray.getJSONObject(i)
                            var tmp : ClubPreviewData = ClubPreviewData(
                                entry.get("clubIdx") as Int,
                                entry.get("name") as String,
                                entry.get("description") as String,
                                entry.get("logoImage") as String,
                                entry.get("isLike") as Int
                            )
                            ArrangedClubList.add(tmp)
                        }
                        Log.d("리스트: ", jsonArray.toString())

                    }else{
                        //작업 실패 했을때
                        Log.d("리스트: ", jsonObject.get("code").toString())
                        Log.d("리스트: ", jsonObject.get("message").toString())
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.await()

            //리사이클러뷰 선언
            viewAdapter = ClubListRecyclerViewAdapter(ArrangedClubList, applicationContext, jwt)
            viewManager = LinearLayoutManager(applicationContext)
            recyclerView = findViewById<RecyclerView>(R.id.ClubList_RecyclerView).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

    }

    fun BuildArrangeDialog(){
        val myAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        myAlertBuilder.setTitle("정렬방식")
        myAlertBuilder.setItems(R.array.sortIdx, DialogInterface.OnClickListener { dialog, pos ->
            when (pos) {
                0 -> {
                    //최신순
                    findViewById<TextView>(R.id.ClubListTitle_TextView).text = "최신 동아리 모집 공고"
                    getArrangedList(jwt, "1")
                    Log.d("리스트: ", "최신순으로 정렬")
                }
                1 -> {
                    //인기순
                    findViewById<TextView>(R.id.ClubListTitle_TextView).text = "인기 동아리 모집 공고"
                    getArrangedList(jwt, "2")
                    Log.d("리스트: ", "인기순으로 정렬")
                }
                2 -> {
                    //TODO: 추천순 추후 추가하기
//                    findViewById<TextView>(R.id.ClubListTitle_TextView).text = "추천 동아리 모집 공고"
//                    getList(jwt, "3")
                    Log.d("리스트: ", "추천순으로 정렬")
                }
            }
        })
        myAlertBuilder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id ->
                //창 닫기.(저절로 닫힘)
            })
        myAlertBuilder.show()
    }

    fun BuildFilterDialog(){

        var dilaog01 = Dialog(this)       // Dialog 초기화
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.filter_dialog);             // xml 레이아웃 파일과 연결
        dilaog01.show()


        var fieldFilterRadioGroup = dilaog01.findViewById<GridRadioGroup>(R.id.fieldFilter_RadioGroup)
        var areaFilterRadioGroup = dilaog01.findViewById<GridRadioGroup>(R.id.areaFilter_RadioGroup)
        var okBttn = dilaog01.findViewById<Button>(R.id.filter_ok)

        okBttn.setOnClickListener {

            //필드값이 먼저 들어간다. 그다음 지역.
            selectedItems[0] = fieldFilterRadioGroup.selectedRadioButtonId!!
            selectedItems[1] = areaFilterRadioGroup.selectedRadioButtonId!!

            dilaog01.dismiss()
            Log.d("필터", selectedItems.toString())

            getIdxs(selectedItems)

            var selectedFieldFilter = findViewById<TextView>(R.id.selectedField_TextView)
            var selectedAreaFilter = findViewById<TextView>(R.id.selectedArea_TextView)

            selectedFieldFilter.text = fieldFilterValues[selectedItems[0]]
            selectedAreaFilter.text = areaFilterValues[selectedItems[1]]


        }

    }

    fun getFilteredList(fieldIdx: Int, areaIdx: Int){

        //서버에서 필터 설정해서 불러오기
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                //동아리 목록 정보를 받아온다.
                //리스트 초기화
                ArrangedClubList.clear()

                val client = OkHttpClient.Builder().build()

                val req = Request.Builder()
                    .url("https://moyeo.shop/search/clubs?keyword=$keyword&areaIdx=$areaIdx&fieldIdx=$fieldIdx")
                    .addHeader("x-access-token", jwt)
                    .build()

                val response: Response = client.newCall(req).execute()

                val result: String = response.body?.string() ?: "null"
                var jsonObject = JSONObject(result)

                if(jsonObject.getBoolean("isSuccess")){
                    var jsonArray = jsonObject.getJSONArray("result")
                    for (i in 0 until jsonArray.length()) {

                        val entry: JSONObject = jsonArray.getJSONObject(i)
                        var tmp : ClubPreviewData = ClubPreviewData(
                            entry.get("clubIdx") as Int,
                            entry.get("name") as String,
                            entry.get("description") as String,
                            entry.get("logoImage") as String,
                            entry.get("isLike") as Int
                        )
                        ArrangedClubList.add(tmp)
                    }
                    Log.d("리스트: ", jsonArray.toString())

                }else{
                    //작업 실패 했을때
                    Log.d("리스트: ", jsonObject.get("code").toString())
                    Log.d("리스트: ", jsonObject.get("message").toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }.await()

            //리사이클러뷰 선언
            viewAdapter = ClubListRecyclerViewAdapter(ArrangedClubList, applicationContext, jwt)
            viewManager = LinearLayoutManager(applicationContext)
            recyclerView = findViewById<RecyclerView>(R.id.ClubList_RecyclerView).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

            selectedItems.clear()

        }
    }

    fun getIdxs(selectedItems:ArrayList<Int>){

        //필터 선택된 것이 없으면 기본값 넣기
        if(selectedItems.size < 2){
            selectedItems.add(0)
            selectedItems.add(1)

            //분야(field), 지역(area)
            getFilteredList(selectedItems[0], selectedItems[1])
        }else{
            when(selectedItems[0]){
                //분야
                R.id.radioButton1 -> selectedItems[0] = 1   //문화/예술/공연
                R.id.radioButton2 -> selectedItems[0] = 2   //봉사/사회활동
                R.id.radioButton3 -> selectedItems[0] = 3   //학술/교양
                R.id.radioButton4 -> selectedItems[0] = 4   //창업/취업
                R.id.radioButton5 -> selectedItems[0] = 5   //어학
                R.id.radioButton6 -> selectedItems[0] = 6   //운동
                R.id.radioButton7 -> selectedItems[0] = 7   //친목
                R.id.radioButton8 -> selectedItems[0] = 8   //기타


            }

            when(selectedItems[1]){
                //지역
                R.id.radioButton9 -> selectedItems[1] = 1   //전국
                R.id.radioButton10 -> selectedItems[1] = 2   //봉사/사회활동
                R.id.radioButton11 -> selectedItems[1] = 3   //학술/교양
                R.id.radioButton12 -> selectedItems[1] = 4   //창업/취업
                R.id.radioButton13 -> selectedItems[1] = 5   //어학
                R.id.radioButton14 -> selectedItems[1] = 6   //운동
                R.id.radioButton15 -> selectedItems[1] = 7   //친목
                R.id.radioButton16 -> selectedItems[1] = 8   //기타
                R.id.radioButton17 -> selectedItems[1] = 9   //친목
                R.id.radioButton18 -> selectedItems[1] = 10   //기타
            }

        }



    }
}

//그리드 라디오 그룹 레이아웃 클래스 출처
//https://stackoverflow.com/questions/2381560/how-to-group-a-3x3-grid-of-radio-buttons/2383978