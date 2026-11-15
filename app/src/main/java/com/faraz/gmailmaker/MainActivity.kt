
package com.faraz.gmailmaker

import android.app.Activity
import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 30, 24, 24)
        }

        layout.addView(TextView(this).apply {
            text = "Gmail ساز"
            textSize = 28f
            gravity = Gravity.CENTER
        })

        firstName = addField(layout, "نام")
        lastName = addField(layout, "نام خانوادگی")
        username = addField(layout, "نام کاربری پیشنهادی")
        password = addField(layout, "رمز عبور پیشنهادی")

        addButton(layout, "ساخت نام کاربری و رمز") {
            val first = transliterate(firstName.text.toString())
            val last = transliterate(lastName.text.toString())

            val base = "$first.$last"
                .lowercase()
                .replace(Regex("[^a-z0-9.]"), "")
                .trim('.')
                .ifBlank { "user" }

            username.setText("$base${(100..9999).random()}")

            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#\$%&"
            password.setText(
                (1..16).map { chars.random() }.joinToString("")
            )
        }

        addButton(layout, "کپی آدرس Gmail") {
            if (username.text.isBlank()) {
                toast("اول نام کاربری بساز")
            } else {
                copy("${username.text}@gmail.com")
            }
        }

        addButton(layout, "کپی رمز عبور") {
            if (password.text.isBlank()) {
                toast("اول رمز بساز")
            } else {
                copy(password.text.toString())
            }
        }

        addButton(layout, "ثبت‌نام در گوگل") {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://accounts.google.com/signup")
                )
            )
        }

        layout.addView(TextView(this).apply {
            text = "این برنامه نام کاربری و رمز پیشنهادی تولید می‌کند. ثبت‌نام و تأیید امنیتی باید توسط خودتان انجام شود."
            textSize = 14f
            setPadding(5, 20, 5, 5)
        })

        setContentView(ScrollView(this).apply {
            addView(layout)
        })
    }

    private fun addField(
        layout: LinearLayout,
        hintText: String
    ): EditText {
        val field = EditText(this).apply {
            hint = hintText
            textSize = 16f
            singleLine = true
        }
        layout.addView(field)
        return field
    }

    private fun addButton(
        layout: LinearLayout,
        title: String,
        action: () -> Unit
    ) {
        layout.addView(Button(this).apply {
            text = title
            setOnClickListener { action() }
        })
    }

    private fun copy(value: String) {
        val clipboard =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText("Gmail Maker", value)
        )
        toast("کپی شد")
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun transliterate(input: String): String {
        val map = mapOf(
            'ا' to "a", 'آ' to "a", 'ب' to "b",
            'پ' to "p", 'ت' to "t", 'ج' to "j",
            'چ' to "ch", 'ح' to "h", 'خ' to "kh",
            'د' to "d", 'ر' to "r", 'ز' to "z",
            'ژ' to "zh", 'س' to "s", 'ش' to "sh",
            'ع' to "a", 'غ' to "gh", 'ف' to "f",
            'ق' to "gh", 'ک' to "k", 'گ' to "g",
            'ل' to "l", 'م' to "m", 'ن' to "n",
            'و' to "v", 'ه' to "h", 'ی' to "i",
            ' ' to "."
        )

        return input.trim().map {
            map[it] ?: if (it.isLetterOrDigit()) it.toString() else ""
        }.joinToString("")
    }
}
