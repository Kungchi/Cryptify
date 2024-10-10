package com.ksh.cryptify.MainPage.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.ksh.cryptify.R
import com.ksh.cryptify.databinding.FragmentSettingPageBinding
import java.util.Locale

class SettingPageFragment : Fragment() {
    private var _binding: FragmentSettingPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingPageBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

        setup_EncodeDecode()

        setup_Language()

        return binding.root
    }

    private fun setup_EncodeDecode() {
        // 인코딩/디코딩 방식 배열 가져오기
        val encodingMethods = resources.getStringArray(R.array.encoding_methods)

        // Spinner에 Adapter 설정
        val encodingAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, encodingMethods)
        encodingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.encodingMethodSpinner.adapter = encodingAdapter

        // SharedPreferences에 저장된 값을 불러와 Spinner 설정
        val savedEncodingMethod = sharedPreferences.getInt("encoding_method", 0)
        binding.encodingMethodSpinner.setSelection(savedEncodingMethod)

        // Spinner에서 항목 선택 시 SharedPreferences에 저장
        binding.encodingMethodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sharedPreferences.edit().putInt("encoding_method", position).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }
    }

    private fun setup_Language() {
        // 언어 설정 배열 가져오기
        val languages = resources.getStringArray(R.array.language_options)

        // Spinner에 Adapter 설정
        val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = languageAdapter

        // SharedPreferences에서 저장된 언어 설정 불러오기
        val savedLanguage = sharedPreferences.getString("language", "ko")
        val languagePosition = when (savedLanguage) {
            "ko" -> 0 // 한국어
            "en" -> 1 // 영어
            else -> 0 // 기본값은 한국어
        }
        binding.languageSpinner.setSelection(languagePosition)

        // Spinner에서 항목 선택 시 언어 변경 처리
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguageCode = when (position) {
                    0 -> "ko"  // 한국어
                    1 -> "en"  // 영어
                    else -> "ko"  // 기본값 한국어
                }

                // 현재 언어가 선택된 언어와 다를 경우만 처리
                if (sharedPreferences.getString("language", "ko") != selectedLanguageCode) {
                    // 선택한 언어를 SharedPreferences에 저장
                    sharedPreferences.edit().putString("language", selectedLanguageCode).apply()

                    // 언어 변경 적용
                    setLocale(requireContext(), selectedLanguageCode)

                    // 액티비티 재시작으로 변경 사항 반영
                    requireActivity().recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}