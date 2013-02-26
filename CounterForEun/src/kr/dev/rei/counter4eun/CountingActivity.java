package kr.dev.rei.counter4eun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CountingActivity extends Activity implements OnClickListener, OnFocusChangeListener {
	public static final String paramKeyIdNum = "KEY_ID_NUM";

	private RelativeLayout layoutMain;

	private EditText textName;

	private Button buttonUp;
	private Button buttonDown;
	private Button buttonResetCount;
	private Button buttonResetMemo;

	private EditText textCount;
	private EditText textDescription;
	private TextView textDateTime;

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor sharedPrefEditor;

	private CountingData data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_counting);

		textName = (EditText) findViewById(R.id.textName);
		layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);
		textCount = (EditText) findViewById(R.id.textCount);
		textDescription = (EditText) findViewById(R.id.textDescription);
		textDateTime = (TextView) findViewById(R.id.textDateTime);
		buttonUp = (Button) findViewById(R.id.buttonUp);
		buttonDown = (Button) findViewById(R.id.buttonDown);
		buttonResetCount = (Button) findViewById(R.id.buttonResetCount);
		buttonResetMemo = (Button) findViewById(R.id.buttonResetMemo);

		layoutMain.setOnClickListener(this);
		textName.setOnFocusChangeListener(this);
		textDescription.setOnFocusChangeListener(this);
		buttonUp.setOnClickListener(this);
		buttonDown.setOnClickListener(this);
		buttonResetCount.setOnClickListener(this);
		buttonResetMemo.setOnClickListener(this);

		sharedPref = getSharedPreferences("CounterForEun", Activity.MODE_PRIVATE);
		sharedPrefEditor = sharedPref.edit();

		initCountingData();

		textName.setText(data.getName());
		textCount.setText(String.valueOf(data.getCount()));
		textDescription.setText(data.getDescription());
		try {
			textDateTime.setText(UsefulTools.GetInstance().getDateTimeStringType02ToType01(data.getCountTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	private void initCountingData() {
		int idNum = getIntent().getIntExtra(CountingActivity.paramKeyIdNum, 1);

		data = new CountingData();
		data.setIdNum(idNum);

		data = selectCountingData(data);
	}

	@Override
	protected void onPause() {
		UsefulTools tools = UsefulTools.GetInstance();

		String name = textName.getText().toString();
		data.setName(name);

		String descStr = textDescription.getText().toString();
		data.setDescription(descStr);

		String nowString =  tools.getDateTimeStringType02(null);
		data.setDescriptionTime(nowString);

		updateCountingData();

		super.onPause();
	}

	@Override
	public void onClick(View v) {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(textDescription.getWindowToken(), 0);

		final UsefulTools tools = UsefulTools.GetInstance();

		if (v.equals(buttonUp))
		{
			int countingValue = data.getCount();
			textCount.setText(String.valueOf(++countingValue));

			String nowString =  tools.getDateTimeStringType02(null);
			try {
				textDateTime.setText(tools.getDateTimeStringType02ToType01(nowString));
			} catch (Exception e) {
				e.printStackTrace();
			}

			data.setCount(countingValue);
			data.setCountTime(nowString);

			updateCountingData();
		}
		else if (v.equals(buttonDown))
		{
			int countingValue = data.getCount();
			textCount.setText(String.valueOf(--countingValue));

			String nowString =  tools.getDateTimeStringType02(null);
			try {
				textDateTime.setText(tools.getDateTimeStringType02ToType01(nowString));
			} catch (Exception e) {
				e.printStackTrace();
			}

			data.setCount(countingValue);
			data.setCountTime(nowString);

			updateCountingData();
		}
		else if (v.equals(buttonResetCount))
		{
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle(getString(R.string.app_name));
			dialog.setMessage(getString(R.string.reset_count_answer));
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					textCount.setText("0");
					String nowString =  tools.getDateTimeStringType02(null);
					try {
						textDateTime.setText(tools.getDateTimeStringType02ToType01(nowString));
					} catch (Exception e) {
						e.printStackTrace();
					}

					data.setCount(0);
					data.setCountTime(nowString);

					updateCountingData();
				}
			});
			dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do nothing
				}
			});

			dialog.show();
		}
		else if (v.equals(buttonResetMemo))
		{
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle(getString(R.string.app_name));
			dialog.setMessage(getString(R.string.reset_memo_answer));
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm)
					, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					textDescription.setText("");
					data.setDescription("");

					String nowString =  tools.getDateTimeStringType02(null);
					data.setDescriptionTime(nowString);

					updateCountingData();
				}
			});
			dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do nothing
				}
			});

			dialog.show();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v.equals(textName))
		{
			String name = textName.getText().toString();
			data.setName(name);

			updateCountingData();
		}
		if (v.equals(textDescription))
		{
			UsefulTools tools = UsefulTools.GetInstance();

			String descStr = textDescription.getText().toString();
			data.setDescription(descStr);

			String nowString =  tools.getDateTimeStringType02(null);
			data.setDescriptionTime(nowString);

			updateCountingData();
		}
	}

	private CountingData selectCountingData(CountingData countingData) {
		UsefulTools tools = UsefulTools.GetInstance();
		UsefulTools.DatabaseAdater dbAdater = tools.getDatabaseAdater(this, tools.getDBVersion(sharedPref));
		SQLiteDatabase db = dbAdater.getReadableDatabase();

		String selectSQL = dbAdater.getSelectCounterDataSQL(countingData.getIdNum());
		Cursor dbCursor = db.rawQuery(selectSQL, null);
		if (dbCursor.moveToFirst())
		{
			countingData.setIdNum(dbCursor.getInt(dbCursor.getColumnIndex(dbAdater.columnIdNum)));
			countingData.setName(dbCursor.getString(dbCursor.getColumnIndex(dbAdater.columnName)));
			countingData.setDescription(dbCursor.getString(dbCursor.getColumnIndex(dbAdater.columnDescription)));
			countingData.setDescriptionTime(dbCursor.getString(dbCursor.getColumnIndex(dbAdater.columnDescriptionDatetime)));
			countingData.setCount(dbCursor.getInt(dbCursor.getColumnIndex(dbAdater.columnCountNum)));
			countingData.setCountTime(dbCursor.getString(dbCursor.getColumnIndex(dbAdater.columnCountDatetime)));
			countingData.setSortNum(dbCursor.getInt(dbCursor.getColumnIndex(dbAdater.columnSortNum)));
		}
		dbCursor.close();
		dbAdater.close();

		return countingData;
	}

	private void updateCountingData() {
		UsefulTools tools = UsefulTools.GetInstance();
		UsefulTools.DatabaseAdater dbAdater = tools.getDatabaseAdater(this, tools.getDBVersion(sharedPref));

		String updateSQL = dbAdater.getUpdateCounterDataSQL(data.getIdNum(),
				data.getName(),
				data.getDescription(),
				data.getDescriptionTime(),
				data.getCount(),
				data.getCountTime(),
				data.getSortNum());

		SQLiteDatabase db = dbAdater.getWritableDatabase();
		db.execSQL(updateSQL);
		db.close();
	}
}
