package com.specmate.cerecognition.sentence;

import java.util.ArrayList;

import com.specmate.cerecognition.pattern.StructureElement;

/**
 * 
 * @author Julian Frattini
 *
 * Superclass of nodes for the internal representation. Subclasses are nodes (for inner nodes)
 * and leafs (for leaf nodes), where the latter also contain information of the dependency parser.
 */

public abstract class Fragment {
	private Fragment parent;
	
	private String tag;
	private String coveredText;
	
	public Fragment(String tag, String coveredText) {
		this.tag = tag;
		this.coveredText = coveredText;
	}

	/**
	 * Gets the parenting fragment of the current fragment
	 * @return The parenting fragment, if it exists
	 */
	public Fragment getParent() {
		return parent;
	}
	
	/**
	 * Sets the parenting fragment
	 * @param parent The parent fragment of this fragment
	 */
	public void setParent(Fragment parent) {
		this.parent = parent;
	}

	/**
	 * Gets the type of this fragment, which is either the part-of-speech-tag (for leaf nodes)
	 * or the constituency tag (for inner nodes)
	 * @return Type of this fragment
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Sets the type of this fragment
	 * @param tag The new type of this fragment
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Gets the text covered by this fragment, which is either the word (for leaf nodes) or the
	 * combination of all words of the leaf nodes which are parented by this fragment (for inner nodes)
	 * @return The text covered by this fragment
	 */
	public String getCoveredText() {
		return coveredText;
	}

	/**
	 * Sets the covered text of this fragment
	 * @param coveredText Text that is be covered by this fragment
	 */
	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
	}
	
	/**
	 * Generates a sentence structure from a sentence by stripping it of leaf nodes
	 * @return The sentence structure disregarding leaf nodes
	 */
	public abstract StructureElement generateStructure();
	
	/**
	 * Returns all child nodes of the current fragment
	 * @return Child nodes of this fragment
	 */
	public abstract ArrayList<Fragment> getChildren();
	
	/**
	 * Checks, if the given fragment is either a direct or a transitive child of this fragment
	 * @param other The assumed child fragment
	 * @return True, if the other fragment is parented euther directly or transitively by this fragment
	 */
	public abstract boolean isParenting(Fragment other);
	
	/**
	 * Gathers all leaf nodes directly or transitively associated with this fragment
	 * @return All leaf nodes related to this fragment
	 */
	public abstract ArrayList<Leaf> getAllLeafs();
	
	/**
	 * Searches for a leaf node specified by its lexical index in the sentence. This allows for the most
	 * specific search for a token
	 * @param beginIndex Index, at what position the covered text of the leaf starts in the sentence
	 * @return The leaf node, where the covered text starts at position beginIndex
	 */
	public abstract Leaf getLeafByToken(int beginIndex);
	
	/**
	 * Splits the fragment and yields all child nodes of this fragment
	 * @return All child nodes of this fragment
	 */
	public abstract ArrayList<Fragment> split();
	
	/**
	 * Gathers all direct or transitive child nodes of the fragment that comply to the search criteria
	 * @param byType True, if the desired nodes shall be searched for by tag, false if by word
	 * @param indicator Either the tag or the word, which the nodes have to contain
	 * @param selected The (initially) empty list in which the results will be stored
	 * @return The selected-list filled with all child nodes that comply to the search criteria
	 */
	public abstract ArrayList<Fragment> getBy(boolean byType, String indicator, ArrayList<Fragment> selected);
	
	/**
	 * Gathers all leaf nodes of the fragment that comply to the search criteria
	 * @param byType True, if the desired nodes shall be searched for by tag, false if by word
	 * @param indicator Either the tag or the word, which the nodes have to contain
	 * @param selected The (initially) empty list in which the results will be stored
	 * @return The selected-list filled with all leaf nodes that comply to the search criteria
	 */
	public abstract ArrayList<Leaf> getLeafs(boolean byType, String indicator, ArrayList<Leaf> selected);
	
	/**
	 * Checks, whether one of the child nodes of this fragment parents the given node
	 * @param child The node, where the parent node is of interest
	 * @return The child node which parents the given node, if it does so
	 */
	public abstract Fragment getParentOf(Fragment child);
	
	/**
	 * Checks, whether one of the child nodes of this fragment directly parents the given node
	 * @param child The node, where the parent node is of interest
	 * @return The child node which directly parents the given node, if it does so
	 */
	public abstract Fragment getDirectParentOf(Fragment child);
	
	/**
	 * Gathers all direct or transitive child nodes of the fragment that comply to the search criteria.
	 * This method only takes into account the first node of one branch which complies to the criteria and
	 * will disregard all following potential nodes of the branch.
	 * @param byType True, if the desired nodes shall be searched for by tag, false if by word
	 * @param indicator Either the tag or the word, which the nodes have to contain
	 * @param selected The (initially) empty list in which the results will be stored
	 * @return The selected-list filled with all child nodes that comply to the search criteria
	 */
	public abstract ArrayList<Fragment> select(boolean byType, String indicator, ArrayList<Fragment> selected);
	
	/**
	 * Checks whether either this node or any of its child nodes complies to the specified criteria
	 * @param byType True, if the desired nodes shall be searched for by tag, false if by word
	 * @param indicator Either the tag or the word, which the nodes have to contain
	 * @return True, if the specified node is contained within the branch spanned from this fragment
	 */
	public abstract boolean contains(boolean byType, String indicator);
	
	/**
	 * Checks whether either this node or any of its child nodes complies to the given fragment
	 * @param fragment The fragment, which is inspected to be contained
	 * @return True, if the specified node is contained within the branch spanned from this fragment
	 */
	public abstract boolean contains(Fragment fragment);
	
	/**
	 * Formats the fragment structure into human-readable output
	 * @param structurized True, if the syntactical structure (constituents) shall be displayed
	 * @param dependencies True, if the semantical structure (dependencies) shall be displayed
	 * @return The fragments content in human-readable form
	 */
	public abstract String toString(boolean structurized, boolean dependencies);
	
	/**
	 * Formats the fragment structure into human-readable output
	 * @param highlights List of words, which will be highlighted in the sentence output
	 * @return The fragments content in human-readable form
	 */
	public abstract String toString(ArrayList<Fragment> highlights);
	
	/**
	 * Formats the fragments structure disregarding leaf nodes into human-readable output
	 * @return The sentence structure of the fragment
	 */
	public abstract String structureToString();
	
}
