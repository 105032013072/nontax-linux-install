package com.bosssoft.install.nontax.linux.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.bosssoft.platform.installer.io.FileUtils;

public class InstallUtil {
	public static String readFile(String sourceFile){
		StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(sourceFile)));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
	}
}
