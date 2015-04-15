### JXcore / Node.JS for Android sample

This project is a kind of 'hello world' for JXcore's native interface on an Android application. 
You need Android Studio and Android-NDK are installed on your system.

The sample project uses JXcore SpiderMonkey JIT build for Android ARM and Intel processors.  
Prebuilt JXcore binaries are already available under the project folders.

### Before Start
[Download Android Studio](https://developer.android.com/sdk/index.html)  
[Download Android NDK](https://developer.android.com/tools/sdk/ndk/index.html)  

### HowTo
1 - Clone this repository `git clone https://github.com/obastemur/jxcore-android-basics`
2 - Open Android Studio and from the Main Screen, Select **Import project (Eclipse ADT, Gradle ..)**
3 - Select the folder `jxcore-droid-astd`
4 - Define `ndk.dir` property under `local.properties` file to whereever you've installed the Android NDK
5 - Sync Gradle build etc. [see howto](http://stackoverflow.com/a/24824736)
6 - You are all set. Run!

