package com.specmate.cerecognition.pattern;

import java.util.ArrayList;
import java.util.StringJoiner;

import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.sentence.Node;

public class StructureElement {
	
	private ArrayList<StructureElement> children;
	
	private String tag;
	
	/**
	 * List of all words, that have to be included in the 'covered text' of a 
	 * corresponding fragment from a sentence
	 */
	private ArrayList<String> keywords_whitelist;
	
	/**
	 * List of all words, that are forbidden to be included in the 'covered text'
	 * of a corresponding fragment from a sentence
	 */
	private ArrayList<String> keywords_blacklist;
	
	// this list is an intermediate memory for the keyword generation process
	private ArrayList<String> proposedKeywords;
	
	public StructureElement(String tag) {
		this.tag = tag;
		
		children = new ArrayList<StructureElement>();
		keywords_whitelist = new ArrayList<String>();
		keywords_blacklist = new ArrayList<String>();
		
		proposedKeywords = new ArrayList<String>();
	}

	public ArrayList<StructureElement> getChildren() {
		return children;
	}

	public void addChild(StructureElement child) {
		children.add(child);
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public ArrayList<String> getKeywords_whitelist() {
		return keywords_whitelist;
	}

	/**
	 * Adds a keyword to this structure element, which either must (whitelist) or must not (blacklist) 
	 * be contained by a fragment to be compliant to this structure element
	 * @param keyword The keyword as a String
	 * @param whitelist True, if the keyword shall be required, false, if it shall be forbidden
	 */
	public void addKeyword(String keyword, boolean whitelist) {
		if(whitelist) {
			keywords_whitelist.add(keyword);
		} else {
			keywords_blacklist.add(keyword);
		}
	}

	public ArrayList<String> getKeywords_blacklist() {
		return keywords_blacklist;
	}
	
	public ArrayList<String> getProposedKeywords() {
		return proposedKeywords;
	}

	public void addProposedKeywords(String proposedKeyword) {
		proposedKeywords.add(proposedKeyword);
	}

	/**
	 * Checks whether a given fragment complies this structure element
	 * @param fragment The fragment of a sentence to be compares
	 * @return True, if all non-leaf nodes of the fragment tree equal this object in structure and tags
	 */
	public boolean compliedBy(Fragment fragment) {
		// check if the tag is equal
		if(!tag.equals(fragment.getTag())) {
			return false;
		}
		
		// if a keyword-whitelist exists: check if the fragment contains at least one whitelisted word
		if(!keywords_whitelist.isEmpty()) {
			boolean pendantFound = false;
			for(String keyword : keywords_whitelist) {
				if(fragment.getCoveredText().contains(keyword)) {
					pendantFound = true;
					break;
				}
			}
			if(!pendantFound) {
				return false;
			}
		}
		
		// if a keyword-blacklist exists: check if the fragment contains no blacklisted word
		if(!keywords_blacklist.isEmpty()) {
			for(String keyword : keywords_blacklist) {
				if(fragment.getCoveredText().contains(keyword)) {
					return false;
				}
			}
		}
		
		// count number of important children (children, which have children themselves)
		int fullsize = 0;
		if(fragment instanceof Node) {
			Node node = ((Node) fragment);
			
			for(Fragment child : node.getChildren()) {
				if(child instanceof Node) {
					fullsize++;
				}
			}
		}
		
		// check if the number of children is equal
		if(children.size() > 0) {
			if(fragment instanceof Node) {				
				if(children.size() != fullsize) { 
					return false;
				}
			} else {
				return false;
			}
		} else {
			if(fragment instanceof Node) {
				if(children.size() != fullsize) {
					return false;
				}
			}
		}
		
		// check if all children are equal and in order
		if(children.size() > 0 && fragment instanceof Node) {
			for(int i = 0; i < children.size(); i++) {
				StructureElement structureChild = ((ArrayList<StructureElement>) children).get(i);
				Fragment fragmentChild = ((Node) fragment).getParentingChildren().get(i);
				
				if(!structureChild.compliedBy(fragmentChild)) {
					return false;
				}
			}
		}
		
		// all negative-checks were passed, so the structure is complied by this fragment
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("(" + tag + ")");
		if(!keywords_whitelist.isEmpty()) {
			sb.append("+(" + getKeywordString(keywords_whitelist) + ")");
		}
		if(!keywords_blacklist.isEmpty()) {
			sb.append("-(" + getKeywordString(keywords_blacklist) + ")");
		}
		
		if(!children.isEmpty()) {
			sb.append("{");
			for(StructureElement child : children) {
				sb.append(child.toString());
			}
			sb.append("}");
		}
		
		return sb.toString();
	}
	
	private String getKeywordString(ArrayList<String> list) {
		StringJoiner sj = new StringJoiner(";");
		
		list.forEach(item -> sj.add(item));
		
		return sj.toString();
	}
	
	public StructureElement clone() {
		StructureElement clone = new StructureElement(this.tag);
		
		for(String keyword : keywords_whitelist) {
			clone.addKeyword(keyword, true);
		}
		for(String keyword : keywords_blacklist) {
			clone.addKeyword(keyword, false);
		}
		for(String keyword : proposedKeywords) {
			clone.addProposedKeywords(keyword);
		}
		
		for(StructureElement child : children) {
			clone.addChild(child.clone());
		}
		
		return clone;
	}
	
	public void listAllProposed(boolean whitelist) {
		if(!proposedKeywords.isEmpty()) {
			for(String keyword : proposedKeywords) {
				(whitelist ? keywords_whitelist : keywords_blacklist).add(keyword);
			}
			proposedKeywords.clear();
		}
		
		for(StructureElement child : children) {
			child.listAllProposed(whitelist);
		}
	}
}
