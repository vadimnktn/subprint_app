@echo off
chcp 65001 >nul
echo.
echo 🔨 Starting APK build process...
echo.

:: Очистка
echo 🧹 Cleaning previous builds...
call gradlew clean
if errorlevel 1 (
    echo ❌ ERROR: Clean failed!
    pause
    exit /b 1
)

:: Сборка APK
echo 📦 Building APK...
call gradlew assembleDebug
if errorlevel 1 (
    echo ❌ ERROR: Build failed!
    echo 💡 Check errors above
    pause
    exit /b 1
)

:: Проверка результата
if exist "app\build\outputs\apk\debug\subprint.apk" (
    echo ✅ SUCCESS: APK built successfully!
    echo 📁 Location: app\build\outputs\apk\debug\subprint.apk
    
    :: Удаляем старый APK если существует и копируем новый
    if exist "subprint.apk" (
        echo 🗑️ Removing old subprint.apk...
        del "subprint.apk"
    )
    copy "app\build\outputs\apk\debug\subprint.apk" "subprint.apk"
    echo 📋 New APK copied to project root: subprint.apk
    echo 📊 File size: 
    for %%F in ("subprint.apk") do echo        %%~zF bytes
    
    :: Автоматическая подпись
    echo.
    echo 🖊️ Starting automatic signing...
    if exist "sign\uber-apk-signer-1.3.0.jar" (
        java -jar sign\uber-apk-signer-1.3.0.jar --apks subprint.apk --ks sign\subprint.keystore --ksAlias subprint --ksPass subprint --ksKeyPass subprint
        
        if exist "subprint-aligned-signed.apk" (
            echo ✅ SUCCESS: APK signed successfully!
            echo 📁 Signed APK: subprint-aligned-signed.apk
            echo 📊 Signed file size: 
            for %%F in ("subprint-aligned-signed.apk") do echo        %%~zF bytes
            echo.
            echo 🎉 Build and sign completed!
        ) else (
            echo ⚠️ WARNING: Signing failed, but APK is built
            echo 💡 You can install with: adb install subprint.apk
        )
    ) else (
        echo ⚠️ WARNING: Signing tools not found, APK is not signed
        echo 💡 Install signing tools or sign manually
    )
) else (
    echo ❌ ERROR: APK not found in build folder!
    echo 💡 Check build errors above
)

echo.
pause