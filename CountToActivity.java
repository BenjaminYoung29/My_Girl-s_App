package com.example.countdown;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 数据库要更新
 * 不知道会不会影响到之前的数据
 * 版本号
 * 插入新的列。找书
 * @author 杨垲泓
 *
 */
class Day{
	public Day(String content2, String days2) {
		content=content2;
		days=days2;
		
	}
	String content;
	String days;

}

public class CountToActivity extends Activity implements OnClickListener, OnItemClickListener,OnItemLongClickListener {
	
	private class DayAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<Day> dates = new ArrayList<Day>();

		class ViewHolder {
			private LinearLayout countToLayout;
			private TextView change;
			private TextView content;
			private TextView date;
		}

		private DayAdapter(Context context, ArrayList<Day> dates) {
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
				vh.countToLayout = (LinearLayout) view.findViewById(R.id.count_down_layout);
				vh.change=(TextView)view.findViewById(R.id.count_downto_text);
				vh.content = (TextView) view.findViewById(R.id.count_down_content);
				vh.date = (TextView) view.findViewById(R.id.count_down_datetext);
				view.setTag(vh);
			} else {
				view = convertView;
				vh = (ViewHolder) view.getTag();
			}
			vh.change.setText("还有");
			vh.content.setText(dates.get(position).content);
			int passed = countDay(dates.get(position).days);
			ImageView i=new ImageView(CountToActivity.this);
			if(passed<3&&passed>=0)
				vh.countToLayout.setBackgroundResource(R.drawable.level5);
			if(passed<6&&passed>=3)
				vh.countToLayout.setBackgroundResource(R.drawable.level4);
			if(passed<9&&passed>=6)
				vh.countToLayout.setBackgroundResource(R.drawable.level3);
			if(passed<12&&passed>=9)
				vh.countToLayout.setBackgroundResource(R.drawable.level2);
			if(passed<0){
				passed=-passed;
				vh.countToLayout.setBackgroundResource(R.drawable.pass);
				vh.change.setText("已经过了");
			}
			vh.date.setText(passed + "天");
			return view;
		}

	}
	private ListView list;
	private Button edit,add;
	private TextView headLine;
	private ArrayList<Day> dayLists;
	Calendar calendar;
	private DayAdapter adapter;
	private MyDataBaseHelper dbHelper;
	private int clickId;
	public int countDay(String s){
		int sum=0;
		calendar=Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH)+1;
		int date=calendar.get(Calendar.DAY_OF_MONTH);
		int day=calendar.get(Calendar.DAY_OF_YEAR);
		StringBuffer sb=new StringBuffer();
		int count=0,y=year,m=month,d=date;
		for(int i=0;i<s.length();i++){
			if(s.charAt(i)=='-'){
				if(count==0&&i==4){
					y=Integer.parseInt(sb.toString());
					count++;
				}
				else if(count==1)
					m=Integer.parseInt(sb.toString());
				sb.setLength(0);
				continue;
			}else{
				sb.append(s.charAt(i));
			}
		}
		d=Integer.parseInt(sb.toString());
		sb.setLength(0);
		calendar.set(y, m-1, d);//设置的时间
		//y,m,d是要设置的时间 ，year,month,date是当前时间，要求ymd离当前时间有多少天
		boolean flag=false;
		int start,end;
		for(int i=year;i<y;i++){
			flag=true;
			if(i==year){
				Calendar tmp=Calendar.getInstance();
				tmp.set(Calendar.YEAR, year);
				if(month-1==0&&date==1){
					sum+=tmp.getMaximum(Calendar.YEAR);
				}else{
					start=day;
					end=tmp.getMaximum(Calendar.DAY_OF_YEAR);
					sum+=(end-start);
				}
				Log.e("count", i+" "+sum);
			}else{
				Calendar tmp=Calendar.getInstance();
				tmp.set(Calendar.YEAR, i);
				sum += tmp.getMaximum(Calendar.DAY_OF_YEAR);
			}
		}
		if(!flag)
			sum+=(calendar.get(Calendar.DAY_OF_YEAR)-day);
		else
			sum+=calendar.get(Calendar.DAY_OF_YEAR);
		return sum;
	}
	public void init(){
		add=(Button)findViewById(R.id.title_btn_add);
		edit=(Button)findViewById(R.id.title_btn_back);
		headLine=(TextView)findViewById(R.id.title_text);
		headLine.setText("倒数日");
		list=(ListView)findViewById(R.id.count_to_list);
		add.setOnClickListener(this);
		edit.setOnClickListener(this);
		dayLists=new ArrayList<Day>();
		SQLiteDatabase db=dbHelper.getReadableDatabase();
		Cursor cursor=db.query("CountDays1",null,null,null,null,null,null);
		if(cursor.moveToFirst()){
			do{
				String content=cursor.getString(cursor.getColumnIndex("content1"));
				String days=cursor.getString(cursor.getColumnIndex("days1"));
				//String memo=cursor.getString(cursor.getColumnIndex("memo1"));
				//Log.e("CountTo", content+" "+days+" "+memo);
				Day date = new Day(content,days);
				dayLists.add(date);
			}while(cursor.moveToNext());
		}
		cursor.close();
		Collections.sort(dayLists,new Comparator<Day>(){
			@Override
			public int compare(Day arg0, Day arg1){
				return arg0.days.compareTo(arg1.days);
			}
		});
		adapter=new DayAdapter(this,dayLists);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		registerForContextMenu(list);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_count_to);
		dbHelper = new MyDataBaseHelper(this, "CountDaysDB.db", null, 2);
		init();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.title_btn_add:
			Intent i=new Intent(this,AddCountToActivity.class);
			startActivityForResult(i,1);
			break;
		case R.id.title_btn_back:
			finish();
			break;
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo menuInfo){
		menu.add(0,1,0,"删除");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		switch(item.getItemId()){
		case 1:
			Day d=(Day)adapter.getItem(clickId);
			String content=d.content;
			for(Day d1:dayLists){
				if(d1.content.equals(content)){
					dayLists.remove(d1);
					break;
				}
			}
			SQLiteDatabase db=dbHelper.getWritableDatabase();
			db.delete("CountDays1", "content1=?", new String[]{d.content});
			adapter.notifyDataSetChanged();
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				if(!data.getBooleanExtra("modified", false))
					dayLists.add(new Day(data.getStringExtra("content"),data.getStringExtra("days")));
				else{
					String id=data.getStringExtra("id");
					for(Day d:dayLists){
						if(id.equals(d.content)){
							d.content=data.getStringExtra("content");
							d.days=data.getStringExtra("days");
							break;
						}
					}
				}
				Collections.sort(dayLists,new Comparator<Day>(){
					@Override
					public int compare(Day arg0, Day arg1){
						return arg0.days.compareTo(arg1.days);
					}
				});
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		clickId=arg2;
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(this,AddCountToActivity.class);
		Day d=(Day) adapter.getItem(arg2);
		Bundle b=new Bundle();
		b.putString("content", d.content);
		b.putString("days", d.days);
		//b.putString("memo", d.memo);
		intent.putExtras(b);
		startActivityForResult(intent,1);
	}
}
