package kr.dev.rei.counter4eun;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public class UsefulTools {
	private static UsefulTools self = null;
	public static UsefulTools GetInstance()
	{
		if (self == null)
		{
			self = new UsefulTools();
		}

		return self;
	}

	private final String preferenceKeyVersion = "PREFERENCE_KEY_VERSION";
	private final String preferenceKeyDBVersion = "PREFERENCE_KEY_DB_VERSION";
	private final String fileName = "Counter4Eun.db";

	private DatabaseAdater dbAdapter;

	private final DecimalFormat df00 = new DecimalFormat("00");
	private final DecimalFormat df0000 = new DecimalFormat("0000");

	private UsefulTools() {
	}

	public String getDateTimeStringType01(Calendar dateTime) {
		StringBuffer dateTimeString = new StringBuffer();
		dateTimeString.append(df0000.format(dateTime.get(Calendar.YEAR))).append(".");
		dateTimeString.append(df00.format(dateTime.get(Calendar.MONTH) + 1)).append(".");
		dateTimeString.append(df00.format(dateTime.get(Calendar.DATE))).append(" ");
		dateTimeString.append(df00.format(dateTime.get(Calendar.HOUR))).append(":");
		dateTimeString.append(df00.format(dateTime.get(Calendar.MINUTE))).append(":");
		dateTimeString.append(df00.format(dateTime.get(Calendar.SECOND)));

		return dateTimeString.toString();
	}

	public String getDateTimeStringType02(Calendar dateTime) {
		StringBuffer dateTimeString = new StringBuffer();
		dateTimeString.append(df0000.format(dateTime.get(Calendar.YEAR)));
		dateTimeString.append(df00.format(dateTime.get(Calendar.MONTH) + 1));
		dateTimeString.append(df00.format(dateTime.get(Calendar.DATE)));
		dateTimeString.append(df00.format(dateTime.get(Calendar.HOUR)));
		dateTimeString.append(df00.format(dateTime.get(Calendar.MINUTE)));
		dateTimeString.append(df00.format(dateTime.get(Calendar.SECOND)));

		return dateTimeString.toString();
	}

	public String getDateTimeStringType01ToType02(String dateTimeStringType01) throws Exception {
		if (dateTimeStringType01.length() != 19)
		{
			throw new Exception("Date-Time format was invalidate.");
		}

		String dateTimeStringType02 = dateTimeStringType01.replace(" ", "");
		dateTimeStringType02 = dateTimeStringType02.replace(".", "");
		dateTimeStringType02 = dateTimeStringType02.replace(":", "");

		if (dateTimeStringType01.length() != 14)
		{
			throw new Exception("Date-Time format was invalidate.");
		}

		return dateTimeStringType02;
	}

	public String getDateTimeStringType02ToType01(String dateTimeStringType02) throws Exception {
		if (dateTimeStringType02.length() != 14)
		{
			throw new Exception("Date-Time format was invalidate.");
		}

		StringBuilder dateTimeStringType01 = new StringBuilder();
		dateTimeStringType01.append(dateTimeStringType02.substring(0, 4)).append(".");
		dateTimeStringType01.append(dateTimeStringType02.substring(4, 6)).append(".");
		dateTimeStringType01.append(dateTimeStringType02.substring(6, 8)).append(" ");
		dateTimeStringType01.append(dateTimeStringType02.substring(8, 10)).append(":");
		dateTimeStringType01.append(dateTimeStringType02.substring(10, 12)).append(":");
		dateTimeStringType01.append(dateTimeStringType02.substring(12));

		if (dateTimeStringType01.length() != 19)
		{
			throw new Exception("Date-Time format was invalidate.");
		}

		return dateTimeStringType01.toString();
	}

	public float getVersion(SharedPreferences sharedPref)
	{
		return sharedPref.getFloat(preferenceKeyVersion, 0.0f);
	}

	public void setVersion(SharedPreferences.Editor sharedPrefEditor, float version)
	{
		sharedPrefEditor.putFloat(preferenceKeyVersion, version);
		sharedPrefEditor.commit();
	}

	public int getDBVersion(SharedPreferences sharedPref)
	{
		return sharedPref.getInt(preferenceKeyDBVersion, 1);
	}

	public void setDBVersion(SharedPreferences.Editor sharedPrefEditor, int version)
	{
		sharedPrefEditor.putInt(preferenceKeyDBVersion, version);
		sharedPrefEditor.commit();
	}

	public DatabaseAdater getDatabaseAdater(Context context, int version)
	{
		if (dbAdapter == null)
		{
			dbAdapter = new DatabaseAdater(context, fileName, null, version);
		}

		return dbAdapter;
	}

	public class DatabaseAdater extends SQLiteOpenHelper
	{
		public final String tableCounterData = "COUNTER_DATA";

		public final String columnIdNum = "ID_NUM";
		public final String columnName = "NAME";
		public final String columnDescription = "DESCRIPTION";
		public final String columnDescriptionDatetime = "DESCRIPTION_DATETIME";
		public final String columnCountNum = "COUNT_NUM";
		public final String columnCountDatetime = "COUNT_DATETIME";
		public final String columnSortNum = "SORT_NUM";

		public DatabaseAdater(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public DatabaseAdater(Context context, String name,
				CursorFactory factory, int version,
				DatabaseErrorHandler errorHandler) {
			super(context, name, factory, version, errorHandler);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(getCreateCounterDataSQL());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}


		private String getCreateCounterDataSQL()
		{
			StringBuilder createSQL = new StringBuilder();
			createSQL.append("CREATE TABLE ").append(tableCounterData).append(" (");
			createSQL.append(columnIdNum).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			createSQL.append(columnName).append(" TEXT, ");
			createSQL.append(columnDescription).append(" TEXT, ");
			createSQL.append(columnDescriptionDatetime).append(" TEXT, ");
			createSQL.append(columnCountNum).append(" INTEGER, ");
			createSQL.append(columnCountDatetime).append(" TEXT, ");
			createSQL.append(columnSortNum).append(" INTEGER)");

			return createSQL.toString();
		}

		public String getInsertCounterDataSQL(String name,
				String description,
				String descriptionDateTime,
				int countNum,
				String countDateTime,
				int sortNum)
		{
			StringBuffer insertSQL = new StringBuffer();
			insertSQL.append("INSERT INTO ").append(tableCounterData);
			insertSQL.append(" (").append(columnName).append(", ");
			insertSQL.append(columnDescription).append(", ");
			insertSQL.append(columnDescriptionDatetime).append(", ");
			insertSQL.append(columnCountNum).append(", ");
			insertSQL.append(columnCountDatetime).append(", ");
			insertSQL.append(columnSortNum).append(") ");
			insertSQL.append("VALUES ('");
			insertSQL.append(name).append("', '");
			insertSQL.append(description).append("', '");
			insertSQL.append(descriptionDateTime).append("', ");
			insertSQL.append(countNum).append(", '");
			insertSQL.append(countDateTime).append("', ");
			insertSQL.append(sortNum).append(")");

			return insertSQL.toString();
		}

		public String getUpdateCounterDataSQL(int idNum,
				String name,
				String description,
				String descriptionDateTime,
				int countNum,
				String countDateTime,
				int sortNum)
		{
			StringBuffer updateSQL = new StringBuffer();
			updateSQL.append("UPDATE ").append(tableCounterData).append(" SET ");
			updateSQL.append(columnName).append(" = '").append(name).append("', ");
			updateSQL.append(columnDescription).append(" = '").append(description).append("', ");
			updateSQL.append(columnDescriptionDatetime).append(" = '").append(descriptionDateTime).append("', ");
			updateSQL.append(columnCountNum).append(" = ").append(countNum).append(", ");
			updateSQL.append(columnCountDatetime).append(" = '").append(countDateTime).append("', ");
			updateSQL.append(columnSortNum).append(" = ").append(sortNum);
			updateSQL.append(" WHERE ").append(columnIdNum).append(" = ").append(idNum);

			return updateSQL.toString();
		}

		public String getSelectCounterDataSQL()
		{
			return getSelectCounterDataSQL(-1, "");
		}

		public String getSelectCounterDataSQL(int idNum)
		{
			return getSelectCounterDataSQL(idNum, "");
		}

		public String getSelectCounterDataSQL(String name)
		{
			return getSelectCounterDataSQL(-1, name);
		}

		public String getSelectCounterDataSQL(int idNum, String name)
		{
			StringBuffer selectSQL = new StringBuffer();
			selectSQL.append("SELECT ").append(columnIdNum).append(", ");
			selectSQL.append(columnName).append(", ");
			selectSQL.append(columnDescription).append(", ");
			selectSQL.append(columnDescriptionDatetime).append(", ");
			selectSQL.append(columnCountNum).append(", ");
			selectSQL.append(columnCountDatetime).append(", ");
			selectSQL.append(columnSortNum);
			selectSQL.append(" FROM ").append(tableCounterData);

			if (idNum > 0 || (name != null && !name.equals("")))
			{
				selectSQL.append(" WHERE ");
				if (idNum > 0)
				{
					selectSQL.append(columnIdNum).append(" = ").append(idNum);

					if (name != null && !name.equals(""))
					{
						selectSQL.append(" AND ");
					}
				}

				if (name != null && !name.equals(""))
				{
					selectSQL.append(columnName).append(" = '").append(name).append("' ");
				}
			}

			selectSQL.append(" ORDER BY ").append(columnSortNum).append(", ").append(columnCountDatetime).append(" DESC ");

			return selectSQL.toString();
		}

		public String getDeleteCounterDataSQL(int idNum)
		{
			StringBuffer deleteSQL = new StringBuffer();
			deleteSQL.append("DELETE FROM ").append(tableCounterData);
			deleteSQL.append(" WHERE ").append(columnIdNum).append(" = ").append(idNum);

			return deleteSQL.toString();
		}
	}
}
