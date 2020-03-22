package com.evideo.kmbox.model.song;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.evideo.kmbox.model.sharedpreferences.KeyName;
import com.evideo.kmbox.model.sharedpreferences.KmSharedPreferences;
import com.evideo.kmbox.util.Base64Util;
import com.evideo.kmbox.util.EvLog;

/**
 * @brief      : [文件功能说明]
 */
public class MembersInfoList {
    
    public static String TAG = "MembersMapInfo";
    
    private Context mContext;
    
    public static boolean instanceFlag = false; //true if 1 instance
    
    // Set constructor private and do nothing
    // Can not new a instance outside class
    private MembersInfoList() {
    }
    
    private static MembersInfoList instance = null;
    
    public static MembersInfoList getInstance() {
        if(! instanceFlag) {
            instanceFlag = true;
            instance = new MembersInfoList();
            return instance;
        }
        return instance;
    }
    
    @Override
    public void finalize() {
        instanceFlag = false;
        instance     = null;
    }
    
    public void InitRes(Context context){
        mContext = context;
        memberlist = this.getMemberUnits(context);
    }
    
    
    public void addMemble(String id, String name, String path){
        if(id == null){
            EvLog.e(TAG, "id is error");
            return;
        }

        try {
            for (MemberInfo m : memberlist) {
                if(m.id.equals(id)){
                    if(!TextUtils.isEmpty(name)){
                        m.name = name;
                    }
                    if(!TextUtils.isEmpty(path)){
                        m.picpath = path;
                    }
                    return;
                }
            }
            MemberInfo member = new MemberInfo(id, name, path);
            EvLog.i(TAG, "add member id: "+id+" name:"+name+" path:"+path);
            memberlist.add(member);
            saveMemberInfoUnits(mContext, memberlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void removeMemble(String id){
        if(id == null){
            EvLog.e(TAG, "id is error");
            return;
        }

        try {
            for (MemberInfo m : memberlist) {
                if(m.id.equals(id)){
                    memberlist.remove(m);
                    EvLog.w(TAG, "remove member id: " + id);
                    saveMemberInfoUnits(mContext, memberlist);
                    return ;
                }
            }
            EvLog.w(TAG, "id is not exist");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getCustomerName(String id){
        if(id == null ){
            EvLog.e(TAG, "id is error");
            return null;
        }

        try {
            for (MemberInfo m : memberlist) {
                if(m.id.equals(id)){
                    return m.name;
                }
            }
            EvLog.w(TAG, "customer name is lost");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getPicPath(String id){
        if(id == null ){
            EvLog.e(TAG, "id is error");
            return null;
        }

        try {
            for (MemberInfo m : memberlist) {
                if(m.id.equals(id)){
                    return m.picpath;
                }
            }
            EvLog.w(TAG, "customer path is lost");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int getSize(){
        return memberlist.size();
    }
    
    private boolean saveMemberInfoUnits(Context context, ArrayList<MemberInfo> memberInfoUnits) {
        if(memberInfoUnits == null) {
            return false;
        }

        String memberBase64 = Base64Util.encodeBase64(memberInfoUnits);
        if(memberBase64 == null) {
            return false;
        }
        return KmSharedPreferences.getInstance().putString(KeyName.KEY_MEMBER_INFO_LIST, memberBase64);
    }
    
    @SuppressWarnings("unchecked")
    private ArrayList<MemberInfo> getMemberUnits(Context context) {
        
        String deleteBase64 = KmSharedPreferences.getInstance().getString(KeyName.KEY_MEMBER_INFO_LIST, "");
        
        if(TextUtils.isEmpty(deleteBase64)) {
            return new ArrayList<MemberInfo>();
        }
        
        ArrayList<MemberInfo> memberInfoUnits = new ArrayList<MemberInfo>();
        Object obj = Base64Util.decodeBase64(deleteBase64);
        if (obj != null) {
            memberInfoUnits = (ArrayList<MemberInfo>)obj;
        }
        return memberInfoUnits  == null ? new ArrayList<MemberInfo>() : memberInfoUnits;
    }
    

    private ArrayList<MemberInfo> memberlist = new ArrayList<MemberInfo>();

    
}
