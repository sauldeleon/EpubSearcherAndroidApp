package com.epubsearcherandroidapp;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dropbox.client2.DropboxAPI.Entry;

/**
 * @author Tirias Clase creada para poder serializar un metadata y poder pasarlo
 *         a traves de las actividades
 */
public class EntryMetadata extends Entry implements Serializable {

	private static final long serialVersionUID = 8376999574027749880L;
	private String path;
	private String name;
	private String modificationDate;
	private Boolean isDir;

	public EntryMetadata(String path, String name, String modificationDate, Boolean isDir) throws ParseException {
		super();
		this.path = path;
		this.name = name;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date d = new Date(modificationDate);
		this.modificationDate = formatter.format(d);
		this.isDir = isDir;
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

	public Boolean getIsDir() {
		return isDir;
	}

	public void setIsDir(Boolean isDir) {
		this.isDir = isDir;
	}

}
