package com.study.keyboardlibrarytest;

import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.ztt.zttkeyboardlibrary.AnKeyboardUtils;

public class MainActivity extends AppCompatActivity {

	private EditText editText0;
	private EditText editText1;
	private EditText editText2;
	private AnKeyboardUtils keyboardUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editText0 = findViewById(R.id.main_ed0);
		editText1 = findViewById(R.id.main_ed1);
		editText2 = findViewById(R.id.main_ed2);

		final KeyboardView keyboardView = findViewById(R.id.keyboard_keyboard);
		keyboardUtils = new AnKeyboardUtils(this, keyboardView, true);
		keyboardUtils.bindEditTextEvent(editText0);
		keyboardUtils.bindEditTextEvent(editText1);
	}

	@Override
	public void onBackPressed() {
		if (keyboardUtils.isShow()) {
			keyboardUtils.hideKeyBoard();
		} else {
			super.onBackPressed();
		}
	}

	public void onClcikStart(View view) {
		startActivity(new Intent(this, MainActivity2.class));
	}
}
