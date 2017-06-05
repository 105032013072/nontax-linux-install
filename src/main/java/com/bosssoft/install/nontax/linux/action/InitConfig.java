package com.bosssoft.install.nontax.linux.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.ExpressionParser;

public class InitConfig implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		String initFiles=params.get("INIT_FILES").toString();
		String[] files=initFiles.split(",");
		for (String ifile : files) {
			Properties p=new Properties();
			try{
				InputStream fis=new FileInputStream(ifile);
				p.load(fis);
				fis.close();
				String appConfig=p.getProperty("APP_CONFIG");
				String conftemp=p.getProperty("CONFIG_TEMPLET");
				String tempVars=p.getProperty("TEMPLET_variables");
				doinit(appConfig,conftemp,tempVars,context);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
	}

	private void doinit(String appConfig, String conftemp, String tempVars,IContext context) {
		appConfig=ExpressionParser.parseString(appConfig);
		
		conftemp=ExpressionParser.parseString(conftemp);
		File f=new File(conftemp);
		
		Properties p = new Properties();
        // 初始化默认加载路径为：D:/template
        p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, f.getParent());
        p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        // 初始化Velocity引擎，init对引擎VelocityEngine配置了一组默认的参数 
        Velocity.init(p);
		
		Template t = Velocity.getTemplate(f.getName()); 
		VelocityContext vc = new VelocityContext();
		
		String[] vars=tempVars.split(",");
		for (String v : vars) {
			vc.put(v, context.getStringValue(v));
		}
		StringWriter writer = new StringWriter();
		t.merge(vc, writer); 
		
		
		try {
			BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(appConfig)));
			bw.write (writer.toString());
			bw.close();
		} catch (IOException e) {
			this.logger.error(e);
		}
		

	}

	public void rollback(IContext context, Map params) throws InstallException {
		
	}

}
