package com.example.fridgeguardian

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class RecipeAdapter(private val recipeList: List<RecipeDataForm>, private val context: Context) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.recipe_image)
        val name: TextView = view.findViewById(R.id.recipe_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item_small_form, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.name.text = recipe.RCP_NM

        val options = RequestOptions()
            .placeholder(R.drawable.foodexample)

        // 이미지 로딩 라이브러리인 Glide를 사용하여 이미지를 불러옵니다.
        val imageUrl = if (!recipe.ATT_FILE_NO_MAIN.isNullOrEmpty()) recipe.ATT_FILE_NO_MAIN else recipe.ATT_FILE_NO_MK
        Glide.with(context)
            .setDefaultRequestOptions(options)
            .load(imageUrl)
            .into(holder.image)



        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailActivity::class.java)
            intent.putExtra("recipeId", recipe.RCP_SEQ) // 세부 정보 페이지에서 사용할 레시피 ID를 전달합니다.
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = recipeList.size
}
