package com.specmate.cerecognition.util;

public class Configuration {
	
	// True if the system shall be trained from the beginning on
	public static final boolean AUTO_TRAIN = true;
	public static final String[] TRAINING_FILES = {
			"database/ceexamples.json",
			"database/ceexamples2.json",
			};
	public static final String[] TESTING_FILES = {
			"database/cee_ahe.json"
			};
	public static final String TRAINING_FILE_SPECIAL =
			"database/ceexamples_reduced.json";
	
	// Logger attributes
	public static int CURRENT_LOG_LEVEL = 0;
	public static int LOG_LEVEL_INFO = 20;
	public static int LOG_LEVEL_WARN = 50;
	public static int LOG_LEVEL_ERROR = 80;
	public static final String LOG_PREFIX = "CEREC";
}
