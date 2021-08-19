package com.project.moyeomoyeo

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.ClubPreviewData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class ClubListActivity : AppCompatActivity() {
    //리사이클러뷰
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    //필터처리된 동아리 리스트
    val RequestedClubList = ArrayList<ClubPreviewData>()

    //사용자 토큰
    var userData = UserData("", 0, "")

    //1->개발
    //2->디자인
    //3->마케팅
    //4->기획
    var sortIdx = 1

    //1-> 최신순
    //2-> 인기순
    var choiceIdx = 1   //최신순

    //전국->0
    //수도권->1
    //충북/충남/대전->2
    //전북->3
    //전남/광주->4
    //경북/대구->5
    //경남/부산/울산->6
    //강원->7
    //제주->8
    //기타->9
    var areaIdx = 0     //전국

    //0->전체
    //1->서버
    //2->웹
    //3->iOS
    //4->안드로이드
    //5->자료구조/알고리즘
    //6->게임
    //7->AI
    //8->데이터 분석
    //9->데이터 시각화
    //10->기타
    var fieldIdx = 0

    //검색 키워드
    var keyword : String? = null

    //필터 선택항목 넣기
    var selectedItems_index = arrayListOf<Int>(0, 1)
    var selectedItems_id = arrayListOf<Int>(R.id.radioButton0, R.id.radioButton11)



    var fieldFilterValues = arrayListOf<String>("전체", "서버", "웹", "iOS", "안드로이드", "자료구조/알고리즘", "게임", "AI", "데이터 분석", "데이터 시각화", "기타")

    var areaFilterValues = arrayListOf<String>("전국", "수도권", "충북/충남/대전", "전북", "전남/광주", "경북/대구", "경남/부산/울산", "강원", "제주", "기타")

    var sortValues = arrayListOf<String>("전체", "개발", "디자인", "마케팅", "기획")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_list)


        //이전 인텐트로 전달받은 jwt가 있다면 저장
        if(intent.getSerializableExtra("userData") != null){
            userData = intent.getSerializableExtra("userData") as UserData
            sortIdx = intent.getIntExtra("sortIdx", 0)

            findViewById<TextView>(R.id.my_toolbar_title).text = "${sortValues[sortIdx]}"
            findViewById<TextView>(R.id.ListTitle_TextView).text = "최신 멘토링 모집 공고"
            findViewById<Button>(R.id.arrangeType_Bttn).text = "최신순"

            getList()

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



        //정렬 버튼
        findViewById<Button>(R.id.arrangeType_Bttn).setOnClickListener {
            BuildArrangeDialog()
        }

        //필터 버튼
        findViewById<Button>(R.id.ListFilter_Bttn).setOnClickListener {
            BuildFilterDialog()
        }

        //검색 버튼
        findViewById<ImageButton>(R.id.search_imageButton).setOnClickListener {
            keyword = findViewById<EditText>(R.id.searchBar_EditText).text.toString()
            getList()

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
                intent.putExtra("userData", userData)
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
    private fun getList(){

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                //동아리 목록 정보를 받아온다.
                //리스트 초기화
                RequestedClubList.clear()

                val client = OkHttpClient.Builder().build()
                var url = BuildUrl()

                val req = Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", userData.jwt)
                    .build()

                val response: Response = client.newCall(req).execute()

                var jsonObject = JSONObject(response.body?.string())

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
                        RequestedClubList.add(tmp)
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
            viewAdapter = ClubListRecyclerViewAdapter(RequestedClubList, applicationContext, userData)
            viewManager = LinearLayoutManager(applicationContext)
            recyclerView = findViewById<RecyclerView>(R.id.ListRecyclerView).apply {
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
                    choiceIdx = 1
                    findViewById<TextView>(R.id.ListTitle_TextView).text = "최신 멘토링 모집 공고"
                    findViewById<Button>(R.id.arrangeType_Bttn).text = "최신순"
                    getList()
                }
                1 -> {
                    //인기순
                    choiceIdx = 2
                    findViewById<TextView>(R.id.ListTitle_TextView).text = "인기 멘토링 모집 공고"
                    findViewById<Button>(R.id.arrangeType_Bttn).text = "인기순"
                    getList()
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
        dilaog01.setContentView(R.layout.dialog_filter);             // xml 레이아웃 파일과 연결
        dilaog01.show()


        var fieldFilterRadioGroup = dilaog01.findViewById<GridRadioGroup>(R.id.fieldFilter_RadioGroup)
        var areaFilterRadioGroup = dilaog01.findViewById<GridRadioGroup>(R.id.areaFilter_RadioGroup)
        var okBttn = dilaog01.findViewById<Button>(R.id.filter_ok)


        fieldFilterRadioGroup.findViewById<RadioButton>(selectedItems_id[0]).isChecked = true
        areaFilterRadioGroup.findViewById<RadioButton>(selectedItems_id[1]).isChecked = true


        okBttn.setOnClickListener {

            //분야값이 먼저 들어간다. 그다음 지역.
            selectedItems_id[0] = fieldFilterRadioGroup.selectedRadioButtonId!!
            selectedItems_id[1] = areaFilterRadioGroup.selectedRadioButtonId!!

            dilaog01.dismiss()
            Log.d("필터", selectedItems_index.toString())

            getIdxs(selectedItems_id)


        }

    }


    fun getIdxs(selectedItems_id:ArrayList<Int>){

        when(selectedItems_id[0]){
            //분야
            R.id.radioButton0 -> selectedItems_index[0] = 0   //전체
            R.id.radioButton1 -> selectedItems_index[0] = 1   //서버
            R.id.radioButton2 -> selectedItems_index[0] = 2   //웹
            R.id.radioButton3 -> selectedItems_index[0] = 3   //ios
            R.id.radioButton4 -> selectedItems_index[0] = 4   //안드로이드
            R.id.radioButton5 -> selectedItems_index[0] = 5   //자료구조/알고리즘
            R.id.radioButton6 -> selectedItems_index[0] = 6   //게임
            R.id.radioButton7 -> selectedItems_index[0] = 7   //AI
            R.id.radioButton8 -> selectedItems_index[0] = 8   //데이터분석
            R.id.radioButton9 -> selectedItems_index[0] = 9   //데이터시각화
            R.id.radioButton10 -> selectedItems_index[0] = 10   //기타
            


        }

        when(selectedItems_id[1]){
            //지역
            R.id.radioButton11 -> selectedItems_index[1] = 0   //전국
            R.id.radioButton12 -> selectedItems_index[1] = 1   //수도권
            R.id.radioButton13 -> selectedItems_index[1] = 2   //충북/충남/대전
            R.id.radioButton14 -> selectedItems_index[1] = 3   //전북
            R.id.radioButton15 -> selectedItems_index[1] = 4   //전남/광주
            R.id.radioButton16 -> selectedItems_index[1] = 5   //경북/대구
            R.id.radioButton17 -> selectedItems_index[1] = 6   //경남/부산/울산
            R.id.radioButton18 -> selectedItems_index[1] = 7   //강원
            R.id.radioButton19 -> selectedItems_index[1] = 8   //제주
            R.id.radioButton20 -> selectedItems_index[1] = 9   //기타
        }

        //분야(field), 지역(area)
        fieldIdx = selectedItems_index[0]
        areaIdx = selectedItems_index[1]



        var selectedFieldFilter = findViewById<TextView>(R.id.selectedField_TextView)
        var selectedAreaFilter = findViewById<TextView>(R.id.selectedArea_TextView)

        selectedFieldFilter.text = fieldFilterValues[fieldIdx]
        selectedAreaFilter.text = areaFilterValues[areaIdx]

        getList()



    }

    fun BuildUrl() : String{
        val url: HttpUrl
        if(keyword.isNullOrEmpty()){
            url = HttpUrl.Builder()
                .scheme("https")
                .host("moyeo.shop")
                .addPathSegment("clubs")
                .addQueryParameter("sortIdx", sortIdx.toString())
                .addQueryParameter("choice", choiceIdx.toString())
                .addQueryParameter("areaIdx", areaIdx.toString())
                .addQueryParameter("fieldIdx", fieldIdx.toString())
                .build()
        }else{
            url = HttpUrl.Builder()
                .scheme("https")
                .host("moyeo.shop")
                .addPathSegment("clubs")
                .addQueryParameter("sortIdx", sortIdx.toString())
                .addQueryParameter("choice", choiceIdx.toString())
                .addQueryParameter("areaIdx", areaIdx.toString())
                .addQueryParameter("fieldIdx", fieldIdx.toString())
                .addQueryParameter("keyword", keyword.toString())
                .build()
        }


        Log.d("리스트", "종류: $sortIdx 정렬: $choiceIdx 지역: $areaIdx 분야: $fieldIdx 키워드: $keyword")
        Log.d("리스트", url.toString())

        return url.toString()
    }
}

//그리드 라디오 그룹 레이아웃 클래스 출처
//https://stackoverflow.com/questions/2381560/how-to-group-a-3x3-grid-of-radio-buttons/2383978