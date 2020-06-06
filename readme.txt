The compiled output files (.class) are provided as part of the submission. Otherwise the project can be compiled by importing the directory into IntelliJ and building it.
In order to run the application two terminal consoles must be open (Console A and B). First a remote object registry must be created by adding the following command in any of the consoles:

rmiregistry 8080 &

Next the server must initialise by first navigating to the output directory using the command cd and then the server can be started up by issuing the following command:

Prison Server

On another console(s) other clients can be initialised by running the following command and following the onscreen instructions:

MainClient

 

 

