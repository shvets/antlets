@echo off

rem 1. init

CALL @install.dir@\bin\env.bat

set LIB_DIR=@install.dir@\lib

rem 2. get script name

set SCRIPT_NAME=build.xml
set CMD_LINE_ARGS=

if "%~1"=="-antlet" goto setScriptName

goto next

:setScriptName
shift
set SCRIPT_NAME=%~1
shift

:next

rem 3. get command line parameters

:setupArgs

if "%~1"=="" goto doneStart

if "%~1"=="-f" goto setScriptName
if "%~1"=="-file" goto setScriptName
if "%~1"=="-buildfile" goto setScriptName

set CMD_LINE_ARGS=%CMD_LINE_ARGS% %~1
shift

goto setupArgs

:doneStart


rem 4. launch JVM

SET LAUNCHER_CLASS=org.shvets.antlet.launcher.GenericLauncher
SET LIBS=-lib %ANT_HOME%\lib -lib %LIB_DIR% -lib %MAVEN_HOME%
SET CLASSPATH="%LIB_DIR%\launchers.jar"
SET MAIN_CLASS=org.shvets.antlet.starter.AntStarter

%JAVA_HOME%\bin\java -Dant.home="%ANT_HOME%" "-Dmaven.home=%MAVEN_HOME%" "-Djelly.home=%MAVEN_HOME%" -Dmain.class=%MAIN_CLASS% -classpath %CLASSPATH% %LAUNCHER_CLASS% %LIBS% -f "%SCRIPT_NAME%" %CMD_LINE_ARGS%


pause
