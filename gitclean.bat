@echo off
echo Removing large files from git...
git rm --cached "sign/commandlinetools-win-13114758_latest.zip"
git rm --cached "app/build/intermediates/dex/debug/mergeExtDexDebug/classes.dex"
git rm --cached "app/build/outputs/apk/debug/subprint.apk"
git rm --cached "subprint.apk"

echo Updating gitignore...
echo. >> .gitignore
echo # Build outputs >> .gitignore
echo app/build/ >> .gitignore
echo *.apk >> .gitignore
echo *.dex >> .gitignore
echo. >> .gitignore
echo # Large binary files >> .gitignore
echo sign/commandlinetools-win-13114758_latest.zip >> .gitignore

echo Committing changes...
git add .
git commit -m "Remove large build files"
git push origin main

echo Done!
pause