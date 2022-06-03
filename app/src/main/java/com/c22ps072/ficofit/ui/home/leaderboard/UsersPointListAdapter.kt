package ui.home.leaderboard

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.data.source.remote.response.UserPoint
import com.c22ps072.ficofit.databinding.ItemUsersPointBinding
import com.c22ps072.ficofit.utils.DiffUtilCallback

class UsersPointListAdapter: RecyclerView.Adapter<UsersPointListAdapter.ViewHolder>() {
    private var listUserPoint = ArrayList<UserPoint>()

    inner class ViewHolder(binding: ItemUsersPointBinding): RecyclerView.ViewHolder(binding.root) {
        val tvPosition = binding.tvPosition
        val tvUsername = binding.tvUsername
        val tvPoints = binding.tvPoints
        val ivTrophy = binding.ivTrophy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("ADAPTER", "listUserPoint: $listUserPoint")
        val itemUsersPointBinding = ItemUsersPointBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemUsersPointBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listUserPoint[position]
        
        when (position) {
            0 -> {
                holder.ivTrophy.setImageResource(R.drawable.ic_trophy_gold)
            }
            1 -> {
                holder.ivTrophy.setImageResource(R.drawable.ic_trophy_silver)
            }
            2 -> {
                holder.ivTrophy.setImageResource(R.drawable.ic_trophy_bronze)
            }
            else -> holder.ivTrophy.setPadding(10)
        }
        
        holder.apply { 
            tvPosition.text = (position + 1).toString()
            tvUsername.text = item.name
            tvPoints.text = item.highScore.toString()
        }
    }

    override fun getItemCount(): Int = listUserPoint.size

    fun setData(newData: ArrayList<UserPoint>) {
        val diffUtilCallback = DiffUtilCallback(listUserPoint, newData)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        listUserPoint = newData
        diffResult.dispatchUpdatesTo(this)
    }

}