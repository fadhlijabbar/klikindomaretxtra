package com.fadhlijabbar.klikindomaretxtra

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
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

        // Menangani tampilan product_quantity dan add_button
        handleProductQuantity()
    }

    private fun handleProductQuantity() {
        // Inisialisasi views untuk kuantitas dan tombol tambah
        val productQuantity = binding.productQuantity
        val addButton = binding.addButton

        // Menyembunyikan EditText product_quantity secara default
        productQuantity.visibility = View.GONE

        // Tampilkan product_quantity dan sembunyikan add_button saat add_button diklik
        addButton.setOnClickListener {
            productQuantity.visibility = View.VISIBLE
            addButton.visibility = View.GONE

            // Set focus dan kursor menjadi tidak terlihat di awal
            productQuantity.isFocusable = false
            productQuantity.isFocusableInTouchMode = false
            productQuantity.isCursorVisible = false
        }

        // Set touch listener untuk menangani klik pada angka dan ikon
        productQuantity.setOnTouchListener { _, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                val drawableStart: Drawable? = productQuantity.compoundDrawables[DRAWABLE_LEFT]
                val drawableEnd: Drawable? = productQuantity.compoundDrawables[DRAWABLE_RIGHT]

                val drawableStartWidth = drawableStart?.bounds?.width() ?: 0
                val drawableEndWidth = drawableEnd?.bounds?.width() ?: 0

                val paddingStart = productQuantity.paddingStart
                val paddingEnd = productQuantity.paddingEnd

                val width = productQuantity.width

                // Deteksi klik pada drawable start (ic_remove)
                if (drawableStart != null && event.x <= (drawableStartWidth + paddingStart)) {
                    Log.d("ProductQuantity", "Clicked on ic_remove")
                    var quantity = productQuantity.text.toString().toIntOrNull() ?: 1
                    if (quantity > 1) {
                        quantity--
                        productQuantity.setText(quantity.toString())
                        animateQuantityChange(productQuantity) // Panggil animasi untuk EditText
                    } else {
                        // Tampilkan dialog konfirmasi jika quantity adalah 1
                        showConfirmationDialog { confirmed ->
                            if (confirmed as Boolean) {
                                productQuantity.visibility = View.GONE
                                addButton.visibility = View.VISIBLE
                            }
                        }
                    }

                    // Sembunyikan keyboard dan kursor setelah mengklik ic_remove
                    hideKeyboardAndCursor(productQuantity)

                    // Animasi pada drawableStart
                    animateDrawable(drawableStart)
                    return@setOnTouchListener true
                }

                // Deteksi klik pada drawable end (ic_plus)
                if (drawableEnd != null && event.x >= (width - drawableEndWidth - paddingEnd)) {
                    Log.d("ProductQuantity", "Clicked on ic_plus")
                    var quantity = productQuantity.text.toString().toIntOrNull() ?: 1
                    quantity++
                    productQuantity.setText(quantity.toString())
                    animateQuantityChange(productQuantity) // Panggil animasi untuk EditText

                    // Sembunyikan keyboard dan kursor setelah mengklik ic_plus
                    hideKeyboardAndCursor(productQuantity)

                    // Animasi pada drawableEnd
                    animateDrawable(drawableEnd)
                    return@setOnTouchListener true
                }

                // Jika klik terjadi di area angka, tampilkan kursor dan keyboard
                val totalWidth = width - drawableStartWidth - drawableEndWidth - paddingStart - paddingEnd
                if (event.x > drawableStartWidth + paddingStart && event.x < totalWidth + drawableStartWidth + paddingStart) {
                    // Tampilkan kursor dan keyboard
                    productQuantity.isCursorVisible = true
                    productQuantity.isFocusable = true
                    productQuantity.isFocusableInTouchMode = true
                    productQuantity.requestFocus()

                    // Tampilkan keyboard
                    val inputMethodManager = getSystemService(InputMethodManager::class.java)
                    inputMethodManager.showSoftInput(productQuantity, InputMethodManager.SHOW_IMPLICIT)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Mengatur TextWatcher untuk mencegah memasukkan nilai 0
        productQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val quantity = s.toString().toIntOrNull() ?: 1
                if (quantity < 1) {
                    productQuantity.setText("1")
                    productQuantity.setSelection(productQuantity.text.length) // Memastikan kursor berada di akhir
                }
            }
        })

        // Menangani perubahan fokus untuk menonaktifkan pengeditan saat kehilangan fokus
        productQuantity.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideKeyboardAndCursor(productQuantity)
            }
        }
    }


    // Fungsi untuk menyembunyikan keyboard dan cursor
    private fun hideKeyboardAndCursor(editText: EditText) {
        editText.isCursorVisible = false
        editText.isFocusable = false
        editText.isFocusableInTouchMode = false

        // Sembunyikan keyboard
        val inputMethodManager = getSystemService(InputMethodManager::class.java)
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }


    private fun showConfirmationDialog(onConfirm: (Boolean) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah yakin untuk menghapus produk?")
            .setPositiveButton("Ya") { _, _ -> onConfirm(true) }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
                onConfirm(false)
            }
            .create()
            .show()
    }



    private fun animateDrawable(drawable: Drawable) {
        val animator = ValueAnimator.ofFloat(0.5f, 1f)
        animator.duration = 100
        animator.addUpdateListener { animation ->
            val alpha = (animation.animatedValue as Float * 255).toInt()
            drawable.alpha = alpha // Mengatur alpha dari drawable
            drawable.invalidateSelf() // Meminta redraw dari drawable
        }
        animator.start()
    }

    // Fungsi untuk mengatur animasi pada angka di EditText
    private fun animateQuantityChange(editText: EditText) {
        val scaleX = ObjectAnimator.ofFloat(editText, "scaleX", 1.2f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(editText, "scaleY", 1.2f, 1.0f)
        scaleX.duration = 150
        scaleY.duration = 150

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleX).with(scaleY)
        animatorSet.start()
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
