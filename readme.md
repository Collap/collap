# Collap

A collaboration platform. More details soon.


## Setup

To set up a general collap development environment, follow these steps:

1. Create a directory that will hold the whole collap environment (Further called "root directory").
2. Clone this repository to the directory 'collap-core' within the root directory.
Repeat this step with any other plugin repository you wish to add (including the 'collap-std' repository).
3. Download Tomcat 8.0.5, rename the folder to "tomcat" (or change the dozen gradle.properties files of every repository),
and put it inside the root directory.
4. Download the MySQL JDBC connector jar (https://dev.mysql.com/downloads/connector/j/) and put it into tomcat's 'lib' folder.
5. Download Gradle and put the gradle command in your execution path. Just make sure you can execute it from the command line.
6. Build the core by going into the 'collap-core' directory and executing the command 'gradle build tomcatCopy tomcatClean'.
7. Build any other plugin repository.
8. Make sure that the MySQL server is running.
9. Start tomcat once, so the collap config files are properly created. Specify the database login information in 'tomcat/collap/config/hikari.properties'.
10. Restart tomcat. Open a browser window and type in 'http://127.0.0.1:8080/collap'. This default URL is not changeable at this point of development (i.e. 'collap').

*Note*: You can use the scripts from the 'scripts' repository to optimize your workflow. Be aware, though, that these scripts
require the standard directory names (tomcat, plugin) as well as collap-std.


## Development Directory Structure

The directory structure for development (plugin or core) is the following:

    root
        collap-core
        collap-std
        plugin
        scripts
        tomcat

Any additional plugin projects/repositories should be placed in the root directory.