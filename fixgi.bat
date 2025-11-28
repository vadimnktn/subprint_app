@echo off
chcp 65001 >nul
echo === NUCLEAR OPTION - Fresh Start ===
echo WARNING: This will create a new repository!
echo.

:: Создаем backup важных файлов
echo Creating backup...
if not exist backup mkdir backup
xcopy sign backup\sign /E /I /Y
xcopy app\src backup\app\src /E /I /Y
xcopy app\build.gradle backup\ /Y
xcopy build.gradle backup\ /Y
xcopy gradle.properties backup\ /Y
xcopy settings.gradle backup\ /Y

:: Удаляем .git и создаем заново
echo Reinitializing git repository...
rmdir /s /q .git
git init

:: Создаем .gitignore
echo Creating .gitignore...
(
echo # Built application files
echo *.apk
echo *.ap_
echo *.aab
echo.
echo # Gradle files
echo .gradle/
echo build/
echo.
echo # Local configuration file
echo local.properties
echo.
echo # Android Studio
echo *.iml
echo .idea/
echo.
echo # Keystore files
echo *.jks
echo *.keystore
echo.
echo # Signing files
echo sign/commandlinetools-win-13114758_latest.zip
echo sign/*.jar
echo.
echo # Build outputs
echo app/build/
echo *.apk
echo *.dex
) > .gitignore

:: Добавляем только исходный код
echo Adding source files...
git add .
git commit -m "Initial commit - clean repository"

echo.
echo === READY FOR NEW REMOTE ===
echo Now you need to:
echo 1. Delete your repository on GitHub
echo 2. Create new repository with same name
echo 3. Run: git remote add origin https://github.com/vadimnktn/subprint_app.git
echo 4. Run: git push -u origin main
echo.
pause