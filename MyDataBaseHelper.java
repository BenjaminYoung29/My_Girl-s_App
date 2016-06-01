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
		dialog.setTitle("������־");
		dialog.setMessage("1.��½ֻ����һ�ΰ�ť\n"
				+ "2.����˼�¼���ٴδ��ֲ����˵�bug\n"
				+ "3.�������ռ������򣬼���������������,���˵Ľ������С���Ϊ���Ѿ���\n"
				+ "4.�������յ�������������ɫ��Խ������ɫԽ��\n"
				+ "5.ͨ��ĳ�ַ�ʽ���ܽ������յ�����תΪ���·���ʾ\n"
				+ "6.ͨ��ĳ�ַ�ʽ���ܴ��ֵ�Ͳ\n"
				+ "7.������ݿ���ֱ����ԭ�еĻ������޸�\n"
				+" 8.�ұ�֮ǰ�������ˡ�\n"
				+ "9.��󣬰�������֣�ϣ����һֱ����ȥ��");
		dialog.setCancelable(false);
		dialog.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.show();
	}

}

