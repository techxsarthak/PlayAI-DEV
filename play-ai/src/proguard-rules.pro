# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.playai.PlayAI {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/playai/repack'
-flattenpackagehierarchy
-dontpreverify
