# server & jni configuration

## vs2015 & opencv3.1 configure
please reference http://blog.csdn.net/lanergaming/article/details/48689841

## vs build jni configre
please reference http://blog.csdn.net/mingjava/article/details/180946  
when you build the project, please do not forget to select the **x64** platform

## make the jni.h file to be find in vs2015
in vs->project->c/c++ ->general->additional include library add：  
**%JAVA_HOME%/include** & **%JAVA_HOME%/include/win32**

## if javah command can not find the class file
please reference  http://stackoverflow.com/questions/3451378/how-to-run-javah-from-eclipse  
simply use the command `javah -classpath /path/to/project/classes com.mycompany.MyClass`, the case is to add the package name before the class name


## make the jni work with the tomcat
please reference http://myswirl.blog.163.com/blog/static/51318642201145104516632/  
add **%TOMCAT_HOME%** to the system environment
### put the dll file into the fellowing directoris
- %TOMCAT_HOME%/bin directory
- use the code System.out.println `((System.getProperty("java.library.path"))` in project to see the library path
