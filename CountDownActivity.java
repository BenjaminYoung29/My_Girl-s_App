package com.example.countdown;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

class Day1 {
	public Day1(String content, int days, String date) {
		this.content = content;
		this.days = days;
		this.date = date;
	}

	String content;
	String date;
	int days;
}

public class CountDownActivity extends Activity
		implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private static long currentTime;
	private int mark = 1;
	private boolean first = true;

	private class DayAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<Day1> dates = new ArrayList<Day1>();

		class ViewHolder {
			private LinearLayout countDownLayout;
			private TextView content;
			private TextView date;
		}

		private DayAdapter(Context context, ArrayList<Day1> dates) {
			this.context = context;
			this.dates = dates;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dates.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return dates.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder vh;
			View view;

			if (convertView == null) {
				vh = new ViewHolder();
				view = LayoutInflater.from(context).inflate(R.layout.list_item, null);
				vh.countDownLayout = (LinearLayout) view.findViewById(R.id.count_down_layout);
				vh.content = (TextView) view.findViewById(R.id.count_down_content);
				vh.date = (TextView) view.findViewById(R.id.count_down_datetext);
				view.setTag(vh);
			} else {
				view = convertView;
				vh = (ViewHolder) view.getTag();
			}
			vh.content.setText(dates.get(position).content);
			int passed = dates.get(position).days;
			if (first){
				vh.date.setText(passed + "天");
				first=false;
			}
			else {
				if (mark == 1) {
					applyRotation(vh.countDownLayout, 0, 90);
					vh.date.setText(passed + "天");
				} else {
					applyRotation(vh.countDownLayout, 0, 90);
					vh.date.setText(passed + "月");
				}
			}
			return view;
		}

	}

	private ListView list;
	private Button edit, add;
	private SensorManager sensorManager;
	private ArrayList<Day1> dayLists;
	Calendar calendar;
	private DayAdapter adapter;
	private MyDataBaseHelper dbHelper;
	private int clickId;

	// 求某一天到今天过了多少天。也就是今天到之前的一天过了多少天
	public int countDay(String s, int mark) {
		calendar = Calendar.getInstance();
		int nowYear = calendar.get(Calendar.YEAR);
		int nowDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		int nowDate = calendar.get(Calendar.DATE);
		int nowMonth = calendar.get(Calendar.MONTH) + 1;
		StringBuffer sb = new StringBuffer();
		int count = 0, y = nowYear, m = calendar.get(Calendar.MONTH) + 1, d = calendar.get(Calendar.DATE);
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '-') {
				if (count == 0 && i == 4) {
					y = Integer.parseInt(sb.toString());
					count++;
				} else if (count == 1)
					m = Integer.parseInt(sb.toString());
				sb.setLength(0);
				continue;
			} else {
				sb.append(s.charAt(i));
			}
		}
		d = Integer.parseInt(sb.toString());
		sb.setLength(0);
		calendar.set(y, m - 1, d);// 设置的时间
		int sum = 0;
		if (mark == 1) {
			int start, end, max;
			boolean flag = false;
			for (int i = calendar.get(Calendar.YEAR); i < nowYear; i++) {
				flag = true;
				if (i == calendar.get(Calendar.YEAR)) {
					if (calendar.get(Calendar.MONTH) == Calendar.JANUARY && calendar.get(Calendar.DATE) == 1) {
						sum += calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
					} else {
						start = calendar.get(Calendar.DAY_OF_YEAR);
						end = calendar.getMaximum(Calendar.DAY_OF_YEAR);
						sum += (end - start);
					}
				} else {
					Calendar tmp = calendar;
					tmp.set(i, Calendar.JANUARY, Calendar.MONDAY);
					sum += tmp.getMaximum(Calendar.DAY_OF_YEAR);
				}
			}
			if (flag)
				sum += nowDayOfYear;
			else {
				sum += (nowDayOfYear - calendar.get(Calendar.DAY_OF_YEAR));
			}
		}
		if (mark == 2) {
			int start, end, max;
			boolean flag = false;
			for (int i = calendar.get(Calendar.YEAR); i < nowYear; i++) {
				flag = true;
				if (i == calendar.get(Calendar.YEAR)) {
					if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
						continue;
					} else {
						sum += Calendar.DECEMBER - calendar.get(Calendar.MONTH);
					}
				} else {
					sum += 12;
				}
			}
			if (flag) {
				if (nowDate >= calendar.get(Calendar.DAY_OF_MONTH)) {
					sum += (nowMonth);
				} else {
					sum += (nowMonth - 1);
				}
			}
		}
		return sum;
	}

	private void init() {
		add = (Button) findViewById(R.id.title_btn_add);
		edit = (Button) findViewById(R.id.title_btn_back);
		list = (ListView) findViewById(R.id.count_down_list);
		add.setOnClickListener(this);
		edit.setOnClickListener(this);
		dayLists = new ArrayList<Day1>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("CountDays", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String content = cursor.getString(cursor.getColumnIndex("content"));
				String date = cursor.getString(cursor.getColumnIndex("days"));
				int days = countDay(date, 1);
				Day1 day1 = new Day1(content, days, date);
				dayLists.add(day1);
			} while (cursor.moveToNext());
		}
		cursor.close();
		adapter = new DayAdapter(this, dayLists);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		registerForContextMenu(list);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		currentTime = System.currentTimeMillis();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_count_down);
		dbHelper = new MyDataBaseHelper(this, "CountDaysDB.db", null, 2);
		init();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn_add:
			Intent i = new Intent(this, AddCountDownActivity.class);
			startActivityForResult(i, 1);
			break;
		case R.id.title_btn_back:
			finish();
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(0, 1, 0, "删除");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Day1 d = (Day1) adapter.getItem(clickId);
			Log.e("clickId", " " + clickId);
			String content = d.content;
			for (Day1 d1 : dayLists) {
				if (d1.content.equals(content)) {
					dayLists.remove(d1);
					break;
				}
			}
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete("CountDays", "content=?", new String[] { d.content });
			adapter.notifyDataSetChanged();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				if (!data.getBooleanExtra("modified", false)) {
					if (mark == 1)
						dayLists.add(new Day1(data.getStringExtra("content"), countDay(data.getStringExtra("days"), 1),
								data.getStringExtra("days")));
					else
						dayLists.add(new Day1(data.getStringExtra("content"), countDay(data.getStringExtra("days"), 2),
								data.getStringExtra("days")));
				} else {
					String id = data.getStringExtra("id");
					for (Day1 d : dayLists) {
						if (id.equals(d.content)) {
							d.content = data.getStringExtra("content");
							d.date = data.getStringExtra("days");
							if (mark == 1)
								d.days = countDay(data.getStringExtra("days"), 1);
							else
								d.days = countDay(data.getStringExtra("days"), 2);
							break;
						}
					}
				}

				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		clickId = arg2;
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, AddCountDownActivity.class);
		Day1 d = (Day1) adapter.getItem(arg2);
		Bundle b = new Bundle();
		b.putString("content", d.content);
		b.putString("days", d.date);
		intent.putExtras(b);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sensorManager != null) {
			sensorManager.unregisterListener(listener);
		}
	}

	private SensorEventListener listener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			float x = Math.abs(event.values[0]);
			float y = Math.abs(event.values[1]);
			float z = Math.abs(event.values[2]);
			if ((System.currentTimeMillis() - CountDownActivity.currentTime > 4000) && (x > 12 || y > 12 || z > 12)) {
				CountDownActivity.currentTime = System.currentTimeMillis();
				if (mark == 1) {
					mark = 2;
					for (Day1 d : dayLists) {
						d.days = countDay(d.date, mark);
						adapter.notifyDataSetChanged();
					}
				} else if (mark == 2) {
					mark = 1;
					for (Day1 d : dayLists) {
						d.days = countDay(d.date, mark);
					}
					adapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	private void applyRotation(LinearLayout layout, float start, float end) {
		final float centerX = layout.getWidth() / 2.0f;
		final float centerY = layout.getHeight() / 2.0f;
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, centerX, centerY, 310.f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());

		rotation.setAnimationListener(new DisplayNextView(layout));

		layout.startAnimation(rotation);
	}

	private final class DisplayNextView implements Animation.AnimationListener {
		private LinearLayout layout;

		DisplayNextView(LinearLayout layout) {
			this.layout = layout;
		}

		public void onAnimationStart(Animation animation) {

		}

		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub
			layout.post(new SnapViews(layout));
		}
	}

	private final class SnapViews implements Runnable {
		private LinearLayout layout;

		SnapViews(LinearLayout layout) {
			this.layout = layout;
		}

		public void run() {
			final float centerX = layout.getWidth() / 2.0f;
			final float centerY = layout.getHeight() / 2.0f;
			Rotate3dAnimation rotation = null;

			layout.requestFocus();
			rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
			rotation.setDuration(500);
			rotation.setInterpolator(new DecelerateInterpolator());
			layout.startAnimation(rotation);
		}
	}
}
