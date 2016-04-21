  Scripting with Java: Antlets, Beanshells, Groovies, JavaScripts etc.


Introduction

This approach is primarily intended for Java/Ant developers, who wants to use
their knowledge in more easy way and to solve wider range of calculation tasks,
including such as calculations for home and prototype programming.

Often you have to do some easy task, which requires simple calculation/transformation.
Here you have two choices: to write the batch script or the program in some programming
language (Java language, for example). Both approaches have drawbacks.

In the first case you are restricted with the language, which cannot express your
intentions in clear way. The second choise is more appropriate for (Java) programmer,
but you still have to think about such things as how to compile and how to run
Java code. Even more, if you want to slightly modify original code, you need to
remember, where sources and classes are. The situation is a little bit
overcomplicated.

The solution is to use code interpreters, written for Java Virtual Machine (JVM).
Java language as a platform now is mature enough and has a lot of convenient
and contemporary APIs. They can be used not only for scientific or enterprise tasks,
but also for home tasks. The scripting language engine for JVM works as simplified
front-end to the set of Java APIs.

Ant tool by itself also can be used as the scripting language. Sometimes it is
easier to use XML syntax that exists for Ant scripts and ready to use Ant tasks
then write them from scratch. We will go further, making Ant scripts
"first-class citizens". To do so, we use ".ant" extension as standard extension
for scripts, written in Ant syntax (former build.xml file).


Installation


We have the installation script which can help to automatically install 
various scripting engines as the extension to the Ant.

Before the installation, modify these variables: "JAVA_HOME", "ANT_HOME" and
"INSTALL_DIR" inside "install.bat" file to point to the correct location of 
preinstalled Java and Ant. For example:

SET JAVA_HOME=C:\Java\jdk1.4.2
SET ANT_HOME=C:\Java\apache-ant-1.6.1
SET INSTALL_DIR=C:\Antlets

Now you can run the script:

>install.bat

The installation is completed.


Registering scripts as the standard extensions


To be convenient tools, scripts should be assosiated with the appropriate
extensions, so easily started from the command line.

For Unix OS the assosiation mechanizm is driven from inside the script:

#hello_unix.bsh

#!/usr/java/bin/java bsh.Interpreter

System.out.println("Hello!!!");

For Windows we register our extensions in Windows registry. If you will
look inside "setup" directory, you will see *.bat and *.reg files that
help you with the registration process. For now we will register the following
extensions:

"ant" - for working with Ant engine;
"bsh" - for working with Beanshell engine;
"js" - for working with JavaScript engine;
"groovy" - for working with Groovy language;
"jar" - for working with Java "jar" files.

After the registration scripts can be launched from the command line, for example:

>hello.bsh

>test.ant


If you don't want to type the extension, do the following (Windows only):

set PATHEXT=.ant;.bsh;..js;.groovy;%PATHEXT%

and the scripts could be invoked as follows:

>hello

You can also use ASSOC and FTYPE Windows commands to register the extension:

>ASSOC .ant=AntScript
>FTYPE AntScript=C:\Antlets\bin\bsh.bat %1 %*


Ant scripts as regular scriptlets (antlets)


In the similar way, as we do it for scripting languages, such as JavaScript
or Beanshell, we could introduce scripting for Ant projects. Let's think
about it not only as the way for creating development projects, but also 
as the convenient infrastructure for expressing various scenarios, built
from the sequence of Ant tasks. Using optional "script" Ant task, we can
"plug-in" any other scripting language to it, greatly extending in such a way
basic functionality of the Ant.

We can register yet another extension for the Ant scripts: ".ant". Let's call
them "antlets", little Ant scripts. Such scripts are regular Ant build files,
but you don't have to specify build file name or to have predefined name,
like build.xml. For example:

<!-- hello.ant -->

<project name="hello.ant" default="start" basedir=".">

  <target name="start">
    <echo>
      Hello, World!!!
    </echo>
  </target>

</project>

You can use any other scripting language inside antlets. To work with scripting engines,
Ant uses Bean Scripting Framework (BSF). Jar files for this framework, as well as jars for
scripting engines should be present in CLASSPATH. Another solution is to use special
class loaders that know about their locations. The installation use the second approach.

For example:

<!-- Hello2.ant -->

<project name="hello2.ant" default="start" basedir=".">

  <target name="start" depends"start.beanshell, start.javascript"/>

  <target name="start.beanshell">
    <script language="beanshell">
      System.out.println("Hello, World!");
    </script>
  </target>

  <target name="start.javascript">
    <script language="javascript">
      importPackage(Packages.java.lang);

      System.out.println("Hello!!!");
    </script>
  </target>

</project>


Building command-line tools based on antlets


Antlets can be used as the convenient way for building command-line tools.
By mixing predefined Ant tasks with scripting languages code, we can build
something more powerful, like Java compiler, Jar tool etc. It could be general
tool or very specific tool that serves your today's needs. Inside this project
we have "prepare.ant" antlet which compiles Java sources, required
by the installation and prepares jar files.

Another example:

<project name="javac.antlet" default="javac" basedir=".">

  <target name="init">
    <property name="debug" value="false"/>
    <property name="optimize" value="false"/>
    <property name="deprecation" value="false"/>

    <property name="src.dir" value="."/>

    <input message="Input -d parameter:"
           addproperty="destination.dir"/>

    <input message="Input files mask:"
           addproperty="files.mask"/>

    <mkdir dir="${destination.dir}"/>
  </target>

  <target name="javac" depends="init">
    <javac destdir="${destination.dir}"
           includeAntRuntime="false"
           debug="${debug}"
           optimize="${optimize}"
           deprecation ="${deprecation}">
        <src path="${src.dir}"/>
        <include name ="${files.mask}"/>
    </javac>
  </target>

</project>


Executing antlets from inside "jar" files


Current implementation of Java execution mechanizm uses special MANIFEST.MF file
properties to represent Java "jar" file as an executable:

>java -jar jarfile

By using antlets we can build more powerful framework. Say, we want to assign set of
commands with given jar file (not only starting the application). To do it, we prepare
special file with the predefined name: "default.ant". This file resides inside "META-INF"
directory, similar to "MANIFEST.MF" file.

As an example, let's assosiate two commands: "start" and "stop" with our jar file.
Antlet for this case looks like:

<project name="jar.invocation.antlet" default="start" basedir=".">

  <target name="start">
    <java classname="Test" fork="yes">
      <classpath>
        <pathelement location="${jar.file}" />
      </classpath>

      <arg line="start"/>
    </java>
  </target>

  <target name="stop">
    <java classname="Test" fork="yes">
      <classpath>
        <pathelement location="${jar.file}" />
      </classpath>

      <arg line="stop"/>
    </java>
  </target>
</project>

Now, we have to assosiate "jar" extension with special program, which will
extracts this antlet from the archive, prepares "project" object and then executes
specified target. The variable "${jar.file}" is prepared
inside this special program.

The typical execution will look like:

>Test.jar
>Test.jar start
>Test.jar stop

First 2 commands do exactly the same: execute "start" target. The 3rd command executes
"stop" target.

The command

>Test.jar start stop

runs "start" then "stop" targets.


Working with command line parameters


When we work with the scripting languages outside of antlets, we have access
to the command line in the standard for this language way (see documentation
for the given language).

For antlets we have to introduce special mechanizm, which can "filter" Ant targets
and separate them from command line parameters:

>CommandLine.ant start [a] [b] [c]

In the above example we have "start" target and command line: "a b c".

In the similar wa we can prepate command line for the "jar" executable:

>Test.jar start [a] [b] [c]


Repetitive execution of Ant commands


It is possible to load Ant script into the memory (for standalone antlets or
antlets from jar files) and run them periodically without reloading Ant
script for every execution.

To do it, place "-i" parameter on your command line:

>test.ant -i

or

>Test.jar -i

In this case the user has the ability to enter various targets interactively
along with the command line parameters.

To leave the interactive mode, enter the "exit" ("e") command.


Standalone JavaScript scripts


This project is equipped with the implementation of JavaScript with
the codename Rhino.

The simplest example looks like:

// hello.js

importPackage(Packages.java.lang);

System.out.println("Hello!!!");


Standalone Benshell scripts


If you don't like JavaScript syntax, but still like the idea of dynamic/scripting
languages, let's play with Beanshell. This interpreter is more convenient,
because it uses semantic of Java language.  Versions 1.2-1.3 of it support
some subset of Java language and version 2.x interpretes full Java programs.

Why to use Beanshell, not Java directly? First, you can interprete Java code. Second,
it has special command like addClassPath() which allows to build scripts as powerful as
batch files.

Example:

// hello.bsh

System.out.println("Hello!!!");

Another example:

// demo.bsh

addClassPath("lib/ant.jar");
addClassPath("lib/xercesImpl.jar");

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

Project project = new Project();
project.init();

File buildFile = new File("build.xml");

ProjectHelper.getProjectHelper().parse(project, buildFile);

project.executeTarget(project.getDefaultTarget());


Using external libraries with scripts


If the script requires external libraries you can also use -lib
parameter from the command line:

>Test.ant -lib lib
>Test.bsh -lib lib

These commands will pick up all libraries from "lib" folder.

If you want to do it automatically, you can add special "addtoclassloader"
Ant task. This class is included int installation. Example:

<!-- Test.ant -->

<?xml version="1.0" encoding="UTF-8"?>

<project name="Test.ant" default="usage" basedir=".">

  <addtoclassloader>
    <path>
      <fileset dir="lib">
        <include name="*.jar"/>
      </fileset>
    </path>
  </addtoclassloader>

  <... do something ...>

</project>


Standalone Groovy scripts


In the same way as we do it for Beanshell and JavaScript scripts, we can
create and run scripts for Groovy language. It is very interesting new
language with the set of advanced features such as closures, native support
for various markup languages (html, xml, ant etc.),  etc.

Simplest example is below:

// helloworld.groovy

println("Hello world")

Groovy scripts can be used to get access to Ant API in "Groovy"-like way.
For example:

ant = new AntBuilder()

ant.echo("hello")

myDir = "dist/src"

ant.mkdir(dir:myDir)

ant.copy(todir:myDir) {
  fileset(dir:".") {
    include(name:"**/*.groovy")
  }
}

ant.input(message:"Press Return key to continue...")

ant.delete(dir:"target")

ant.echo("done")


Antlet adapter


If you are not ready to convert your favorite project file into antlet,
let's use the adapter:

<?xml version="1.0" encoding="UTF-8"?>

<project name="my.favorite.project.ant" default="usage" basedir=".">

 <import file="build.xml" />

</project>


Versions for used components


Ant       1.6.1       http://ant.apache.org
BSF       2.3.0-rc1   http://jakarta.apache.org/bsf
Beanshell 2.0b1       http://beanshell.org
JS        1.5R3       http://www.mozilla.org/rhino
Groovy    1.0beta4    http://groovy.codehaus.org
Jelly     1.0-beta-3  http://jakarta.apache.org/commons/jelly
Maven     1.0-rc2     http://maven.apache.org
Antlets   1.0         http://sourceforge.net/projects/antlets


Implementation Notes

This project uses GenericLauncher class which is based on the AntLauncher class
from the Ant installation. This helps us to simplify batch files and to introduce
dynamic class loading. In such a way we can easily call scripting engines
from Ant scripts as well as Ant API from inside scripting engines. 

GenericLauncher class can be used for launching any project, not only antlets.

Use cases

1. Enterprise

You are developing the new software platform and future features are unknown
at the current time. You can have customer-specific customization as scripting
code, leaving main core intacted. Later on, if you decide that code is
production-ready, it can be moved into the core. You have to move only general
ideas from your script into the core.

2. Reverse Engineering

You are trying to understand how 3-rd party library works. Unfortunately, 
you don't have the sources. You can decompile the bytecode and analyze it visually.
If you have the question about spesific chunk of code (say, what does this static 
code do?), you can copy this code as the Beanshell script and run-test it immediately.

3. Templates generation

Groovy has an excellent feature such as native support for various markup
languages (html, xml etc.). You can use it to generate xml/html files for tests.
Jelly is also can do it.

4. Fast customization

Adding <addtoclassloader> task to Ant engine simplifies adding external libraries 
to your antlet. For example, you have the Ant script, that is relying on 3-rd party
library. You have to download this library and install it somewhere, according to 
the current Ant script. You probably have this library, previously installed somewhere.
To win in this situation, you can write simple script-adapter:

<project name="3rdparty.adapter.ant" basedir=".">

  <property name="3rdparty.lib.home" value="somewhere_on_drive"/>

  <!-- This is required if the script wants global variables. -->
  <property name="env.3RD_PARTY_HOME" value="${3rdparty.lib.home}"/>


  <addtoclassloader>
    <path>
      <fileset dir="${3rdparty.lib.home}">
        <include name="*.jar"/>
      </fileset>
    </path>
  </addtoclassloader>

  <import file="${basedir}/build.xml"/>

</project>


Results

1. Ant installation is separated from it's extensions, such as scripting engines,
networking etc. We can easily upgrade Ant whithout touching original Ant installation.

2. Allows to write standalone Ant, Beanshell, JavaScript etc. scripts. These
script languages are out-of-the-box, conveniently accessible from the command line.

3. Introduces new Java invocation mechanizm for jar files.

4. Simplifies prototyping.

5. Differentiates Ant scripts from other xml files.
