package com.rjb.dianfeng.fileexchange.interfaces;

import java.io.File;
import java.util.List;

/**
 * activity 和 folderFragment 之间通信的接口
 * 
 * @author 龙
 * 
 */
public interface ICopyFile2 {
	public void commBetween2Fragment(List<File> copyFile, Boolean isCopy);

}
