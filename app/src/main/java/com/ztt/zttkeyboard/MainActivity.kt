package com.ztt.zttkeyboard

import android.inputmethodservice.KeyboardView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.ztt.zttkeyboardlibrary.KeyboardUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val ed01 = findViewById<EditText>(R.id.ed01);
        val ed02 = findViewById<EditText>(R.id.ed02);
        val keyboardView = findViewById<KeyboardView>(R.id.keyboard_keyboard)
        val keyboardUtils = KeyboardUtils(this, keyboardView, true)
        keyboardUtils.bindEditTextEvent(ed01)
        keyboardUtils.bindEditTextEvent(ed02)
    }
}
