package com.ksh.cryptify.MainPage.Fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ksh.cryptify.Handler.FileSaveHandler
import com.ksh.cryptify.R
import com.ksh.cryptify.databinding.FragmentFilePageBinding

class FilePageFragment : Fragment() {
    private var _binding: FragmentFilePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var FileSaveHandler: FileSaveHandler

    private var fileUri: Uri? = null
    private var fileName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilePageBinding.inflate(inflater, container, false)
        FileSaveHandler = FileSaveHandler()
        buttonGroup()

        return binding.root
    }

    private fun buttonGroup() {
        binding.encodeButton.setOnClickListener {
            fileUri?.let { uri ->
                fileUri?.let { name ->
                    // 파일 인코딩 처리
                    FileSaveHandler.encodeFile(requireContext(), uri, binding.fileNameTextView.text.toString())
                } ?: run {
                    showCustomToast(getString(R.string.toast_no_file_name))
                }
            } ?: run {
                showCustomToast(getString(R.string.toast_select_file))
            }
        }
        binding.decodeButton.setOnClickListener {
            fileUri?.let { uri ->
                fileUri?.let { name ->
                    // 파일 디코딩 처리
                    FileSaveHandler.decodeFile(requireContext(), uri, binding.fileNameTextView.text.toString())
                } ?: run {
                    showCustomToast(getString(R.string.toast_no_file_name))
                }
            } ?: run {
                showCustomToast(getString(R.string.toast_select_file))
            }
        }
        binding.fileSelectButton.setOnClickListener {
            //TODO 이미지 버튼
            FileSaveHandler.FilePicker(this)
        }
    }

    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        // 커스텀 레이아웃의 TextView에 메시지 설정
        val textView = layout.findViewById<TextView>(R.id.custom_toast_message)
        textView.text = message

        // 커스텀 Toast 생성
        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout

        // 위치 조정 (예: 화면 상단에 표시)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
        toast.show()
    }

    // 파일 선택 후 결과를 처리하는 메서드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FileSaveHandler.REQUEST_FILE_GET && resultCode == Activity.RESULT_OK) {
            data?.data?.let { returnUri ->
                fileUri = returnUri
                context?.contentResolver?.query(returnUri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    fileName = cursor.getString(nameIndex)
                    binding.fileNameTextView.text = fileName
                }
            }
        }
    }
}