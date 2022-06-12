package ui.home.leaderboard

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listUserPoint[position]
        
        holder.apply {
            fun imageLoader(resource: Int) {
                Glide.with(itemView.context)
                    .load(resource)
                    .into(ivTrophy)
            }

            when (item.position) {
                1 -> {
                    ivTrophy.setPadding(0)
                    imageLoader(R.drawable.ic_trophy_gold)
                }
                2 -> {
                    ivTrophy.setPadding(0)
                    imageLoader(R.drawable.ic_trophy_silver)
                }
                3 -> {
                    ivTrophy.setPadding(0)
                    imageLoader(R.drawable.ic_trophy_bronze)
                }
                else -> {
                    ivTrophy.setPadding(20)
                    imageLoader(R.drawable.ic_medal)
                }
            }

            tvPosition.text = item.position.toString()
            tvUsername.text = item.name
            tvPoints.text = item.score.toString()
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