package kr.dev.rei.counter4eun;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable, AdapterView.OnItemClickListener, View.OnClickListener{
	private RelativeLayout layoutMain;
	private RelativeLayout layoutTitle;
	private LinearLayout layoutSubMain;
	private LinearLayout layoutButtons;
	private ListView listCounting;
	private Button buttonAdd;
	private Button buttonRemove;

	private ArrayAdapter<CountingData> listAdapterCounting;
	private ArrayList<CountingData> listCountingData;

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor sharedPrefEditor;

	private TitleHandler titleHandler;
	private boolean titleShown;

	private boolean removeState;
	private ArrayList<Integer> willRemoveCountingData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);
		layoutTitle = (RelativeLayout) findViewById(R.id.layoutTitle);
		layoutSubMain = (LinearLayout) findViewById(R.id.layoutSubMain);
		layoutButtons = (LinearLayout) findViewById(R.id.layoutButtons);
		listCounting = (ListView) findViewById(R.id.listCounting);
		buttonAdd = (Button) findViewById(R.id.buttonAdd);
		buttonRemove = (Button) findViewById(R.id.buttonRemove);

		sharedPref = getSharedPreferences("CounterForEun", Activity.MODE_PRIVATE);
		sharedPrefEditor = sharedPref.edit();

		versioning();

		removeState = false;

		initCountingList();

		buttonAdd.setOnClickListener(this);
		buttonRemove.setOnClickListener(this);

		titleShown = false;
		titleHandler = new TitleHandler();
		Thread finishThread = new Thread(this);
		finishThread.start();
	}

	@Override
	public void run() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		titleHandler.sendMessage(new Message());
		titleShown = true;
	}

	@SuppressLint("HandlerLeak")
	private class TitleHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			layoutSubMain.setVisibility(View.VISIBLE);
			layoutTitle.setVisibility(View.INVISIBLE);

			super.handleMessage(msg);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		this.getCountingData();

		if (titleShown)
		{
			layoutSubMain.setVisibility(View.VISIBLE);
			layoutTitle.setVisibility(View.INVISIBLE);
		}
		else
		{
			layoutSubMain.setVisibility(View.INVISIBLE);
			layoutTitle.setVisibility(View.VISIBLE);
		}
	}

	private void initCountingList() {
		TextView txtEmpty = new TextView(this);
		txtEmpty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		txtEmpty.setText(R.string.empty_counting_list);
		txtEmpty.setTextColor(getResources().getColor(R.color.text_grey));
		txtEmpty.setVisibility(View.VISIBLE);
		listCounting.setEmptyView(txtEmpty);

		listAdapterCounting = new ArrayAdapter<CountingData>(this, R.layout.listviewitem_counting){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View listItemView = convertView;
				if (listItemView == null)
				{
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					listItemView = inflater.inflate(R.layout.listviewitem_counting, null);
				}

				TextView txtName = (TextView) listItemView.findViewById(R.id.txtName);
				TextView txtDateTime = (TextView) listItemView.findViewById(R.id.txtDateTime);
				CheckBox checkBoxWillRemove = (CheckBox) listItemView.findViewById(R.id.checkBoxWillRemove);

				final CountingData data = listCountingData.get(position);

				if (data != null)
				{
					txtName.setText(data.getName());

					try {
						UsefulTools tools = UsefulTools.GetInstance();
						txtDateTime.setText(tools.getDateTimeStringType02ToType01(data.getCountTime()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (removeState)
				{
					checkBoxWillRemove.setVisibility(View.VISIBLE);
					checkBoxWillRemove.setChecked(false);
				}
				else
				{
					checkBoxWillRemove.setVisibility(View.GONE);
					checkBoxWillRemove.setChecked(false);
				}
				checkBoxWillRemove.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@SuppressLint("UseValueOf")
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked)
						{
							willRemoveCountingData.add(new Integer(data.getIdNum()));
						}
						else
						{
							willRemoveCountingData.remove(new Integer(data.getIdNum()));
						}
					}
				});

				return listItemView;
			}
		};
		listCounting.setAdapter(listAdapterCounting);

		listCounting.setOnItemClickListener(this);

		this.getCountingData();
	}

	private void getCountingData() {
		listCountingData = selectCountingDataAll(listCountingData);

		if (listAdapterCounting != null)
		{
			listAdapterCounting.clear();
			Iterator<CountingData> iterator = listCountingData.iterator();
			while (iterator.hasNext())
			{
				listAdapterCounting.add(iterator.next());
			}
		}
	}

	private void versioning() {
		UsefulTools tools = UsefulTools.GetInstance();
		float version = tools.getVersion(sharedPref);

		if (version == 0.0f)
		{
			DecimalFormat df00 = new DecimalFormat("00");
			DecimalFormat df0000 = new DecimalFormat("0000");

			int countingValue = sharedPref.getInt("CountingValue", 0);
			String descStr = sharedPref.getString("Description", "");
			int year = sharedPref.getInt("CountingYear", 2013);
			int month = sharedPref.getInt("CountingMonth", 1);
			int date = sharedPref.getInt("CountingDate", 1);
			int hour = sharedPref.getInt("CountingHour", 0);
			int minute = sharedPref.getInt("CountingMinute", 0);
			int second = sharedPref.getInt("CountingSecond", 0);
			StringBuffer countingTime = new StringBuffer(df0000.format(year));
			countingTime.append(df00.format(month));
			countingTime.append(df00.format(date));
			countingTime.append(df00.format(hour));
			countingTime.append(df00.format(minute));
			countingTime.append(df00.format(second));

			UsefulTools.DatabaseAdater dbAdater = tools.getDatabaseAdater(this, tools.getDBVersion(sharedPref));
			String insertSQL = dbAdater.getInsertCounterDataSQL("default",
					descStr,
					tools.getDateTimeStringType02(null),
					countingValue,
					countingTime.toString(),
					1);

			SQLiteDatabase db = dbAdater.getWritableDatabase();
			db.execSQL(insertSQL);
			dbAdater.close();

			if (!countingTime.toString().equals("20130101000000"))
			{
				sharedPrefEditor.remove("CountingValue");
				sharedPrefEditor.remove("Description");
				sharedPrefEditor.remove("CountingYear");
				sharedPrefEditor.remove("CountingMonth");
				sharedPrefEditor.remove("CountingDate");
				sharedPrefEditor.remove("CountingHour");
				sharedPrefEditor.remove("CountingMinute");
				sharedPrefEditor.remove("CountingSecond");
			}

			tools.setDBVersion(sharedPrefEditor, 1);
			tools.setVersion(sharedPrefEditor, 1.2f);
		}
		else if (version == 1.2f)
		{
			// add on next version
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CountingData data = listCountingData.get(position);

		moveToCountingActivity(data);
	}

	@Override
	public void onClick(View v) {
		if (removeState)
		{
			if (v.equals(buttonAdd))
			{
				// cancel
			}
			else if (v.equals(buttonRemove))
			{
				// remove data
				Iterator<Integer> itor = willRemoveCountingData.iterator();
				while (itor.hasNext()) {
					Integer idNum = itor.next();
					deleteCountingData(idNum);
				}
				willRemoveCountingData.clear();

				getCountingData();
			}

			buttonAdd.setText(R.string.text_add);
			buttonRemove.setText(R.string.text_delete);
			buttonAdd.setWidth(getResources().getDimensionPixelSize(R.dimen.buttonSizeNonRemoveState));
			buttonRemove.setWidth(getResources().getDimensionPixelSize(R.dimen.buttonSizeNonRemoveState));
			listAdapterCounting.notifyDataSetChanged();

			removeState = false;
		}
		else
		{
			if (v.equals(buttonAdd))
			{
				UsefulTools tools = UsefulTools.GetInstance();

				CountingData data = new CountingData();
				data.setIdNum(0);
				data.setCountTime(tools.getDateTimeStringType02(null));
				data = insertCountingData(data);
				moveToCountingActivity(data);
			}
			else if (v.equals(buttonRemove))
			{
				buttonAdd.setText(R.string.cancel);
				buttonRemove.setText(R.string.text_remove);
				buttonAdd.setWidth(getResources().getDimensionPixelSize(R.dimen.buttonSizeRemoveState));
				buttonRemove.setWidth(getResources().getDimensionPixelSize(R.dimen.buttonSizeRemoveState));
				listAdapterCounting.notifyDataSetChanged();

				if (willRemoveCountingData == null)
				{
					willRemoveCountingData = new ArrayList<Integer>();
				}
				willRemoveCountingData.clear();

				removeState = true;
			}
		}
	}

	private void moveToCountingActivity(CountingData data) {
		Intent countingIntent = new Intent(this, CountingActivity.class);
		countingIntent.putExtra(CountingActivity.paramKeyIdNum, data.getIdNum());
		startActivity(countingIntent);
	}

	private CountingData insertCountingData(CountingData data) {
		UsefulTools tools = UsefulTools.GetInstance();
		UsefulTools.DatabaseAdater dbAdater = tools.getDatabaseAdater(this, tools.getDBVersion(sharedPref));

		String insertSQL = dbAdater.getInsertCounterDataSQL(	data.getName(),
				data.getDescription(),
				data.getDescriptionTime(),
				data.getCount(),
				data.getCountTime(),
				data.getSortNum());

		SQLiteDatabase db = dbAdater.getWritableDatabase();
		db.execSQL(insertSQL);
		db.close();

		return selectCountingData(data);
	}

	private ArrayList<CountingData> selectCountingDataAll(ArrayList<CountingData> dataList) {
		if (dataList == null)
		{
			dataList = new ArrayList<CountingData>();
		}
		else
		{
			dataList.clear();
		}

		UsefulTools tools = UsefulTools.GetInstance();
		UsefulTools.DatabaseAdater dbAdater = tools.getDatabaseAdater(this, tools.getDBVersion(sharedPref));
		String selectSQL = dbAdater.getSelectCounterDataSQL();

		SQLiteDatabase db = dbAdater.getReadableDatabase();
		Cursor dbCursor = db.rawQuery(selectSQL, null);
		if (dbCursor.moveToFirst())
		{
			do {
				int idNum = dbCursor.getInt(dbCursor.getColumnIndex(dbAdater.columnIdNum));
				String name = dbCursor.getString(dbCursor.getColumnIndex(dbAdater.columnName));
				String description = dbCursor.getString(dbCursor.getColumnIndex(dbAdater.columnDescription));
				String descriptionDateTime = dbCursor.getString(dbCursor.getColumnIndex(dbAdater.columnDescriptionDatetime));
				int countNum = dbCursor.getInt(dbCursor.getColumnIndex(dbAdater.columnCountNum));
				String countDateTime = dbCursor.getString(dbCursor.getColumnIndex(dbAdater.columnCountDatetime));
				int sortNum = dbCursor.getInt(dbCursor.getColumnIndex(dbAdater.columnSortNum));

				dataList.add(new CountingData(idNum, name, description, descriptionDateTime, countNum, countDateTime, sortNum));
			}while (dbCursor.moveToNext());
		}
		dbCursor.close();
		dbAdater.close();

		return dataList;
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

	private void deleteCountingData(int idNum) {
		UsefulTools tools = UsefulTools.GetInstance();
		UsefulTools.DatabaseAdater dbAdater = tools.getDatabaseAdater(this, tools.getDBVersion(sharedPref));

		String deleteSQL = dbAdater.getDeleteCounterDataSQL(idNum);

		SQLiteDatabase db = dbAdater.getWritableDatabase();
		db.execSQL(deleteSQL);
		db.close();
	}
}
