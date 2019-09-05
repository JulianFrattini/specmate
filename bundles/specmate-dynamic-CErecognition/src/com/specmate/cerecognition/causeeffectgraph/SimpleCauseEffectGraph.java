package com.specmate.cerecognition.causeeffectgraph;

import com.specmate.cerecognition.util.CELogger;

public class SimpleCauseEffectGraph implements ICauseEffectGraph {

	private String cause;
	private String effect;
	
	public SimpleCauseEffectGraph(String cause, String effect) {
		this.cause = cause;
		this.effect = effect;
	}
	
	public String getCause() {
		return cause;
	}

	public String getEffect() {
		return effect;
	}
	
	public String getCausePrepared() {
		return prepareExpression(cause);
	}

	public String getEffectPrepared() {
		return prepareExpression(effect);
	}
	
	private String prepareExpression(String expression) {
		String result = expression;
		
		if(result.contains("n't")) {
			System.out.println("Expression '" + result + "' contained n't");
			result = result.replaceAll("n't", " n't");
		}
		if(result.contains("'s")) {
			System.out.println("Expression '" + result + "' contained 's");
			result = result.replaceAll("'s", " 's");
		}
		
		return result;
	}

	@Override
	public boolean equals(ICauseEffectGraph other) {
		CELogger.log().info("Checking compliance between CEG's:");
		CELogger.log().info("  - generated: '" + getCause() + "' -> '" + getEffect() + "'");
		CELogger.log().info("  - given: '" + other.getCausePrepared() + "' -> '" + other.getCausePrepared() + "'");
		
		
		if(!getCause().equals(other.getCausePrepared())) {
			CELogger.log().warn("ERROR: The causes do not align:");
			CELogger.log().warn("\t- " + getCause());
			CELogger.log().warn("\t- " + other.getCausePrepared());
			return false;
		}
		if(!getEffect().equals(other.getEffectPrepared())) {
			CELogger.log().warn("ERROR: The effects do not align:");
			CELogger.log().warn("\t- " + getEffect());
			CELogger.log().warn("\t- " + other.getEffectPrepared());
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return cause + " -> " + effect;
	}
}
