package com.rjb.dianfeng.fileexchange.entity;

public class MyFile {
	private String name;
	private String path;
	private int size;

	public MyFile(String name, String path, int size) {
		super();
		this.name = name;
		this.path = path;
		this.size = size;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
