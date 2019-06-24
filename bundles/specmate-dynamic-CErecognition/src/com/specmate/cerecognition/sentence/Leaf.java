package com.specmate.cerecognition.sentence;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class Leaf extends Fragment {

	private Token reference;
	
	public Leaf(String tag, String coveredText, Token reference) {
		super(tag, coveredText);
		this.reference = reference;
	}


	public Token getReference() {
		return reference;
	}

	public void setReference(Token reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		return super.getCoveredText();
	}
}
