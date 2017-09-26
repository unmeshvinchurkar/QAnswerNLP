package com.search;

import java.util.HashSet;
import java.util.Set;

public class NdSearchConstants {
	
	/* @localization off */
	
	
	// DMP constants
	
	public static final String DMP_DATA_DIR = "DMP_DATA_DIR";
	public static final String DMP_INF = "DMP-INF";
	public static final String DMP_SEARCH_INDEX_DIR = "search_index";
	public static final String DMP_DIR_VARIABLE = "%DMP_DATA_DIR%";
	
	// Handler parameter
	public static final String PARAM_QUERY_STR = "queryStr";
	
	// Handler commands
	public static final String SEARCH_COMMAND ="search";
	public static final String REPOSITORY_SUPPORTED_COMMAND ="repositorySupportedCommand";
	
	// Json properties
	public static final String JSON_TYPE_INT = "typeInt";
	public static final String JSON_SUB_TYPE_INT = "subTypeInt";
	public static final String JSON_CONTENT_TYPE_INT = "contentTypeInt";
	public static final String JSON_RESULT = "result";
	
	
	// Type or subType values	
	public static final String BLAZE_TABLE_TYPE_VALUE ="decision table";
	public static final String BLAZE_TREE_TYPE_VALUE ="tree";
	public static final String BLAZE_SCORE_MODEL_TYPE_VALUE ="score model";
	public static final String BLAZE_MINING_MODEL_TYPE_VALUE ="pmml mining model";
	public static final String BLAZE_BUSINESS_TERM_TYPE_VALUE = "business term set";
	public static final String BLAZE_RULEFLOW_TYPE_VALUE = "ruleflow";
	public static final String BLAZE_QUERY_TYPE_VALUE = "query";
	public static final String BLAZE_SAS_PROGRAM_VALUE = "sas program";
	public static final String BLAZE_SUBYPE_NONE = "none";
	
	public static final String TABLE_TYPE_VALUE ="decision table";
	public static final String TREE_TYPE_VALUE ="decision tree";
	public static final String SCORE_MODEL_TYPE_VALUE ="scorecard";
	public static final String BUSINESS_TERM_TYPE_VALUE = "business term set";
	public static final String RULEFLOW_TYPE_VALUE = "decision flow";
	public static final String SEARCH_QUERY_TYPE_VALUE = "search";
	
	
	// Index document keys and values
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DISPLAY_NAME = "displayname";
	public static final String PATH = "path";
	public static final String DATE = "date";
	public static final String GUID = "guid";
	public static final String TYPE = "type";
	public static final String TYPE_BLAZE = "typeblaze";
	public static final String SUB_TYPE_BLAZE = "subtype";
	public static final String CONTENT = "content";
	public static final String CONTENT_TYPE_INSTANCE = "instance";
	public static final String OWNER = "owner";
	public static final String IS_NEW = "isnew";
	public static final String DISPLAY_PATH = "displaypath";
	public static final String VERSION_ID = "versionid";
	public static final String IS_LOCALLY_DELETED = "islocallydeleted";
	public static final String IS_DELETED_SUBINSTANCE = "isdeletedsubinstance";
	public static final String DEFAULT_OWNER = "SystemOwner";

	
	public static final String INNOVATOR_ATTRIB_FILE_POST_FIX = ".innovator_attbs";
	public static final String DELETED_INSTANCE_DIRECTORY_NAME = "com_blazesoft_deleted_folder";
	public static final String SSS_FOLDER_PREFIX = /* NOI18N */"__Items for__";
	public static final String SYSTEM_DIR = /* NOI18N */"system";

	// Inoovator Attr file properties
	
	public static final String IA_TYPE = "type";
	public static final String IA_SUB_TYPE = "subType";
	public static final String IA_CONTENT_TYPE = "contentType";
	public static final String IA_CHECKED_OUT_USER = "com.blazesoft.repository.local.checkedOut";
	public static final String IA_LAST_CHECKEDIN_USER = "entryLastCheckInUser";
	public static final String IA_DISPLAY_NAME = "innovatorDisplayName";
	public static final String IA_LOCALLY_CREATED = "com.blazesoft.repository.local.locallycreated";
	public static final String IA_MGMT_PROP_PREFIX ="managementProperty.";
	public static final String IA_VERSION_ID = "innovatorItemVersionId";
	public static final String IA_GUID = "guid";
	public static final String IA_NAME = "innovatorName";
	public static final String IA_CREATED_BY = "innovatorItemCreationId";
	public static final String IA_MODIFIED_BY = "innovatorLastModifiedUser";
	
	public static  Set<String> STORED_FIELDS = new HashSet<String>();
	
	static{
		STORED_FIELDS.add(ID);
		STORED_FIELDS.add(NAME);
		STORED_FIELDS.add(PATH);
		STORED_FIELDS.add(DISPLAY_NAME);
		STORED_FIELDS.add(CONTENT);
		STORED_FIELDS.add(TYPE_BLAZE);
		STORED_FIELDS.add(SUB_TYPE_BLAZE);
		STORED_FIELDS.add(TYPE);
		STORED_FIELDS.add(IA_CONTENT_TYPE);
		STORED_FIELDS.add(GUID);
		STORED_FIELDS.add(OWNER);
		STORED_FIELDS.add(IS_NEW);
		STORED_FIELDS.add(IS_LOCALLY_DELETED);
		STORED_FIELDS.add(DISPLAY_PATH);
	}
	
	// Types of search supported
	public static final String ELASTIC_SEARCH = "ELASTIC_SEARCH";
	public static final String LUCENE_DISK_INDEX = "LUCENE_DISK_INDEX";
	public static final String LUCENE_IN_MEMORY_INDEX = "LUCENE_IN_MEMORY_INDEX";
	

	// Types of repositories supported
	public static final int REPOSITORY_TYPE_FILE = 1;
	public static final int REPOSITORY_TYPE_MONGODB = 7;
	public static final String INDEX_DIR = "indexDirectory";
	
	
	/* @localization on */
}
