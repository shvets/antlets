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

if "%~1"=="-v" goto setVerifyOn

set CMD_LINE_ARGS=%CMD_LINE_ARGS% %~1

goto setupArgs

:setVerifyOn
set VERIFY_FLAG=%~1
shift
goto setupArgs

:doneStart

rem 4. launch JVM

SET LAUNCHER_CLASS=org.shvets.antlet.launcher.GenericLauncher
SET LIBS=-lib %ANT_HOME%\lib -lib %LIB_DIR%
SET CLASSPATH="%LIB_DIR%\launchers.jar"
SET MAIN_CLASS=bsh.Interpreter

if "%VERIFY_FLAG%"=="-v" goto runVerifier

%JAVA_HOME%\bin\java -Dmain.class=%MAIN_CLASS% -classpath %CLASSPATH% %LAUNCHER_CLASS% %LIBS% "%SCRIPT_NAME%" %CMD_LINE_ARGS%

goto done

:runVerifier

SET MAIN_CLASS=bsh.Parser

%JAVA_HOME%\bin\java -Dmain.class=%MAIN_CLASS% -classpath %CLASSPATH% %LAUNCHER_CLASS% %LIBS% "%SCRIPT_NAME%"

:done

pause
