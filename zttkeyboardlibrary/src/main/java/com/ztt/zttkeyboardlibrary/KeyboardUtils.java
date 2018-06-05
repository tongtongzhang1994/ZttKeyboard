package com.ztt.zttkeyboardlibrary;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ztt : 2018-02-23
 */
public class KeyboardUtils {

	private Activity activity;
	private KeyboardView keyboardView;
	private Keyboard keyboardNumber;
	private Keyboard keyboardAcb;
	private Keyboard keyboardSymbol;
	private boolean isRandom = false;
	private EditText editText;

	public OnOkClick mOkClick;
	public OnCancelClick mCancelClick;

	public void setmOkClick(OnOkClick mOkClick) {
		this.mOkClick = mOkClick;
	}

	public void setmCancelClick(OnCancelClick mCancelClick) {
		this.mCancelClick = mCancelClick;
	}

	public EditText getEditText() {
		return editText;
	}

	public KeyboardUtils(Activity context, KeyboardView keyboardView, boolean isRandom) {
		this.activity = context;
		this.keyboardView = keyboardView;
		keyboardView.setPreviewEnabled(false);
		keyboardView.setOnKeyboardActionListener(onKeyboardActionListener);

		keyboardNumber = new Keyboard(activity, R.xml.keyboard_number);
		keyboardAcb = new Keyboard(activity, R.xml.keyboard_abc);
		keyboardSymbol = new Keyboard(activity, R.xml.keyboard_symbol);
		this.isRandom = isRandom;
		if (isRandom) {
			randomKeyboardNumber();
		}
	}


	//点击事件触发
	private void attachTo(EditText editText) {
		hideSystemSofeKeyboard(activity, editText);
		this.editText = editText;
		showSoftKeyboard();
	}

	/**
	 * 隐藏系统键盘
	 *
	 * @param editText
	 */
	public static void hideSystemSofeKeyboard(Context context, EditText editText) {
		int sdkInt = Build.VERSION.SDK_INT;
		if (sdkInt >= 11) {
			try {
				Class<EditText> cls = EditText.class;
				Method setShowSoftInputOnFocus;
				setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(editText, false);

			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			editText.setInputType(InputType.TYPE_NULL);
		}
		// 如果软键盘已经显示，则隐藏
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}


	private void hideKeyBoard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == KeyboardView.VISIBLE) {
			keyboardView.setVisibility(KeyboardView.GONE);
		}
	}

	private void showSoftKeyboard() {
		if (keyboardView == null) {
			return;
		}
		keyboardView.setKeyboard(keyboardNumber);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(false);
		keyboardView.setVisibility(View.VISIBLE);
		keyboardView.setOnKeyboardActionListener(onKeyboardActionListener);
	}

	private void randomKeyboardNumber() {
		List<Keyboard.Key> keyList = keyboardNumber.getKeys();
		// 查找出0-9的数字键
		List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
		for (int i = 0; i < keyList.size(); i++) {
			if (keyList.get(i).label != null
					&& isNumber(keyList.get(i).label.toString())) {
				newkeyList.add(keyList.get(i));
			}
		}
		// 数组长度
		int count = newkeyList.size();
		// 结果集
		List<KeyModel> resultList = new ArrayList<KeyModel>();
		// 用一个LinkedList作为中介
		LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
		// 初始化temp
		for (int i = 0; i < count; i++) {
			temp.add(new KeyModel(48 + i, i + ""));
		}
		// 取数
//		Random rand = new Random();
		// 更换为安全的随机算法
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < count; i++) {
//			int num = rand.nextInt(count - i);
			int num = random.nextInt(count - i);
			resultList.add(new KeyModel(temp.get(num).getCode(),
					temp.get(num).getLable()));
			temp.remove(num);
		}
		for (int i = 0; i < newkeyList.size(); i++) {
			newkeyList.get(i).label = resultList.get(i).getLable();
			newkeyList.get(i).codes[0] = resultList.get(i)
					.getCode();
		}
		//   hideKeyBoard();
		keyboardView.setKeyboard(keyboardNumber);
	}

	/**
	 * 是不是数字
	 *
	 * @param str 字符串
	 * @return 是否是数字
	 */
	private boolean isNumber(String str) {
		String wordstr = "0123456789";
		return wordstr.contains(str);
	}

	/**
	 * 软键盘点击监听
	 */
	KeyboardView.OnKeyboardActionListener onKeyboardActionListener = new KeyboardView
			.OnKeyboardActionListener() {
		@Override
		public void onPress(int primaryCode) {

		}

		@Override
		public void onRelease(int primaryCode) {

		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
//			vibrate(activity, 100L);
			if (editText == null) {
				return;
			}
			Editable editable = editText.getText();
			int start = editText.getSelectionStart();
			//key  codes 为-5
			if (primaryCode == Keyboard.KEYCODE_DELETE) {
				// 功能按键 删除
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
				// 取消
				hideKeyBoard();
				if (mCancelClick != null) {
					mCancelClick.onCancelClick();
				}
			} else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
				// 功能按键 数字键盘切换
				if (keyboardView.getKeyboard() == keyboardNumber) {
					keyboardView.setKeyboard(keyboardAcb);
				} else {
					keyboardView.setKeyboard(keyboardNumber);
				}
			} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
				// 功能按键 大小写切换
				changeKey();
				keyboardView.setKeyboard(keyboardAcb);
			} else if (primaryCode == Keyboard.KEYCODE_ALT) {
				// 功能按键 alt
				// 符号切换
				if (keyboardView.getKeyboard() == keyboardSymbol) {
					keyboardView.setKeyboard(keyboardAcb);
				} else {
					keyboardView.setKeyboard(keyboardSymbol);
				}
			} else if (primaryCode == Keyboard.KEYCODE_DONE) {
				// 功能按键 数字键盘切换
				if (keyboardView.getKeyboard() == keyboardNumber) {
					keyboardView.setKeyboard(keyboardAcb);
				} else {
					keyboardView.setKeyboard(keyboardNumber);
				}
			} else {
				editable.insert(start, Character.toString((char) primaryCode));
			}

		}

		@Override
		public void onText(CharSequence text) {

		}

		@Override
		public void swipeLeft() {

		}

		@Override
		public void swipeRight() {

		}

		@Override
		public void swipeDown() {

		}

		@Override
		public void swipeUp() {

		}
	};


	private boolean isupper;

	/**
	 * 键盘大小写切换
	 */
	private void changeKey() {
		List<Keyboard.Key> keylist = keyboardAcb.getKeys();
		if (isupper) {//大写切换小写
			isupper = false;
			for (Keyboard.Key key : keylist) {
				if (key.label != null && isword(key.label.toString())) {
					key.label = key.label.toString().toLowerCase();
					key.codes[0] = key.codes[0] + 32;
				}
			}
		} else {//小写切换大写
			isupper = true;
			for (Keyboard.Key key : keylist) {
				if (key.label != null && isword(key.label.toString())) {
					key.label = key.label.toString().toUpperCase();
					key.codes[0] = key.codes[0] - 32;
				}
			}
		}
	}

	private boolean isword(String str) {
		return "abcdefghijklmnopqrstuvwxyz".contains(str.toLowerCase());
	}

	public void bindEditTextEvent(final EditText editText) {

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					attachTo(editText);
				}
			}
		});

		editText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				attachTo(editText);
			}
		});
	}
}
