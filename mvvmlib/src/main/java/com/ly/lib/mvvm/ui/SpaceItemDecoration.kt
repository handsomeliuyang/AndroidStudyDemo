package com.ly.lib.mvvm.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val space: Int, private val mNumCol: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
//        super.getItemOffsets(outRect, view, parent, state)
        outRect.apply {
            right = space
            bottom = space
            left = space
        }
        val position = parent.getChildLayoutPosition(view)
        if (mNumCol <= 2) {
            if (position == 0) {
                outRect.left = space
                outRect.right = space / 2
            } else {
                if (position % mNumCol != 0) {
                    outRect.left = space / 2
                    outRect.right = space
                } else {
                    outRect.left = space
                    outRect.right = space / 2
                }
            }
        } else {
            if (position == 0) {
                outRect.left = space
                outRect.right = space / 2
            } else {
                when {
                    position % mNumCol == 0 -> {
                        outRect.left = space
                        outRect.right = space / 2
                    }
                    position % mNumCol == mNumCol - 1 -> {
                        outRect.left = space / 2
                        outRect.right = space
                    }
                    else -> {
                        outRect.left = space / 2
                        outRect.right = space / 2
                    }
                }
            }
        }
    }
}