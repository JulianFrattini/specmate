package com.specmate.cerecognition.genetics;

import java.util.ArrayList;
import java.util.StringJoiner;

import com.specmate.cerecognition.sentence.Fragment;
import com.specmate.cerecognition.util.CELogger;

/**
 * 
 * @author Julian Frattini
 * 
 * Command for vertical selection. Given a sentence in the form of a tree of syntactical nodes
 * select one of a specific type/content.
 */

public class CommandSelect extends SimpleCommand {
	
	/**
	 * Simple selection of the current node;
	 */
	private boolean take;
	
	/**
	 * Simple selection byType of the tag / word indicator
	 */
	private boolean byType;
	private String indicator;
	
	/**
	 * Sometimes a fragment tree contains multiple nodes with identical attributes
	 * (same text and type). In order to identify which fragment to chose, the selector
	 * can be extended with an index.
	 */
	private int index;
	
	/**
	 * Sometimes the content of a CEElement (cause/effect) are not all grouped in
	 * one exclusive branch of the constituency-tree. In this case, a governing parent
	 * leaf is chosen which has a reference on all other leaf nodes relevant for the
	 * CEElement.
	 */
	private ArrayList<CommandPick> horizontalSelection;
	
	/**
	 * The positionOfSelectedBetweenHorizontalSelection is the index, at which position
	 * the governing parent leaf is located between its governed references
	 */
	private int positionOfSelectedBetweenHorizontalSelection;

	public CommandSelect() {
		super();
		take = true;
		index = 0;
		
		horizontalSelection = new ArrayList<CommandPick>();
	}
	
	public CommandSelect(boolean byType, String indicator) {
		super();
		this.byType = byType;
		this.indicator = indicator;
		index = 0;
		
		horizontalSelection = new ArrayList<CommandPick>();
	}
	
	public CommandSelect(boolean byType, String indicator, int index) {
		super();
		this.byType = byType;
		this.indicator = indicator;
		this.index = index;
		
		horizontalSelection = new ArrayList<CommandPick>();
	}
	
	public void addHorizontalSelection(CommandPick picker) {
		horizontalSelection.add(picker);
	}
	
	public void setPositionOfSelectedBetweenHorizontalSelection(int pos) {
		positionOfSelectedBetweenHorizontalSelection = pos;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateOutput(Fragment fragment) {
		Fragment selection = null;
		
		if(take) {
			// directly take the current fragment
			selection = fragment;
		} else {
			// select a certain fragment from the children of the current
			ArrayList<Fragment> selected = new ArrayList<Fragment>();
			fragment.select(byType, indicator, selected);
	
			if(selected.size() > 0) {
				selection = selected.get(index);
				
			// error case: either 0 or more than 1 nodes were applicable to the selection
			} else if(selected.size() == 0) {
				CELogger.log().warn("CommandSelect did not identify an eligible result");
				CELogger.log().warn("  Sentence under test: " + fragment.toString(false, false));
				CELogger.log().warn("  Checking for " + (byType ? "type" : "word") + " " + indicator + " yielded no result");
			} else {
				CELogger.log().warn("ERROR: CommandSelect yielded an unknown selection error");
			}
		}
		
		if(selection != null) {
			if(successor == null) {
				// construct the cause-/effect-expression
				return constructResult(selection);
			} else {
				// continue processing recursively
				return successor.generateOutput(selection);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Construct the cause-/effect-expression 
	 * @param selection fragment identified by this select command
	 * @return cause-/effect-expression
	 */
	private String constructResult(Fragment selection) {
		if(horizontalSelection.isEmpty()) {
			// no horizontal selection, simply use the text covered by the selected fragment node
			return selection.getCoveredText();
		} else {
			// horizontal selection is active and CommandPick's have to be resolved
			String[] result = new String[horizontalSelection.size()+1];
			
			// resolve all pick commands, order them in an array and place the content of the selected
			// fragment node in the according position
			for(int i = 0; i < result.length; i++) {
				if(i < positionOfSelectedBetweenHorizontalSelection) {
					result[i] = horizontalSelection.get(i).generateOutput(selection);
				} else if(i == positionOfSelectedBetweenHorizontalSelection) {
					result[i] = selection.getCoveredText();
				} else if(i > positionOfSelectedBetweenHorizontalSelection) {
					result[i] = horizontalSelection.get(i-1).generateOutput(selection);
				}
			}
			
			StringJoiner sj = new StringJoiner(" ");
			for(String s : result) {
				// ignore all empty picker-results
				if(!s.isEmpty()) 
					sj.add(s);
			}
			return sj.toString();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if(take) {
			sb.append("select");
		} else {
			sb.append("select " + (byType ? "type" : "word") + " " + indicator);
		}
		
		if(!horizontalSelection.isEmpty()) {
			StringJoiner sj = new StringJoiner(" & ");
			horizontalSelection.forEach(picker -> sj.add(picker.toString()));
			sb.append(" (" + sj.toString() + ")");
		}
		
		if(successor != null) {
			sb.append(" --> " + successor.toString());
		}
		
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommandSelect getFinal() {
		if(successor == null) {
			return this; 
		} else {
			return successor.getFinal();
		}
	}
}
