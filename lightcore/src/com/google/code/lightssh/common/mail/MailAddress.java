package com.google.code.lightssh.common.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮件地址
 * 
 */
public class MailAddress implements Serializable{

	private static final long serialVersionUID = 3888301813301283330L;

	/**
	 * 收件人
	 */
	private List<Address> to;

	/**
	 * 发件人
	 */
	private Address from;

	/**
	 * 抄送
	 */
	private List<Address> cc;

	public MailAddress() {
	}

	public MailAddress(List<Address> to, Address from, List<Address> cc) {
		this.to = to;
		this.from = from;
		this.cc = cc;
	}

	public MailAddress(Address to, Address from, List<Address> cc) {
		List<Address> toList = new ArrayList<Address>(1);
		toList.add(to);

		this.to = toList;
		this.from = from;
		this.cc = cc;
	}

	public MailAddress(List<Address> to, Address from, Address cc) {
		this.to = to;
		this.from = from;

		List<Address> ccList = new ArrayList<Address>(1);
		ccList.add(cc);
		this.cc = ccList;
	}

	public MailAddress(Address to, Address from, Address cc) {
		List<Address> toList = new ArrayList<Address>(1);
		toList.add(to);
		this.to = toList;
		this.from = from;

		List<Address> ccList = new ArrayList<Address>(1);
		ccList.add(cc);
		this.cc = ccList;
	}

	public MailAddress(List<Address> to, Address from) {
		this.to = to;
		this.from = from;
	}
	
	public MailAddress( Address to,Address from ){
		this.addTo(to);
		this.from = from;
	}

	// -- util methods--------------------------------------------------------

	/**
	 * 添加收件人
	 */
	public void addTo(Address address) {
		if (this.to == null)
			this.to = new ArrayList<Address>();
		this.to.add(address);
	}

	/**
	 * 添加收件人
	 */
	public void addTo(String email, String name) {
		if (this.to == null)
			this.to = new ArrayList<Address>();

		Address address = new Address();
		address.setEmail(email);
		address.setName(name);
		this.to.add(address);
	}

	/**
	 * 添加抄送人
	 * 
	 * @param address
	 */
	public void addCc(Address address) {
		if (this.cc == null)
			this.cc = new ArrayList<Address>();
		this.cc.add(address);
	}

	/**
	 * 添加抄送人
	 */
	public void addCc(String email, String name) {
		if (this.cc == null)
			this.cc = new ArrayList<Address>();

		Address address = new Address();
		address.setEmail(email);
		address.setName(name);
		this.cc.add(address);
	}

	public void setFrom(String email, String name) {
		Address from = new Address();
		from.setEmail(email);
		from.setName(name);
		this.from = from;
	}

	// -- getters and setters--------------------------------------------------

	public List<Address> getTo() {
		return to;
	}

	public void setTo(List<Address> to) {
		this.to = to;
	}

	public Address getFrom() {
		return from;
	}

	public void setFrom(Address from) {
		this.from = from;
	}

	public List<Address> getCc() {
		return cc;
	}

	public void setCc(List<Address> cc) {
		this.cc = cc;
	}

}
