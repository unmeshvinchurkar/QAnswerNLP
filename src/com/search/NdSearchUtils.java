//
// $Id: NdSearchUtils.java 296376 2016-12-08 09:51:32Z UnmeshVinchurkar $
//

// This material is the confidential, unpublished property of 
// Fair Isaac Corporation. Receipt or possession of this material does not 
// convey rights to divulge, reproduce, use, or allow others to use it 
// without the specific written authorization of Fair Isaac Corporation and 
// use must conform strictly to the license agreement.
//
// Copyright (c) 2007-2016 Fair Isaac Corporation. All Rights Reserved.
//
package com.search;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
/**
 * This class contains utility methods.
 * 
 * @author UnmeshVinchurkar
 *
 */
public class NdSearchUtils {
	
	private static final String _REPOSITORY_FOLDER_ARG = /* NOI18N */"com_blazesoft_repository_repositoryfolder";
	
	private static Map _loggerCache = new HashMap();

	

	public static String convertTraceToStr(Exception e) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();

	}

	/**
	 * Returns relative path of parent instance for a given SSS.
	 * 
	 * @param path
	 * @return
	 */
	public static String getParentInstancePath(String path) {

		if (NdSearchUtils.isSSS(path)) {

			int folderIndex = path.indexOf(NdSearchConstants.SSS_FOLDER_PREFIX);
			String parentDir = path.substring(0, folderIndex);

			String parentName = path.substring(folderIndex + NdSearchConstants.SSS_FOLDER_PREFIX.length(),
					path.lastIndexOf(/* NOI18N */"/"));

			return parentDir + parentName;
		}
		return null;
	}

	/**
	 * Given a path, it finds out whether it is a SSS
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isSSS(String path) {
		return path.contains(NdSearchConstants.SSS_FOLDER_PREFIX);
	}

	/**
	 * Normalizes path by replacing all back slashes with forward slashes
	 * 
	 * @param path
	 * @return String
	 */
	public static String normalizePath(String path) {
		return path.replace(/* NOI18N */"\\", /* NOI18N */"/").replaceAll(/* NOI18N */"/{2,}", /* NOI18N */"/");
	}

	/**
	 * Extracts instance file name from its path
	 * 
	 * @param path
	 * @return String
	 */
	public static String getInstanceFileName(String path) {
		return path.substring(path.lastIndexOf(/* NOI18N */"/") + 1);
	}

	/**
	 * Returns instance parent directory path.
	 * 
	 * @param path
	 * @return
	 */
	public static String getInstanceParentDirectory(String path) {
		return path.substring(0, path.lastIndexOf(/* NOI18N */"/"));
	}

	/**
	 * Returns Absolute location of SSS directory.
	 * 
	 * @param path
	 * @param wsPath
	 * @return
	 */
	public static String getSSSDirectory(String path, String wsPath) {

		boolean isSSS = path.contains(NdSearchConstants.SSS_FOLDER_PREFIX);
		String sssDirPath = null;

		if (!isSSS) {
			String parentDir = getInstanceParentDirectory(path);

			if (parentDir.indexOf(NdSearchConstants.DELETED_INSTANCE_DIRECTORY_NAME) > -1) {
				parentDir = getInstanceParentDirectory(parentDir);
			}

			sssDirPath = parentDir + /* NOI18N */"/" + NdSearchConstants.SSS_FOLDER_PREFIX + getInstanceFileName(path);

			File sssDir = new File(wsPath + sssDirPath);
			if (!sssDir.exists()) {
				sssDirPath = null;
			}
		}

		return sssDirPath;
	}

}
