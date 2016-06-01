package com.example.countdown;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

class Row {
	String left;
	String right;

	Row(String s1, String s2) {
		left = s1;
		right = s2;
	}
}

class ViewHolder {
	TextView tl, tr;
}

public class AddCountDownActivity extends Activity implements OnClickListener, OnItemClickListener {
	private ArrayList<Row> rows = new ArrayList<Row>();
	private ListView menuList;
	private TextView textView;
	private Button btn_back, btn_add;
	private MyDataBaseHelper dbHelper;
	private AddAdapter adapter;
	private String id = "",passed="";
	private String newId="",newPassed="";
	private boolean flag = false;

	private void initRowNull() {
		Row row1 = new Row("标题", "未命名");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String day = sdf.format(date);
		Row row2 = new Row("日期", day);
		Row row3 = new Row("提醒", "无提醒");
		rows.add(row1);
		rows.add(row2);
		rows.add(row3);
	}

	private void initRow(Bundle b) {
		Row row1 = new Row("标题", b.getString("content"));
		Row row2 = new Row("日期", b.getString("days"));
		Row row3 = new Row("提醒", "无提醒");
		rows.add(row1);
		rows.add(row2);
		rows.add(row3);
		id = b.getString("content");
		passed=b.getString("days");
	}

	private class AddAdapter extends BaseAdapter {

		private ArrayList<Row> list;
		private Context context;

		public AddAdapter(Context context, ArrayList<Row> objects) {
			list = objects;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			View v;
			if (convertView == null) {
				vh = new ViewHolder();
				v = LayoutInflater.from(context).inflate(R.layout.add_countdown_list_item, null);
				vh.tl = (TextView) v.findViewById(R.id.add_left);
				vh.tr = (TextView) v.findViewById(R.id.add_right);
				v.setTag(vh);
			} else {
				v = convertView;
				vh = (ViewHolder) v.getTag();
			}
			vh.tl.setText(list.get(position).left);
			vh.tr.setText(list.get(position).right);
			return v;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		switch (position) {
		case 0: {
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_view, null);
			final EditText edit = (EditText) layout.findViewById(R.id.dialog_edittext);
			Row row=(Row)adapter.getItem(position);
			edit.setText(row.right);
			edit.setSelection(row.right.length());
			Builder dialog = new AlertDialog.Builder(this).setView(layout).setTitle("标题")
					.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							View v = menuList.getChildAt(0);
							TextView tv = (TextView) v.findViewById(R.id.add_right);
							tv.setText(edit.getText().toString());

						}
					}).setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
			dialog.show();
		}
			break;
		case 1: {
			final Calendar c = Calendar.getInstance();
			DatePickerDialog dialog = new DatePickerDialog(AddCountDownActivity.this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					// c.set(Calendar.YEAR,arg1);
					// c.set(Calendar.MONTH, arg2);
					// c.set(Calendar.DATE, arg3);
					if (arg1 > c.get(Calendar.YEAR)) {
						Toast.makeText(AddCountDownActivity.this, "日期不能比今天晚", Toast.LENGTH_SHORT).show();
					} else if (arg1 == c.get(Calendar.YEAR)) {
						if (arg2 > c.get(Calendar.MONTH)) {
							Toast.makeText(AddCountDownActivity.this, "日期不能比今天晚", Toast.LENGTH_SHORT).show();
						} else if (arg2 == c.get(Calendar.MONTH)) {
							if (arg3 > c.get(Calendar.DAY_OF_MONTH)) {
								Toast.makeText(AddCountDownActivity.this, "日期不能比今天晚", Toast.LENGTH_SHORT).show();
							} else {
								String s = arg1 + "-" + (arg2 + 1) + '-' + arg3;
								View v = menuList.getChildAt(1);
								TextView tv = (TextView) v.findViewById(R.id.add_right);
								tv.setText(s);
							}
						} else {
							String s = arg1 + "-" + (arg2 + 1) + '-' + arg3;
							View v = menuList.getChildAt(1);
							TextView tv = (TextView) v.findViewById(R.id.add_right);
							tv.setText(s);
						}
					} else {
						String s = arg1 + "-" + (arg2 + 1) + '-' + arg3;
						View v = menuList.getChildAt(1);
						TextView tv = (TextView) v.findViewById(R.id.add_right);
						tv.setText(s);
					}
				}

			}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
			dialog.setTitle("选择日期");
			dialog.show();
		}
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_count_down);
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		if (b != null) {
			initRow(b);
			flag = true;//修改
		} else {
			flag = false;
			initRowNull();
		}

		dbHelper = new MyDataBaseHelper(this, "CountDaysDB.db", null, 2);
		btn_back = (Button) findViewById(R.id.title_btn_back);
		btn_add = (Button) findViewById(R.id.title_btn_add);
		btn_back.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		adapter = new AddAdapter(this, rows);
		menuList = (ListView) findViewById(R.id.add_count_down_list);
		menuList.setOnItemClickListener(this);
		menuList.setAdapter(adapter);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.title_btn_add: {
			boolean success = false;
			String name = null;
			String days = null;
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			for (int i = 0; i < 2; i++) {
				View v = menuList.getChildAt(i);
				TextView tv1 = (TextView) v.findViewById(R.id.add_right);
				if (i == 0) {
					cv.put("content", tv1.getText().toString());
					name = tv1.getText().toString();
				}
				if (i == 1) {
					cv.put("days", tv1.getText().toString());
					days = tv1.getText().toString();
				}
			}

			if (id.equals("")) {
				Cursor c = db.rawQuery("select content from CountDays where content=?", new String[] { name });
				if (c.moveToFirst())
					success = false;
				else
					success = true;
				db.insert("CountDays", null, cv);
			} else {
				if (id.equals(name)) {
					db.update("CountDays", cv, "content" + "=" + "?", new String[] { id });
					success = true;
				} else {
					Cursor c = db.rawQuery("select content from CountDays where content=?", new String[] { name });
					if (c.moveToFirst()) {
						success = false;
					} else
						success = true;
					db.update("CountDays", cv, "content" + "=" + "?", new String[] { id });
				}
			}
			if (success) {
				db.close();
				cv.clear();
				Intent intent = new Intent();
				intent.putExtra("content", name);
				intent.putExtra("days", days);
				if (id.equals("")) {
					intent.putExtra("modified", false);
				} else {
					intent.putExtra("modified", true);
				}
				intent.putExtra("id", id);
				setResult(RESULT_OK, intent);
				Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "不能跟其他重名噢", Toast.LENGTH_SHORT).show();
			}
		}
			break;
		case R.id.title_btn_back: {
			for (int i = 0; i < 2; i++) {
				View v = menuList.getChildAt(i);
				TextView tv1 = (TextView) v.findViewById(R.id.add_right);
				if (i == 0) {
					newId = tv1.getText().toString();
				}
				if (i == 1) {
					newPassed = tv1.getText().toString();
				}
			}
			if(flag==false||(flag==true&&(!id.equals(newId)||!passed.equals(newPassed)))){
				AlertDialog dialog = new AlertDialog.Builder(this).setMessage("还没保存哦，确定退出吗？")
						.setPositiveButton("退出", new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

							}
						}).show();
			}else{
				Log.e("count", passed+"  "+newPassed+" "+" "+id.equals(newId)+" "+passed.equals(newPassed));
				
				finish();
			}
		}
		}
	}

}
