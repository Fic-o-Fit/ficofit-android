package com.c22ps072.ficofit.utils

import androidx.recyclerview.widget.DiffUtil
import com.c22ps072.ficofit.data.source.remote.response.UserPoint

class DiffUtilCallback(
    private val oldList: ArrayList<UserPoint>,
    private val newList: ArrayList<UserPoint>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].name != newList[newItemPosition].name -> false
            oldList[oldItemPosition].highScore != newList[newItemPosition].highScore -> false
            else -> true
        }
    }
}