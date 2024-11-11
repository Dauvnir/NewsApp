package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityChooseCountryBinding

class CountryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseCountryBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val selectedCountry = mutableSetOf<String>()
    private val countries = listOf(
        "Argentina",
        "Australia",
        "Austria",
        "Belgium",
        "Brazil",
        "Bulgaria",
        "Canada",
        "China",
        "Colombia",
        "Czech Republic",
        "Egypt",
        "France",
        "Germany",
        "Greece",
        "Hong Kong",
        "Hungary",
        "India",
        "Indonesia",
        "Ireland",
        "Israel",
        "Italy",
        "Japan",
        "Latvia",
        "Lithuania",
        "Malaysia",
        "Mexico",
        "Morocco",
        "Netherlands",
        "New Zealand",
        "Nigeria",
        "Norway",
        "Philippines",
        "Poland",
        "Portugal",
        "Romania",
        "Saudi Arabia",
        "Serbia",
        "Singapore",
        "Slovakia",
        "Slovenia",
        "South Africa",
        "South Korea",
        "Sweden",
        "Switzerland",
        "Taiwan",
        "Thailand",
        "Turkey",
        "UAE",
        "Ukraine",
        "United Kingdom",
        "United States",
        "Venezuela"
    )
    private val countryMap = mapOf(
        "Argentina" to "ar",
        "Australia" to "au",
        "Austria" to "at",
        "Belgium" to "be",
        "Brazil" to "br",
        "Bulgaria" to "bg",
        "Canada" to "ca",
        "China" to "cn",
        "Colombia" to "co",
        "Czech Republic" to "cz",
        "Egypt" to "eg",
        "France" to "fr",
        "Germany" to "de",
        "Greece" to "gr",
        "Hong Kong" to "hk",
        "Hungary" to "hu",
        "India" to "in",
        "Indonesia" to "id",
        "Ireland" to "ie",
        "Israel" to "il",
        "Italy" to "it",
        "Japan" to "jp",
        "Latvia" to "lv",
        "Lithuania" to "lt",
        "Malaysia" to "my",
        "Mexico" to "mx",
        "Morocco" to "ma",
        "Netherlands" to "nl",
        "New Zealand" to "nz",
        "Nigeria" to "ng",
        "Norway" to "no",
        "Philippines" to "ph",
        "Poland" to "pl",
        "Portugal" to "pt",
        "Romania" to "ro",
        "Saudi Arabia" to "sa",
        "Serbia" to "rs",
        "Singapore" to "sg",
        "Slovakia" to "sk",
        "Slovenia" to "si",
        "South Africa" to "za",
        "South Korea" to "kr",
        "Sweden" to "se",
        "Switzerland" to "ch",
        "Taiwan" to "tw",
        "Thailand" to "th",
        "Turkey" to "tr",
        "UAE" to "ae",
        "Ukraine" to "ua",
        "United Kingdom" to "gb",
        "United States" to "us",
        "Venezuela" to "ve"
    )

    private fun toggleCountryCode(buttonText: String){
        val langCode = countryMap[buttonText].toString()

        if(selectedCountry.contains(langCode)){
            selectedCountry.remove(langCode)
        }else{
            selectedCountry.add(langCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        binding.backBtn.setOnClickListener{
            val editor = sharedPreferences.edit()
            editor.remove("COUNTRY")
            editor.remove("CATEGORY")
            editor.apply()

            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

        binding.nextBtn.setOnClickListener{
            if(selectedCountry.isNotEmpty()){
                val editor = sharedPreferences.edit()
                val convertedCountry= selectedCountry.joinToString(", ")
                editor.putString("COUNTRY", convertedCountry)
                editor.apply()

                val intent = Intent(this, ConnectedToDatabaseActivity::class.java)
                startActivity(intent)

            }else{
                Toast.makeText(this, "Choose country.", Toast.LENGTH_SHORT).show()
            }
        }
        val linearLayout: LinearLayout = binding.listOfButtons

        for (country in countries) {
            val button = Button(this).apply {
                text = country
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(20, 20, 0, 20)
                }
                textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                setPadding(80, 40, 0, 40)
                setBackgroundResource(R.drawable.rounded_corners)
                setTextColor(Color.parseColor("#C074A4"))
                textSize = 24f

                setOnClickListener {
                    val whiteColor = getColor(R.color.white)
                    val purpleColor = getColor(R.color.font)

                    if (this.currentTextColor == whiteColor) {
                        this.setTextColor(purpleColor)
                        this.backgroundTintList = ColorStateList.valueOf(whiteColor)
                    } else {
                        this.setTextColor(whiteColor)
                        this.backgroundTintList = ColorStateList.valueOf(purpleColor)
                    }
                    val currentText = this.text.toString()
                    toggleCountryCode(currentText)
                }
            }

            linearLayout.addView(button)
        }

    }
}