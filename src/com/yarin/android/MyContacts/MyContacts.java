package com.yarin.android.MyContacts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.util.EncodingUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlSerializer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MyContacts extends ListActivity {
	private static final int AddContact_ID 		= Menu.FIRST;
	private static final int ViewContact_ID 	= Menu.FIRST+1;
	private static final int EditContact_ID 	= Menu.FIRST+2;
	private static final int DeleContact_ID 	= Menu.FIRST+3;
	private static final int ExitContact_ID 	= Menu.FIRST+4;
	private static final int ExportContact_ID 	= Menu.FIRST+6;
	private static final int SearchContact_ID 	= Menu.FIRST+7;
	
	ProgressDialog m_Dialog;
	String str = null;
	
	public void onCreate(Bundle savedInstanceState)	{
		super.onCreate(savedInstanceState);
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		
        Intent intent = getIntent();
        if(intent.getData() == null){
            intent.setData(ContactsProvider.CONTENT_URI);
        }

        getListView().setOnCreateContextMenuListener(this);
        getListView().setBackgroundResource(R.drawable.bg);

        Cursor cursor = managedQuery(getIntent().getData(), ContactColumn.PROJECTION, null, null,null);

        //ע��ÿ���б��ʾ��ʽ ������ + �ƶ��绰
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
        		android.R.layout.simple_list_item_2, cursor,
				new String[] {ContactColumn.NAME, ContactColumn.MOBILENUM },
				new int[] { android.R.id.text1, android.R.id.text2 });

        setListAdapter(adapter);
	}

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, MyContacts.class), 
                null, intent, 0, null);
        
        // �����ϵ��
        menu.add(0, AddContact_ID, 0, R.string.add_user)
        	.setShortcut('1', 'a')
        	.setIcon(R.drawable.img1);
        
        // ������ϵ��
        menu.add(0, SearchContact_ID, 0, R.string.search)
        	.setShortcut('3', 'c')
        	.setIcon(R.drawable.search);
        
        // ����
        menu.add(0, ExportContact_ID, 0, R.string.export)
        	.setShortcut('4', 'd')
        	.setIcon(R.drawable.export);
         
        //�˳�����
        menu.add(0, ExitContact_ID, 0, R.string.exit)
    		.setShortcut('7', 'f')
    		.setIcon(R.drawable.img2);
        
        return true;
    }
    
    //����˵�����
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
        case AddContact_ID:
            //�����ϵ��
            startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            return true;
        case SearchContact_ID:
            //������ϵ��
        	Intent intent = new Intent();
        	intent.setClass(MyContacts.this, ContactSearch.class);
            startActivity(intent);
            return true;
        case ExportContact_ID:
        	//����
//        	Intent intent1 = new Intent();
//        	intent1.setClass(MyContacts.this, ExportView.class);
//            startActivity(intent1);
//            this.finish();
        	export();
            return true;
        case ExitContact_ID:
        	//�˳�����
        	this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //�����Ĭ�ϲ���Ҳ���������ﴦ��
    protected void onListItemClick(ListView l, View v, int position, long id){   
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);   
        //�鿴��ϵ��
        startActivity(new Intent(Intent.ACTION_VIEW, uri));       
    }   
    
    //���������Ĳ˵�
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo){
		AdapterView.AdapterContextMenuInfo info;
		try{
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		}catch (ClassCastException e){
			return;
		}
		
		//�õ�������������
		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null){
			return;
		}

		menu.setHeaderTitle(cursor.getString(1));
		//���ɾ���˵�
		menu.add(0, ViewContact_ID, 0, R.string.view_user);
		//���ɾ���˵�
		menu.add(1, EditContact_ID, 0, R.string.editor_user);
		//���ɾ���˵�
		menu.add(2, DeleContact_ID, 0, R.string.delete_user);
	}
    
    @Override
    public boolean onContextItemSelected(MenuItem item){
		AdapterView.AdapterContextMenuInfo info;
		try{
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		}catch (ClassCastException e){
			return false;
		}

		final Uri uri = ContentUris.withAppendedId(getIntent().getData(), info.id);
		
		switch (item.getItemId()){
			case DeleContact_ID:
				Dialog dialog = new AlertDialog.Builder(MyContacts.this)
				.setTitle("ɾ��")
				.setMessage("�Ƿ�ɾ����ϵ�ˣ�")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton)
					{
						//ɾ��һ����¼
						getContentResolver().delete(uri, null, null);
					}
				}).setNeutralButton("ȡ��", 	new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton)
					{
					}
				}).create();//������ť
				dialog.show();
				break;
			case EditContact_ID:
				//�༭��ϵ��
			    startActivity(new Intent(Intent.ACTION_EDIT, uri));  
				break;
			case ViewContact_ID:
				//�༭��ϵ��
			    startActivity(new Intent(Intent.ACTION_VIEW, uri)); 
				break;
		}
		return false;
	}
    
    private void export(){
		LayoutInflater factory = LayoutInflater.from(MyContacts.this);
		//�õ��Զ���Ի���
        final View view = factory.inflate(R.layout.export, null);
        //�����Ի���
        AlertDialog dlg = new AlertDialog.Builder(MyContacts.this)
        .setTitle("���뵼��")
        .setView(view)//�����Զ���Ի������ʽ
        .setPositiveButton("ȷ��", 
        new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
            	final RadioButton radio1 = (RadioButton)view.findViewById(R.id.RadioButton1);
            	final RadioButton radio2 = (RadioButton)view.findViewById(R.id.RadioButton2);
            	final RadioButton radio3 = (RadioButton)view.findViewById(R.id.RadioButton3);
            	final RadioButton radio4 = (RadioButton)view.findViewById(R.id.RadioButton4);
            	
            	if(radio1.isChecked()){
            		str = "���ڵ�����������...";
            	}else if(radio2.isChecked()){
            		str = "���ڴӷ���������...";
            	}else if(radio3.isChecked()){
            		str = "���ڵ������ı�...";
            	}else if(radio4.isChecked()){
            		str = "���ڴ��ı�����...";
            	}
            	//������ɺ󣬵����ȷ������ʼ��½
            	m_Dialog = ProgressDialog.show(MyContacts.this,
                             "��ȴ�...", str, true);
                new Thread(){ 
                	public void run(){ 
	                    try{ 
	                    	if(radio1.isChecked()){
	        					if(generateXML()){
	        						Toast.makeText(MyContacts.this, "�������������ɹ���", Toast.LENGTH_SHORT).show();
	        					}else{
	        						Toast.makeText(MyContacts.this, "������������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
	        					}
	        				}else if(radio2.isChecked()){
	        					if(parseXML()){
	        						Toast.makeText(MyContacts.this, "�ӷ���������ɹ���", Toast.LENGTH_SHORT).show();
	        					}else{
	        						Toast.makeText(MyContacts.this, "�ӷ���������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
	        					}
	        				}else if(radio3.isChecked()){
	        					if(generateText()){
	        						Toast.makeText(MyContacts.this, "�������ı��ɹ���", Toast.LENGTH_SHORT).show();
	        					}else{
	        						Toast.makeText(MyContacts.this, "�������ı�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
	        					}
	        				}else if(radio4.isChecked()){
	        					if(parseText()){
	        						Toast.makeText(MyContacts.this, "���ı�����ɹ���", Toast.LENGTH_SHORT).show();
	        					}else{
	        						Toast.makeText(MyContacts.this, "���ı�����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
	        					}
	        				}
	                    }catch (Exception e){
	                    	e.printStackTrace();
	                    }finally{
	                    	//��¼������ȡ��m_Dialog�Ի���
	                    	m_Dialog.dismiss();
	                    }
                	}
                }.start(); 
            }
        })
        .setNegativeButton("ȡ��", 
        new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
            	//���"ȡ��"��ť֮���˳�����
            }
        })
        .create();//����
        dlg.show();//��ʾ
    }
    
	private boolean generateXML(){
    	SQLiteDatabase sqlDB = null;
    	String name;
    	String mNumber;
    	String hNumber;
    	String address;
    	String email;
    	String blog;
    	int numColumn;
    	
    	XmlSerializer serializer = Xml.newSerializer();   
        StringWriter writer = new StringWriter();   
        
        sqlDB = this.openOrCreateDatabase("mycontacts.db", MODE_PRIVATE, null);
        
        Cursor cur = sqlDB.rawQuery("select * from contacts", null);
        if(cur == null){
        	sqlDB.close();
        	return false;
        }
        
        try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			
			serializer.startTag(null, "database");
			serializer.attribute(null, "name", "mycontacts.db");
			
			serializer.startTag(null, "table");
			serializer.attribute(null, "name", "contacts");
			
			cur.moveToFirst();
			int i = 1;
			do{
				/* ȡ�ñ����ֶ�*/
				numColumn = cur.getColumnIndex("name");	//name
    			name = cur.getString(numColumn);
    			
    			numColumn = cur.getColumnIndex("mobileNumber");	//mobileNumber
    			mNumber = cur.getString(numColumn);
    			
    			numColumn = cur.getColumnIndex("homeNumber");	//homeNumber
    			hNumber = cur.getString(numColumn);
    			
    			numColumn = cur.getColumnIndex("address");	//address
    			address = cur.getString(numColumn);
    			
    			numColumn = cur.getColumnIndex("email");	//email
    			email = cur.getString(numColumn);
    			
    			numColumn = cur.getColumnIndex("blog");	//blog
    			blog = cur.getString(numColumn);
				
    			/* ����XML*/
				serializer.startTag(null, "contact");
				serializer.attribute(null, "id", String.valueOf(i++));
				
				serializer.startTag(null, "name");		
				serializer.text(name);
				serializer.endTag(null, "name");
				
				serializer.startTag(null, "mobileNumber");
				serializer.text(mNumber);
				serializer.endTag(null, "mobileNumber");
				
				serializer.startTag(null, "homeNumber");
				serializer.text(hNumber);
				serializer.endTag(null, "homeNumber");
				
				serializer.startTag(null, "address");
				serializer.text(address);
				serializer.endTag(null, "address");
				
				serializer.startTag(null, "email");
				serializer.text(email);
				serializer.endTag(null, "email");
				
				serializer.startTag(null, "blog");
				serializer.text(blog);
				serializer.endTag(null, "blog");
				
				serializer.endTag(null, "contact");		//contact����
				
			}while(cur.moveToNext());
			serializer.endTag(null, "table");		//table����
			serializer.endTag(null, "database");		//database����
			serializer.endDocument();
			
			String text = writer.toString();
			connectToServer(text);
		} catch (Exception e) {
			sqlDB.close();
			Log.e("ExportView", e.toString());
		}
		sqlDB.close();
		
		return true;
    }
    
    private boolean parseXML(){
    	ArrayList<Contact> list = null;
    	String database;
    	String table;
    	String source;
    	
    	SAXHandler handler = new SAXHandler();
    	SAXParserFactory factory = SAXParserFactory.newInstance();   
        SAXParser parser;
        
        source = connectToServer(null);
        if(source == null){
        	return false;
        }
        
		try {
			parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();  
	        reader.setContentHandler(handler);   
	        reader.parse(new InputSource(new FileInputStream(source))); 
		} catch (Exception e) {
			Log.e("ExportView", e.toString());
		}   
        list = handler.getList();
        database = handler.getDatabase();
        table = handler.getTable();
        
        //�������ݿ� 
        return insertData(database, table, list);
    }
    
    private boolean insertData(String database, String table, ArrayList<Contact> list){
    	SQLiteDatabase sqlDB = null;
        sqlDB = this.openOrCreateDatabase(database, MODE_PRIVATE, null);
        if(sqlDB == null){
        	return false;
        }
        for(Contact c : list){
        	ContentValues values = new ContentValues();
        	values.put(ContactColumn.NAME, c.getName());
        	values.put(ContactColumn.MOBILENUM, c.getMobileNumber());
        	values.put(ContactColumn.HOMENUM, c.getHomeNumber());
        	values.put(ContactColumn.ADDRESS, c.getAddress());
        	values.put(ContactColumn.EMAIL, c.getEmail());
        	values.put(ContactColumn.BLOG, c.getBlog());
        	sqlDB.insert(table, "_id", values);
        }
        sqlDB.close();
        
        return true;
    }
    
    private boolean generateText(){
    	SQLiteDatabase sqlDB = null;
    	StringBuilder str = null;
    	String name;
    	String mNumber;
    	String hNumber;
    	String address;
    	String email;
    	String blog;
    	int numColumn;
    	
        sqlDB = this.openOrCreateDatabase("mycontacts.db", MODE_PRIVATE, null);
        str = new StringBuilder();
        str.append("database=mycontacts.db;table=contacts;");
        
        Cursor cur = sqlDB.rawQuery("select * from contacts", null);
        if(cur == null){
        	sqlDB.close();
        	return false;
        }
        
		cur.moveToFirst();
		do{
			numColumn = cur.getColumnIndex("name");	//name
			name = cur.getString(numColumn);
			
			numColumn = cur.getColumnIndex("mobileNumber");	//mobileNumber
			mNumber = cur.getString(numColumn);
			
			numColumn = cur.getColumnIndex("homeNumber");	//homeNumber
			hNumber = cur.getString(numColumn);
			
			numColumn = cur.getColumnIndex("address");	//address
			address = cur.getString(numColumn);
			
			numColumn = cur.getColumnIndex("email");	//email
			email = cur.getString(numColumn);
			
			numColumn = cur.getColumnIndex("blog");	//blog
			blog = cur.getString(numColumn);
			
			str.append("contacts=" + name + "," + mNumber + "," + hNumber + "," + address + "," + email + "," + blog + ";");
		}while(cur.moveToNext());
		sqlDB.close();
		
		return writeFileData("contacts.txt", str.toString());
    }
    
    private boolean parseText(){
    	String content = null;
    	String database = null;
    	String table = null;
    	ArrayList<Contact> list = null;
    	Contact contact = null;
    	
    	list = new ArrayList<Contact>();
    	content = readFileData("contacts.txt");
    	if(content == null){
    		return false;
    	}
    	String[] str = content.split(";");
    	for(String s : str){
    		if(s.startsWith("database=")){
    			database = s.substring(9);
    		}else if(s.startsWith("table=")){
    			table = s.substring(6);
    		}else if(s.startsWith("contacts=")){
    			String tmp = s.substring(9);
    			String[] contacts = tmp.split(",");
    			
    			contact = new Contact();
    			switch(contacts.length){
    			case 0:
    				break;
    			case 1:
    				contact.setName(contacts[0]);
    				break;
    			case 2:
    				contact.setName(contacts[0]);
    				contact.setMobileNumber(contacts[1]);
    				break;
    			case 3:
    				contact.setName(contacts[0]);
    				contact.setMobileNumber(contacts[1]);
    				contact.setHomeNumber(contacts[2]);
    				break;
    			case 4:
    				contact.setName(contacts[0]);
    				contact.setMobileNumber(contacts[1]);
    				contact.setHomeNumber(contacts[2]);
    				contact.setAddress(contacts[3]);
    				break;
    			case 5:
    				contact.setName(contacts[0]);
    				contact.setMobileNumber(contacts[1]);
    				contact.setHomeNumber(contacts[2]);
    				contact.setAddress(contacts[3]);
    				contact.setEmail(contacts[4]);
    				break;
    			case 6:
    				contact.setName(contacts[0]);
    				contact.setMobileNumber(contacts[1]);
    				contact.setHomeNumber(contacts[2]);
    				contact.setAddress(contacts[3]);
    				contact.setEmail(contacts[4]);
    				contact.setBlog(contacts[5]);
    				break;
    			}
    			list.add(contact);
    		}
    	}
    	return insertData(database, table, list);
    }
    
    private boolean writeFileData(String filename, String content){
    	FileOutputStream out = null;
    	try {
    		Log.e("ExportView", "open");
			out = openFileOutput(filename, MODE_PRIVATE);
			if(out == null){
				Log.e("ExportView", "open fail");
				return false;
			}
			byte[] bytes = content.getBytes();
			out.write(bytes);
			out.close();
		} catch (Exception e) {
			Log.e("ExportView", e.toString());
		}
		return true;
    }
    
    private String readFileData(String filename){
    	String result = null;
    	try {
			FileInputStream in = openFileInput(filename);
			int length =in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			result = EncodingUtils.getString(buffer, "UTF-8");
			in.close();
    	} catch (Exception e) {
    		Log.e("ExportView", e.toString());
		}
    	return result;
    }
    
    private String connectToServer(String msg){
    	Socket socket = null;
    	String result = null;
    	
    	msg = msg + "\r\n";
    	try {
			socket = new Socket("192.168.1.110", 54321);
			//�������������Ϣ
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			if(msg != null){
				out.println(msg);
			}
			
			// �������Է���������Ϣ
			BufferedReader in = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
			result = in.readLine();
			if(result == null){
				return null;
			}
			
			// �ر���
			out.close();
			in.close();
			// �ر�socket
			socket.close();
		} catch (Exception e) {
			Log.e("ExporView", e.toString());
		}
    	
    	return result;
    }
}
