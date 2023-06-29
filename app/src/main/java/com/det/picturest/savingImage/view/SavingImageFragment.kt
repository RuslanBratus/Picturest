package com.det.picturest.savingImage.view
Hello from B
Hello there it is A!
  
import android.content.Context
import android.content.ContextWrapper
import android.database.SQLException
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.det.picturest.R
import com.det.picturest.context.application.ImagesApplication
import com.det.picturest.databinding.FragmentSavingImageBinding
import com.det.picturest.images.model.Image
import com.det.picturest.images.model.Resource
import com.det.picturest.savingImage.viewmodel.SavingViewModel
import com.det.picturest.utils.Utils
import com.det.picturest.utils.extensions.ActivityExtensions.Companion.hideKeyboardAndFocus
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class SavingImageFragment : Fragment() {
    private var _binding: FragmentSavingImageBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var viewModel: SavingViewModel
    private lateinit var currentImageUriString: String

    override fun onAttach(context: Context) {
        (requireContext().applicationContext as ImagesApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSavingImageBinding.bind(view)
        getUriArgument()

SAFASFasf
    }

    private fun getUriArgument() {
        try {
            currentImageUriString = requireArguments().getString(Utils.uriKey)!!
            initListeners()
            setImage()
        }
        catch (exception: Exception) {
            showUriError()
        }

    }

    private fun setImage() {
        Glide.with(requireContext())
            .load(Uri.parse(currentImageUriString))
            .addListener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    showUriError()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    showNormalUI()
                    return false
                }

            })
            .into(binding.savingImageView)
    }

    private fun showLoadingAnimation(visibility: Int) {
        binding.loadingAnimation.visibility = visibility
    }
    private fun showNormalUI() {
        showLoadingAnimation(View.INVISIBLE)
        showMainUI(View.VISIBLE)
        showErrorUI(View.GONE)
    }

    private fun showMainUI(visible: Int) {
        binding.savingImageView.visibility = visible
        binding.saveIcon.visibility = visible
        binding.customNameLayout.visibility = visible
        binding.customNameEdit.visibility = visible
    }

    private fun showErrorUI(visible: Int, message: String? = null) {
        binding.errorImage.visibility = visible
        binding.errorMessage.visibility = visible
        binding.backArrow.visibility = visible
        message.let { binding.errorMessage.text = it }
    }

    private fun showUriError() {
        showLoadingAnimation(View.GONE)
        showMainUI(View.GONE)
        showErrorUI(View.VISIBLE, getString(R.string.oops_image_was_not_found))
    }

    @Suppress("DEPRECATION")
    private fun initListeners() {
        binding.backArrow.setOnClickListener { requireActivity().onBackPressed() }

        binding.customNameEdit.addTextChangedListener {
            isNameCorrect()
        }

        viewModel.savingProcess.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> showSuccessfullySaved()
                is Resource.Loading -> showSavingLoading()
                is Resource.Error -> showErrorSaving(it)
            }
        }

        binding.saveIcon.setOnClickListener {
            if (isNameCorrect()) {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(Uri.parse(currentImageUriString))
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            saveImageIntoInternalStorage(resource,
                                binding.customNameEdit.text.toString())
                        }


                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                    })
                viewModel.saveImage(
                    Image(
                        customName = binding.customNameEdit.text.toString()
                    )
                )
            }
        }

        binding.customNameEdit.setOnEditorActionListener { _, _, _ ->
            requireActivity().hideKeyboardAndFocus()
            return@setOnEditorActionListener true
        }
    }

    private fun showErrorSaving(throwable: Resource.Error) {
        when (throwable.throwable) {
            is SQLException -> showErrorSaving(R.string.error_unique_name)
            else -> showErrorSaving(R.string.error_while_saving_image)
        }
    }

    private fun showErrorSaving(errorMessageResource: Int) {
        showMainUI(View.GONE)
        showErrorUI(View.VISIBLE, getString(errorMessageResource))
    }

    private fun showSavingLoading() {
        showMainUI(View.GONE)
        showErrorUI(View.GONE)
        showLoadingAnimation(View.GONE)
    }

    private fun showSuccessfullySaved() {
        showMainUI(View.GONE)
        showErrorUI(View.GONE)
        showSuccessUI()
    }

    private fun showSuccessUI() {
        binding.successImage.visibility = View.VISIBLE
        binding.backArrow.visibility = View.VISIBLE
        binding.successMessage.visibility = View.VISIBLE
    }

    private fun isNameCorrect(): Boolean {
        val text = binding.customNameEdit.text.toString()
        if (text.isEmpty()) {
            binding.customNameLayout.error = getString(R.string.name_cannot_be_empty)
            return false
        }
        else if (text.length > 26) {
            binding.customNameLayout.error = getString(R.string.too_long_name)
            return false
        }
        binding.customNameLayout.error = null
        return true
    }

    private fun saveImageIntoInternalStorage(bitmapImage: Bitmap, imageName: String): String {
        val cw = ContextWrapper(requireContext().applicationContext)
        val directory = cw.getDir(Utils.imagesDirectoryName, Context.MODE_PRIVATE)
        val mypath = File(directory, imageName)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saving_image, container, false)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}