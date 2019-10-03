[license]: https://github.com/Sean-Powell/DiscordBot/blob/master/LICENSE
[license-shield]: https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg
[ ![license-shield][] ][license]

# A Discord Bot
A simple discord bot that does some mild moderations such as keeping track of past names for people who spam name changes. Removes some very basic racism even in a vertical manner. Also has the ability to create links between words and youtube videos then whenever that word is typed the link is played in the channel that user is in.

## Commands:

* ,ban 'phrase' - bans a phrase from being used
* ,cleanse 'text' - clears all occurrences of the text in the channel in the last 'limit' messages 
* ,unban 'phrase' - unbans a phrase from being used
* ,limit 'limit' - sets the limit for the cleanse command
* ,dfban @user - bans the user from using deep fryer
* ,dfunban @user - unbans the user from using deep fryer
* ,member @user name - makes a new member entry for the user
* ,admin @user - makes a user into an admin
* ,help - displays the help page
* ,ytadd 'name' 'link' - creates a new keyword that when typed will play that youtube video in the users channel
* ,ytremove 'name' - removes the keyword from the list
* ,skip - skips the currently playing track
* ,volume 'volume' - sets the volume to the number provided, range 0-100
* ,nameban @user - makes it so that all the users name changes are tracked and if a duplicate is found they are kicked
* ,nameunban @user - removes the name restrictions on the user

## Made with:

* [Maven](https://maven.apache.org/) - Dependency Management
* [LavaPlayer](https://github.com/sedmelluq/lavaplayer) - A java audio libary
* [JDA](https://github.com/DV8FromTheWorld/JDA) - A Java wrapper for Discord

