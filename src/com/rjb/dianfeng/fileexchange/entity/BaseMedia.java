package com.rjb.dianfeng.fileexchange.entity;


public class BaseMedia{

	private String name;
	private String path;
	private int size;
	private String mime_type;
	private int id;

	public BaseMedia(String name, String path, int size, String mime_type,
			int id) {
		super();
		this.name = name;
		this.path = path;
		this.size = size;
		this.mime_type = mime_type;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getMime_type() {
		return mime_type;
	}

	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}

}
