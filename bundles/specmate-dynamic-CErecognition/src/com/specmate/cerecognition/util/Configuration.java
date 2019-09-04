package com.specmate.cerecognition.util;

public class Configuration {
	
	// True if the system shall be trained from the beginning on
	public static final boolean AUTO_TRAIN = true;
	public static final String[] TRAINING_FILES = {
			"database/causeeffect/training/cee_jfr_weather.json",
			"database/causeeffect/training/cee_jfr_nfl.json",
			"database/causeeffect/training/cee_jfr_gov.json",
			"database/causeeffect/training/cee_jfr_dnd.json",
			"database/causeeffect/training/cee_jfr_pc.json",
			"database/causeeffect/training/cee_jfr_sw.json",
			"database/causeeffect/training/cee_jfr_re.json"
			//"database/causeeffect/ceexamples.json",
			//"database/causeeffect/ceexamples2.json",
			};
	public static final String[] TESTING_FILES = {
			"database/causeeffect/testing/original/cee_afr.json",
			"database/causeeffect/testing/original/cee_ahe.json",
			"database/causeeffect/testing/original/cee_aob.json",
			"database/causeeffect/testing/original/cee_asp.json",
			"database/causeeffect/testing/original/cee_lod.json",
			"database/causeeffect/testing/original/cee_mbi.json",
			"database/causeeffect/testing/original/cee_mfr.json",
			"database/causeeffect/testing/original/cee_mse.json",
			"database/causeeffect/testing/original/cee_nfr.json"
			};
	public static final String[] TESTING_FILES_PURE = {
			"database/causeeffect/testing/purified/cee_afr.json",
			"database/causeeffect/testing/purified/cee_ahe.json",
			"database/causeeffect/testing/purified/cee_aob.json",
			"database/causeeffect/testing/purified/cee_asp.json",
			"database/causeeffect/testing/purified/cee_lod.json",
			"database/causeeffect/testing/purified/cee_mbi.json",
			"database/causeeffect/testing/purified/cee_mfr.json",
			"database/causeeffect/testing/purified/cee_mse.json",
			"database/causeeffect/testing/purified/cee_nfr.json"
			};
	public static final String TRAINING_FILE_SPECIAL =
			"database/causeeffect/ceexamples_reduced.json";
	
	// Logger attributes
	public static int CURRENT_LOG_LEVEL = 0;
	public static int LOG_LEVEL_INFO = 20;
	public static int LOG_LEVEL_WARN = 50;
	public static int LOG_LEVEL_ERROR = 80;
	public static final String LOG_PREFIX = "CEREC";
}
