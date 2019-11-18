package com.specmate.cerecognition.util;

public class Configuration {
	
	/**
	 * True if the system shall be trained from the beginning on with all avaliable training files
	 */
	public static final boolean AUTO_TRAIN = true;
	
	/**
	 * List of paths to all training files
	 */
	public static final String[] TRAINING_FILES = {
			"database/causeeffect/training/cee_jfr_weather.json",
			"database/causeeffect/training/cee_jfr_nfl.json",
			"database/causeeffect/training/cee_jfr_gov.json",
			"database/causeeffect/training/cee_jfr_dnd.json",
			"database/causeeffect/training/cee_jfr_pc.json",
			"database/causeeffect/training/cee_jfr_sw.json",
			"database/causeeffect/training/cee_jfr_re.json",
			"database/causeeffect/training/cee_jfr_colloquial.json"
			};
	
	/**
	 * Path to the original testing files
	 */
	public static final String TESTING_PATH_ORIGINAL_= "database/causeeffect/testing/original/";
	
	/**
	 * Path to the corrected testing files, where invalid causality examples have been corrected
	 */
	public static final String TESTING_PATH_PURE = "database/causeeffect/testing/purified/";
	
	/**
	 * List of file names of all testing files
	 */
	public static final String[] TESTING_FILES = {
			"cee_afr.json",
			"cee_ahe.json",
			"cee_aob.json",
			"cee_asp.json",
			"cee_lod.json",
			"cee_mbi.json",
			"cee_mfr.json",
			"cee_mse.json",
			"cee_nfr.json"
			};
	
	/**
	 * Log levels, where the current log level has to be lower then the log levels of info, warn, and
	 * error in order for them to be shown. Log level 0 will permit all log messages to be shown
	 */
	public static int CURRENT_LOG_LEVEL = 0;
	public static int LOG_LEVEL_INFO = 20;
	public static int LOG_LEVEL_WARN = 50;
	public static int LOG_LEVEL_ERROR = 80;
	
	/**
	 * String prefix before all log messages of the CELogger
	 */
	public static final String LOG_PREFIX = "CEREC";
}
