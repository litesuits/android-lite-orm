# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/quinn/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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


# 使用注解
-keepattributes *Annotation*,Signature,Exceptions,InnerClasses

# 开发者需要调用，不可以混淆
-keep public class com.litesuits.orm.LiteOrm { *; }
-keep public class com.litesuits.orm.db.* { *; }
-keep public class com.litesuits.orm.db.model.** { *; }
-keep public class com.litesuits.orm.db.annotation.** { *; }
-keep public class com.litesuits.orm.db.enums.** { *; }
-keep public class com.litesuits.orm.log.* { *; }
-keep public class com.litesuits.orm.db.assit.* { *; }