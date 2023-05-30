package com.det.picturest.images.view

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.det.picturest.R
import com.det.picturest.context.application.ImagesApplication
import com.det.picturest.databinding.FragmentImagesBinding
import com.det.picturest.images.model.Image
import com.det.picturest.images.view.adapters.ImagesRecyclerAdapter
import com.det.picturest.images.view.adapters.listeners.OnClickListener
import com.det.picturest.images.viewmodel.ImagesViewModel
import com.det.picturest.utils.Utils
import com.det.picturest.utils.extensions.ActivityExtensions.Companion.hideKeyboardAndFocus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


class ImagesFragment : Fragment(), OnClickListener {
    private var _binding: FragmentImagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mAdapter: ImagesRecyclerAdapter

    @Inject lateinit var viewModel: ImagesViewModel

    override fun onAttach(context: Context) {
        (requireContext().applicationContext as ImagesApplication).appComponent.inject(this)
        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_images, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentImagesBinding.bind(view)
        initRecyclerAdapter()
        initImagePicker()
        setAdapterData()
        initUIListeners()
    }

    private fun initRecyclerAdapter() {
        mAdapter = ImagesRecyclerAdapter(this)
        binding.imagesRecyclerView.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            setHasFixedSize(true)
            itemAnimator = null
            addItemDecoration(ImagesRecyclerAdapter.SpacesItemDecoration(5))
        }

    }

    private fun initImagePicker() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) {
            it?.let { openImageSavingFragment(uri = it) }
        }
    }

    private fun openImageSavingFragment(uri: Uri) {
        val bundle = Bundle()
        bundle.putString(Utils.uriKey, uri.toString())
        findNavController().navigate(R.id.action_imagesFragment_to_savingImageFragment, bundle)
    }

    private fun setAdapterData() {
        lifecycleScope.launch {
            viewModel.getImagesPaging(binding.searchingEdit.text.toString())
                .collectLatest { pagingData ->
                    mAdapter.submitData(pagingData)
                }
        }
    }

    private fun initUIListeners() {
        binding.searchingEdit.setOnEditorActionListener { _, _, _ ->
            requireActivity().hideKeyboardAndFocus()
            return@setOnEditorActionListener true
        }
        binding.searchingEdit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                setAdapterData()
            }
        })
        binding.addImage.setOnClickListener {
            pickImage()
        }
        binding.deleteImages.setOnClickListener {
            viewModel.deleteImages(mAdapter.selectedImages.toList())
            lifecycleScope.launch {
                delay(200)
                setAdapterData()
            }
            mAdapter.clearSelectedState()
        }
        mAdapter.isSelectedProcess.observe(viewLifecycleOwner) {
            if (it) binding.deleteImages.visibility = View.VISIBLE
            else binding.deleteImages.visibility = View.GONE
        }
    }

    private fun pickImage() {
        activityResultLauncher.launch(Array(1){"image/*"})
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun <T> itemWasClicked(item: T) {
        val image : Image = item as Image
        val cw = ContextWrapper(requireContext().applicationContext)
        val directory = cw.getDir(Utils.imagesDirectoryName, Context.MODE_PRIVATE)
        val mypath = File(directory, image.customName)
        val fileUri : Uri? = FileProvider.getUriForFile(requireContext(), "com.det.picturest.provider", mypath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileUri, "image/*")
        intent.flags = FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }
}