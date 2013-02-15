package kr.dev.rei.counter4eun;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable, AdapterView.OnItemSelectedListener{
	private RelativeLayout layoutTitle;

	private ListView listCounting;
	private ArrayAdapter<CountingData> listAdapterCounting;
	private ArrayList<CountingData> listCountingData;

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor sharedPrefEditor;

	private TitleHandler titleHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		layoutTitle = (RelativeLayout) findViewById(R.id.layoutTitle);
		listCounting = (ListView) findViewById(R.id.listCounting);

		sharedPref = getSharedPreferences("CounterForEun", Activity.MODE_PRIVATE);
		this.sharedPrefEditor = sharedPref.edit();

		this.versioning();

		this.initCountingList();

		listCounting.setVisibility(View.INVISIBLE);
		layoutTitle.setVisibility(View.VISIBLE);

		titleHandler = new TitleHandler();
		Thread finishThread = new Thread(this);
		finishThread.start();
	}

	private void initCountingList() {
		TextView txtEmpty = new TextView(this);
		txtEmpty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		txtEmpty.setText(R.string.empty_counting_list);
		txtEmpty.setTextColor(getResources().getColor(R.color.text_grey));
		txtEmpty.setVisibility(View.VISIBLE);
		listCounting.setEmptyView(txtEmpty);

		this.getCountingData();

		listAdapterCounting = new ArrayAdapter<CountingData>(this, R.layout.listviewitem_counting, listCountingData){
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

				CountingData data = listCountingData.get(position);

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

				return listItemView;
			}
		};
		listCounting.setAdapter(listAdapterCounting);

		listCounting.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listCounting.setOnItemSelectedListener(this);
	}

	private void getCountingData() {
		if (listCountingData == null)
		{
			listCountingData = new ArrayList<MainActivity.CountingData>();
		}
		else
		{
			listCountingData.clear();
		}

		UsefulTools tools = UsefulTools.GetInstance();
		UsefulTools.DatabaseAdater dbAdater = tools.getDatabaseAdater(this, tools.getDBVersion(sharedPref));
		SQLiteDatabase db = dbAdater.getReadableDatabase();

		String selectSQL = dbAdater.getSelectCounterDataSQL();
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

				listCountingData.add(new CountingData(idNum, name, description, descriptionDateTime, countNum, countDateTime, sortNum));
			}while (dbCursor.moveToNext());
		}
		dbAdater.close();
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

			if (!countingTime.toString().equals("20130101000000"))
			{
				UsefulTools.DatabaseAdater dbAdater = tools.getDatabaseAdater(this, tools.getDBVersion(sharedPref));
				SQLiteDatabase db = dbAdater.getWritableDatabase();
				db.execSQL(dbAdater.getInsertCounterDataSQL("default",
						descStr,
						tools.getDateTimeStringType01(Calendar.getInstance()),
						countingValue,
						countingTime.toString(),
						1));
				dbAdater.close();

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
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		titleHandler.sendMessage(new Message());
	}

	private class TitleHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			listCounting.setVisibility(View.VISIBLE);
			layoutTitle.setVisibility(View.INVISIBLE);

			super.handleMessage(msg);
		}
	}

	private class CountingData {
		private int idNum;
		private String name;
		private String description;
		private String descriptionTime;
		private int count;
		private String countTime;
		private int sortNum;

		public CountingData()
		{
		}

		public CountingData(int idNum, String name, String description,
				String descriptionTime, int count, String countTime,
				int sortNum) {
			super();
			this.idNum = idNum;
			this.name = name;
			this.description = description;
			this.descriptionTime = descriptionTime;
			this.count = count;
			this.countTime = countTime;
			this.sortNum = sortNum;
		}

		public int getIdNum() {
			return idNum;
		}
		public void setIdNum(int idNum) {
			this.idNum = idNum;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getDescriptionTime() {
			return descriptionTime;
		}
		public void setDescriptionTime(String descriptionTime) {
			this.descriptionTime = descriptionTime;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public String getCountTime() {
			return countTime;
		}
		public void setCountTime(String countTime) {
			this.countTime = countTime;
		}
		public int getSortNum() {
			return sortNum;
		}
		public void setSortNum(int sortNum) {
			this.sortNum = sortNum;
		}
	}
}
