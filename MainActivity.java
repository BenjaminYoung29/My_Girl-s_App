package com.example.countdown;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	private Button countDown,countTo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select);
		countDown=(Button)findViewById(R.id.count_down_btn);
		countTo=(Button)findViewById(R.id.count_to_btn);
		countDown.setOnClickListener(this);
		countTo.setOnClickListener(this);
		MyDataBaseHelper dbHelper=new MyDataBaseHelper(this, "CountDaysDB.db", null, 2);
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("content", "First Hug");
		cv.put("days", "2015-11-29");
		db.insert("CountDays", null, cv);
		cv.put("content", "First Holding hands");
		cv.put("days", "2015-12-2");
		db.insert("CountDays", null, cv);
		cv.put("content", "First Kiss");
		cv.put("days", "2015-12-10");
		db.insert("CountDays", null, cv);
		db.close();
		cv.clear();
	}
	
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.count_down_btn:
			Intent intent=new Intent(this,CountDownActivity.class);
			startActivity(intent);
			break;
		case R.id.count_to_btn:
			Intent intent1=new Intent(this,CountToActivity.class);
			startActivity(intent1);
			break;
		}
	}
}
