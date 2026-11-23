package com.haskenlink

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.view.ViewGroup

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 40, 32, 32)
        layout.gravity = Gravity.CENTER
        layout.setBackgroundColor(Color.rgb(10, 25, 40))

        val title = TextView(this)
        title.text = "HASKEN LINK"
        title.textSize = 30f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "Sadarwa ba tare da Internet ba"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.LTGRAY)
        subtitle.gravity = Gravity.CENTER

        val messageInput = EditText(this)
        messageInput.hint = "Rubuta saƙonka..."
        messageInput.setTextColor(Color.WHITE)
        messageInput.setHintTextColor(Color.GRAY)

        val sendButton = Button(this)
        sendButton.text = "Aika Saƙo"

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "An shirya saƙon: $message",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Ka rubuta saƙo tukuna",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val bluetoothButton = Button(this)
        bluetoothButton.text = "Bluetooth"

        bluetoothButton.setOnClickListener {
            Toast.makeText(
                this,
                "Bluetooth: Za a haɗa tsarin Bluetooth a mataki na gaba",
                Toast.LENGTH_LONG
            ).show()
        }

        val info = TextView(this)
        info.text = "HASKEN LINK • Offline Network"
        info.setTextColor(Color.LTGRAY)
        info.gravity = Gravity.CENTER

        layout.addView(title)
        layout.addView(subtitle)

        layout.addView(
            messageInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        layout.addView(sendButton)
        layout.addView(bluetoothButton)
        layout.addView(info)

        setContentView(layout)
    }
}
