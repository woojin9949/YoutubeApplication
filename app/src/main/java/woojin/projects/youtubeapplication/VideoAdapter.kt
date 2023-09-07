package woojin.projects.youtubeapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woojin.projects.youtubeapplication.databinding.ItemVideoBinding
import woojin.projects.youtubeapplication.model.Youtuber

class VideoAdapter(private val context: Context) :
    ListAdapter<Youtuber, VideoAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Youtuber) {
            Glide.with(binding.videoThumbnailImageView)
                .load(item.image)
                .into(binding.videoThumbnailImageView)

            Glide.with(binding.channelLogoImageView)
                .load(R.drawable.baseline_person_24)
                .into(binding.channelLogoImageView)

            binding.titleTextView.text = "제목"
            binding.nameTextView.text = context.getString(R.string.video_info, item.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : ItemCallback<Youtuber>() {
            override fun areItemsTheSame(oldItem: Youtuber, newItem: Youtuber): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Youtuber, newItem: Youtuber): Boolean {
                return oldItem == newItem
            }
        }
    }
}