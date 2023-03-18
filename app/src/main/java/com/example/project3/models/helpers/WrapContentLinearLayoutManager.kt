package com.example.project3.models.helpers

import android.content.Context
import android.util.Log.e
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WrapContentLinearLayoutManager(
    context: Context,
    ori: Int,
    rev: Boolean = false
) : LinearLayoutManager(context, ori, rev) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e("TAG", "meet a IOOBE in RecyclerView")
        }
    }
}