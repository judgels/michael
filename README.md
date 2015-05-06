#Judgels Michael 

##Description
Michael is a builder and watcher application built using [Play Framework](https://www.playframework.com/) to control judgels applications.

##Set Up And Run
To set up Michael, you need to:

1. Clone [Judgels Play Commons](https://github.com/ia-toki/judgels-play-commons) into the same level of Michael directory, so that the directory looks like:
    - Parent Directory
        - judgels-play-commons
        - judgels-michael

2. Copy conf/application_default.conf into conf/application.conf and change the configuration accordingly. **Refer to the default configuration file for explanation of the configuration keys.** Sealtiel needs Rabbitmq to store incoming messages, you need to setup the access in the configuration.

3. Copy conf/db_default.conf into conf/db.conf and change the configuration accordingly. **Refer to the default configuration file for explanation of the configuration keys.** 

To run Michael, just run "activator" then it will check and download all dependencies and enter Play Console.
In Play Console use "run" command to run Sandalphon. By default it will listen on port 9000. For more information of Play Console, please read the [documentation](https://www.playframework.com/documentation/2.3.x/PlayConsole).

The version that is recommended for public use is [v0.4.0](https://github.com/ia-toki/judgels-michael/tree/v0.4.0).
