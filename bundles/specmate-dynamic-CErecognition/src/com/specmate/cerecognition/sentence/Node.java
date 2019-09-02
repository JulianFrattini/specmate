package com.specmate.cerecognition.sentence;

import java.util.ArrayList;
import java.util.StringJoiner;

import com.specmate.cerecognition.pattern.StructureElement;

public class Node extends Fragment {
	
	private ArrayList<Fragment> children;

	public Node(String tag, String coveredText) {
		super(tag, coveredText);
		children = new ArrayList<Fragment>();
	}

	public void addChild(Fragment child) {
		child.setParent(this);
		children.add(child);
	}

	public ArrayList<Fragment> getChildren() {
		return children;
	}

	/**
	 * Returns only those children that have children themselves (meaning that they are relevant for the structure)
	 * @return
	 */
	public ArrayList<Fragment> getParentingChildren() {
		ArrayList<Fragment> parenting = new ArrayList<Fragment>();
		
		for(Fragment child : children) {
			if(child.getChildren() != null) {
				parenting.add(child);
			}
		}
		
		return parenting;
	}

	public ArrayList<Leaf> getAllLeafs() {
		ArrayList<Leaf> result = new ArrayList<Leaf>();
		
		for(Fragment child : children) {
			result.addAll(child.getAllLeafs());
		}
		
		return result;
	}
	
	// Optimize by index
	public Leaf getLeafByToken(int beginIndex) {
		for(Fragment child : children) {
			Leaf result = child.getLeafByToken(beginIndex);
			if(result != null) {
				return result;
			}
		}
		return null;
	}
	
	@Override
	public boolean isParenting(Fragment other) {
		for(Fragment child : children) {
			if(child.equals(other)) {
				return true;
			}
			
			if(child.isParenting(other)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public StructureElement generateStructure() {
		StructureElement structure = new StructureElement(super.getTag());
		for(Fragment child : children) {
			StructureElement childStructure = child.generateStructure();
			if(childStructure != null) 
				structure.addChild(childStructure);
		}
		return structure;
	}
	
	@Override
	public ArrayList<Fragment> split() {
		return children;
	}
	
	@Override 
	public ArrayList<Fragment> getBy(boolean byType, String indicator, ArrayList<Fragment> selected) {
		if(byType && super.getTag().equals(indicator)) {
			selected.add(this);
		} else if(!byType && toString().equals(indicator)) {
			selected.add(this);
			//return selected;
		}
		
		for(Fragment child : children) {
			child.getBy(byType, indicator, selected);
		}
		
		return selected;
	}
	
	@Override 
	public ArrayList<Leaf> getLeafs(boolean byType, String indicator, ArrayList<Leaf> selected) {
		for(Fragment child : children) {
			child.getLeafs(byType, indicator, selected);
		}
		
		return selected;
	}
	
	@Override
	public ArrayList<Fragment> select(boolean byType, String indicator, ArrayList<Fragment> selected) {
		if(byType && super.getTag().equals(indicator)) {
			selected.add(this);
			return selected;
		} else if(!byType && toString().equals(indicator)) {
			selected.add(this);
			return selected;
		}
		
		for(Fragment child : children) {
			child.select(byType, indicator, selected);
		}
		
		return selected;
	}
	
	@Override
	public Fragment getParentOf(Fragment child) { 
		for(Fragment own_child : children) {
			if(own_child.contains(child)) {
				return own_child;
			}
		}
		return null;
	}
	
	@Override
	public Fragment getDirectParentOf(Fragment child) {
		for(Fragment own_child : children) {
			for(Fragment grandchild : own_child.getChildren()) {
				if(grandchild.equals(child)) {
					return own_child;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public boolean contains(boolean byType, String indicator) {
		if(byType && super.getTag().equals(indicator)) {
			return true;
		} else if(!byType && super.getCoveredText().equals(indicator)) {
			return true;
		}
		
		for(Fragment child : children) {
			if(child.contains(byType, indicator)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean contains(Fragment fragment) {
		if(this.equals(fragment)) {
			return true;
		}
		
		for(Fragment child : children) {
			if(child.contains(fragment)) {
				return true;
			}
		}
		return false;
	}

	public boolean equals(Fragment other) {
		if(other instanceof Node) {
			Node oNode = (Node) other;
			if(super.getTag().equals(other.getTag()) && 
					super.getCoveredText().equals(other.getCoveredText())) {
				// negative-checks if children are equals
				if(children.size() != oNode.getChildren().size())
					return false;
				for(int i = 0; i < children.size(); i++) {
					Fragment ownChild = children.get(i);
					Fragment otherChild = oNode.getChildren().get(i);
					if(!ownChild.equals(otherChild))
						return false;
				}
				// if all negative-checks pass, it must be equal
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.getCoveredText();
	}
	
	@Override
	public String toString(boolean structurized, boolean dependencies) {
		if(structurized) {
			StringJoiner sj = new StringJoiner("");
			sj.add("(" + super.getTag() + "){");
			children.forEach(c -> sj.add(c.toString(true, dependencies)));
			sj.add("}");
			return sj.toString();
		} else {
			StringJoiner sj = new StringJoiner(" ");
			children.forEach(c -> sj.add(c.toString(false, dependencies)));
			return sj.toString();
		}
	}
	
	@Override
	public String toString(ArrayList<Fragment> highlights) {
		if(highlights.contains(this)) {
			StringJoiner sj = new StringJoiner(" ");
			children.forEach(c -> sj.add("*" + c.toString(highlights) + "*"));
			return sj.toString();
		} else {
			StringJoiner sj = new StringJoiner(" ");
			children.forEach(c -> sj.add(c.toString(highlights)));
			return sj.toString();
		}
	}
	
	@Override
	public String structureToString() {
		String result = "(" + super.getTag() + ")";

		StringJoiner sj = new StringJoiner(" ");
		for(Fragment child : children) {
			String childStructure = child.structureToString();
			if(childStructure != null) {
				sj.add(childStructure);
			}
		}
		
		String childStructure = sj.toString();
		if(!childStructure.isEmpty()) {
			result = result + " (" + childStructure + ")";
		}
		
		return result;
	}
}
