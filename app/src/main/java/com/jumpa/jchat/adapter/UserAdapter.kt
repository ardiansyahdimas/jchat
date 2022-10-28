package com.jumpa.jchat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jumpa.jchat.data.ModelUser
import com.jumpa.jchat.R
import com.jumpa.jchat.databinding.ItemUserBinding
import java.util.ArrayList

class UserAdapter : RecyclerView.Adapter<UserAdapter.ListViewHolder>() {

    private var listData = ArrayList<ModelUser>()
    var onItemClick: ((ModelUser) -> Unit)? = null

    fun setData(newListData: ModelUser?) {
        if (newListData == null) return
        listData.addAll(listOf(newListData))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))

    override fun getItemCount() = listData.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUserBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bind(data: ModelUser) {
            with(binding) {
                tvName.text = data.name
                if(data.gender.toString() == "Male"){
                    Glide.with(itemView.context)
                        .load("https://reqres.in/img/faces/6-image.jpg")
                        .into(userImage)
                }else {
                    Glide.with(itemView.context)
                        .load("https://reqres.in/img/faces/3-image.jpg")
                        .into(userImage)
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(listData[absoluteAdapterPosition])
            }
        }
    }
}