package com.ksh.cryptify.MainPage.Fragment

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ksh.cryptify.Handler.CameraHandler
import com.ksh.cryptify.Handler.FileSaveHandler
import com.ksh.cryptify.OverlayPage.OverlayActivity
import com.ksh.cryptify.R
import com.ksh.cryptify.Utility.CustomToast
import com.ksh.cryptify.Utility.Endecode
import com.ksh.cryptify.Utility.MLkit
import com.ksh.cryptify.databinding.FragmentMainPageBinding

class MainPageFragment : Fragment() {
    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraHandler: CameraHandler
    private lateinit var endecode: Endecode
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        cameraHandler = CameraHandler()
        endecode = Endecode()
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

        buttonGroup()
        return binding.root
    }
    private fun buttonGroup() {
        binding.encodeBtn.setOnClickListener {
            binding.textView.text = ""
            val inputText = binding.editText.text.toString()
            val encodeData = endecode.processText(requireContext(), inputText, true)
            binding.textView.text = encodeData
            val savedCopy = sharedPreferences.getBoolean("auto_copy", false)
            if(savedCopy) {
                copy(encodeData)
            }
        }
        binding.decodeBtn.setOnClickListener {
            binding.textView.text = ""
            val inputText = binding.editText.text.toString()
            val decodeData = endecode.processText(requireContext(), inputText, false)
            binding.textView.text = decodeData
            val savedCopy = sharedPreferences.getBoolean("auto_copy", false)
            if(savedCopy) {
                copy(decodeData)
            }
        }
        binding.clearBtn.setOnClickListener {
            binding.editText.text.clear()
        }
        binding.clearBtn2.setOnClickListener {
            binding.textView.text = ""
        }
        binding.copyBtn.setOnClickListener {
            val text = binding.editText.text.toString()
            copy(text)
        }
        binding.copyBtn2.setOnClickListener {
            val text = binding.textView.text.toString()
            copy(text)
        }
        binding.downloadBtn.setOnClickListener {
            if(checkWritePer()) {
                if(binding.textView.text.isNotEmpty()) {
                    FileSaveHandler().saveFile(requireContext(), binding.textView.text.toString(), binding.textView.text.toString())
                } else {
                    CustomToast.show(requireContext(),getString(R.string.toast_no_content))
                }
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
        binding.cameraBtn.setOnClickListener {
            if(checkCameraPer()) {
                cameraHandler.dispatchTakePictureIntent(this)
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    101
                )
            }
        }
    }

    private fun copy(text : String) {
        if(text.isEmpty()) {
            CustomToast.show(requireContext(),getString(R.string.toast_no_copy_content))
        } else {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
            CustomToast.show(requireContext(),getString(R.string.toast_copied))
        }
    }

    private fun checkWritePer(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // 안드로이드 10 이상에서는 권한이 필요 없음
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun checkCameraPer(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraHandler.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            cameraHandler.photoURI?.let { uri ->
                MLkit().processImage(this, uri) { texts, boundingBoxes ->
                    if (texts.isNotEmpty() && boundingBoxes.isNotEmpty()) {
                        val intent = Intent(requireContext(), OverlayActivity::class.java).apply {
                            putExtra("imageUri", uri.toString())
                            putStringArrayListExtra("texts", ArrayList(texts))
                            putParcelableArrayListExtra("boundingBoxes", ArrayList(boundingBoxes))
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                // 카메라 권한 처리
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    cameraHandler.dispatchTakePictureIntent(this)
                } else {
                    CustomToast.show(requireContext(),getString(R.string.toast_camera_permission_needed))
                }
            }
            1 -> {
                // 쓰기 권한 처리
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    FileSaveHandler().saveFile(requireContext(), binding.textView.text.toString(), binding.textView.text.toString())
                } else {
                    CustomToast.show(requireContext(),getString(R.string.toast_write_permission_needed))
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}