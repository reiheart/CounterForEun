package kr.dev.rei.counter4eun;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener, OnFocusChangeListener {
	Button buttonUp;
	Button buttonDown;
	Button buttonResetCount;
	Button buttonResetMemo;
	
	EditText textCount;
	EditText textDescription;

	SharedPreferences sharedPref;
	SharedPreferences.Editor sharedPrefEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textCount = (EditText) findViewById(R.id.textCount);
		textDescription = (EditText) findViewById(R.id.textDescription);
		buttonUp = (Button) findViewById(R.id.buttonUp);
		buttonDown = (Button) findViewById(R.id.buttonDown);
		buttonResetCount = (Button) findViewById(R.id.buttonResetCount);
		buttonResetMemo = (Button) findViewById(R.id.buttonResetMemo);
		
		textDescription.setOnFocusChangeListener(this);
		buttonUp.setOnClickListener(this);
		buttonDown.setOnClickListener(this);
		buttonResetCount.setOnClickListener(this);
		buttonResetMemo.setOnClickListener(this);
		
		sharedPref = getSharedPreferences("CounterForEun", Activity.MODE_PRIVATE);
		sharedPrefEditor = sharedPref.edit();
		int countingValue = sharedPref.getInt("CountingValue", 0);
		textCount.setText(String.valueOf(countingValue));
		String descStr = sharedPref.getString("Description", "");
		textDescription.setText(descStr);
	}

	@Override
	protected void onPause() {
		String descStr = textDescription.getText().toString();
		sharedPrefEditor.putString("Description", descStr);
		sharedPrefEditor.commit();
		
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		if (v.equals(buttonUp))
		{
			int countingValue = sharedPref.getInt("CountingValue", 0);
			textCount.setText(String.valueOf(++countingValue));
			sharedPrefEditor.putInt("CountingValue", countingValue);
			sharedPrefEditor.commit();
		}
		else if (v.equals(buttonDown))
		{
			int countingValue = sharedPref.getInt("CountingValue", 0);
			textCount.setText(String.valueOf(--countingValue));
			sharedPrefEditor.putInt("CountingValue", countingValue);
			sharedPrefEditor.commit();
		}
		else if (v.equals(buttonResetCount))
		{
			textCount.setText("0");
			sharedPrefEditor.putInt("CountingValue", 0);
			sharedPrefEditor.commit();
		}
		else if (v.equals(buttonResetMemo))
		{
			textDescription.setText("");
			sharedPrefEditor.putString("Description", "");
			sharedPrefEditor.commit();	
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v.equals(textDescription))
		{
			String descStr = textDescription.getText().toString();
			sharedPrefEditor.putString("Description", descStr);
			sharedPrefEditor.commit();
		}
	}
	
}
