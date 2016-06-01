package com.example.countdown;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * �������ƹ���
 * ��ͷ��������ƣ��رպ�ſ����������½
 * layout�ļ���û����á���Ҫ��ԭ����imageviewȥ��
 * setFousable��Ϊfalse����
 * @author ������
 *
 */
public class LoginActivity extends Activity {

	private Button button;
	private Button lightBtn;
	private EditText editTextpw;
	//private ImageView head;
	private boolean flag;
	private Camera camera = null;
	private Parameters parameters = null;
	public boolean light=false;
	private RelativeLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		flag=false;
		layout=(RelativeLayout)findViewById(R.id.root);
		
		button=(Button)findViewById(R.id.log_in_btn);
		editTextpw=(EditText)findViewById(R.id.log_in_pw);
		lightBtn=(Button)findViewById(R.id.light_btn);
		lightBtn.setFocusable(true);
		lightBtn.requestFocus();
		lightBtn.setFocusableInTouchMode(true);
		lightBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(!flag){
					layout.setBackgroundColor(Color.parseColor("#292421"));
					lightBtn.setBackgroundResource(R.drawable.flash_head);
					camera = Camera.open();
	                parameters = camera.getParameters();
	                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// ����
	                camera.setParameters(parameters);
	                camera.startPreview();
	                flag=true;
				}else{
					flag=false;
					layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
					lightBtn.setBackgroundResource(R.drawable.log_in_head_2);
					 parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// �ر�
		             camera.setParameters(parameters);
		             camera.stopPreview();
		             camera.release();
		             camera=null;
				}
			}
		});
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(!flag){
				String pw=editTextpw.getText().toString();
				if(pw.equals("112329")){
					Intent intent=new Intent(LoginActivity.this,MainActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(LoginActivity.this, "�������", Toast.LENGTH_SHORT).show();
				}
				}else{
					Toast.makeText(LoginActivity.this, "���ȹص��ֵ�Ͳ", Toast.LENGTH_SHORT).show();
				}
			}
		});
		editTextpw.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					if(!flag)
						lightBtn.setBackgroundResource(R.drawable.log_in_head1);
					else
						lightBtn.setBackgroundResource(R.drawable.flash_head2);
				}else{
					if(!flag)
						lightBtn.setBackgroundResource(R.drawable.log_in_head_2);
					else
						lightBtn.setBackgroundResource(R.drawable.flash_head);
				}
			}
		});
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
        camera.release();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
