package com.example.fridgeguardian

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.ingredient_name)
        val expDateTextView: TextView = view.findViewById(R.id.ingredient_exp_date)
        val dDayTextView: TextView = view.findViewById(R.id.ingredient_d_day)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredientsList[position]
        holder.nameTextView.text = ingredient.name
        holder.expDateTextView.text =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ingredient.expDate)

        val today = Calendar.getInstance().time
        val expDate = ingredient.expDate ?: today
        val dDay = ((expDate.time - today.time) / (1000 * 60 * 60 * 24)).toInt()
        holder.dDayTextView.text = holder.itemView.context.getString(
            R.string.d_day_format,
            Math.abs(dDay)
        )

        if (dDay <= 5) {
            holder.dDayTextView.setTextColor(Color.RED)
        } else {
            holder.dDayTextView.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount() = ingredientsList.size
}