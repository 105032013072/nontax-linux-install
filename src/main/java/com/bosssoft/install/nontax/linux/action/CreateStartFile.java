package com.bosssoft.install.nontax.linux.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

public class CreateStartFile implements IAction{
    
	transient Logger logger=Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		String environments=params.get("environments").toString();
		String servers=params.get("servers").toString();
		String shFile=params.get("targetDir").toString()+File.separator+"start.sh";
		String accredits=params.get("accredit").toString();
		createShFile(shFile,environments,servers,accredits,context,params);
	
	}


	private void createShFile(String shFile, String environments, String servers,String accredits, IContext context,Map params) {
         File f=new File(shFile);
		 String installDir=context.getStringValue("INSTALL_DIR");
         
        try {
        	//如果文件存在就删除，重建
    		if(f.exists()) f.delete();
    		f.createNewFile();
    		
        	BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,true)));
        	StringBuffer strBuffer=new StringBuffer("#!/bin/bash").append(System.getProperty("line.separator"));
        	//设置环境变量
        	String[] es=environments.split(",");
        	for (String en : es) {
        		String path=context.getStringValue(en);
        		strBuffer.append(en+"="+path+System.getProperty("line.separator"));
    		}
        	//授权
        	String[] as=accredits.split(",");
           for (String accredit : as) {
		    	
		    	String path=context.getStringValue(accredit).replace(installDir, ".");
		        if(path.contains(" ")){
		        	path=path.substring(0, path.indexOf(" "));
		        }
		    	strBuffer.append("chmod 755 "+path);
		    	strBuffer.append(System.getProperty("line.separator"));
			}
        	
        	
        	//设置启动的服务
        	String[] ss=servers.split(",");
        	String[] swd=((String)params.get("serverWordDir")).split(",");
		    for (int i=0;i<ss.length;i++) {
		      
		    	String server=ss[i];
		    	String folderpath=swd[i];
		    	String serverpath=context.getStringValue(server);
		
		    	String serverName=serverpath.replaceAll(folderpath, ".");
		    	
		        
		    	strBuffer.append("cd "+folderpath+System.getProperty("line.separator"));
		    	strBuffer.append("exec "+serverName+" &");
		    	strBuffer.append(System.getProperty("line.separator"));
			}
		    out.write(strBuffer.toString());
		    out.close();
        	
        } catch (Exception e) {
        	this.logger.error(e);
			throw new InstallException("Faild to create start File",e);
		} 
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
