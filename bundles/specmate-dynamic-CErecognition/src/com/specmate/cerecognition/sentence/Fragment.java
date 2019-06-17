package com.specmate.cerecognition.sentence;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public abstract class Fragment {
	private String tag;
	private String coveredText;
	
	public Fragment(String tag, String coveredText) {
		this.tag = tag;
		this.coveredText = coveredText;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getCoveredText() {
		return coveredText;
	}

	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
	}
	
	public abstract String toString();
}
