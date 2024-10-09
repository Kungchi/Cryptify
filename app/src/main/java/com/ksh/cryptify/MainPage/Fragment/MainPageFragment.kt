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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ksh.cryptify.Handler.CameraHandler
import com.ksh.cryptify.Handler.FileSaveHandler
import com.ksh.cryptify.Utility.MLkit
import com.ksh.cryptify.Utility.endecode
import com.ksh.cryptify.databinding.FragmentMainPageBinding

class MainPageFragment : Fragment() {
    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraHandler: CameraHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        cameraHandler = CameraHandler()

        buttonGroup()
        return binding.root
    }

    private fun buttonGroup() {
        binding.encodeBtn.setOnClickListener {
            binding.textView.text = ""
            var encodeData = endecode().encodeBase64(binding.editText.text.toString())
            Log.d("확인용", encodeData)
            binding.textView.text = encodeData
        }
        binding.decodeBtn.setOnClickListener {
            binding.textView.text = ""
            var decodeData = endecode().decodeBase64(binding.editText.text.toString())
            binding.textView.text = decodeData
        }
        binding.clearBtn.setOnClickListener {
            binding.editText.text.clear()
        }
        binding.clearBtn2.setOnClickListener {
            binding.textView.text = ""
        }
        binding.copyBtn.setOnClickListener {
            val text = binding.editText.text.toString()
            if(text.isEmpty()) {
                Toast.makeText(requireContext(), "복사할 내용이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "내용이 복사되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.copyBtn2.setOnClickListener {
            val text = binding.textView.text.toString()
            if(text.isEmpty()) {
                Toast.makeText(requireContext(), "복사할 내용이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "내용이 복사되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.downloadBtn.setOnClickListener {
            if(checkWritePer()) {
                FileSaveHandler().saveFile(requireContext(),binding.textView.text.toString())
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
        binding.cameraBtn.setOnClickListener {
            if(checkCameraPer()) {
                cameraHandler.dispatchTakePictureIntent(this)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    101
                )
            }
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
            Log.d("실행되나???", "실행되나??")
            Log.d("확인하자", cameraHandler.photoURI.toString())
            cameraHandler.photoURI?.let { uri ->
                MLkit().processImage(this, uri) { recognizedText ->
                    recognizedText?.let {
                        // 성공적으로 텍스트 인식 시 처리
                        binding.editText.setText(recognizedText)
                        Log.d("인식된 텍스트", it)
                    } ?: run {
                        // 텍스트 인식 실패 시 처리
                        Log.d("오류", "텍스트 인식 실패")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}