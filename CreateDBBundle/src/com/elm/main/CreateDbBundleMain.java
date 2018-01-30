package com.elm.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.elm.service.DbBundleService;
import com.elm.service.DbBundleServiceImpl;

public class CreateDbBundleMain {
	
	private static Properties properties;
	private static String dbBundleName;
	private static String applicationName;
	private static boolean hasETL;
	private static DbBundleService  bundleService;
	
	
	 static{
		properties = new Properties();
		try {
			properties.load(new FileInputStream("app.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbBundleName = properties.getProperty("DbBundleName");
		applicationName = properties.getProperty("applicationName");
		hasETL = properties.getProperty("hasETL").equals("yes");
		bundleService = new DbBundleServiceImpl();
	}
	
	
	public static void main(String[] args) throws IOException {
		
		bundleService.prepareFolders(applicationName, hasETL);
		bundleService.copyFilesFolders(applicationName,hasETL);
		bundleService.createZipFile(dbBundleName);
		// copy and place files
		// zip the files
		//System.out.println();
		//System.out.println(applicationName);
		//System.out.println(hasETL);
		//String str = "sdfsgsgsg.sql";
		//	System.out.println(str.substring(0, str.length()-4));
	}
}
