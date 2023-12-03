package c_contentsList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeguardian.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ContentListActivity : AppCompatActivity() {

    lateinit var myRef:DatabaseReference

    //리사이클러뷰를 통해서 tip페이지에서 원하는 음식 카테고리를 클릭하면 해당 카테고리의 꿀팁들이 보일 수 있게함
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_list)

        val items = ArrayList<ContentModel>()
        val rvAdapter = ContentRVAdapter(baseContext, items)

        val database = Firebase.database

        //지금 아래의 category에 category1(all)이냐, category2(한국음식)이냐가 들어가 있음
        val category = intent.getStringExtra("category")


        if(category=="category1"){

            myRef = database.getReference("contents")


        }else if(category == "category2"){

            myRef = database.getReference("contents2")

        }else if(category == "category3"){

            myRef = database.getReference("contents3")

        }else if(category == "category4"){

            myRef = database.getReference("contents4")

        }

        //파이어 베이스에서 데이터를 이제 가져오는 부분이 바로 아래 부분임
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //원래는 데이터 스냅샷이 한 번에 뽑히는데, for문 통해서 분리해서 뽑는 부분
                for(dataModel in dataSnapshot.children){
                    Log.d("ContentListActivity",dataModel.toString())
                    val item = dataModel.getValue(ContentModel::class.java)
                    //파이어베이스에서 데이터 하나씩 뽑아서 내가 만든 ContentModel형식으로 받겠다
                    items.add(item!!)
                }
                rvAdapter.notifyDataSetChanged()
                //데이터를 다 받은 다음에는 어댑터를 동기화시켜야한다

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addValueEventListener(postListener)




        //val myRef = database.getReference("contents")
        // 밑에 주석 처리된 부분 절대 지우지 마세요. 이 부분이 firebase에 데이터를 넣어놓는 부분인데, 인선이 한 번 넣어놔서 주석처리한거에요
        //혹시 모르니까 남겨둬야 합니다
//        myRef.push().setValue(
//            ContentModel("Steamed clam","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FqHJH1%2FbtsytRvqouZ%2FK43OBx9Zzl6vZ7EHm6QuT0%2Fimg.png",
//                "https://gocompany.tistory.com/m/entry/%ED%99%88-%EB%A0%88%EC%8B%9C%ED%94%BC-%EB%B0%94%EC%A7%80%EB%9D%BD%EC%88%A0%EC%B0%9C-%EB%A7%8C%EB%93%A4%EA%B8%B0-%EC%B4%88%EA%B0%84%EB%8B%A8-%EB%A0%88%EC%8B%9C%ED%94%BC-%EC%88%A0%EC%95%88%EC%A3%BC-%EC%B6%94%EC%B2%9C%EC%9A%94%EB%A6%AC-%EB%B0%94%EC%A7%80%EB%9D%BD%EC%A1%B0%EA%B0%9C%ED%95%B4%EA%B0%90%EB%B2%95")
//        )
//        myRef.push().setValue(
//            ContentModel("Spicy Beef Sandwich","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FF796i%2FbtsAbpQXFb4%2FY1KWfOX2g63x8l23QpTLt0%2Fimg.png",
//                "https://dasslscripted.com/m/entry/%ED%99%88%EB%A9%94%EC%9D%B4%EB%93%9C-%EB%A7%A4%EC%BD%A4%ED%95%9C-%EC%86%8C%EA%B3%A0%EA%B8%B0-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98-%EB%A0%88%EC%8B%9C%ED%94%BC-English-VersionTitle-Homemade-Spicy-Beef-Sandwich-Recipe-%EB%A7%A4%EC%BD%A4%ED%95%9C-%EC%86%8C%EA%B3%A0%EA%B8%B0-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%ED%99%88%EB%A9%94%EC%9D%B4%EB%93%9C-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98-%EB%A0%88%EC%8B%9C%ED%94%BC%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98-%EC%96%91%EC%8B%9D%EC%86%8C%EA%B3%A0%EA%B8%B0-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98-%EC%9A%94%EB%A6%AC%EB%B2%95%EA%B0%80%EC%A0%95%EC%97%90%EC%84%9C-%EB%A7%8C%EB%93%9C%EB%8A%94-%EB%A7%A4%EC%BD%A4%ED%95%9C-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91-%EC%8A%A4%ED%83%80%EC%9D%BC-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%EC%86%8C%EA%B3%A0%EA%B8%B0-%EA%B3%A0%EC%B6%94%EC%9E%A5-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%EC%96%91%EB%85%90-%EC%86%8C%EA%B3%A0%EA%B8%B0-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%EA%B0%84%ED%8E%B8-%EC%9A%94%EB%A6%AC-%EB%A0%88%EC%8B%9C%ED%94%BC%ED%95%9C%EA%B5%AD-%EC%8A%A4%ED%83%80%EC%9D%BC-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98")
//        )
//        myRef.push().setValue(
//          ContentModel("Egg In Hell","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcA9vNC%2FbtsrtimDlVO%2F4A7M1m68Y3Fchyeo8HvXF0%2Fimg.png",
//                "https://llnv.tistory.com/m/entry/%EC%A0%80%ED%83%84%EC%88%98-%EB%A0%88%EC%8B%9C%ED%94%BC-%EC%97%90%EA%B7%B8%EC%9D%B8%ED%97%AC-%EC%96%91%EC%8B%9D-%EB%95%A1%EA%B8%B0%EB%8A%94-%EB%82%A0-%EB%A8%B9%EB%8A%94-%EA%B0%90%EB%9F%89%ED%85%9C"))

//        myRef.push().setValue(
//            ContentModel("Gambas","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FyQK3L%2Fbtsy7gOJJCL%2Fh8k4ufVvk6CcCXiqX7TMjK%2Fimg.jpg",
//                "https://dasho25.tistory.com/m/25"))
//
//        myRef.push().setValue(
//          ContentModel("Pork belly Pasta","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fcqpxfr%2Fbtstx4HD55n%2FBVwGFOYIaMkl3ynxbFThK0%2Fimg.jpg",
//                "https://jounwon.tistory.com/m/entry/%EC%82%BC%EA%B2%B9%EC%82%B4-%EA%B0%84%EC%9E%A5%ED%8C%8C%EC%8A%A4%ED%83%80-%EB%A7%8C%EB%93%9C%EB%8A%94-%EB%B0%A9%EB%B2%95"))
//        myRef.push().setValue(
//         ContentModel("Oxtail soup Pasta","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbtig9C%2Fbtq65UGxyWI%2FPRBIGUKJ4rjMkI7KTGrxtK%2Fimg.png",
//                "https://philosopher-chan.tistory.com/1237"))
//        myRef.push().setValue(
//         ContentModel("Toowoomba pasta","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcOYyBM%2Fbtq67Or43WW%2F17lZ3tKajnNwGPSCLtfnE1%2Fimg.png",
//                "https://philosopher-chan.tistory.com/1238"))



        val rv : RecyclerView = findViewById(R.id.rv)

        // 근데 이제 우리는 데이터 모델을 넘길거임. Model("imgUrl","title") 이런식으로
        //왜 데이터 모델을 쓰냐, 양식 카테고리를 딱 누르면 양식과 관련된 여러 레시피들이 사진과 제목, 이렇게 한 쌍으로 같이 뜸 그래서 그럼
        // webView를 이용해서, 해당 사진을 클릭하면, 해당 사진의 레시피가 있는 인터넷 사이트로 넘어감


        rv.adapter=rvAdapter

        //그리드 레이아웃 쓰면 2개씩 보이게 할 수 있음
        rv.layoutManager = GridLayoutManager(this,2)

//        rvAdapter.itemClick = object :ContentRVAdapter.ItemClick{
//            override fun onClick(view: View, position:Int){
//
//                Toast.makeText(baseContext,items[position].title,Toast.LENGTH_LONG).show()
//
//                val intent = Intent(this@ContentListActivity,ContentShowActivity::class.java)
//                intent.putExtra("url",items[position].webUrl)
//                //url이라는 이름안에 items중에서 사용자로부터 선택된 position의 weburl을 넘기는 부분
//                //-> ContentShowActivity가 얘가 보낸 애들을 받아줘야함
//                startActivity(intent)
//
//            }
//        }

//       val myRef4 = database.getReference("contents4")
//        myRef2.push().setValue(
//            ContentModel("Steamed clam","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FqHJH1%2FbtsytRvqouZ%2FK43OBx9Zzl6vZ7EHm6QuT0%2Fimg.png",
//                "https://gocompany.tistory.com/m/entry/%ED%99%88-%EB%A0%88%EC%8B%9C%ED%94%BC-%EB%B0%94%EC%A7%80%EB%9D%BD%EC%88%A0%EC%B0%9C-%EB%A7%8C%EB%93%A4%EA%B8%B0-%EC%B4%88%EA%B0%84%EB%8B%A8-%EB%A0%88%EC%8B%9C%ED%94%BC-%EC%88%A0%EC%95%88%EC%A3%BC-%EC%B6%94%EC%B2%9C%EC%9A%94%EB%A6%AC-%EB%B0%94%EC%A7%80%EB%9D%BD%EC%A1%B0%EA%B0%9C%ED%95%B4%EA%B0%90%EB%B2%95")
//        )
//        myRef4.push().setValue(
//            ContentModel("Spicy Beef Sandwich","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FF796i%2FbtsAbpQXFb4%2FY1KWfOX2g63x8l23QpTLt0%2Fimg.png",
//                "https://dasslscripted.com/m/entry/%ED%99%88%EB%A9%94%EC%9D%B4%EB%93%9C-%EB%A7%A4%EC%BD%A4%ED%95%9C-%EC%86%8C%EA%B3%A0%EA%B8%B0-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98-%EB%A0%88%EC%8B%9C%ED%94%BC-English-VersionTitle-Homemade-Spicy-Beef-Sandwich-Recipe-%EB%A7%A4%EC%BD%A4%ED%95%9C-%EC%86%8C%EA%B3%A0%EA%B8%B0-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%ED%99%88%EB%A9%94%EC%9D%B4%EB%93%9C-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98-%EB%A0%88%EC%8B%9C%ED%94%BC%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98-%EC%96%91%EC%8B%9D%EC%86%8C%EA%B3%A0%EA%B8%B0-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98-%EC%9A%94%EB%A6%AC%EB%B2%95%EA%B0%80%EC%A0%95%EC%97%90%EC%84%9C-%EB%A7%8C%EB%93%9C%EB%8A%94-%EB%A7%A4%EC%BD%A4%ED%95%9C-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91-%EC%8A%A4%ED%83%80%EC%9D%BC-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%EC%86%8C%EA%B3%A0%EA%B8%B0-%EA%B3%A0%EC%B6%94%EC%9E%A5-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%EC%96%91%EB%85%90-%EC%86%8C%EA%B3%A0%EA%B8%B0-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98%EA%B0%84%ED%8E%B8-%EC%9A%94%EB%A6%AC-%EB%A0%88%EC%8B%9C%ED%94%BC%ED%95%9C%EA%B5%AD-%EC%8A%A4%ED%83%80%EC%9D%BC-%EC%83%8C%EB%93%9C%EC%9C%84%EC%B9%98")
//        )
//        myRef4.push().setValue(
//          ContentModel("Egg In Hell","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcA9vNC%2FbtsrtimDlVO%2F4A7M1m68Y3Fchyeo8HvXF0%2Fimg.png",
//                "https://llnv.tistory.com/m/entry/%EC%A0%80%ED%83%84%EC%88%98-%EB%A0%88%EC%8B%9C%ED%94%BC-%EC%97%90%EA%B7%B8%EC%9D%B8%ED%97%AC-%EC%96%91%EC%8B%9D-%EB%95%A1%EA%B8%B0%EB%8A%94-%EB%82%A0-%EB%A8%B9%EB%8A%94-%EA%B0%90%EB%9F%89%ED%85%9C"))
//        myRef4.push().setValue(
//            ContentModel("Gambas","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FyQK3L%2Fbtsy7gOJJCL%2Fh8k4ufVvk6CcCXiqX7TMjK%2Fimg.jpg",
//                "https://dasho25.tistory.com/m/25"))
//
//        myRef3.push().setValue(
//          ContentModel("Pork belly Pasta","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fcqpxfr%2Fbtstx4HD55n%2FBVwGFOYIaMkl3ynxbFThK0%2Fimg.jpg",
//                "https://jounwon.tistory.com/m/entry/%EC%82%BC%EA%B2%B9%EC%82%B4-%EA%B0%84%EC%9E%A5%ED%8C%8C%EC%8A%A4%ED%83%80-%EB%A7%8C%EB%93%9C%EB%8A%94-%EB%B0%A9%EB%B2%95"))
//        myRef2.push().setValue(
//         ContentModel("Oxtail soup Pasta","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbtig9C%2Fbtq65UGxyWI%2FPRBIGUKJ4rjMkI7KTGrxtK%2Fimg.png",
//                "https://philosopher-chan.tistory.com/1237"))
//        myRef2.push().setValue(
//         ContentModel("Toowoomba pasta","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcOYyBM%2Fbtq67Or43WW%2F17lZ3tKajnNwGPSCLtfnE1%2Fimg.png",
//                "https://philosopher-chan.tistory.com/1238"))
//

    }



}