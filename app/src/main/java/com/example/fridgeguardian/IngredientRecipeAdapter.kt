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
import org.w3c.dom.Text


class IngredientRecipeAdapter(private val recipeList: List<Pair<RecipeDataForm, Int>>, private val context: Context) : RecyclerView.Adapter<IngredientRecipeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.recipe_image)
        val name: TextView = view.findViewById(R.id.recipe_name)
        val count : TextView = view.findViewById(R.id.count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item_small_form2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.name.text = recipe.first.RCP_NM
        holder.count.text = recipe.second.toString()


        // 이미지 로딩 라이브러리인 Glide를 사용하여 이미지를 불러옵니다.
        val imageUrl = recipe.first.ATT_FILE_NO_MK
        val options = RequestOptions()
            .placeholder(R.drawable.loading) // 로드 중에 표시할 이미지
            .error(R.drawable.error) // 에러 발생 시 표시할 이미지

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
            .into(holder.image)


        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailActivity::class.java)
            Log.d("ITM", "abc ${recipe}")
            Log.d("ITM", "bookmark.second type: ${recipe::class.java}")
            intent.putExtra("recipe", recipe) // 전체 RecipeDataForm 객체를 전달합니다.
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = recipeList.size
}
