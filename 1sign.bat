@echo off
chcp 65001 >nul
echo.
echo 🔨 Starting APK signing process...
echo.

:: Подписание APK
echo 📦 Signing subprint.apk...
java -jar sign\uber-apk-signer-1.3.0.jar --apks subprint.apk --ks sign\subprint.keystore --ksAlias subprint --ksPass subprint --ksKeyPass subprint

:: Проверка результата
if exist subprint-aligned-signed.apk (
    echo ✅ SUCCESS: Signed APK created!
    echo 📁 File: subprint-aligned-signed.apk
    echo 📊 File size: 
    for %%F in ("subprint-aligned-signed.apk") do echo        %%~zF bytes
    echo.
    echo 🎉 Signing completed successfully!
) else (
    echo ❌ ERROR: Signed APK not created!
    echo 💡 Check if:
    echo    - subprint.apk exists in project root
    echo    - Keystore file is valid
    echo    - Passwords are correct
)

echo.
pause