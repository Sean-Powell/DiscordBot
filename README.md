[license]: https://github.com/Sean-Powell/DiscordBot/blob/master/LICENSE
[license-shield]: https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg
[ ![license-shield][] ][license]
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1773c4a298ac4dc082b5ff798887868d)](https://www.codacy.com/manual/Sean-Powell/DiscordBot?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Sean-Powell/DiscordBot&amp;utm_campaign=Badge_Grade)

# A Discord Bot
A simple discord bot that does some mild moderations such as keeping track of past names for people who spam name changes. Removes some very basic racism even in a vertical manner. 

Also has the ability to create links between words and youtube videos then whenever that word is typed the link is played in the channel that user is in. This is provided that the user has been in the channel for the required ammount of time. By default this is 5 seconds, this is to stop people from joining a channel and using a command to annoy the users in that channel.

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
* ,ytlist - lists all the YouTube keywords configured on the bot.
* ,skip - skips the currently playing track
* ,clear - skips all the tracks in the queue and the current track being played
* ,volume 'volume' - sets the volume to the number provided, range 0-100
* ,nameban @user - makes it so that all the users name changes are tracked and if a duplicate is found they are kicked
* ,nameunban @user - removes the name restrictions on the user
* ,nranks - lists the users and the amount of times that user has sent the "n word" in the past

## Made with:

* [Maven](https://maven.apache.org/) - Dependency Management
* [LavaPlayer](https://github.com/sedmelluq/lavaplayer) - A Java audio library
* [JDA](https://github.com/DV8FromTheWorld/JDA) - A Java wrapper for Discord

