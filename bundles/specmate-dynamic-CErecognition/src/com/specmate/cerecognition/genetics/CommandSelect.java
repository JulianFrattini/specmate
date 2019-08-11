package com.specmate.cerecognition.genetics;

import java.util.ArrayList;
import java.util.StringJoiner;

import com.specmate.cerecognition.sentence.Fragment;

public class CommandSelect extends SimpleCommand {
	
	private boolean byType;
	private String indicator;
	
	/*
	 * Sometimes the content of a CEElement (cause/effect) are not all grouped in
	 * one exclusive branch of the constituency-tree. In this case, a governing parent
	 * leaf is chosen which has a reference on all other leaf nodes relevant for the
	 * CEElement.
	 */
	private ArrayList<CommandPick> horizontalSelection;
	/*
	 * The positionOfSelectedBetweenHorizontalSelection is the index, at which position
	 * the governing parent leaf is located between its governed references
	 */
	private int positionOfSelectedBetweenHorizontalSelection;

	public CommandSelect(boolean byType, String indicator) {
		super();
		this.byType = byType;
		this.indicator = indicator;
		
		horizontalSelection = new ArrayList<CommandPick>();
	}
	
	public void addHorizontalSelection(CommandPick picker) {
		horizontalSelection.add(picker);
	}
	
	public void setPositionOfSelectedBetweenHorizontalSelection(int pos) {
		positionOfSelectedBetweenHorizontalSelection = pos;
	}

	@Override
	public String generateOutput(Fragment fragment) throws IllegalArgumentException, Exception {
		ArrayList<Fragment> selected = new ArrayList<Fragment>();
		fragment.select(byType, indicator, selected);

		if(selected.size() == 1) {
			Fragment selection = selected.get(0);
			
			if(successor == null) {
				if(horizontalSelection.isEmpty()) {
					return selection.getCoveredText();
				} else {
					// horizontal selection is active and CommandPick's have to be resolved
					
					String[] result = new String[horizontalSelection.size()+1];
					
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
						sj.add(s);
					}
					return sj.toString();
				}
			} else {
				successor.generateOutput(selection);
			}
		} else if(selected.size() == 0) {
			System.out.println("ERROR: CommandSelect did not identify an eligible result");
			System.out.println("  Sentence under test: " + fragment.toString(false, false));
			System.out.println("  Checking for " + (byType ? "type" : "word") + " " + indicator + " yielded no result");
		} else if(selected.size() > 1) {
			System.out.println("ERROR: CommandSelect did identify too many eligible results");
			System.out.println("  Sentence under test: " + fragment.toString(true, false));
			System.out.println("  Checking for " + (byType ? "type" : "word") + " " + indicator + " yielded the following result");
			for(Fragment s : selected) {
				System.out.println("   - " + s.toString(true, false));
			}
		} else {
			System.out.println("ERROR: CommandSelect yielded an unknown selection error");
		}
		
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("select " + (byType ? "type" : "word") + " " + indicator);
		
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

	@Override
	public CommandSelect getFinal() {
		if(successor == null) {
			return this; 
		} else {
			return successor.getFinal();
		}
	}
}
