package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.ActivitySuccessBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConnectedToDatabaseActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        val languagePref = sharedPreferences.getString("LANGUAGES", null)?.replace(" ", "") ?: ""
        val countryPref = sharedPreferences.getString("COUNTRY", null)?.replace(" ", "") ?: ""
        val categoryPref = sharedPreferences.getString("CATEGORY", null)?.replace(" ", "") ?: ""

        Log.d("values", "$languagePref, $countryPref, $categoryPref")
        fetchNews(categoryPref, languagePref, countryPref)

        binding.imageButton.setOnClickListener{
            val themedContext = ContextThemeWrapper(this, R.style.overwritten) // Use the base theme
            val popupMenu = PopupMenu(themedContext, binding.imageButton)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId){
                    R.id.password -> {
                        val intent = Intent(this, ChangePasswordActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.account -> {
                        val intent = Intent(this, DeleteAccountActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.email -> {
                        val intent = Intent(this, ChangeEmailActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.preference -> {
                        val editor = sharedPreferences.edit()
                        editor.remove("LANGUAGES")
                        editor.remove("CATEGORY")
                        editor.remove("COUNTRY")
                        editor.apply()

                        val intent = Intent(this, LanguageActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun fetchNews(categoryPref: String, languagePref: String, countryPref: String){
        val apiKey = BuildConfig.API_KEY
        val call = RetrofitInstance.api.getNews(
            accessKey = apiKey,
            categories = categoryPref,
            countries = countryPref,
            languages = languagePref,
        )

        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val newsList = response.body()
                    val total = response.body()?.pagination?.total
                    if (newsList == null || newsList.data.isEmpty() ) {
                        Log.w("errorcon", "Received empty data list from API.")
                        Toast.makeText(this@ConnectedToDatabaseActivity, "No data received from server.", Toast.LENGTH_SHORT).show()
                    }else{
                        if(total == 0){
                            val constraintLayout = binding.linearLayout
                            val textView = TextView(this@ConnectedToDatabaseActivity).apply {
                                id = View.generateViewId()
                                text = "Not New News"
                                textSize = 18f
                                gravity = Gravity.CENTER
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    gravity = Gravity.CENTER
                                }
                            }
                            constraintLayout.addView(textView)
                        }
                        else {
                            val jsonString = Gson().toJson(newsList)
                            val newsResponse = Gson().fromJson(jsonString, NewsResponse::class.java)

                            Log.d("jsonfile", jsonString)
                            createElements(newsResponse)
                            Log.d("errorcon", "Data successfully fetched and processed.")
                        }
                    }
                } else {
                    val statusCode = response.code()
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = response.message()

                    Log.e("errorcon", "Error fetching data. Status code: $statusCode, Message: $errorMessage")

                    when (statusCode) {
                        400 -> Log.e("errorcon", "Bad Request - Server could not understand the request.")
                        401 -> Log.e("errorcon", "Unauthorized - Check API key or permissions.")
                        403 -> Log.e("errorcon", "Forbidden - Access denied.")
                        404 -> Log.e("errorcon", "Not Found - The requested resource is unavailable.")
                        500 -> Log.e("errorcon", "Internal Server Error - Server encountered an error.")
                        else -> Log.e("errorcon", "Unexpected error. Error body: $errorBody")
                    }

                    Toast.makeText(this@ConnectedToDatabaseActivity, "Error: Unable to fetch data. Code: $statusCode", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                when (t) {
                    is java.net.SocketTimeoutException -> Log.e("errorcon", "Network timeout - Check your internet connection.")
                    is java.net.UnknownHostException -> Log.e("errorcon", "No internet connection or server is unreachable.")
                    else -> Log.e("errorcon", "Network failure: ${t.message}")
                }

                Toast.makeText(this@ConnectedToDatabaseActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()


            }
        })

    }
    //
    private fun createElements(newsResponse: NewsResponse){
//        val jsonString = "{\"pagination\":{\"limit\":2,\"offset\":0,\"count\":2,\"total\":2},\"data\":[{\"author\":\"TMZ Staff\",\"title\":\"Rafael Nadal Pulls Out Of U.S. Open Over COVID-19 Concerns\",\"description\":\"Rafael Nadal is officially OUT of the U.S. Open ... the tennis legend said Tuesday it's just too damn unsafe for him to travel to America during the COVID-19 pandemic. \\\"The situation is very complicated worldwide,\\\" Nadal wrote in a statement. \\\"The…\",\"url\":\"https://www.tmz.com/2020/08/04/rafael-nadal-us-open-tennis-covid-19-concerns/\",\"source\":\"TMZ.com\",\"image\":\"https://imagez.tmz.com/image/fa/4by3/2020/08/04/fad55ee236fc4033ba324e941bb8c8b7_md.jpg\",\"category\":\"general\",\"language\":\"en\",\"country\":\"us\",\"published_at\":\"2020-08-05T05:47:24+00:00\"}, {\"author\":\"TMZ Staff\",\"title\":\"Rafael Nadal Pulls Out Of U.S. Open Over COVID-19 Concerns\",\"description\":\"Rafael Nadal is officially OUT of the U.S. Open ... the tennis legend said Tuesday it's just too damn unsafe for him to travel to America during the COVID-19 pandemic. \\\"The situation is very complicated worldwide,\\\" Nadal wrote in a statement. \\\"The…\",\"url\":\"https://www.tmz.com/2020/08/04/rafael-nadal-us-open-tennis-covid-19-concerns/\",\"source\":\"TMZ.com\",\"image\":\"https://imagez.tmz.com/image/fa/4by3/2020/08/04/fad55ee236fc4033ba324e941bb8c8b7_md.jpg\",\"category\":\"general\",\"language\":\"en\",\"country\":\"us\",\"published_at\":\"2020-08-05T05:47:24+00:00\"}]}"

        val size = newsResponse.data.size
        val linearLayout: LinearLayout = binding.newsList

        for (i in 0 until size) {

                val title = newsResponse.data[i].title
                val description = newsResponse.data[i].description
                val image = newsResponse.data[i].image
                val date = newsResponse.data[i].published_at
                val author =  newsResponse.data[i].author
                val url =  newsResponse.data[i].url
                val textBtn: String = "To source"

                val articleContainer = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(40, 40 ,40, 40)
                    background = ContextCompat.getDrawable(context, R.drawable.rounded_corners)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply{
                        setMargins(0, 20, 0, 150)
                    }
                }
                val dateAndAuthorContainer = LinearLayout(this).apply{
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(20, 20, 20, 20)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply{
                        setMargins(0, 0,  0 ,20)
                    }
                }

                val spacerView = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                val titleView = TextView(this).apply{
                    text = title
                    textSize = 24f
                    setTypeface(null, Typeface.BOLD)
                    setPadding(20, 20, 20, 20)
                    setTextColor(Color.parseColor("#C074A4"))
                }

                val imageView = ImageView(this).apply{
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        400
                        ).apply {
                        setMargins(20, 20, 20, 20)
                    }

                    scaleType = ImageView.ScaleType.FIT_CENTER


                        Glide.with(this@ConnectedToDatabaseActivity)
                            .load(image)
                            .placeholder(R.drawable.blankpage)
                            .error(R.drawable.blankpage)
                            .into(this)

                }
                val descriptionView = TextView(this).apply{
                    text = description
                    textSize = 16f
                    setPadding(20, 0, 20, 20)
                    setTextColor(Color.parseColor("#C074A4"))
                }

                val authorView = TextView(this).apply {
                    text = author
                    textSize = 12f
                    setPadding(0,0,0,0)
                    setTextColor(Color.parseColor("#666666"))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.START
                    }
                }
                val dateView = TextView(this).apply {
                    text = date
                    textSize = 12f
                    setPadding(0,0,0,0)
                    setTextColor(Color.parseColor("#666666"))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.END
                    }
                }

                val buttonView = Button(this).apply{
                    text = textBtn
                    textSize = 16f
                    setPadding(20, 20, 20, 20)
                    background = ContextCompat.getDrawable(context, R.drawable.rounded_corners_but_purple)

                    setTextColor(Color.parseColor("#FFFFFF"))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.END
                        setMargins(0, 20, 20, 20)
                    }
                    setOnClickListener {
                        if (url.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(url)
                            }

                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Link is invalid.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Not available.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dateAndAuthorContainer.addView(authorView)
                dateAndAuthorContainer.addView(spacerView)

                dateAndAuthorContainer.addView(dateView)

                articleContainer.addView(titleView)
                articleContainer.addView(dateAndAuthorContainer)
                articleContainer.addView(imageView)
                articleContainer.addView(descriptionView)
                articleContainer.addView(buttonView)

                linearLayout.addView(articleContainer)


        }
    }
}