# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

## ----- Begin: proguard configurations common for all Android apps -----

-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

-allowaccessmodification
-keepattributes *Annotation*, Signature, Exception
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-repackageclasses ''

-dontwarn javax.**
-dontwarn lombok.**
-dontwarn org.apache.**

## ----- End: proguard configurations common for all Android apps -----

# // For later use of any new section
## ----- Begin: proguard configuration for <> -----
## ----- End: proguard configuration for Gson <> -----