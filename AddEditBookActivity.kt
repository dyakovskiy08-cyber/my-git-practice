package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class AddEditBookActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etAuthor: TextInputEditText
    private lateinit var cbRead: CheckBox
    private lateinit var imgCover: ImageView
    private lateinit var btnDelete: Button
    private lateinit var btnSave: Button

    private var selectedImageUri: String? = null
    private var currentBook: Book? = null

    // Выбор картинки
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Даем приложению права на чтение этого файла (важно для Android 10+)
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedImageUri = it.toString()
            imgCover.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_book)

        etTitle = findViewById(R.id.editTextBookTitle)
        etAuthor = findViewById(R.id.editTextBookAuthor)
        cbRead = findViewById(R.id.checkBoxRead)
        imgCover = findViewById(R.id.imageBookCover)
        btnDelete = findViewById(R.id.buttonDeleteBook)
        btnSave = findViewById(R.id.buttonSaveBook)

        currentBook = intent.getParcelableExtra("BOOK_DATA")

        // Заполнение данных при редактировании
        currentBook?.let { book ->
            etTitle.setText(book.title)
            etAuthor.setText(book.author)
            cbRead.isChecked = book.isRead
            selectedImageUri = book.imageUri
            book.imageUri?.let { imgCover.setImageURI(Uri.parse(it)) }
            btnDelete.visibility = View.VISIBLE
        }

        imgCover.setOnClickListener { pickImage.launch("image/*") }

        btnSave.setOnClickListener { sendResult(isDelete = false) }
        btnDelete.setOnClickListener { sendResult(isDelete = true) }
    }

    private fun sendResult(isDelete: Boolean) {
        val title = etTitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()

        if (!isDelete && (title.isEmpty() || author.isEmpty())) {
            Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent().apply {
            putExtra("IS_DELETED", isDelete)
            putExtra("BOOK_ID_TO_UPDATE", currentBook?.id ?: -1L)
            if (!isDelete) {
                val book = Book(
                    id = currentBook?.id ?: 0L,
                    title = title,
                    author = author,
                    isRead = cbRead.isChecked,
                    imageUri = selectedImageUri
                )
                putExtra("BOOK_DATA", book)
                putExtra("IS_NEW_BOOK", currentBook == null)
            }
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
