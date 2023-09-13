package woojin.projects.youtubeapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woojin.projects.youtubeapplication.databinding.ItemVideoBinding
import woojin.projects.youtubeapplication.databinding.ItemVideoHeaderBinding
import woojin.projects.youtubeapplication.model.YoutuberItem

class VideoDetailAdapter(
    private val context: Context,
    private val onClick: (YoutuberItem) -> Unit
) :
    ListAdapter<YoutuberItem, RecyclerView.ViewHolder>(diffUtil) {

    inner class HeaderViewHolder(private val binding: ItemVideoHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: YoutuberItem) {
            binding.subTitleTextView.text = context.getString(R.string.header_video_info)
            binding.titleTextView.text = item.name
            binding.channelNameTextView.text = item.name
            Glide.with(binding.channelLogoImageView)
                .load(item.image)
                .centerCrop()
                .into(binding.channelLogoImageView)
        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: YoutuberItem) {
            Glide.with(binding.videoThumbnailImageView)
                .load(item.image)
                .into(binding.videoThumbnailImageView)

            Glide.with(binding.channelLogoImageView)
                .load(R.drawable.baseline_person_24)
                .into(binding.channelLogoImageView)

            binding.titleTextView.text = "제목"
            binding.nameTextView.text = context.getString(R.string.video_info, item.name)
            binding.root.setOnClickListener {
                onClick.invoke(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_VIDEO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(
                ItemVideoHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            VideoViewHolder(
                ItemVideoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            (holder as HeaderViewHolder).bind(currentList[position])
        } else {
            (holder as VideoViewHolder).bind(currentList[position])
        }
    }


    companion object {

        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_VIDEO = 1

        val diffUtil = object : DiffUtil.ItemCallback<YoutuberItem>() {
            override fun areItemsTheSame(
                oldItem: YoutuberItem,
                newItem: YoutuberItem
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: YoutuberItem,
                newItem: YoutuberItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}