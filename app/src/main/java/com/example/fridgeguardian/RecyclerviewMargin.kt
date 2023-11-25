package com.example.fridgeguardian

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerviewMargin : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = 2
        outRect.top = 2
        outRect.left = 2
        outRect.right = 2
        super.getItemOffsets(outRect, view, parent, state)
    }
}