package com.fx.inventory.ui.main.fragments.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fx.inventory.databinding.CameraFragmentBinding
import com.fx.inventory.ui.main.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()

    lateinit var cameraManager: CameraManager
    private lateinit var cameraFragmentBinding: CameraFragmentBinding

    companion object {
        private const val TAG = "CameraFragment"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)


        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraFragmentBinding = CameraFragmentBinding.inflate(LayoutInflater.from(context))
        return cameraFragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideRetakeButton()
        hideDoneButton()

        val uris = arrayListOf<Uri>()

        cameraManager = CameraManager(
            cameraFragmentBinding.viewFinder,
            context = context!!,
            onPictureCaptured = {
                showRetake()
                hideClick()
                showDoneButton()
                uris.add(it)
            },
            onErrorCapturingImage = {},
            viewLifecycleOwner = viewLifecycleOwner,

            )

        checkForPermissionsAndStartCamera()

        cameraFragmentBinding.clickImage.setOnClickListener {
            hideDoneButton()
            hideRetakeButton()
            cameraManager.takePicture()
        }

        cameraFragmentBinding.retakeImage.setOnClickListener {
            showClick()
            hideRetakeButton()
            hideDoneButton()
            uris.removeLast()
            cameraManager.restartCamera()
        }

        cameraFragmentBinding.saveImage.setOnClickListener {
            viewModel.saveUriForItem(uris)
            pop()
        }

        cameraFragmentBinding.closeCamera.setOnClickListener {
            pop()
        }

    }

    private fun checkForPermissionsAndStartCamera() {
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            val requestMultiplePermissions =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
                { permissions ->
                    if (permissions.containsKey(Manifest.permission.CAMERA)) {
                        if (permissions[Manifest.permission.CAMERA]!!) {
                            cameraManager.startCamera()
                        }
                        //TODO ask user to allow camera rationale
                    }
                }


            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                )
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context!!, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onDestroy() {
        cameraManager.onFragmentDestroy()
        super.onDestroy()
    }

    private fun hideRetakeButton() {
        activity?.runOnUiThread {
            cameraFragmentBinding.retakeImage.visibility = View.GONE
        }
    }

    private fun showRetake() {
        activity?.runOnUiThread {
            cameraFragmentBinding.retakeImage.visibility = View.VISIBLE
        }
    }

    private fun hideClick() {
        activity?.runOnUiThread {
            cameraFragmentBinding.clickImage.visibility = View.GONE
        }
    }

    private fun showClick() {
        activity?.runOnUiThread {
            cameraFragmentBinding.clickImage.visibility = View.VISIBLE
        }
    }

    private fun showDoneButton() {
        activity?.runOnUiThread {
            cameraFragmentBinding.saveImage.visibility = View.VISIBLE
        }
    }

    private fun hideDoneButton() {
        activity?.runOnUiThread {
            cameraFragmentBinding.saveImage.visibility = View.GONE
        }
    }

    private fun pop() {
        viewModel.getAllDocumentsForItem()
        parentFragmentManager.popBackStack()
    }


}