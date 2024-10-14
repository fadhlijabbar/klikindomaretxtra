package com.fadhlijabbar.klikindomaretxtra

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import android.view.View
import androidx.core.view.WindowInsetsCompat
import com.fadhlijabbar.klikindomaretxtra.databinding.ActivitySearchPageBinding

class SearchPage : AppCompatActivity() {
    private lateinit var binding: ActivitySearchPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up search bar
        setupSearchBar()

        // Set up insets listener for padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets -> // Use binding.root instead of findViewById
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSearchBar() {
        val editTextSearch = binding.customSearchBar.editTextSearch

        // Hide "x" icon initially
        editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this, R.drawable.ic_search),
            null,
            null,
            null
        )

        // Add TextWatcher to monitor text changes
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    // Show "x" icon if there's text
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this@SearchPage, R.drawable.ic_search),
                        null,
                        ContextCompat.getDrawable(this@SearchPage, R.drawable.ic_close),
                        null
                    )
                } else {
                    // Hide "x" icon if no text
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(this@SearchPage, R.drawable.ic_search),
                        null,
                        null,
                        null
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Add OnTouchListener for detecting clicks on the "x" icon
        editTextSearch.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event != null && event.action == MotionEvent.ACTION_UP) {
                val drawableEnd: Drawable? = editTextSearch.compoundDrawables[2]
                if (drawableEnd != null) {
                    val bounds = drawableEnd.bounds
                    val x = event.rawX.toInt()
                    val width = editTextSearch.width
                    val paddingEnd = editTextSearch.paddingEnd
                    val drawableWidth = bounds.width()

                    // Check if click is on the "x" icon
                    if (x >= (width - paddingEnd - drawableWidth)) {
                        editTextSearch.text.clear() // Clear text in EditText
                        editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.ic_search),
                            null,
                            null,
                            null
                        ) // Hide "x" icon
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }
}
