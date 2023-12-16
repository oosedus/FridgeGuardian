package com.example.fridgeguardian

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView


class RecipeDetailFinalAdapter(private val recipeData: RecipeDataForm, private val context: Context) : RecyclerView.Adapter<RecipeDetailFinalAdapter.ViewHolder>() {
    val data = mutableMapOf<String, List<String>>()
    init {
        changedata()
    }

    fun changedata() {
        val 조리방법 = listOfNotNull(
            recipeData.RCP_WAY2,
        )
        val 만드는법 = listOfNotNull(
            recipeData.MANUAL01,
            recipeData.MANUAL02,
            recipeData.MANUAL03,
            recipeData.MANUAL04,
            recipeData.MANUAL05,
        )
        val 재료정보 = listOfNotNull(
            recipeData.RCP_PARTS_DTLS
        )
        // 위에서 생성한 리스트들을 맵에 추가합니다.
        data["조리방법"] = 조리방법
        Log.d("ITM", "${조리방법}")
        data["만드는법"] = 만드는법
        Log.d("ITM", "${만드는법}")
        data["재료정보"] = 재료정보
        Log.d("ITM", "${재료정보}")
    }


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            val layout01 = view.findViewById<MaterialCardView>(R.id.layout01)
            layout01.setOnClickListener {
                val layoutDetail01 = itemView.findViewById<LinearLayout>(R.id.layoutDetail01)
                val layoutBtn01 = itemView.findViewById<ImageButton>(R.id.layoutBtn01)
                if (layoutDetail01.visibility == View.VISIBLE) {
                    layoutDetail01.visibility = View.GONE
                    layoutBtn01.animate().apply {
                        duration = 200
                        rotation(0f)
                    }
                } else {
                    layoutDetail01.visibility = View.VISIBLE
                    layoutBtn01.animate().apply {
                        duration = 200
                        rotation(180f)
                    }
                }
            }
        }

        fun bind(pos: Int) {
            val textView01 = itemView.findViewById<TextView>(R.id.textView01)
            textView01.text = data.keys.elementAt(pos)
            data.values.elementAt(pos).forEach {
                val view = TextView(itemView.context).apply {
                    text = "· $it"
                    textSize = 20f
                    setPadding(10, 10, 5, 10)
                }
                itemView.findViewById<ViewGroup>(R.id.layoutDetail01).addView(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeDetailFinalAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}





