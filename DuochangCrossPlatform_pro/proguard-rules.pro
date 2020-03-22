# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-libraryjars android_framework/classes.jar

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

#sdk 通过 proguard 混淆代码时默认已经将 lib目录中的 jar 都已经添加到打包脚本中，所以不需要再次手动添加
#-libraryjars android_framework/classes.jar
#-libraryjars libs/activation.jar
#-libraryjars libs/additionnal.jar
#-libraryjars libs/android-support-v4.jar
#-libraryjars libs/apache-ant-zip.jar
#-libraryjars libs/commons-codec.jar
#-libraryjars libs/mail.jar
#-libraryjars libs/pushservice-4.6.2.39.jar
#-libraryjars libs/umeng-analytics-v6.1.1.jar
#-libraryjars libs/gifview.jar
#-libraryjars libs/universal-image-loader-1.9.5.jar
#-libraryjars libs/rabbitmqclient-v3.1.6.jar


-libraryjars libs/jniLibs/armeabi/libbdpush_V2_5.so
-libraryjars libs/jniLibs/armeabi/libjdns_sd.so
-libraryjars libs/jniLibs/armeabi/libnext_reboot_update.so
-libraryjars libs/jniLibs/armeabi/libstlport_shared.so
-libraryjars libs/jniLibs/armeabi/libWave.so
#-libraryjars libs/armeabi/libxml2.so


-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-ignorewarnings


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep class com.baidu.** { *; }

#-keep public class com.evideo.kmbox.model.intonation.** { *; }
-keep public class com.evideo.kmbox.model.record.** { *; }
-keep public class com.evideo.kmbox.model.gradesing.** { *; }

-keep public class com.evideo.kmbox.model.audioeffectservice.** { *; }

-keep public class com.evideo.kmbox.model.datacenter.** { *; }

-dontwarn android.support.v4.**
-dontwarn org.apache.commons.net.**
#-dontwarn com.baidu.**

-keepclasseswithmembernames class * {
        native <methods>;
}
-keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
        public void *(android.view.View);
}

-keepclassmembers class * {
        public <init>(org.json.JSONObject);
    public static final int *;
}
-keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
        public static final android.os.Parcelable$Creator *;
}

-keep public class [your_pkg].R$*{
    public static final int *;
}

-keep public class com.evideo.kmbox.widget.common.OrderSongAnimView {*;}
-keep public class com.evideo.kmbox.widget.common.OrderSongAnimView$AnimViewHolder {*;}

-keep class com.evideo.kmbox.model.setting.** {*;}

-keep class com.apple.** {*;}

-keep class pl.droidsonroids.** {*;}
#umeng
-keep public class com.umeng.** { *; }

#rabbitmq
-keep public class com.rabbitmq.** { *; }

#qiniu
-keep public class com.qiniu.android.** { *; }

#VLC
-keep public class org.videolan.** { *; }

-keepclassmembers class **.R$* {  
    public static <fields>;  
}  
-keep class **.R$* { *; }

-keep public class tv.huan.huanpay4.** { *; }

-keep public class * extends WebView

-keep public class tv.huan.huanpay4.HuanPayView { *; }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#ijk
-keep class tv.danmaku.ijk.media.player** { *; }

#华为代理SDK
-keep public class com.example.proxysdk

-keep public class com.sdk.commplatform.**{ *; }

-keep class *.R$ { *; }


#以下是华为SDK
-keep class com.huawei.tools.**{*;}
-keep class com.huawei.ability.**{*;}
-keep class org.kxml2.**{*;}
-keep class org.xmlpull.**{*;}
-keep class com.google.**{*;} 
-keep class org.tantalum.**{*;}
-keep class android.**{*;}
-keep class assets.**{*;}
-keep class com.android.**{*;}
-keep class java.**{*;}
-keep class javax.**{*;}
-keep class junit.**{*;}
-keep class org.**{*;}
-keep class res.**{*;}


-keep public class * extends android.content.ContextWrapper
-keep public class * extends com.odin.framework.foundation.BasePluginActivity
-keep public class * extends com.odin.framework.foundation.BasePluginService

-keep class com.odin.framework.utils.FileUtil{*;}
-keep class com.odin.framework.utils.StringUtil{*;}
-keep class com.odin.framework.utils.ThreadUtil{*;}
-keep class com.odin.framework.proxy.ProxyManager{*;}
-keep class com.odin.framework.plugable.Logger{*;}
-keep class com.odin.framework.foundation.Framework{*;}
-keep class com.odin.framework.foundation.ExposedService{*;}
-keep class com.odin.framework.foundation.PluginInfo{*;}

-keep class com.odin.framework.utils.FileUtil{*;}
-keep class com.odin.framework.utils.StringUtil{*;}
-keep class com.odin.framework.utils.ThreadUtil{*;}
-keep class com.odin.framework.proxy.ProxyManager{*;}
-keep class com.odin.framework.plugable.**{*;}
-keep class com.odin.framework.foundation.**{*;}
-keep class com.odin.framework.hack.**{*;}
-keep class com.odin.framework.compatible.**{*;}

-keep class com.sdk.commplatform.impl.**{*;}
-keep class com.sdk.plugable.TvPayment.**{*;}
-keep class com.odin.plugable.api.AbilityServiceFactory{*;}
-keep class com.sdk.plugable.**{*;}
-keep class com.sdk.plugable.TvPayment.ui.service.WebViewLoadService{*;}
-keep class com.odin.plugable.api.**{*;}
-keep class com.sdk.plugable.TvPayment.impl.InitImpl{*;}
-keep class com.sdk.plugable.ability.log.LogImpl{*;}
-keep class com.sdk.plugable.payment.PaymentService{*;}
-keep class com.sdk.plugable.TvPayment.TvPaymentService{*;}
-keep class com.sdk.plugable.ability.download.DownloadService{*;}
-keep class com.sdk.plugable.ability.platform.PlatformService{*;}
-keep class com.sdk.plugable.ability.upgrade.UpgradeService{*;}
-keep class com.sdk.plugable.testin.TestinService{*;}

-keep class com.odin.framework.compatible.ParasiticFramework{*;}

-keep class com.huawei.edata.**{*;}
-keep class com.huawei.amp.**{*;}
-keep class com.huawei.ability.http.**{*;}
-keep class com.sdk.plugable.payment.ui.view.webview.WebViewCallbackInterface{*;}
-keep class com.sdk.plugable.payment.logic.partner.impl.unipay.UniPaySDKProxy{*;}
-keep class com.sdk.plugable.payment.logic.partner.impl.mm.MMBillingSDKProxy{*;}
-keep class com.sdk.plugable.payment.logic.partner.impl.migu.MiguBillingSDKProxy{*;}
-keep class com.sdk.plugable.payment.logic.partner.impl.egame.EgamePaySDKProxy{*;}
-keep class com.sdk.plugable.payment.ui.activity.WelcomeMMActivity{*;}
-keep class com.sdk.plugable.payment.logic.server.PaymentLogic{*;}

-keep class com.sdk.plugable.payment.ui.activity.**{*;}

-dontwarn com.huawei.tools.**
-dontwarn com.huawei.ability.**
-dontwarn org.tantalum.jme.**
-dontwarn android.**

-keep class com.huawei.digital.payment.android.**{*;}
-keep class com.sdk.plugable.payment.ui.**{*;}

# 保留内部类
-keepattributes InnerClasses

#Dynamic R File
-keep class com.huawei.digital.tv.R{*;}

-keep class com.huawei.digital.tv.R$*{*;}

#plugin_in R File
-keep class com.odin_plugin.R{*;}

#沙发管家
-keep class com.xmxgame.**  { *; }

#阿里TV
-keep class com.yunos.tv.apppaysdk.** { *;}
-keep class com.alibaba.** { *;}
-keep class com.alibaba.wireless.security.open.** { *;}
-dontwarn com.alibaba.wireless.security.open.**
-keep class com.taobao.** { *;}
-keep class com.yunos.** { *;}
-keep class com.ut.** { *;}
-keep class com.ta.utdid2.** { *;}

#Gson and zxing
-keep class com.google.gson.** { *;}
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *;}

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *;}
-keep interface com.squareup.okhttp.** { *;}
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-keep interface okhttp3.** { *;}

# Okio
-keep class sun.misc.Unsafe { *;}
-dontwarn java.nio.file.*
-keep class  java.nio.file.** { *;}
-dontwarn org.codehaus.mojo.*
-keep class org.codehaus.mojo.** { *;}
-dontwarn okio.**
-keep class okio.** {*;}
-dontwarn java.nio.*
-keep class java.nio.** { *;}

