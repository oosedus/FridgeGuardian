package c_contentsList

import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fridgeguardian.R
import utils.FBAuth
import utils.FBRef

class ContentRVAdapter(
    val context: Context,
    val items : ArrayList<ContentModel>,
    val keyList : ArrayList<String>,
    val bookmarkIdList : MutableList<String>
    )
    : RecyclerView.Adapter<ContentRVAdapter.Viewholder>() {

//    interface ItemClick{
//        fun onClick(view:View, position: Int)
//    }
//    var itemClick:ItemClick? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_rv_item,parent,false)

        Log.d("ContentRVAdapter",keyList.toString())
        Log.d("ContentRVAdapter",bookmarkIdList.toString())
        return Viewholder(v)
    }
    //위에 코드는 꿀팁페이지에서 양식 탭을 누르면, 양식에 관련된 레시피들을 하나하나 가져와서 각각 하나의 레이아웃을 만들어줌

    override fun onBindViewHolder(holder: ContentRVAdapter.Viewholder, position: Int) {

//        if(itemClick != null){
//            holder.itemView.setOnClickListener{
//                v-> itemClick?.onClick(v,position)
//            }
//        }

        holder.bindItems(items[position], keyList[position])
    }
    //여기는 양식 카테고리에 있는 레시피 하나하나들을 밑에 innerclass Viewholder가서 하나씩 연결해주고 있는거임

    override fun getItemCount(): Int {
        return items.size
    }
    //이건 전체 아이템의 수가 몇개인지, 양식 카테고리 눌렀을 때 글이 몇개 뜨냐고

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(item : ContentModel,key:String){

            itemView.setOnClickListener{
                Toast.makeText(context,item.title,Toast.LENGTH_LONG).show()
                val intent = Intent(context,ContentShowActivity::class.java)
                intent.putExtra("url",item.webUrl)
                itemView.context.startActivity(intent)
            }


            val contentTitle = itemView.findViewById<TextView>(R.id.textArea)
            //바로 위의 itemView는 content_rv_item을 의미함
            val imageViewArea = itemView.findViewById<ImageView>(R.id.imageArea)
            val bookmarkArea = itemView.findViewById<ImageView>(R.id.bookmarkArea)

            //Bookmark에 대한 정보를 가지고 있냐 아니냐를 검사해보자
            if(bookmarkIdList.contains(key)){
                bookmarkArea.setImageResource(R.drawable.c_t_rheart)
            }else{
                bookmarkArea.setImageResource(R.drawable.c_t_wheart)
            }



            bookmarkArea.setOnClickListener{
                Log.d("ContentRVAdapter",FBAuth.getUid())
                Toast.makeText(context, key,Toast.LENGTH_LONG).show()

                if(bookmarkIdList.contains(key)){
                    //북마크가 있을 때 삭제
                    bookmarkIdList.remove(key)

                    FBRef.bookmarkRef
                        .child(FBAuth.getUid())
                        .child(key)
                        .removeValue()
                    //bookmarkRef의 값을 가져와서 child가지에 FBAuth의 uid값을 넣는다. 그리고 uid안에 key값(contents id값)을 넣는다.

                }else{
                    //북마크가 없을 때
                    FBRef.bookmarkRef
                        .child(FBAuth.getUid())
                        .child(key)
                        .setValue(true)
                }




            }
            //북마크를 클릭할때, key값에 대한 정보랑 uid정보를 알고있어야 파이어베이스에 저장가능



            contentTitle.text=item.title
            // 바로 위의 item은 ArrayList<ContentModel>, contentmodel에서 받아온 리스트 애들 하나씩 의미함
            Glide.with(context)
                .load(item.imageUrl)
                .into(imageViewArea)
            //imageUrl에 있는걸 imageViewArea에 넣겠다는 부분임

        }
    }
}