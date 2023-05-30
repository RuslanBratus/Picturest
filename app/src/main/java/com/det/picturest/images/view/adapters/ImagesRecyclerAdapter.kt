package com.det.picturest.images.view.adapters

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.det.picturest.R
import com.det.picturest.images.model.Image
import com.det.picturest.images.view.adapters.listeners.OnClickListener
import com.det.picturest.utils.Utils


class ImagesRecyclerAdapter(private val onClickListener: OnClickListener): PagingDataAdapter<Image, ImagesRecyclerAdapter.ImagesViewHolder>(
    DIFF_CALLBACK) {

    private val _selectedImages: MutableList<Image> = mutableListOf()
    val selectedImages get() = _selectedImages as List<Image>
    private var _isSelectedProcess: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSelectedProcess get() = _isSelectedProcess as LiveData<Boolean>

    inner class ImagesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.rawImage)
        private val textView = itemView.findViewById<TextView>(R.id.rawImageText)
        val checkBox = itemView.findViewById<CheckBox>(R.id.rawImageCheckBox)

        fun bind(image: Image) {
            Glide.with(imageView.context)
                .load(Utils.imagesDirectoryPath + "/" + image.customName)
                .into(imageView)
            textView.text = image.customName

            checkBox.visibility = View.INVISIBLE

            itemView.setOnLongClickListener {
                if (_isSelectedProcess.value == true) unselectImage(this, image)
                else startSelectingProcess(this, image)
                return@setOnLongClickListener true
            }

            itemView.setOnClickListener {
                if (_isSelectedProcess.value == true) imageWasSelected(this, image)
                else onClickListener.itemWasClicked(image)
            }
        }
    }

    private fun unselectImage(holder: ImagesViewHolder, image: Image) {
        holder.checkBox.visibility = View.INVISIBLE
        _selectedImages.remove(image)
        if (_selectedImages.isEmpty()) _isSelectedProcess.value = false
    }

    private fun startSelectingProcess(holder: ImagesViewHolder, image: Image) {
        _isSelectedProcess.value = true
        selectImage(holder, image)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val image = getItem(position)
        image?.let { itImage ->
            holder.bind(itImage)
        }
    }

    private fun imageWasSelected(holder : ImagesViewHolder, selectedImage: Image) {
        if (isImageSelected(selectedImage)) unselectImage(holder, selectedImage)
        else selectImage(holder, selectedImage)
    }

    private fun isImageSelected(image: Image): Boolean {
        return _selectedImages.contains(image)
    }

    private fun selectImage(holder: ImagesViewHolder, selectedImage: Image) {
        holder.checkBox.visibility = View.VISIBLE
        _selectedImages.add(selectedImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.raw_image, parent, false))
    }

    fun clearSelectedState() {
        _isSelectedProcess.value = false
        _selectedImages.clear()

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Image>() {
            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem == newItem
            }

        }
    }

    class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            if (parent.getChildLayoutPosition(view) % 3 == 0)
                outRect.left = 0
            else
                outRect.left = space




            outRect.right = space
            outRect.bottom = space



            if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1) {
                outRect.top = space
            } else {
                outRect.top = 0
            }
        }
    }

}