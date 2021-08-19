package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.UserAttendData

class EnterpriseListActivity : AppCompatActivity() {

    lateinit var recyclerView : RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprise_list)

        recyclerView = findViewById(R.id.recyclerView)

        var enterpriseList = ArrayList<UserAttendData>()

        enterpriseList.add(UserAttendData("",0,0,"삼성전자(주)","https://t1.daumcdn.net/cfile/tistory/2105213758E7D95610",0))
        enterpriseList.add(UserAttendData("",0,0,"네이버","https://image.rocketpunch.com/company/5466/naver_logo.png?s=400x400&t=inside",0))
        enterpriseList.add(UserAttendData("",0,0,"(주)카카오게임즈","https://t1.daumcdn.net/cfile/tistory/99BF9E475F60647A1D",0))
        enterpriseList.add(UserAttendData("",0,0,"라인파이낸셜플러스(주)","https://d.line-scdn.net/n/_s1/_0/linecorp-web-uit/images/line_icon_200_v3.jpg",0))
        enterpriseList.add(UserAttendData("",0,0,"우아한형제들","https://grepp-programmers.s3.amazonaws.com/production/company/logo/602/logo-woowahan.png",0))
        enterpriseList.add(UserAttendData("",0,0,"위메프","https://i0.wp.com/img.aapks.com/imgs/5/3/3/533eb800527ddc688fec7dfb1e4ab370_icon.png",0))
        enterpriseList.add(UserAttendData("",0,0,"컴투스","https://blog.kakaocdn.net/dn/KLcsW/btq5arzRHVm/KqBd2FKcMx2Jk99EVp1ij1/img.jpg",0))
        enterpriseList.add(UserAttendData("",0,0,"한국전자금융","https://www.nicetcm.co.kr/images/sub/ci.gif",0))
        enterpriseList.add(UserAttendData("",0,0,"(주)안랩","https://img1.daumcdn.net/thumb/R800x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F99B889485BDA08240E",0))
        enterpriseList.add(UserAttendData("",0,0,"쿠팡","https://image10.coupangcdn.com/image/mobile/v3/img_fb_like.png",0))
        enterpriseList.add(UserAttendData("",0,0,"당근마켓","https://blog.kakaocdn.net/dn/S0wSJ/btqDogzoUNX/kZBkpKPGjdGKJSvKKs35D0/img.png",0))

        viewAdapter = AttendMemberRecyclerViewAdapter(enterpriseList, applicationContext)
        viewManager = LinearLayoutManager(applicationContext)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기
    }

    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //my page 아이콘만 있는 툴바
        menuInflater.inflate(R.menu.mypage_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.MyPage->{
                Toast.makeText(applicationContext, "마이페이지", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
            //뒤로가기
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }
}