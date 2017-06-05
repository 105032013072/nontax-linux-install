package com.bosssoft.install.nontax.linux.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;

import com.bosssoft.install.nontax.linux.util.InstallUtil;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.option.ModuleDef;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatEnv;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatServerImpl;

public class CreateUninstallFile implements IAction{
	
	transient Logger logger=Logger.getLogger(getClass());

	public void execute(IContext context, Map params) throws InstallException {
		
			String createFile=params.get("targetDir").toString()+File.separator+"uninstall.sh";
			createShFile(createFile,context);
	}

	

	private void createShFile(String createFile, IContext context) {
		File f=new File(createFile);
		try {
			
        	//如果文件存在就删除，重建
    		if(f.exists()) f.delete();
    		f.createNewFile();
    		
        	BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,true)));
        	StringBuffer strBuffer=new StringBuffer("#!/bin/bash").append(System.getProperty("line.separator"));
        	

        	//删除安装目录下的文件
        	String installPath=context.getStringValue("INSTALL_DIR");
        	strBuffer.append("rm -rf "+installPath).append(System.getProperty("line.separator"));	
        	
        	
        	String appsvrType = context.getStringValue("APP_SERVER_TYPE");
        	List<ModuleDef> optionsCompList=(List<ModuleDef>) context.getValue("MODULE_OPTIONS");
        	for (ModuleDef moduleDef : optionsCompList) {
        		//删除应用服务器下组件程序
        		appSvrDir(appsvrType,moduleDef,strBuffer,context);
        		
        		//删除boss_home下的文件
        		bossHomeDir(moduleDef,strBuffer,context);
			}
        	
        	
        	out.write(strBuffer.toString());
		    out.close();
        	
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void appSvrDir(String appsvrType, ModuleDef moduleDef, StringBuffer strbuffer, IContext context) {
		String delpath=null;
		if(appsvrType.toLowerCase().indexOf("tomcat")!=-1){
            delpath=context.getStringValue("AS_TOMCAT_HOME")+File.separator+"webapps"+File.separator+moduleDef.getNameKey();
            
		}else if(appsvrType.toLowerCase().indexOf("weblogic")!=-1){
			//删除autodeploy下的工程文件
			delpath=context.getStringValue("AS_WL_DOMAIN_HOME")+File.separator+"autodeploy"+File.separator+moduleDef.getNameKey();
		}
        if(!new File(delpath).exists()) return;

	     strbuffer.append("rm -rf "+delpath+System.getProperty("line.separator"));
	}

	private void bossHomeDir(ModuleDef moduleDef, StringBuffer strBuffer, IContext context) {
		String delPath=context.getStringValue("BOSSSOFT_HOME")+File.separator+moduleDef.getNameKey();
		if(!new File(delPath).exists()) return;
			strBuffer.append("rm -rf "+delPath+System.getProperty("line.separator"));
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}
    
	
}
