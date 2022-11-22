package com.example.kmmtest.android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import http.ImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ImageListActivity : AppCompatActivity() {

    private val viewModel by lazy { MainViewModel }

    private val loadingView by lazy { findViewById<View>(R.id.loadingView) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private val button by lazy { findViewById<View>(R.id.button) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_list)

        //init view
        button.setOnClickListener {
            viewModel.requestImages()
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val adapter = ImageAdapter {
            ImageActivity.startNewInstance(this, it)
        }
        recyclerView.adapter = adapter

        //init data
        viewModel.urls.observe(this) {
            when (it) {
                is Result.Error -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    loadingView.visibility = View.GONE
                }
                is Result.Loading -> {
                    loadingView.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    adapter.submitList(it.result) {
                        recyclerView.smoothScrollToPosition(0)
                    }
                    loadingView.visibility = View.GONE
                }
            }
        }

        viewModel.requestImages()

    }
}


object MainViewModel {
    //kmm shared repository
    private val repository = ImageRepository()

    private val _urls = MutableLiveData<Result<List<String>>>(Result.Loading())
    val urls: LiveData<Result<List<String>>> = _urls

    fun requestImages() {
        _urls.value = Result.Loading()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val images = repository.getImageUrls()
                _urls.value = Result.Success(images)
            } catch (e: Exception) {
                _urls.value = Result.Error(e.message ?: "Unknown error")
            }

        }
    }
}

sealed class Result<T> {
    class Loading<T> : Result<T>()
    class Success<T>(val result: T) : Result<T>()
    class Error<T>(val error: String) : Result<T>()
}


class ImageAdapter(private val itemClick: (url: String) -> Unit) :
    ListAdapter<String, ImageAdapter.ImageHolder>(ImageDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false)
        val holder = ImageHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.position
            if ((0 until itemCount).contains(position).not()) return@setOnClickListener
            itemClick(getItem(position))
        }
        return holder
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageHolder(view: View) : ViewHolder(view) {

        private val imageView by lazy { itemView.findViewById<ImageView>(R.id.imageView) }

        fun bind(url: String) {
            imageView.load(url) {
                placeholder(R.drawable.placeholder)
                crossfade(true)
            }
        }
    }

    object ImageDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

}