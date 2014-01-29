package com.epubsearcherandroidapp;

import java.io.Serializable;
import java.util.Date;

import com.dropbox.client2.DropboxAPI.Entry;

public class EntryMetadata extends Entry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8376999574027749880L;
	private String path;
	private String name;
	private Date modificationDate;
	
	

	public EntryMetadata(String path, String name, Date modificationDate) {
		super();
		this.path = path;
		this.name = name;
		this.modificationDate = modificationDate;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

}

