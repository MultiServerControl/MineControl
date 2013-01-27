MineControl
===========

MineControl is a java based library for managing minecraft servers via command line (webinterface and app - in future releases).

You want to work on the project with us?
--------
No problem! MineControl is Open-Source (licence information follows), we'd really enjoy to work with you!
##### What you'll need?
* Linux OS
* Java JDK 5 or greater
* Git `install git-core`
* [Maven](http://maven.apache.org/download.cgi)

1. Fork the project on GitHub or check it out with `git clone https://github.com/MultiServerControl/MineControl.git`
2. Move to the root of the project and call `mvn clean install` followed by `mvn assembly:single` to build the library
3. Get the [minecraft_server.jar](https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar) and save it to the target directory
4. Now call the minecontrol library `java -jar minecontrol.jar <server> (start|stop|restart|status|pid)`
