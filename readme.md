# Collap

A collaboration platform. More details soon.


## Setup

To set up a general collap development environment, follow these steps:

1. Create a directory that will hold the whole collap environment (Further called 'root directory').
2. Clone this repository to the directory 'collap-core' within the root directory.
Repeat this step with any other plugin repository you wish to add. Also clone the [collap-std](https://github.com/Collap/collap-std), 
[scripts](https://github.com/Collap/scripts) and [jade4j](https://github.com/Collap/jade4j) repositories.
3. Download Tomcat 8.0.5, rename the folder to 'tomcat' (or change the script files in 'scripts'),
and put it inside the root directory.
4. Download the MySQL JDBC connector jar (https://dev.mysql.com/downloads/connector/j/) and put it into tomcat's 'lib' folder.
5. Download Gradle and put the 'gradle' command in your execution path. Just make sure you can execute it from the command line.
6. Edit 'tomcat/conf/catalina.properties' and add `"${catalina.base}/collap/module/*.jar","${catalina.base}/collap/lib/*.jar"`
to the 'common.loader' property.
7. Make sure that the MySQL server is running and that you created a fresh database (Called 'collap' by default).
8. Build and run collap once by executing 'scripts/run.sh'. The collap config files will be created.
9. Specify the database login information in 'tomcat/collap/config/hibernate.properties'.
10. Execute the run script again. Open a browser window and type in 'http://127.0.0.1:8080/collap'. This default URL is not changeable at this point of development (i.e. 'collap').




## Connection to MySQL

### Unix

As the MySQL JDBC driver does not support *unix sockets*, on Unix you must allow an IP connection over a port.




## Development Directory Structure

The directory structure for development is the following:

    root
        collap-core
        collap-std
        jade4j
        lib
        module
        scripts
        tomcat

Any additional plugin projects/repositories should be placed in the root directory.