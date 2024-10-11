package com.ksh.cryptify.MainPage.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.ksh.cryptify.R
import com.ksh.cryptify.Utility.Language
import com.ksh.cryptify.databinding.FragmentSettingPageBinding

class SettingPageFragment : Fragment() {
    private var _binding: FragmentSettingPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var languageManager: Language


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingPageBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

        languageManager = Language(requireContext())

        setup_EncodeDecode()

        setup_Language()

        setup_theme()

        switch_checked()

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
            "ja" -> 2 // 일본어
            "zh" -> 3 // 중국어
            "hi" -> 4 // 힌디어
            "ur" -> 5 // 우르두어
            "es" -> 6 // 스페인어
            "ar" -> 7 // 아랍어
            "pt" -> 8 // 포르투갈어
            "bn" -> 9 // 벵골어
            "ru" -> 10 // 러시아어
            "de" -> 11 // 독일어
            "fr" -> 12 // 프랑스어
            "tr" -> 13 // 터키어
            "it" -> 14 // 이탈리아어
            else -> 1 // 기본값은 영어
        }
        binding.languageSpinner.setSelection(languagePosition)

        // Spinner에서 항목 선택 시 언어 변경 처리
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguageCode = when (position) {
                    0 -> "ko"  // 한국어
                    1 -> "en"  // 영어
                    2 -> "ja"  // 일본어
                    3 -> "zh"  // 중국어
                    4 -> "hi"  // 힌디어
                    5 -> "ur"  // 우르두어
                    6 -> "es"  // 스페인어
                    7 -> "ar"  // 아랍어
                    8 -> "pt"  // 포르투갈어
                    9 -> "bn"  // 벵골어
                    10 -> "ru"  // 러시아어
                    11 -> "de"  // 독일어
                    12 -> "fr"  // 프랑스어
                    13 -> "tr"  // 터키어
                    14 -> "it"  // 이탈리아어
                    else -> "en"  // 기본값 영어
                }
                // 언어 변경 적용
                if (savedLanguage != selectedLanguageCode) {
                    // SharedPreferences에 새로운 언어 설정 저장
                    sharedPreferences.edit().putString("language", selectedLanguageCode).apply()

                    languageManager.setLanguage(selectedLanguageCode)

                    // 언어 변경 적용 후 액티비티 재시작
                    requireActivity().recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setup_theme() {
        // 인코딩/디코딩 방식 배열 가져오기
        val themes = resources.getStringArray(R.array.theme_options)

        // Spinner에 Adapter 설정
        val themeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, themes)
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.themeSpinner.adapter = themeAdapter

        // SharedPreferences에 저장된 값을 불러와 Spinner 설정
        val savedTheme = sharedPreferences.getInt("theme", 0)
        binding.themeSpinner.setSelection(savedTheme)

        // Spinner에서 항목 선택 시 SharedPreferences에 저장
        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sharedPreferences.edit().putInt("theme", position).apply()
                val themeMode = when(position) {
                    0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    1 -> AppCompatDelegate.MODE_NIGHT_NO
                    2 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                if(themeMode != savedTheme) { AppCompatDelegate.setDefaultNightMode(themeMode) }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun switch_checked() {
        binding.autoCopySwitch.isChecked = sharedPreferences.getBoolean("auto_copy", false)
        binding.autoCopySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                sharedPreferences.edit().putBoolean("auto_copy", isChecked).apply()
            } else {
                sharedPreferences.edit().putBoolean("auto_copy", isChecked).apply()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}