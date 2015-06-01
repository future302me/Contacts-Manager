package com.yarin.android.MyContacts;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactSearch extends Activity {

	private static final int REVERT_ID = Menu.FIRST;
	
	private SQLiteDatabase sqlDB = null;
	private TextView countText = null;
	private EditText searchText = null;
	private ListView listView = null;
	private SimpleCursorAdapter adapter = null;
	private Cursor cursor = null; 
	private String content;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(ContactsProvider.CONTENT_URI);
        }
		
        countText = (TextView)findViewById(R.id.countText);
		searchText = (EditText)findViewById(R.id.searchText);	
		listView = (ListView)findViewById(R.id.searchList);
		
		sqlDB = this.openOrCreateDatabase("mycontacts.db", MODE_PRIVATE, null);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
				Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);   
		        Log.e("click", "@@@@@@@@@@@@" + String.valueOf(id));
		        Intent intent = new Intent();
		    	intent.setClass(ContactSearch.this, ContactView.class);
		    	intent.setData(uri);
		        startActivity(intent);
			}
		});
		searchText.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, 
					int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				content = searchText.getText().toString().trim();
				cursor = sqlDB.rawQuery(
						"select * from contacts where name like '%"+ content + "%'", 
						null);
				countText.setText("查找结果：" + String.valueOf(cursor.getCount()) + "条");
				adapter = new SimpleCursorAdapter(
		        		ContactSearch.this, 
		        		android.R.layout.simple_list_item_2, 
		        		cursor,
		        		new String[] {ContactColumn.NAME, ContactColumn.MOBILENUM },
		        		new int[] { android.R.id.text1, android.R.id.text2 });
				ContactSearch.this.listView.setAdapter(adapter);
			}
		}); 
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		//添加菜单
		menu.add(0, REVERT_ID, 0, R.string.revert).setShortcut('0', 'r').setIcon(R.drawable.listuser);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == REVERT_ID){
				setResult(RESULT_CANCELED);
				finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
}
