SET JAVA_HOME=d:\Java\jdk1.4.2

SET ANT_HOME=d:\Env\apache-ant-1.6.1
SET MAVEN_HOME=d:\Env\maven-1.0-rc3

SET INSTALL_DIR=d:\Env\Antlets

SET INSTALL_DIR_PROPERTY=-Dinstall.dir=%INSTALL_DIR%

SET LIB_DIR=.

SET CLASSPATH="%ANT_HOME%\lib\ant-launcher.jar"
SET START_CLASS=org.apache.tools.ant.launch.Launcher
SET LIBS=-lib %LIB_DIR%\lib


%JAVA_HOME%\bin\java -Dant.home="%ANT_HOME%" -Dmaven.home=%MAVEN_HOME% %INSTALL_DIR_PROPERTY% -classpath %CLASSPATH% %START_CLASS% %LIBS% -f antlets.ant install.antlets
