package com.specmate.cerecognition.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringJoiner;

import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.sentence.Node;

public class StructureElement {
	private Collection<StructureElement> children;
	
	private String tag;
	private Collection<String> keywords_whitelist;
	private Collection<String> keywords_blacklist;
	
	public StructureElement(String tag) {
		this.tag = tag;
		
		children = new ArrayList<StructureElement>();
		keywords_whitelist = new ArrayList<String>();
		keywords_blacklist = new ArrayList<String>();
	}

	public Collection<StructureElement> getChildren() {
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

	public Collection<String> getKeywords_whitelist() {
		return keywords_whitelist;
	}

	public void addKeyword(String keyword, boolean whitelist) {
		if(whitelist) {
			keywords_whitelist.add(keyword);
		} else {
			keywords_blacklist.add(keyword);
		}
	}

	public Collection<String> getKeywords_blacklist() {
		return keywords_blacklist;
	}
	
	/**
	 * Checks whether a given fragment complies this structure element
	 * @param fragment The fragment of a sentence to be compares
	 * @return True, if all non-leaf nodes of the fragment tree equal this object in structure and tags
	 */
	public boolean compliedBy(Fragment fragment) {
		// check if the tag is equal
		if(!tag.equals(fragment.getTag()))
				return false;
		
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
		
		// check if the number of children is equal
		if(children.size() > 0) {
			if(fragment instanceof Node) {
				if(children.size() != ((Node) fragment).getChildren().size())
					return false;
			} else {
				return false;
			}
		} else {
			if(fragment instanceof Node) {
				if(children.size() != ((Node) fragment).getChildren().size())
					return false;
			}
		}
		
		// check if all children are equal and in order
		if(children.size() > 0 && fragment instanceof Node) {
			for(int i = 0; i < children.size(); i++) {
				StructureElement structureChild = ((ArrayList<StructureElement>) children).get(i);
				Fragment fragmentChild = ((Node) fragment).getChildren().get(i);
				
				if(!structureChild.compliedBy(fragmentChild))
					return false;
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
			sb.insert(0, "{");
			for(StructureElement child : children) {
				sb.append(child.toString());
			}
			sb.append("}");
		}
		
		return sb.toString();
	}
	
	private String getKeywordString(Collection<String> list) {
		StringJoiner sj = new StringJoiner(";");
		
		list.forEach(item -> sj.add(item));
		
		return sj.toString();
	}
}
