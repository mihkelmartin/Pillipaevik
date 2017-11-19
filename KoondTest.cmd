SET PATH=%PATH%;E:\Android\android-sdk\emulator\;E:\Android\android-sdk\platform-tools\;
CALL gradlew.bat assembleAndroidTest
CALL md C:\Users\Public\Rakid\Pillipaevik\app\build\outputs\testresults\
for %%x in (2.7_4.4_19, 2.7_6.0_23, 3.2_6.0_23, Nexus_10_6.0_23, Nexus_6P_7.0_24, Nexus_7_5.1_23, Pixel_C_API_27) do (
start emulator -avd %%x
timeout 35

adb wait-for-device push C:\Users\Public\Rakid\Pillipaevik\app\build\outputs\apk\app-debugTestAVD.apk /data/local/tmp/com.vaskjala.vesiroosi20.pillipaevik
adb wait-for-device push C:\Users\Public\Rakid\Pillipaevik\app\build\outputs\apk\app-debugTestAVD-androidTest.apk /data/local/tmp/com.vaskjala.vesiroosi20.pillipaevik.test

adb wait-for-device shell pm install -r "/data/local/tmp/com.vaskjala.vesiroosi20.pillipaevik"
adb wait-for-device shell pm install -r "/data/local/tmp/com.vaskjala.vesiroosi20.pillipaevik.test"
adb wait-for-device shell am instrument -w -r   -e debug false -e class com.vaskjala.vesiroosi20.pillipaevik.KoondTest com.vaskjala.vesiroosi20.pillipaevik.test/android.support.test.runner.AndroidJUnitRunner 2>&1|C:\Users\Public\Rakid\PilliTests\tee.exe C:\Users\Public\Rakid\Pillipaevik\app\build\outputs\testresults\%%x.log
adb -s emulator-5554 emu kill

timeout 25

)

