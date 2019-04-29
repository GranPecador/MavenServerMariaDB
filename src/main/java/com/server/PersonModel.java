package com.server;

import java.util.HashSet;
import java.util.Set;

public class PersonModel {
	private int id;
	private Set<InformationModel> interestingInfo;
	
	public PersonModel(int id){
		this.id = id;
		interestingInfo = new HashSet<InformationModel>();
	}
	
	public void addInterestingInfo(InformationModel info) {
		interestingInfo.add(info);
	}
	
	public void removeInterestingInfo() {
		interestingInfo.clear();
	}
}
