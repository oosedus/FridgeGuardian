package com.example.fridgeguardian

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeguardian.databinding.IngredientItemBinding
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
){
    val daysUntilExpired: Int
        get() = ((expDate?.time ?: 0L) - Date().time).div(1000 * 60 * 60 * 24).toInt()
}

class IngredientAdapter(private val ingredientsList: ArrayList<Ingredient>) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(val binding: IngredientItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = IngredientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredientsList[position]
        with(holder.binding) {
            ingredientName.text = ingredient.name
            ingredientExpDate.text =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ingredient.expDate)

            val today = Calendar.getInstance().time
            val expDate = ingredient.expDate ?: today
            val dDay = ((expDate.time - today.time) / (1000 * 60 * 60 * 24)).toInt()
            ingredientDDay.text = root.context.getString(
                R.string.d_day_format,
                Math.abs(dDay)
            )
            // D-Day 5일 이하는 빨간 글씨로 표시, 나머지는 검정색으로 표시
            ingredientDDay.setTextColor(if (dDay <= 5) Color.RED else Color.BLACK)
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