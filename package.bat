@ECHO OFF

set JAVA_VERSION=17
set MAIN_JAR=hsgui-%PROJECT_VERSION%.jar
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.3

rem Set desired installer type: "app-image" "msi" "exe".
set INSTALLER_TYPE=msi

rem --- parameters ---
echo using java version: %JAVA_VERSION%
echo using java home: %JAVA_HOME%
echo using project version: %PROJECT_VERSION%
echo using installer type: %INSTALLER_TYPE%
echo using main jar: %MAIN_JAR%

rem ------ SETUP DIRECTORIES AND FILES ----------------------------------------

IF EXIST target\java-runtime rmdir /S /Q  .\target\java-runtime
IF EXIST target\installer rmdir /S /Q target\installer

xcopy /S /Q target\libs\* target\installer\input\libs\
copy target\%MAIN_JAR% target\installer\input\libs\

rem ------ PACKAGING ----------------------------------------------------------

call "%JAVA_HOME%\bin\jpackage.exe" --name "Hunt Showdown Random Loadouts" ^
              --input src/main/resources ^
              --icon src/main/resources/icons/hs_ico_d.ico ^
              --module-path target/installer/input/libs ^
              --module hs.loadoutgen.gui/hsgui.AppEntry ^
              --jlink-options --bind-services ^
              --java-options -Dpolyglot.js.nashorn-compat=true ^
              --type %INSTALLER_TYPE% ^
              --app-version %APP_VERSION% ^
              --vendor "ACME Inc." ^
              --copyright "Copyright © 2019-22 ACME Inc." ^
              --win-dir-chooser ^
              --win-shortcut ^
              --win-per-user-install ^
              --win-menu ^
              --dest target/installer








