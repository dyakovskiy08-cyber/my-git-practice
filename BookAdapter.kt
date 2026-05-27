package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.view.*
import android.widget.*

class BookAdapter(private val context: Context, private val books: List<Book>) : BaseAdapter() {
    override fun getCount(): Int = books.size
    override fun getItem(position: Int): Any = books[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_book, parent, false)
        val book = books[position]

        val title: TextView = view.findViewById(R.id.textBookTitle)
        val author: TextView = view.findViewById(R.id.textBookAuthor)
        val status: TextView = view.findViewById(R.id.textBookReadStatus)
        val image: ImageView = view.findViewById(R.id.imgSmallCover)

        title.text = book.title
        author.text = book.author
        status.text = if (book.isRead) "Прочитано" else "Не прочитано"
        status.setTextColor(if (book.isRead) 0xFF4CAF50.toInt() else 0xFFF44336.toInt())

        if (book.imageUri != null) {
            image.setImageURI(Uri.parse(book.imageUri))
        } else {
            image.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        return view
    }
}
