package kr.dev.rei.counter4eun;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnFocusChangeListener {
	private Button buttonUp;
	private Button buttonDown;
	private Button buttonResetCount;
	private Button buttonResetMemo;
	
	private EditText textCount;
	private EditText textDescription;
	private TextView textDateTime;

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor sharedPrefEditor;

	private DecimalFormat df00 = new DecimalFormat("00");
	private DecimalFormat df0000 = new DecimalFormat("0000");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textCount = (EditText) findViewById(R.id.textCount);
		textDescription = (EditText) findViewById(R.id.textDescription);
		textDateTime = (TextView) findViewById(R.id.textDateTime);
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

		int year = sharedPref.getInt("CountingYear", 2013);
		int month = sharedPref.getInt("CountingMonth", 1);
		int date = sharedPref.getInt("CountingDate", 1);
		int hour = sharedPref.getInt("CountingHour", 0);
		int minute = sharedPref.getInt("CountingMinute", 0);
		int second = sharedPref.getInt("CountingSecond", 0);
		StringBuffer sb = new StringBuffer(df0000.format(year));
		sb.append(".");
		sb.append(df00.format(month));
		sb.append(".");
		sb.append(df00.format(date));
		sb.append(" ");
		sb.append(df00.format(hour));
		sb.append(":");
		sb.append(df00.format(minute));
		sb.append(":");
		sb.append(df00.format(second));
		textDateTime.setText(sb.toString());
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

			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) + 1;
			int date = now.get(Calendar.DATE);
			int hour = now.get(Calendar.HOUR_OF_DAY);
			int minute = now.get(Calendar.MINUTE);
			int second = now.get(Calendar.SECOND);
			StringBuffer sb = new StringBuffer(df0000.format(year));
			sb.append(".");
			sb.append(df00.format(month));
			sb.append(".");
			sb.append(df00.format(date));
			sb.append(" ");
			sb.append(df00.format(hour));
			sb.append(":");
			sb.append(df00.format(minute));
			sb.append(":");
			sb.append(df00.format(second));
			textDateTime.setText(sb.toString());
			
			sharedPrefEditor.putInt("CountingValue", countingValue);
			
			sharedPrefEditor.putInt("CountingYear", year);
			sharedPrefEditor.putInt("CountingMonth", month);
			sharedPrefEditor.putInt("CountingDate", date);
			sharedPrefEditor.putInt("CountingHour", hour);
			sharedPrefEditor.putInt("CountingMinute", minute);
			sharedPrefEditor.putInt("CountingSecond", second);
			
			sharedPrefEditor.commit();
		}
		else if (v.equals(buttonDown))
		{
			int countingValue = sharedPref.getInt("CountingValue", 0);
			textCount.setText(String.valueOf(--countingValue));

			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) + 1;
			int date = now.get(Calendar.DATE);
			int hour = now.get(Calendar.HOUR_OF_DAY);
			int minute = now.get(Calendar.MINUTE);
			int second = now.get(Calendar.SECOND);
			StringBuffer sb = new StringBuffer(df0000.format(year));
			sb.append(".");
			sb.append(df00.format(month));
			sb.append(".");
			sb.append(df00.format(date));
			sb.append(" ");
			sb.append(df00.format(hour));
			sb.append(":");
			sb.append(df00.format(minute));
			sb.append(":");
			sb.append(df00.format(second));
			textDateTime.setText(sb.toString());
			
			sharedPrefEditor.putInt("CountingValue", countingValue);

			sharedPrefEditor.putInt("CountingYear", year);
			sharedPrefEditor.putInt("CountingMonth", month);
			sharedPrefEditor.putInt("CountingDate", date);
			sharedPrefEditor.putInt("CountingHour", hour);
			sharedPrefEditor.putInt("CountingMinute", minute);
			sharedPrefEditor.putInt("CountingSecond", second);
			
			sharedPrefEditor.commit();
		}
		else if (v.equals(buttonResetCount))
		{
			textCount.setText("0");

			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) + 1;
			int date = now.get(Calendar.DATE);
			int hour = now.get(Calendar.HOUR_OF_DAY);
			int minute = now.get(Calendar.MINUTE);
			int second = now.get(Calendar.SECOND);
			StringBuffer sb = new StringBuffer(df0000.format(year));
			sb.append(".");
			sb.append(df00.format(month));
			sb.append(".");
			sb.append(df00.format(date));
			sb.append(" ");
			sb.append(df00.format(hour));
			sb.append(":");
			sb.append(df00.format(minute));
			sb.append(":");
			sb.append(df00.format(second));
			textDateTime.setText(sb.toString());
			
			sharedPrefEditor.putInt("CountingValue", 0);

			sharedPrefEditor.putInt("CountingYear", year);
			sharedPrefEditor.putInt("CountingMonth", month);
			sharedPrefEditor.putInt("CountingDate", date);
			sharedPrefEditor.putInt("CountingHour", hour);
			sharedPrefEditor.putInt("CountingMinute", minute);
			sharedPrefEditor.putInt("CountingSecond", second);
			
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
