package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private val bookList = mutableListOf<Book>()
    private lateinit var adapter: BookAdapter
    private val gson = Gson()

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val isDeleted = data.getBooleanExtra("IS_DELETED", false)
            val bookId = data.getLongExtra("BOOK_ID_TO_UPDATE", -1L)

            if (isDeleted) {
                bookList.removeAll { it.id == bookId }
            } else {
                val book = data.getParcelableExtra<Book>("BOOK_DATA") ?: return@registerForActivityResult
                if (data.getBooleanExtra("IS_NEW_BOOK", false)) {
                    val newId = (bookList.maxOfOrNull { it.id } ?: 0L) + 1
                    bookList.add(book.copy(id = newId))
                } else {
                    val index = bookList.indexOfFirst { it.id == bookId }
                    if (index != -1) bookList[index] = book
                }
            }
            saveData()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()
        adapter = BookAdapter(this, bookList)
        findViewById<ListView>(R.id.listViewBooks).apply {
            adapter = this@MainActivity.adapter
            setOnItemClickListener { _, _, i, _ ->
                val intent = Intent(this@MainActivity, AddEditBookActivity::class.java)
                intent.putExtra("BOOK_DATA", bookList[i])
                launcher.launch(intent)
            }
        }

        findViewById<FloatingActionButton>(R.id.fabAddBook).setOnClickListener {
            launcher.launch(Intent(this, AddEditBookActivity::class.java))
        }
    }

    private fun saveData() {
        val json = gson.toJson(bookList)
        getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString("books", json).apply()
    }

    private fun loadData() {
        val json = getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("books", null)
        if (json != null) {
            val type = object : TypeToken<List<Book>>() {}.type
            bookList.addAll(gson.fromJson(json, type))
        }
    }
}
