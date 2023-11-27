package com.example.fridgeguardian

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeguardian.databinding.IngredientItemFormBinding
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

//식재료 데이터 담을 클래스
data class Ingredient(
    var name: String = "",
    var category: String = "",
    var quantity: Int = 0,
    var expDate: Date? = null
) : Serializable {
    var documentId: String = ""
    val daysUntilExpired: Int
        get() {
            val today = Calendar.getInstance().time
            return if (expDate != null) {
                ((expDate!!.time - today.time) / (1000 * 60 * 60 * 24)).toInt()
            } else {
                0
            }
        }
}

class IngredientAdapter(private val ingredientsList: ArrayList<Ingredient>) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(val binding: IngredientItemFormBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = IngredientItemFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }

    // 식재료 이름, 카테고리, 수량, 유통기한 모두 View
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredientsList[position]
        with(holder.binding) {
            ingredientName.text = ingredient.name
            ingredientCategory.text = ingredient.category
            ingredientQuantity.text = "Quantity : ${ingredient.quantity.toString()}"

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            ingredientExpDate.text = ingredient.expDate?.let { sdf.format(it) } ?: "N/A"
            ingredientDDay.text = "D-${ingredient.daysUntilExpired}"

            ingredientDDay.setTextColor(if (ingredient.daysUntilExpired <= 5) Color.RED else Color.BLACK)
        }

        // 식재료 별 수정하는 버튼, 각 식재료에 대한 정보 EDIT ACTIVITY 로 전달
        holder.binding.ingredientEditButton.setOnClickListener {
            val context = holder.itemView.context
            val ingredient = ingredientsList[position]
            val intent = Intent(context, IngredientEditActivity::class.java).apply {
                putExtra("ingredient", ingredient)
                putExtra("ingredientId", ingredient.documentId)
            }
            context.startActivity(intent)
        }
    }

    // 식재료 리스트 오름차순/내림차순 정렬
    fun sortIngredients(ascending: Boolean) {
        ingredientsList.sortWith(compareBy { it.daysUntilExpired })
        if (!ascending) {
            ingredientsList.reverse()
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = ingredientsList.size
}