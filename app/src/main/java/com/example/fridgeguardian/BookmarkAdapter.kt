package com.example.fridgeguardian

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

class BookmarkAdapter(private val bookmarksList: List<Pair<String, Any>>, private val context: Context) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookmarkPicture: ImageView = itemView.findViewById(R.id.imageView)
        val bookmarkName: TextView = itemView.findViewById(R.id.textView5)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bookmark_item_small_form, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookmark = bookmarksList[position]
        holder.bookmarkName.text = bookmark.first

        // 그냥 bookmark.second로 전달하면 hashmap형태이므로 intent에 담겨서 전달 되지 않는다
        val temprecipe = bookmark.second as HashMap<String, Any>


        val imageUrl = (bookmark.second as? HashMap<String, Any>)?.get("이미지경로") as? String ?: "기본 URL"
        val RCP_SEQ = (bookmark.second as? HashMap<*, *>)?.get("일련번호")as? Long
        val INFO_ENG = (bookmark.second as? HashMap<*, *>)?.get("열량")as? Double
        val INFO_CAR = (bookmark.second as? HashMap<*, *>)?.get("탄수화물")as? Double
        val INFO_PRO = (bookmark.second as? HashMap<*, *>)?.get("단백질")as? Double
        val INFO_FAT = (bookmark.second as? HashMap<*, *>)?.get("지방")as? Double
        val INFO_NA = (bookmark.second as? HashMap<*, *>)?.get("나트륨") as? Double

        val recipe = RecipeDataForm(
            RCP_SEQ = RCP_SEQ,
            RCP_NM = bookmark.first,
            RCP_WAY2 = temprecipe["조리방법"] as? String,
            INFO_ENG = INFO_ENG,
            INFO_CAR =INFO_CAR,
            INFO_PRO = INFO_PRO,
            INFO_FAT = INFO_FAT,
            INFO_NA = INFO_NA,
            HASH_TAG = temprecipe["해쉬태그"] as? String,
            ATT_FILE_NO_MK = temprecipe["이미지경로"] as? String,
            RCP_PARTS_DTLS = temprecipe["재료정보"] as? String,
            MANUAL01 = temprecipe["만드는법_01"] as? String,
            MANUAL02 = temprecipe["만드는법_02"] as? String,
            MANUAL03 = temprecipe["만드는법_03"] as? String,
            MANUAL04 = temprecipe["만드는법_04"] as? String,
            MANUAL05 = temprecipe["만드는법_05"] as? String,
            MANUAL06 = temprecipe["만드는법_06"] as? String,
            MANUAL07 = temprecipe["만드는법_07"] as? String,
            MANUAL08 = temprecipe["만드는법_08"] as? String,
            MANUAL09 = temprecipe["만드는법_09"] as? String,
            MANUAL10 = temprecipe["만드는법_10"] as? String,
            MANUAL11 = temprecipe["만드는법_11"] as? String,
            MANUAL12 = temprecipe["만드는법_12"] as? String,
            MANUAL13 = temprecipe["만드는법_13"] as? String,
            MANUAL14 = temprecipe["만드는법_14"] as? String,
            MANUAL15 = temprecipe["만드는법_15"] as? String,
            MANUAL16 = temprecipe["만드는법_16"] as? String,
            MANUAL17 = temprecipe["만드는법_17"] as? String,
            MANUAL18 = temprecipe["만드는법_18"] as? String,
            MANUAL19 = temprecipe["만드는법_19"] as? String,
            MANUAL20 = temprecipe["만드는법_20"] as? String,
            RCP_NA_TIP = temprecipe["저감 조리법 TIP"] as? String
        )


        val options = RequestOptions()
            .placeholder(R.drawable.foodexample) // 로드 중에 표시할 이미지
            .error(R.drawable.foodexample2) // 에러 발생 시 표시할 이미지

        Glide.with(context)
            .load(imageUrl)
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("ITM", "Image load failed")
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("ITM","It's success")
                    return false
                }
            })
            .into(holder.bookmarkPicture)


        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailActivity::class.java)
            intent.putExtra("recipe", recipe) // 전체 RecipeDataForm 객체를 전달합니다.
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return bookmarksList.size
    }

}
