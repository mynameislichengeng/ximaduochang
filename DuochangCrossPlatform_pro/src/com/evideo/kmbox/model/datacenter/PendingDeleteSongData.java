package com.evideo.kmbox.model.datacenter;

import java.util.ArrayList;
import java.util.List;

public class PendingDeleteSongData {
	private int mVersion = 0;
	private List<Integer> mList = null;
	
	public PendingDeleteSongData() {
		mList = new ArrayList<Integer>();
	}
	
	public PendingDeleteSongData(int version, List<Integer> list) {
		mVersion = version;
		mList = list;
	}
	
	public int getVersion() {
		return mVersion;
	}
	
	public void setVersion(int version) {
		mVersion = version;
	}
	
	public List<Integer> getList() {
		return mList;
	}
	
	public void setList(List<Integer> list) {
		mList = list;
	}
}
