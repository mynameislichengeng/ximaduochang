package com.evideo.kmbox.util;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlUtil {
	private static final String TAG = XmlUtil.class.getSimpleName();
	private Document mDocument;
	private Element mRoot;
	
	private boolean state = false;
	public XmlUtil(InputStream is){
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
			mDocument = builder.parse(is);
			mRoot = mDocument.getDocumentElement();
			state = true;
		} catch (Exception e) {
			EvLog.e(TAG, "parse xml fail.");
		} 
	}
	
	public Element getRootElement(){
		return mRoot;
	}
	
	public boolean bParseNormal(){
		return state;
	}
}
