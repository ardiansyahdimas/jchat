package com.jumpa.jchat.adapter

import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.jumpa.jchat.data.ModelMessage
import com.jumpa.jchat.R
import com.jumpa.jchat.databinding.ItemMessageBinding
import java.io.IOException
import java.net.URL
import java.net.URLConnection

class MessageAdapter(
    options: FirebaseRecyclerOptions<ModelMessage>,
    private val currentIdUser: String?
) : FirebaseRecyclerAdapter<ModelMessage, MessageAdapter.MessageViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        val binding = ItemMessageBinding.bind(view)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int,
        model: ModelMessage
    ) {
        holder.bind(model)
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModelMessage) {
            binding.tvMessage.movementMethod = LinkMovementMethod.getInstance()
            binding.tvMessage2.movementMethod = LinkMovementMethod.getInstance()
            if (currentIdUser == item.senderId && item.senderId != null) {
                binding.pengirim.visibility = View.GONE
                binding.pengirim2.visibility = View.VISIBLE
                if(isUrlValid(item.message)){
                    binding.lyImage2.visibility = View.VISIBLE
                    binding.triangleRedImg.visibility = View.VISIBLE
                    binding.tvTimestampImg2.visibility = View.VISIBLE
                    binding.tvMessage2.visibility = View.GONE
                    binding.triangleRed.visibility = View.GONE
                    binding.tvTimestamp2.visibility = View.GONE
                    Glide.with(itemView.context.applicationContext)
                        .load(item.message)
                        .into(binding.msImg2)
                    if (item.timestamp != null){
                        binding.tvTimestampImg2.setReferenceTime(item.timestamp)
                    }
                } else {
                    binding.lyImage2.visibility = View.GONE
                    binding.triangleRedImg.visibility = View.GONE
                    binding.tvTimestampImg2.visibility = View.GONE
                    binding.tvMessage2.text = item.message
                    binding.tvMessage2.visibility = View.VISIBLE
                    binding.triangleRed.visibility = View.VISIBLE
                    binding.tvTimestamp2.visibility = View.VISIBLE
                    if (item.timestamp != null) {
                        binding.tvTimestamp2.setReferenceTime(item.timestamp)
                    }
                }
            } else {
                binding.pengirim2.visibility = View.GONE
                binding.pengirim.visibility = View.VISIBLE
                if(isUrlValid(item.message)){
                    binding.lyImage.visibility = View.VISIBLE
                    binding.triangleBlueImg.visibility = View.VISIBLE
                    binding.tvTimestampImg.visibility = View.VISIBLE
                    binding.tvMessage.visibility = View.GONE
                    binding.triangleBlue.visibility = View.GONE
                    binding.tvTimestamp.visibility = View.GONE
                    Glide.with(itemView.context.applicationContext)
                        .load(item.message)
                        .into(binding.msImg)
                    if (item.timestamp != null){
                        binding.tvTimestampImg.setReferenceTime(item.timestamp)
                    }
                } else {
                    binding.lyImage.visibility = View.GONE
                    binding.triangleBlueImg.visibility = View.GONE
                    binding.tvTimestampImg.visibility = View.GONE
                    binding.tvMessage.text = item.message
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.triangleBlue.visibility = View.VISIBLE
                    binding.tvTimestamp.visibility = View.VISIBLE
                    if (item.timestamp != null) {
                        binding.tvTimestamp.setReferenceTime(item.timestamp)
                    }
                }
            }

        }
    }

    fun isUrlValid(url: String?): Boolean {
        url ?: return false
        return Patterns.WEB_URL.matcher(url).matches() && URLUtil.isValidUrl(url)
    }


}