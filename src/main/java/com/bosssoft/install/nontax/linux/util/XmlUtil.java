package com.bosssoft.install.nontax.linux.util;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class XmlUtil {
	public static Document getDocument(File file)throws IOException{
		return Jsoup.parse(file, "UTF-8");
	}
	
	public static Elements findElements(Document doc,String nodeName){
		return doc.select(nodeName);
	}

}
