package com.epubsearcherandroidapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import com.dropbox.client2.DropboxAPI.Entry;

/**
 * @author Tirias
 * Clase creada para poder serializar un metadata y poder pasarlo a traves de las actividades
 */
public class EntryMetadata extends Entry implements Serializable {

	
	private static final long serialVersionUID = 8376999574027749880L;
	private String path;
	private String name;
	private String modificationDate;
	
	

	public EntryMetadata(String path, String name, String modificationDate) {
		super();
		this.path = path;
		this.name = name;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
		this.modificationDate = formatter.format(modificationDate);
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

	public String getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(String modificationDate) {
		this.modificationDate = modificationDate;
	}

}

