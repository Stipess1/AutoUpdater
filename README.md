# Spigot Auto Updater
Spigot Auto Updater is easy, safe auto-updater for your plugins.

![alt text](https://spiget.org/img/logo-plain-x64.png "Spiget")

Uses Spiget [API](https://spiget.org/) to get information of your plugin.

## Usage
```java
String ID = "29742";
Updater updater = new Updater(this, ID, this.getFile(), Updater.UpdateType.CHECK_DOWNLOAD, false);
```
`this` - is the plugin instance.

`ID` - is the project id which can be found on project site.

`this.getFile()` - The plugin's file name.

`Updater.UpdateType.CHECK_DOWNLOAD` - sets the download type, this type will check if new update exists and immediately downloads if it does.

`false` - if set to false Updater won't log the process to console.

## UpdateType
There are 3 different update types:
1. `Updater.UpdateType.CHECK_VERSION` - Only check the version of the latest file. (Which can be obtained with Updater#getVersion)
2. `Updater.UpdateType.DOWNLOAD` - Downloads the latest file without checking version.
3. `Updater.UpdateType.CHECK_DOWNLOAD` - Checks if update exists if it does downloads it.

## Result
There are 5 different result types:
1. `Updater.Result.UPDATE_FOUND` - When update is found.
2. `Updater.Result.NO_UPDATE` - When no update is found.
3. `Updater.Result.SUCCESS` - When file is successfuly downloaded.
4. `Updater.Result.FAILED` - For some reason update was not successfuly downloaded.
5. `Updater.Result.BAD_ID` - If bad ID was passed.

You can get Result with Updater#getResult.
    
## ID
You can find your project id on spigot resource site.

![alt text](https://i.imgur.com/sReoiZd.png)
