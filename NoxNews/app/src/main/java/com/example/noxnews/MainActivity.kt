package com.example.noxnews

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.noxnews.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {


        //https://newsapi.org/v2/top-headlines?country=us&category=general&apiKey=e145d0ed4d974df59b294a451d4d4e96&pageSize=30
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadNews()

        binding.swipeRefresh.setOnRefreshListener { loadNews() }


    }
    private fun loadNews(){
        val retro = Retrofit
            .Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val c = retro.create(NewsCallable::class.java)
        c.getNews().enqueue(object : Callback<News>{
            override fun onResponse(
                call: Call<News?>,
                response: Response<News?>
            ) {
                val news = response.body()
                val articles = news?.articles!!

                articles.removeAll{
                    it.title == "[Removed]"
                }
//                Log.d("trace" , "Articles: $articles")
                showNews(articles)
                binding.progress.isVisible = false
                binding.swipeRefresh.isRefreshing = false
            }

            override fun onFailure(
                call: Call<News?>,
                t: Throwable
            ) {
                Log.d("trace" , "Error: ${t.message}")
                binding.progress.isVisible = false
            }
        })
    }
    private fun showNews(articles: ArrayList<Article>){
        val adapter = NewsAdapter(this , articles)
        binding.newsList.adapter = adapter

    }

}