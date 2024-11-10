package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityChooseLanguagesBinding

class LanguageActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChooseLanguagesBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val selectedLanguages = mutableSetOf<String>()
    private val languageMap = mapOf(
        "Arabic" to "ar",
        "German" to "de",
        "English" to "en",
        "Spanish" to "es",
        "French" to "fr",
        "Hebrew" to "he",
        "Italian" to "it",
        "Dutch" to "nl",
        "Norwegian" to "no",
        "Portuguese" to "pt",
        "Swedish" to "se",
        "Chinese" to "zh"
    )


    private fun toggleLangCode(buttonText: String){
        val langCode = languageMap[buttonText].toString()

        if(selectedLanguages.contains(langCode)){
            selectedLanguages.remove(langCode)
        }else{
            selectedLanguages.add(langCode)
        }
    }
    private fun setupLanguageButton(button: Button) {
        button.setOnClickListener {
            val language = button.text.toString()
            toggleLangCode(language)

            val whiteColor = getColor(R.color.white)
            val purpleColor = getColor(R.color.font)

            if (button.currentTextColor == whiteColor) {
                button.setTextColor(purpleColor)
                button.backgroundTintList = ColorStateList.valueOf(whiteColor)
            } else {
                button.setTextColor(whiteColor)
                button.backgroundTintList = ColorStateList.valueOf(purpleColor)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLanguagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttons = listOf(
            binding.button13,
            binding.button14,
            binding.button15,
            binding.button16,
            binding.button17,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9,
            binding.button10,
            binding.button11,
            binding.button12
        )
        buttons.forEach { button ->
            setupLanguageButton(button)
        }

        binding.nextBtn.setOnClickListener{
            if(selectedLanguages.isNotEmpty()){
                sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val convertedLanguages = selectedLanguages.joinToString(", ")
                editor.putString("LANGUAGES", convertedLanguages)
                editor.apply()

                val intent = Intent(this, CategoryActivity::class.java)
                startActivity(intent)
                println(convertedLanguages)
            }else{
                Toast.makeText(this, "Choose language.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}