package com.bosssoft.install.nontax.linux.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bosssoft.install.nontax.linux.util.InstallUtil;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.option.CompDef;
import com.bosssoft.platform.installer.core.option.ComponentsDefHelper;
import com.bosssoft.platform.installer.core.option.ModuleDef;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatEnv;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicEnv;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicServerImpl;
import com.bosssoft.platform.installer.jee.server.internal.TargetModelImpl;

/**
 * 部署相关组件
 * @author Windows
 *
 */
public class DeployOptionCompsSilent implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		//读取组件信息
		try{
			List<ModuleDef> optionsCompList = ComponentsDefHelper.getOptionCompsDef();
			context.setValue("MODULE_OPTIONS", optionsCompList);
			for (ModuleDef moduleDef : optionsCompList) {
				if(moduleDef.getFilesPath().endsWith("zip")) deployZip(context,moduleDef);
				else if(moduleDef.getFilesPath().endsWith("war")) deployWar(context,moduleDef);
			}
		    if(params.get("INSTALL_FILES")!=null) install(context,params);
		}catch(Exception e){
           this.logger.error(e);
		}
		
		
	}

	private void deployWar(IContext context, ModuleDef moduleDef) throws IOException {
		String appsvrType = context.getStringValue("APP_SERVER_TYPE");
		if(appsvrType.toLowerCase().indexOf("tomcat")!=-1){
			tomcatDeploy(context, moduleDef);
			
		}else if(appsvrType.toLowerCase().indexOf("jboss")!=-1){
			
		}else if(appsvrType.toLowerCase().indexOf("weblogic")!=-1){
			weblogicDeploy(context, moduleDef);
		}
	}

	private void weblogicDeploy(IContext context, ModuleDef moduleDef) throws IOException {
		String beaHome=context.getStringValue("AS_WL_BEA_HOME");
		String weblogicHome=context.getStringValue("AS_WL_HOME");
		String domainHome=context.getStringValue("AS_WL_DOMAIN_HOME");
		String host=context.getStringValue("AS_WL_TARGET_SERVER");
		String port=context.getStringValue("AS_WL_WEBSVR_PORT");
		String appName=moduleDef.getNameKey();
		String appWarPath=ExpressionParser.parseString(moduleDef.getFilesPath());
			
		weblogicDeploy(appName,appWarPath,weblogicHome,domainHome);
			
		
	}

	private void weblogicDeploy(String appName, String appWarPath, String weblogicHome, String domainHome) throws IOException {
		File dest=new File(domainHome+"/autodeploy/");
		File destAppDir = new File(dest, appName);
		File appFile=new File(appWarPath);
		try {
			if (appFile.isDirectory()) {
				FileUtils.copy(appFile, destAppDir, null, null);
			} else
				FileUtils.unzip(appFile, destAppDir, null, null);
		} catch (OperationException e) {
			String message = e.getMessage();
			if ((StringUtils.isEmpty(message)) && (e.getCause() != null)) {
				message = e.getCause().getMessage();
			}
			throw new IOException(message);
		}
		
	}

	private void tomcatDeploy(IContext context, ModuleDef moduleDef) {
		try{
			String tomcatHome=context.getStringValue("AS_TOMCAT_HOME");
			TomcatEnv env = new TomcatEnv(tomcatHome);
			TomcatServerImpl server = new TomcatServerImpl(env);
			String appName=moduleDef.getNameKey();
			String appSourceFile=ExpressionParser.parseString(moduleDef.getFilesPath());
			server.deploy(appName, new File(appSourceFile), null, null);
		}catch(Exception e){
		    e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void deployZip(IContext context, ModuleDef moduleDef) {
		String mf=moduleDef.getFilesPath();
		String srcPath=ExpressionParser.parseString(mf);
		String destPath=context.getStringValue("INSTALL_DIR").toString();
		
		File srcFile = new File(srcPath);
		File destDir = new File(destPath);
		try {
			FileUtils.unzip(srcFile, destDir, null, FileOperationMessageListener.INSTANCE);
		} catch (OperationException e) {
			throw new InstallException("Failed to unzip " + srcPath + " to " + destPath, e);
		}
	}
	
	//调用安装脚本
	private void install(IContext context,Map params) throws Exception {
         String installfiles=params.get("INSTALL_FILES").toString();
        
         String[] files=installfiles.split(",");
       
        	 for (String file : files) {
          		//替换执行文件中的变量
        		 String content=InstallUtil.readFile(file);
                 content=content.replace("${INSTALL_DIR}", ExpressionParser.parseString("${INSTALL_DIR}"));
               //重新写入
         		BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
         		out.write(content);
         		out.close();
         		
        		 //授权，执行
         		Process process=null;
        		  String c1="chmod a+x"+" "+file;
        		 process=Runtime.getRuntime().exec(c1);
        			process.waitFor();
        			
        			Runtime.getRuntime().exec(file).waitFor();
    		} 
      	
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
