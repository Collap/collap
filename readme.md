# Collap

A collaboration platform.


## Setup

1. Clone the repository to a local directory (for example 'collap').
2. Download Tomcat 8, rename the folder to 'tomcat', and put it inside the collap directory ('collap/').
3. Download Gradle and put the 'gradle' command in the Windows PATH or make it available in the console otherwise.
4. Build collap with `gradle build tomcatCopy tomcatClean` with the collap directory as working directory in the console.
5. Add a 'collap.properties' file to the root directory of tomcat and specify the database information (see 'app/res/default.properties').
6. Start tomcat with a console from the tomcat root folder.
7. Open a browser window and type in `http://127.0.0.1:8080/collap'.