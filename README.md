### JXcore / Node.JS for Android sample

This project is a kind of 'hello world' for JXcore's native interface on an Android application. 
It's an Eclipse IDE project. You also need Android-NDK is installed on your system.

The sample project use JXcore SpiderMonkey JIT build for Android ARM and Intel processors.  
Prebuilt JXcore binaries are already available under 'jxcore-droid/jxcore-binaries'

### Before Start
[How to setup Android NDK for Eclipse IDE](http://tools.android.com/recent/usingthendkplugin)  
[Download Android NDK](https://developer.android.com/tools/sdk/ndk/index.html)  
[Eclipse Android Plugin](http://developer.android.com/tools/sdk/eclipse-adt.html)

### Tips
Open Eclipse IDE and from the 'Project Explorer', Right Click->New->Other  
Android->Android Project from Existing Code  
Pick 'jxcore-droid' folder and move forward.

Do exactly the same with 'android-support-v7....' folder.

After all, you should have two eclipse projects on 'Project Explorer'. 
One of them is an Android support project. The other one is JXcore sample
project. Make sure you have both of them on Eclipse. 

Right click to 'jxcore-droid' project -> Android Tools -> Add Support Library .. and follow the steps.

If you have errors etc. (from top menu) Project -> Clean -> (Select both of the projects)

Right click to 'android-support-v7...' project and 'Build'. 

You may need to update some project settings for your environment. Feel free to ask from Github!