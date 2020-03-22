package com.evideo.kmbox.util;

import android.util.Log;
import android.util.Xml;

import org.apache.commons.logging.impl.LogFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by PoPEyE on 2019/8/2.
 */

public class XMLUtlis {

    private static DocumentBuilderFactory dbFactory = null;
    private static DocumentBuilder db = null;
    private static Document document = null;
    private static List<PayInfoSyncReqBean> payInfoSyncReqBeans;
    private static ArrayList list1;

    static {

        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            db = dbFactory.newDocumentBuilder();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static List<PayInfoSyncReqBean> getPayInfoSyncReqBeans(String filName) throws Exception {

        //将给定 URI 的内容解析为一个 XML 文档,并返回Document对象
        document = db.parse(filName);
        //按文档顺序返回包含在文档中且具有给定标记名称的所有 Element 的 NodeList
        NodeList payList=document.getElementsByTagName("PayInfoSyncReq");
        payInfoSyncReqBeans = new ArrayList<>();
        // 遍历payInfoSyncReqBeans
        for (int i = 0;i< payList.getLength();i++){
            PayInfoSyncReqBean payInfoSyncReqBean = new PayInfoSyncReqBean();
            //获取第i个结点
            Node node = payList.item(i);
            //获取第i个payInfoSyncReqBean的所有属性
            NamedNodeMap attributes = node.getAttributes();
            NodeList cList = node.getChildNodes();//System.out.println(cList.getLength());9

            ArrayList<String> contens = new ArrayList<>();
            for (int j = 0;j< cList.getLength(); j++){
                Node cNode = cList.item(j);
                String textContent = cNode.getFirstChild().getTextContent();
                contens.add(textContent);
                Log.i("gsp", "getPayInfoSyncReqBeans: "+contens);
            }
            payInfoSyncReqBean.setCtype(contens.get(0));
            payInfoSyncReqBeans.add(payInfoSyncReqBean);
            Log.i("gsp", "getPayInfoSyncReqBeans: "+payInfoSyncReqBeans.toString());
        }

        return payInfoSyncReqBeans;
    }

    public static PayInfoSyncReqBean pull2xmls(InputStream is) throws Exception {

        PayInfoSyncReqBean payInfoSyncReq = new PayInfoSyncReqBean();
        List<PayInfoSyncReqBean.PayInfo> list = null;
        PayInfoSyncReqBean.PayInfo payInfo = null;
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();
        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");
        //读取文件的类型
        int type = parser.getEventType();
        //无限判断文件类型进行读取
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                //开始标签
                case XmlPullParser.START_TAG:
                    if ("Ctype".equals(parser.getName())) {
                        String ctype = parser.nextText();
                        payInfoSyncReq.setCtype(ctype);
                    }else if ("OrderId".equals(parser.getName())) {
                        //获取Index属性
                        String orderId = parser.nextText();
                        payInfoSyncReq.setOrderId(orderId);
                    }else if ("PayNum".equals(parser.getName())) {
                        //获取Index属性
                        String payNum = parser.nextText();
                        payInfoSyncReq.setPayNum(payNum);
                    }else if ("BizType".equals(parser.getName())) {
                        //获取Index属性
                        String bizType = parser.nextText();
                        payInfoSyncReq.setBizType(bizType);
                    }else if ("StbID".equals(parser.getName())) {
                        //获取Index属性
                        String stbID = parser.nextText();
                        payInfoSyncReq.setStbID(stbID);
                    }else if ("ChargePolicy".equals(parser.getName())) {
                        //获取Index属性
                        String chargePolicy = parser.nextText();
                        payInfoSyncReq.setChargePolicy(chargePolicy);
                    }else if ("CustomBizExpiryDate".equals(parser.getName())) {
                        //获取Index属性
                        String customBizExpiryDate = parser.nextText();
                        payInfoSyncReq.setCustomBizExpiryDate(customBizExpiryDate);
                    }else if ("OperCode".equals(parser.getName())) {
                        //获取Index属性
                        String customBizExpiryDate = parser.nextText();
                        payInfoSyncReq.setOperCode(customBizExpiryDate);
                    } else if ("PayInfos".equals(parser.getName())) {
                        list = new ArrayList<>();
                    } else if ("PayInfo".equals(parser.getName())) {
                        payInfo = new PayInfoSyncReqBean.PayInfo();
                    } else if ("Index".equals(parser.getName())) {
                        //获取Index属性
                        String index = parser.nextText();
                        payInfo.setIndex(index);
                    }else if ("IsMonthly".equals(parser.getName())) {
                        //获取Index属性
                        String isMonthly = parser.nextText();
                        payInfo.setIsMonthly(isMonthly);
                    }else if ("CustomPeriod".equals(parser.getName())) {
                        //获取Index属性
                        String customPeriod = parser.nextText();
                        payInfo.setCustomPeriod(customPeriod);
                    }else if ("BillTimes".equals(parser.getName())) {
                        //获取Index属性
                        String billTimes = parser.nextText();
                        payInfo.setBillTimes(billTimes);
                    }else if ("CooperateCode".equals(parser.getName())) {
                        //获取Index属性
                        String cooperateCode = parser.nextText();
                        payInfo.setCooperateCode(cooperateCode);
                    }else if ("BillInterval".equals(parser.getName())) {
                        //获取Index属性
                        String billInterval = parser.nextText();
                        payInfo.setBillInterval(billInterval);
                    }else if ("CampaignId".equals(parser.getName())) {
                        //获取Index属性
                        String campaignId = parser.nextText();
                        payInfo.setCampaignId(campaignId);
                    }else if ("Fee".equals(parser.getName())) {
                        //获取Index属性
                        String fee = parser.nextText();
                        payInfo.setFee(fee);
                    }else if ("SpCode".equals(parser.getName())) {
                        //获取Index属性
                        String spCode = parser.nextText();
                        payInfo.setSpCode(spCode);
                    }else if ("ServCode".equals(parser.getName())) {
                        //获取Index属性
                        String servCode = parser.nextText();
                        payInfo.setServCode(servCode);
                    }else if ("ChannelCode".equals(parser.getName())) {
                        //获取Index属性
                        String channelCode = parser.nextText();
                        payInfo.setChannelCode(channelCode);
                    }else if ("ProductCode".equals(parser.getName())) {
                        //获取Index属性
                        String productCode = parser.nextText();
                        payInfo.setProductCode(productCode);
                    }else if ("ContentCode".equals(parser.getName())) {
                        //获取Index属性
                        String contentCode = parser.nextText();
                        payInfo.setContentCode(contentCode);
                    }else if ("PlatForm_Code".equals(parser.getName())) {
                        //获取Index属性
                        String platForm_Code = parser.nextText();
                        payInfo.setPlatForm_Code(platForm_Code);
                    }else if ("Cpparam".equals(parser.getName())) {
                        //获取Index属性
                        String cpparam = parser.nextText();
                        payInfo.setCpparam(cpparam);
                    }else if ("ReserveParam".equals(parser.getName())) {
                        //获取Index属性
                        String reserveParam = parser.nextText();
                        payInfo.setReserveParam(reserveParam);
                    }
                    break;
                //结束标签
                case XmlPullParser.END_TAG:
                    if ("PayInfo".equals(parser.getName())) {
                        list.add(payInfo);
                        payInfoSyncReq.setPayInfoList(list);
                    }
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
        return payInfoSyncReq;
    }
    public static ArrayList<String> signXml(InputStream is) throws Exception {

        PayInfoSyncReqBean payInfoSyncReq = new PayInfoSyncReqBean();
        SignBean signBean = new SignBean();
        List<PayInfoSyncReqBean.PayInfo> list = null;
        PayInfoSyncReqBean.PayInfo payInfo = null;
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();
        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");

        list1 = new ArrayList();
        //读取文件的类型
        int type = parser.getEventType();
        //无限判断文件类型进行读取
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                //开始标签
                case XmlPullParser.START_TAG:
                    if ("loginAuth".equals(parser.getName())) {

                        for (int i =0 ;i<parser.getAttributeCount();i++){
                            String userId = parser.getAttributeValue(i) ;
                            list1.add(userId);
                        }
//                        Log.i("gsp", "signXml: loginAuth进来的数据是+"+userId+"attributeCount"+attributeCount+"attributeName"+attributeName);
                    }else if ("businessInfo".equals(parser.getName())) {
                        //获取Index属性
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String businessInfo = parser.getAttributeValue(i);
                            list1.add(businessInfo);
                        }

                    }
                    break;
                //结束标签
                case XmlPullParser.END_TAG:

                    if ("message".equals(parser.getName())) {
//                        list.add(payInfo);
//                        payInfoSyncReq.setPayInfoList(list);
                    }
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
        Log.i("gsp", "signXml:返回来的数据是getXme "+list1.toString());
        return list1;
    }

    public static ArrayList<String> connectXml(InputStream is) throws Exception {

        PayInfoSyncReqBean payInfoSyncReq = new PayInfoSyncReqBean();
        SignBean signBean = new SignBean();
        List<PayInfoSyncReqBean.PayInfo> list = null;
        PayInfoSyncReqBean.PayInfo payInfo = null;
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();
        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");

        list1 = new ArrayList();
        //读取文件的类型
        int type = parser.getEventType();
        //无限判断文件类型进行读取
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                //开始标签
                case XmlPullParser.START_TAG:

                    if ("result".equals(parser.getName())) {

                            String connect = parser.nextText();
                            list1.add(connect);
//                        Log.i("gsp", "signXml: loginAuth进来的数据是+"+userId+"attributeCount"+attributeCount+"attributeName"+attributeName);
                    }else if ("resultDesc".equals(parser.getName())) {
                        //获取Index属性
                            String businessInfo = parser.nextText();
                            list1.add(businessInfo);

                    }
                    break;
                //结束标签
                case XmlPullParser.END_TAG:
                    if ("message".equals(parser.getName())) {
//                        list.add(payInfo);
//                        payInfoSyncReq.setPayInfoList(list);
                    }
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
        Log.i("gsp", "connectXml: getcoonnect 返回的数据是"+list1.toString());
        return list1;
    }

    public static ArrayList<String> authorizeXml(InputStream is) throws Exception {

        PayInfoSyncReqBean payInfoSyncReq = new PayInfoSyncReqBean();
        SignBean signBean = new SignBean();
        List<PayInfoSyncReqBean.PayInfo> list = null;
        PayInfoSyncReqBean.PayInfo payInfo = null;
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();
        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");

        list1 = new ArrayList();
        //读取文件的类型
        int type = parser.getEventType();
        //无限判断文件类型进行读取
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                //开始标签
                case XmlPullParser.START_TAG:

                    if ("authorize".equals(parser.getName())) {

                        for (int i =0 ;i<parser.getAttributeCount();i++){
                            String userId = parser.getAttributeValue(i) ;
                            list1.add(userId);
                        }
//                        Log.i("gsp", "signXml: loginAuth进来的数据是+"+userId+"attributeCount"+attributeCount+"attributeName"+attributeName);
                    }else if ("Product".equals(parser.getName())) {
                        //获取Index属性
                        for (int i =0 ;i<parser.getAttributeCount();i++){
                            String Product = parser.getAttributeValue(i) ;
                            list1.add(Product);
                        }
                    }
                    break;
                //结束标签
                case XmlPullParser.END_TAG:
                    if ("message".equals(parser.getName())) {
//                        list.add(payInfo);
//                        payInfoSyncReq.setPayInfoList(list);
                    }
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
        Log.i("gsp", "authorizeXml: 返回鉴权数据"+list1.toString());
        return list1;
    }

    public static ArrayList<String> advPayXml(InputStream is) throws Exception {

        PayInfoSyncReqBean payInfoSyncReq = new PayInfoSyncReqBean();
        SignBean signBean = new SignBean();
        List<PayInfoSyncReqBean.PayInfo> list = null;
        PayInfoSyncReqBean.PayInfo payInfo = null;
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();
        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");

        list1 = new ArrayList();
        //读取文件的类型
        int type = parser.getEventType();
        //无限判断文件类型进行读取
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                //开始标签
                case XmlPullParser.START_TAG:

                    if ("advPay".equals(parser.getName())) {

                        for (int i =0 ;i<parser.getAttributeCount();i++){
                            String userId = parser.getAttributeValue(i) ;
                            list1.add(userId);
                        }
//                        Log.i("gsp", "signXml: loginAuth进来的数据是+"+userId+"attributeCount"+attributeCount+"attributeName"+attributeName);
                    }
                    break;
                //结束标签
                case XmlPullParser.END_TAG:
                    if ("message".equals(parser.getName())) {
//                        list.add(payInfo);
//                        payInfoSyncReq.setPayInfoList(list);
                    }
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
        Log.i("gsp", "authorizeXml: 下单返回数据"+list1.toString());
        return list1;
    }
}
