package com.elm.service;

import java.io.IOException;

public interface DbBundleService {
	
	int prepareFolders(String applicationName,boolean hasEtl) throws IOException; 
	

	int createZipFile(String dbBundleName);


	int copyFilesFolders(String applicationName, boolean hasEtl) throws IOException;
}

