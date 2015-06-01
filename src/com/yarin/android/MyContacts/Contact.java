package com.yarin.android.MyContacts;

public class Contact {
	String name = null;
	String mobileNumber = null;
	String homeNumber = null;
	String address = null;
	String email = null;
	String blog = null;
	
	public Contact(){}
	
	public Contact(String name, String mobileNumber, String homeNumber, String address, String emial, String blog){
		this.name = name;
		this.mobileNumber = mobileNumber;
		this.homeNumber = homeNumber;
		this.address = address;
		this.email = emial;
		this.blog = blog;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getHomeNumber() {
		return homeNumber;
	}

	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBlog() {
		return blog;
	}

	public void setBlog(String blog) {
		this.blog = blog;
	}
}
