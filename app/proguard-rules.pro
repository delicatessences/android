# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Tom/Documents/Projets/android-sdk-macosx/tools/proguard/proguard-android.txt
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

-keep class android.support.v7.widget.SearchView { *; }

# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod


-keep class fr.delicatessences.delicatessences.model.**
-keepclassmembers class fr.delicatessences.delicatessences.model.** { *; }
-keep enum fr.delicatessences.delicatessences.model.**
-keepclassmembers enum fr.delicatessences.delicatessences.model.** { *; }
-keep interface fr.delicatessences.delicatessences.model.**
-keepclassmembers interface fr.delicatessences.delicatessences.model.** { *; }


# Keep the helper class and its constructor
-keep class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
-keepclassmembers class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper {
  public <init>(android.content.Context);
}


# Keep all model classes that are used by OrmLite
# Also keep their field names and the constructor
-keep @com.j256.ormlite.table.DatabaseTable class * {
    @com.j256.ormlite.field.DatabaseField <fields>;
    @com.j256.ormlite.field.ForeignCollectionField <fields>;
    # Add the ormlite field annotations that your model uses here
    <init>();
}

# Recommended flags for Firebase Auth
-keepattributes Signature
-keepattributes *Annotation*


-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }

-dontwarn retrofit2.**

-keep class org.apache.harmony.lang.annotation.**

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

-keep class android.arch.lifecycle.** { *; }
-keep class android.arch.core.** { *; }
-keep class com.github.amlcurran.showcaseview.** { *; }
-keep class com.manuelpeinado.fadingactionbar.** { *; }
-keep class com.melnykov.fab.** { *; }
-keep class fr.delicatessences.delicatessences.** { *; }
-keep class net.mediavrog.irr.** { *; }
-keep class net.mediavrog.ruli.** { *; }
