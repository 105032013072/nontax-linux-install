package com.bosssoft.install.nontax.linux.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.install.nontax.linux.util.InstallUtil;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.option.ModuleDef;
/**
 * 配置nginx
 * @author Windows
 *
 */
public class ConfigComponents implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		String sourFile=context.getStringValue("nginx_home")+File.separator+"conf"+File.separator+"nginx.conf";
		String sourceContext=InstallUtil.readFile(sourFile);
		String tempContext=InstallUtil.readFile(params.get("CONFIG_TEMPLATE_PATH").toString());

		String port=context.getStringValue("APP_SERVER_PORT");
		
		StringBuffer strbuffer=new StringBuffer("");
		
		//根据安装的组件配置相应的路径
		List<ModuleDef> optionsCompList=(List<ModuleDef>) context.getValue("MODULE_OPTIONS");
	    for (ModuleDef moduleDef : optionsCompList) {
			if(moduleDef.getFilesPath().endsWith("war")){
				String tc=tempContext.replace("%app_name%", moduleDef.getNameKey())
  		              .replace("%ip%","127.0.0.1")
  		              .replace("%port%", port);
				strbuffer.append(tc).append(System.getProperty("line.separator"));
			}
		}
	    
	    //替换原配置文件的内容
	    int start=sourceContext.indexOf("location");
        int end=sourceContext.indexOf("}", start);
        String result=sourceContext.replace(sourceContext.substring(start, end+1), strbuffer.toString());
        result=result.replaceAll("%ip%", "127.0.0.1").replaceAll("%port%", port);
        try {
           BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(sourFile)));
			bw.write (result);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	    
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}
	


}
