package kr.dev.rei.counter4eun;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {
	Button buttonUp;
	Button buttonDown;
	Button buttonReset;
	
	EditText textCount;

	SharedPreferences sharedPref;
	SharedPreferences.Editor sharedPrefEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textCount = (EditText) findViewById(R.id.textCount);
		buttonUp = (Button) findViewById(R.id.buttonUp);
		buttonDown = (Button) findViewById(R.id.buttonDown);
		buttonReset = (Button) findViewById(R.id.buttonReset);
		
		buttonUp.setOnClickListener(this);
		buttonDown.setOnClickListener(this);
		buttonReset.setOnClickListener(this);
		
		sharedPref = getSharedPreferences("CounterForEun", Activity.MODE_PRIVATE);
		sharedPrefEditor = sharedPref.edit();
		int countingValue = sharedPref.getInt("CountingValue", 0);
		textCount.setText(String.valueOf(countingValue));
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
		else if (v.equals(buttonReset))
		{
			textCount.setText("0");
			sharedPrefEditor.putInt("CountingValue", 0);
			sharedPrefEditor.commit();
		}
	}
	
}
