package com.example.countdown;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper {
	private static String CREATE_TEXT="create table CountDays ("
			+"content text primary key, "
			+" days text)";
	
	private static String CREATE_TEXT1="create table CountDays1 ("
			+"_content1 text primary key, "
			+" days1 text, " 
			+ " memo1 text)";
			
	private Context mContext;
	
	public MyDataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext=context;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TEXT);
		db.execSQL(CREATE_TEXT1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialog=new AlertDialog.Builder(mContext);
		dialog.setTitle("更新日志");
		dialog.setMessage("1.登陆只需点击一次按钮\n"
				+ "2.解决了记录后再次打开又不见了的bug\n"
				+ "3.给倒数日加了排序，即将发生的在上面,过了的将“还有”改为“已经”\n"
				+ "4.给倒数日的日期增加了颜色，越靠近颜色越深\n"
				+ "5.通过某种方式，能将纪念日的日期转为以月份显示\n"
				+ "6.通过某种方式，能打开手电筒\n"
				+ "7.点击内容可以直接在原有的基础上修改\n"
				+" 8.我比之前更爱你了。\n"
				+ "9.最后，半周年快乐，希望能一直走下去。");
		dialog.setCancelable(false);
		dialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.show();
	}

}

