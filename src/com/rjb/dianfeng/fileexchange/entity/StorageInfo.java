package com.rjb.dianfeng.fileexchange.entity;

/**
 * android设备挂载点 实体
 * 
 * @author 龙
 * 
 */
public class StorageInfo {

	public String path;
	public String state;
	public boolean isRemoveable;

	public StorageInfo(String path) {
		this.path = path;
	}

	public boolean isMounted() {
		return "mounted".equals(state);
	}

}
