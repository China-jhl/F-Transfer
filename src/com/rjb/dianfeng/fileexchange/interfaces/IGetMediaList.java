package com.rjb.dianfeng.fileexchange.interfaces;

import java.util.List;

import com.rjb.dianfeng.fileexchange.entity.BaseMedia;

/**
 * ��ȡ media list
 * 
 * @author ��
 * 
 */
public interface IGetMediaList {
	public void setMediaList(List<? extends BaseMedia> mediaList);
}
