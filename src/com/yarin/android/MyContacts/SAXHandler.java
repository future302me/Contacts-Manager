package com.yarin.android.MyContacts;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SAXHandler extends DefaultHandler {
	private ArrayList<Contact> list = new ArrayList<Contact>();
	private Contact contact;
	private String database;
	private String table;
	private String tag;

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if("database".equals(localName)){
			database = attributes.getValue(0);
		}else if("table".equals(localName)){
			table = attributes.getValue(0);
		}else if("contact".equals(localName)){
			contact = new Contact();
		}
		tag = localName;
		Log.e("startTag", localName);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		tag = null;
		if("contact".equals(localName)){
			list.add(contact);
			contact = null;
		}
		Log.e("endTag", localName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(tag != null){
			String value = new String(ch, start, length).trim();
			if(value.length() == 0){
				return;
			}
			Log.e("text", value);
			
			if("name".equals(tag)){
				contact.setName(value);
			}else if("mobileNumber".equals(tag)){
				contact.setMobileNumber(value);
			}else if("homeNumber".equals(tag)){
				contact.setHomeNumber(value);
			}else if("address".equals(tag)){
				contact.setAddress(value);
			}else if("email".equals(tag)){
				contact.setEmail(value);
			}else if("blog".equals(tag)){
				contact.setBlog(value);
			}
			
		}
	}
	
	public ArrayList<Contact> getList() {
		return list;
	}

	public void setList(ArrayList<Contact> list) {
		this.list = list;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

}