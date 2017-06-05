package com.bosssoft.install.nontax.linux.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
/**
 *设置应用服务器的启动路径
 * @author Windows
 *
 */
public class SetAppsvrPath implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		String appsvrType = context.getStringValue("APP_SERVER_TYPE");
		if(appsvrType.toLowerCase().indexOf("tomcat")!=-1){
			
			String serverdir=ExpressionParser.parseString("${AS_TOMCAT_HOME}/bin/");
			context.setValue("appSvr_path", serverdir+"startup.sh");

		}else if(appsvrType.toLowerCase().indexOf("jboss")!=-1){
			
		}else if(appsvrType.toLowerCase().indexOf("weblogic")!=-1){
			String serverdir=ExpressionParser.parseString("${AS_WL_DOMAIN_HOME}/");
			context.setValue("appSvr_path", serverdir+"startWebLogic.sh");
		}
		
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
