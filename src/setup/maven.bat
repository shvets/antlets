@echo off

rem 1. init

CALL @install.dir@\bin\env.bat

set LIB_DIR=@install.dir@\lib

rem 2. get script name

set SCRIPT_NAME=maven.xml
set CMD_LINE_ARGS=

if "%~1"=="-maven" goto setScriptName

goto next

:setScriptName
shift
set SCRIPT_NAME=%~1
shift

:next

rem 3. get command line parameters

:setupArgs

if "%~1"=="" goto doneStart

if "%~1"=="-p" goto setScriptName
if "%~1"=="--pom" goto setScriptName

set CMD_LINE_ARGS=%CMD_LINE_ARGS% %~1
shift

goto setupArgs

:doneStart


rem 4. launch JVM org.apache.maven.cli.App

SET LAUNCHER_CLASS=org.shvets.antlet.launcher.MavenLauncher
SET LIBS=-lib %ANT_HOME%\lib -lib %LIB_DIR% -lib %MAVEN_HOME%\lib
SET CLASSPATH="%MAVEN_HOME%\lib\forehead-1.0-beta-5.jar;%LIB_DIR%\launchers.jar"
SET ENDORSED_DIRS_PROPS=-Djava.endorsed.dirs=%JAVA_HOME%\lib\endorsed;%MAVEN_HOME%\lib\endorsed
SET XML_BUILDERS_PROPS=-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl

%JAVA_HOME%\bin\java "-Dant.home=%ANT_HOME%" "-Dmaven.home=%MAVEN_HOME%" %ENDORSED_DIRS_PROPS% %XML_BUILDERS_PROPS% "-Dtools.jar=%JAVA_HOME%\lib\tools.jar" "-Dforehead.conf.file=%MAVEN_HOME%\bin\forehead.conf" -classpath %CLASSPATH% "%LAUNCHER_CLASS%" -b -Dant.file="%SCRIPT_NAME%" %CMD_LINE_ARGS%

pause
