@echo off

rem 1. init

CALL @install.dir@\bin\env.bat

set LIB_DIR=@install.dir@\lib

rem 2. get script name

set SCRIPT_NAME=%~1

rem 3. get command line parameters

set CMD_LINE_ARGS=

:setupArgs
shift

if "%~1"=="" goto doneStart

set CMD_LINE_ARGS=%CMD_LINE_ARGS% %~1

goto setupArgs

:setVerifyOn
set VERIFY_FLAG=%~1
shift
goto setupArgs

:doneStart

rem 4. launch JVM

SET LAUNCHER_CLASS=org.shvets.antlet.launcher.GenericLauncher
SET LIBS=-lib %ANT_HOME%\lib -lib %MAVEN_HOME%\lib
SET CLASSPATH="%LIB_DIR%\launchers.jar"
SET MAIN_CLASS=org.apache.commons.jelly.Jelly

%JAVA_HOME%\bin\java -Dmain.class=%MAIN_CLASS% -classpath %CLASSPATH% %LAUNCHER_CLASS% %LIBS% "%SCRIPT_NAME%" %CMD_LINE_ARGS%

:done

pause
