package com.elm.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.elm.util.AppZip;

public class DbBundleServiceImpl implements DbBundleService {
	
	private static final String SOURCE_DB_SCRIPTS_PATH = "Code/DBScripts";
	private static final String SOURCE_ETL_PATH = "Code/ETL";
	private static final String OUTPUT_PATH = "Release/";
	private static final String RESOURCE_PATH = "Resources/";
	
	private AppZip zipper = new AppZip();
	

	@Override
	public int prepareFolders(String applicationName, boolean hasEtl) throws IOException {
		int returnVal=0;
		File outputFolder = new File(OUTPUT_PATH);
		
		File etlFolder = new File(OUTPUT_PATH+"/ETL");
		File dbScriptsFolder = new File(OUTPUT_PATH+"/"+applicationName+"/DBScripts");
		File dbScriptsOutputFolder =  new File(OUTPUT_PATH+"/"+applicationName+"/DBScripts_Output");
		
		System.out.println("Cleaning Release folder...");
		for(File file : outputFolder.listFiles()){
			if(file.isDirectory())
				FileUtils.deleteDirectory(file);
			else
				file.delete();
		}
		
		System.out.println("Preparing Release folder...");
		return returnVal;
	}

	
	

	@Override
	public int createZipFile(String dbBundleName) {
		
		System.out.println("Preparing DB Bundle zip...");
		zipper.zipFolder(dbBundleName + ".zip",new File(OUTPUT_PATH).getAbsolutePath());
		
		return 0;
	}




	@Override
	public int copyFilesFolders(String applicationName,boolean hasEtl) throws IOException {
		if(hasEtl){
			System.out.println("Copying ETL code...");
			File source = new File(SOURCE_ETL_PATH);
			File dest = new File(OUTPUT_PATH);
			FileUtils.copyDirectoryToDirectory(source, dest);
			
		}
		
		System.out.println("Copying DB code...");
		File source = new File(SOURCE_DB_SCRIPTS_PATH);
		File dest = new File(OUTPUT_PATH+"/"+applicationName);
		FileUtils.copyDirectoryToDirectory(source, dest);
		
		System.out.println("Copying Utility code...");
		source = new File(RESOURCE_PATH+"UpgradeDBSchema.cmd");
		dest = new File(OUTPUT_PATH+"/"+applicationName);
		FileUtils.copyFileToDirectory(source, dest);
		
		source = new File(RESOURCE_PATH+"VERIFY_PREREQUISITE.sql");
		dest = new File(OUTPUT_PATH+"/"+applicationName+"/DBScripts");
		FileUtils.copyFileToDirectory(source, dest);
		
		source = new File(RESOURCE_PATH+"INSERT_VERSION.sql");
		dest = new File(OUTPUT_PATH+"/"+applicationName+"/DBScripts");
		FileUtils.copyFileToDirectory(source, dest);
		
		System.out.println("Preparing SQLShell code...");
		prepareSqlCmdCode(applicationName);
		
		return 0;
	}




	private void prepareSqlCmdCode(String applicationName) throws IOException {
		File sqlCmd = new File(OUTPUT_PATH+applicationName+"/SQLShell.cmd");
		sqlCmd.createNewFile();
		FileOutputStream fos = new FileOutputStream(sqlCmd);
		
		fos.write("@echo off".getBytes());
		fos.write("\n".getBytes());
		
		fos.write("set SSP_CORE_DB_SERVER=%1".getBytes());
		fos.write("\n".getBytes());
		
		fos.write("set SSP_CORE_DB_NAME=%2".getBytes());
		fos.write("\n".getBytes());
		
		fos.write("set SSP_PORTAL_DB_SERVER=%3".getBytes());
		fos.write("\n".getBytes());
		
		fos.write("set SSP_PORTAL_DB_NAME=%4".getBytes());
		fos.write("\n".getBytes());
		
		fos.write("set SSP_ARCHIVE_DB_SERVER=%5".getBytes());
		fos.write("\n".getBytes());
		
		fos.write("set SSP_ARCHIVE_DB_NAME=%6".getBytes());
		fos.write("\n".getBytes());
		fos.write("\n".getBytes());
		fos.write("\n".getBytes());
		
		File dbScripts = new File(SOURCE_DB_SCRIPTS_PATH);
		
		List<String> fileList = Arrays.asList(dbScripts.list());
		Collections.sort(fileList);
		for(String fileName:fileList){
			if(!"INSERT_VERSION.sql".equals(fileName) && !"VERIFY_PREREQUISITE.sql".equals(fileName) )
			{
				String fileNameWithoutExtn = fileName.substring(0, fileName.length()-4);
				String serverName = "SSP_CORE_DB_SERVER";
				String dbName = "SSP_CORE_DB_NAME";
				
				if(fileName.contains("(PORTAL)")){
					serverName = "SSP_PORTAL_DB_SERVER";
					dbName = "SSP_PORTAL_DB_NAME";
				}
				if(fileName.contains("(ARCHIVE)")){
					serverName = "SSP_ARCHIVE_DB_SERVER";
					dbName = "SSP_ARCHIVE_DB_NAME";
				}
				
				fos.write("echo.".getBytes());
				fos.write("\n".getBytes());
				
				fos.write(("echo Executing script "+fileName+"...").getBytes());
				fos.write("\n".getBytes());
				
				fos.write(("sqlcmd -E -S %"+serverName+"% -d %"+dbName+"% -V 10 -i \"%INSTALL_HOME%/DBScripts/"+fileName+"\" -o \"%INSTALL_HOME%/DBScripts_Output/"+fileNameWithoutExtn+".log\" -I").getBytes());
				fos.write("\n".getBytes());
				
				fos.write(("if not %ERRORLEVEL% == 0 echo Error occured during  execution of "+fileName+" ! Please check installation logs in \"..\\DBScripts_Output\\"+fileNameWithoutExtn+".log\".").getBytes());
				fos.write("\n".getBytes());
				
				fos.write("\n".getBytes());
				fos.write("\n".getBytes());
			}
		}
		
		fos.write("\n".getBytes());
		fos.write("\n".getBytes());
	}

}
