package com.specmate.cerecognition.util;

import java.util.ArrayList;

public class StringUtils {
	public static ArrayList<String> generateListWithout(String[] list, String except) {
		ArrayList<String> set = new ArrayList<String>();
		
		for(String item : list) {
			if(!item.contentEquals(except)) {
				set.add(item);
			}
		}
		
		return set;
	}
}
