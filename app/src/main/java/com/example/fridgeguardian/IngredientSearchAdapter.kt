package com.example.fridgeguardian

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date


class IngredientSearchAdapter(private val ingredients: List<Triple<String, String, Date>>, private val context: Context) : RecyclerView.Adapter<IngredientSearchAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientname: TextView = itemView.findViewById(R.id.ingredient_name_search)
        val date: TextView = itemView.findViewById(R.id.date)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    private val selectedIngredients = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_search_ingredient_small_form, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tempingredient = ingredients[position]
        holder.ingredientname.text = tempingredient.second

        // Date to String
        val dateFormat = SimpleDateFormat("yyyy-MM-dd") // 원하는 형식으로 수정 가능
        val dateString = dateFormat.format(tempingredient.third)
        holder.date.text = dateString

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedIngredients.add(tempingredient.second)
            } else {
                selectedIngredients.remove(tempingredient.second)
            }
        }

    }

    fun getSelectedIngredients(): List<String> {
        return selectedIngredients
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}



