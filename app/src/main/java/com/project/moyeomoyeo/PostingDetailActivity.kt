package com.project.moyeomoyeo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.moyeomoyeo.DataClass.CommentData
import com.project.moyeomoyeo.DataClass.PostingData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class PostingDetailActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    var Data = PostingData(0,0,0, "","", "", "", 0)

    var userData = UserData("", 0, "")

    var toRecommentIdx = 0

    val RequestedCommentList = mutableListOf<CommentData>()
    val RecommentList = mutableListOf<CommentData>()

    var FinalCommentData = mutableListOf<CommentData>()


    //키보드와 연결해주는.
    var imm : InputMethodManager? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting_detail)
        CoroutineScope(Dispatchers.Main).launch {

            imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            var commentEditText = findViewById<EditText>(R.id.EditComment_EditText)
            var SendCommentBttn = findViewById<Button>(R.id.SendComment_Bttn)



            withContext(
                CoroutineScope(Dispatchers.IO).coroutineContext
            ) {


                if(intent.getSerializableExtra("userData") != null){
                    userData = intent.getSerializableExtra("userData") as UserData
                    Data = intent.getSerializableExtra("postingData") as PostingData

                    getCommentData(userData.jwt, Data.clubIdx, Data.postIdx)

                    //댓글 등록 버튼
                    SendCommentBttn.setOnClickListener {
                        //댓글 post
                        PostComment(userData.jwt, Data.clubIdx, Data.postIdx, commentEditText)

                    }


                }else{
                    Log.d("리스트 ", "멤버 조회 실패")
                }


            }


            findViewById<TextView>(R.id.Posting_NickName).text = Data.nickname
            findViewById<TextView>(R.id.Posting_Date).text = Data.createdAt
            findViewById<TextView>(R.id.Posting_Content_TextView).text = Data.content

        }


        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기




    }

    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //마이페이지만 있는 툴바
        menuInflater.inflate(R.menu.mypage_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.MyPage->{
                Toast.makeText(applicationContext, "마이페이지", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MyPageActivity::class.java)
                intent.putExtra("userData", userData)
                startActivity(intent)
            }
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }

    private fun getPostingData(jwt: String, clubIdx: Int, postIdx: Int) {

        val client = OkHttpClient.Builder().build()

        val req = Request.Builder()
            .url("https://moyeo.shop/clubs/$clubIdx/posts/$postIdx")
            .addHeader("x-access-token", jwt)
            .build()

        val response: Response = client.newCall(req).execute()
        var jsonObject = JSONObject(response.body?.string() ?: "null")
        var detailData = jsonObject.getJSONArray("result")

        if (jsonObject.getBoolean("isSuccess")) {
            for (i in 0 until detailData.length()) {

                val entry: JSONObject = detailData.getJSONObject(i)
                Data = PostingData(
                    entry.get("postIdx") as Int,
                    entry.get("userIdx") as Int,
                    entry.get("clubIdx") as Int,
                    entry.get("nickname") as String,
                    entry.get("profileImage") as String,
                    entry.get("content") as String,
                    entry.get("createdAt") as String,
                    entry.get("commentsCount") as Int
                )

            }



        } else {
            //작업 실패 했을때
            Log.d("리스트 ", jsonObject.get("code").toString())
            Log.d("리스트 ", jsonObject.get("message").toString())
        }

    }

    private fun getCommentData(jwt: String, clubIdx: Int, postIdx: Int) {

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                RequestedCommentList.clear()
                RecommentList.clear()
                FinalCommentData.clear()

                val client = OkHttpClient.Builder().build()

                val req = Request.Builder()
                    .url("https://moyeo.shop/clubs/$clubIdx/posts/$postIdx/comments")
                    .addHeader("x-access-token", jwt)
                    .build()

                val response: Response = client.newCall(req).execute()
                var jsonObject = JSONObject(response.body?.string() ?: "null")

                if (jsonObject.getBoolean("isSuccess")) {
                    var commentData = jsonObject.getJSONArray("result")

                    for (i in 0 until commentData.length()) {

                        val entry: JSONObject = commentData.getJSONObject(i)
                        var Tmp = CommentData(
                            entry.get("commentIdx") as Int,
                            entry.get("content") as String,
                            entry.get("userIdx") as Int,
                            entry.get("nickname") as String,
                            entry.get("profileImage") as String,
                            entry.get("postIdx") as Int,
                            entry.get("parentCommentIdx") as Int,
                            entry.get("seq") as Int,
                            entry.get("createdAt") as String,
                            entry.get("status") as Int

                        )

                        //답글 이라면
                        if(Tmp.parentCommentIdx != 0){
                            //답글 리스트에 저장
                            RecommentList.add(Tmp)
                        }else{
                            //댓글 리스트에 저장
                            RequestedCommentList.add(Tmp)
                        }


                    }



                } else {
                    //작업 실패 했을때
                    Log.d("리스트 ", jsonObject.get("code").toString())
                    Log.d("리스트 ", jsonObject.get("message").toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT ).show()
                    }
                }

            }.await()


//            Log.d("댓글", RecommentList.toString())
            //답글을 골라서 parent코멘트 밑에다가 끼워넣기
            var acc = 0
            for (i in 0..RequestedCommentList.size-1) {
                FinalCommentData.add(RequestedCommentList[i])
                for (j in 0 until RecommentList.size) {
                    if(RequestedCommentList[i].commentIdx.equals(RecommentList[j].parentCommentIdx)){
                        acc += 1
                        Log.d("댓글", "$i, $j")
                        Log.d("댓글", acc.toString())
                        FinalCommentData.add(RecommentList[j])
                    }
                }
                acc = 0
            }

            Log.d("댓글", RequestedCommentList.toString())

            //리사이클러뷰 선언
            viewAdapter = CommentListRecyclerViewAdapter(FinalCommentData, applicationContext, userData, this@PostingDetailActivity)
            viewManager = LinearLayoutManager(applicationContext)
            recyclerView = findViewById<RecyclerView>(R.id.CommentList_RecyclerView).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

    }

    fun PostComment(jwt: String, clubIdx: Int, postIdx: Int, commentEditText: EditText){


        //1.클라이언트 만들기
        val client = OkHttpClient.Builder().build()

        val formBody: FormBody

        formBody = FormBody.Builder()
            .add("content", commentEditText.text.toString())
            .add("parentCommentIdx", toRecommentIdx.toString())
            .build()

        val req = Request.Builder().url("https://moyeo.shop/clubs/$clubIdx/posts/$postIdx/comments")
            .addHeader("x-access-token", jwt)
            .post(formBody)
            .build()


        //3.응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(applicationContext, "서버에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "다시 시도하세요.", Toast.LENGTH_SHORT).show()
                }
                Log.d("댓글 작성", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {

                try {
                    //주의* response.body!!.string() 값은 한번밖에 호출 못함.
                    var jsonString = response.body!!.string()
                    var jsonObject = JSONObject(jsonString)

                    if(jsonObject.getBoolean("isSuccess")){
                        getCommentData(jwt, clubIdx, postIdx)
                        getPostingData(jwt, clubIdx, postIdx)
                        CoroutineScope(Dispatchers.Main).launch {

                            //댓글이 게시되면 입력창을 초기화하고 키보드 숨긴다.
                            commentEditText.setText(null)
                            commentEditText.clearFocus()
                            imm?.hideSoftInputFromWindow(commentEditText.windowToken, 0)


                            //포스팅 새로고침
                            findViewById<TextView>(R.id.Posting_NickName).text = Data.nickname
                            findViewById<TextView>(R.id.Posting_Date).text = Data.createdAt
                            findViewById<TextView>(R.id.Posting_Content_TextView).text = Data.content

                        }

                    }else{

                        //작업 실패 했을때
                        Log.d("댓글 작성 ", jsonObject.get("code").toString())
                        Log.d("댓글 작성 ", jsonObject.get("message").toString())
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT ).show()
                        }
                    }
                    
                    //답글을 달 코멘트 초기화 -> 그냥 댓글
                    toRecommentIdx = 0


                }catch (e : JSONException){
                    Log.d("댓글 작성 ", "JSON 오류")
                }


            }

        })
    }



    fun setRecommentData(CommentIdx: Int){
        //에딧 텍스트에 포커스 주고
        var commentEditText = findViewById<EditText>(R.id.EditComment_EditText)
        commentEditText.hint = "답글을 작성해 주세요."
        commentEditText.requestFocus()
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        //답글을 달 코멘트의 인덱스값을 저장한다.
        toRecommentIdx = CommentIdx
    }


}
