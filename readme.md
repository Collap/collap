# Collap

A collaboration platform. More details soon.


## Setup

To set up a general collap development environment, follow these steps:

1. Create a directory that will hold the whole collap environment (Further called 'root directory').
x. Clone this repository to the directory 'collap-core' within the root directory.
Repeat this step with any other plugin repository you wish to add. Also clone the [collap-std](https://github.com/Collap/collap-std) and
[scripts](https://github.com/Collap/scripts) repositories.
x. Get the bryg jar required by the current version of collap and install it to your local repository (You can build it from source [here](https://github.com/Collap/bryg)).
x. Download Tomcat 8.0.5, rename the folder to 'tomcat' (or change the script files in 'scripts'), and put it inside the root directory.
x. Download the MySQL JDBC connector jar (https://dev.mysql.com/downloads/connector/j/) and put it into tomcat's 'lib' folder.
x. Download Gradle and put the 'gradle' command in your execution path. Just make sure you can execute it from the command line.
x. Edit 'tomcat/conf/catalina.properties' and add `"${catalina.base}/collap/module/*.jar","${catalina.base}/collap/lib/*.jar"`
to the 'common.loader' property.
x. Make sure that the MySQL server is running and that you created a fresh database (Called 'collap' by default).
x. Build and run collap once by executing 'scripts/run.sh'. The collap config files will be created.
x. Specify the database login information in 'tomcat/collap/config/hibernate.properties'.
x. Execute the run script again. Open a browser window and type in 'http://127.0.0.1:8080/collap'. This default URL is not changeable at this point of development (i.e. 'collap').




## Connection to MySQL

### Unix

As the MySQL JDBC driver does not support *unix sockets*, on Unix you must allow an IP connection over a port.




## Development Directory Structure

The directory structure for development is the following:

    root
        collap-core
        collap-std
        lib
        module
        scripts
        tomcat

Any additional plugin projects/repositories should be placed in the root directory.