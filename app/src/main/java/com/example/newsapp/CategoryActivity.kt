package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityChooseCategoryBinding

class CategoryActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChooseCategoryBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val selectedCategory = mutableSetOf<String>()
    private val categoryMap = mapOf(
        "General" to "general",
        "Business" to "business",
        "Entertainment" to "entertainment",
        "Health" to "health",
        "Science" to "science",
        "Sports" to "sports",
        "Technology" to "technology",
    )

    private fun toggleCategory(buttonText: String){
        val langCode = categoryMap[buttonText].toString()

        if(selectedCategory.contains(langCode)){
            selectedCategory.remove(langCode)
        }else{
            selectedCategory.add(langCode)
        }
    }
    private fun setupCategoryButton(button: Button) {
        button.setOnClickListener {
            val category = button.text.toString()
            toggleCategory(category)

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
        binding = ActivityChooseCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val buttons = listOf(
            binding.newsBtn1,
            binding.newsBtn2,
            binding.newsBtn3,
            binding.newsBtn4,
            binding.newsBtn5,
            binding.newsBtn6,
            binding.newsBtn7,
        )
        buttons.forEach { button ->
            setupCategoryButton(button)
        }

        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        binding.nextBtn.setOnClickListener{
            if(selectedCategory.isNotEmpty()){
                val editor = sharedPreferences.edit()
                val convertedCategory = selectedCategory.joinToString(", ")
                editor.putString("CATEGORY", convertedCategory)
                editor.apply()

                val intent = Intent(this, CountryActivity::class.java)
                startActivity(intent)

            }else{
                Toast.makeText(this, "Choose category.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backBtn.setOnClickListener{
            val editor = sharedPreferences.edit()
            editor.remove("LANGUAGES")
            editor.remove("CATEGORY")
            editor.apply()

            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }
    }
}