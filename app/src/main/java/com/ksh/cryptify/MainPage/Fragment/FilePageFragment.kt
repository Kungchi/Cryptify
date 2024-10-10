package com.ksh.cryptify.MainPage.Fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ksh.cryptify.Handler.FileSaveHandler
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
                    Toast.makeText(requireContext(), "파일 이름을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "먼저 파일을 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.decodeButton.setOnClickListener {
            fileUri?.let { uri ->
                fileUri?.let { name ->
                    // 파일 디코딩 처리
                    FileSaveHandler.decodeFile(requireContext(), uri, binding.fileNameTextView.text.toString())
                } ?: run {
                    Toast.makeText(requireContext(), "파일 이름을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "먼저 파일을 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.fileSelectButton.setOnClickListener {
            //TODO 이미지 버튼
            FileSaveHandler.FilePicker(this)
        }

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