SET PATH=%PATH%;C:\Program Files (x86)\Android\android-sdk\tools\;C:\Program Files (x86)\Android\android-sdk\platform-tools\;
CALL gradlew.bat assembleDebugTestAVDAndroidTest
for %%x in (Nexus_4_API_23,Nexus_7_API_23,Vaikseke,Nexus_10_API_23,Vaikseke_4_4) do (
start emulator -avd %%x
timeout 25

adb wait-for-device push C:\Users\Public\Rakid\Pillipaevik\app\build\outputs\apk\app-debugTestAVD.apk /data/local/tmp/com.vaskjala.vesiroosi20.pillipaevik
adb wait-for-device push C:\Users\Public\Rakid\Pillipaevik\app\build\outputs\apk\app-debugTestAVD-androidTest-unaligned.apk /data/local/tmp/com.vaskjala.vesiroosi20.pillipaevik.test

adb wait-for-device shell pm install -r "/data/local/tmp/com.vaskjala.vesiroosi20.pillipaevik"
adb wait-for-device shell pm install -r "/data/local/tmp/com.vaskjala.vesiroosi20.pillipaevik.test"
adb wait-for-device shell am instrument -w -r   -e debug false -e class com.vaskjala.vesiroosi20.pillipaevik.KoondTest com.vaskjala.vesiroosi20.pillipaevik.test/android.support.test.runner.AndroidJUnitRunner 2>&1|C:\Users\Public\Rakid\PilliTests\tee.exe C:\Users\Public\Rakid\Pillipaevik\app\build\outputs\testresults\%%x.log
adb -s emulator-5554 emu kill

)