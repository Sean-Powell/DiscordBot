package commands;

import logging.Logger;
import net.dv8tion.jda.api.entities.Message;

public class Report extends Command{
    Report(Logger logger, String keyword, String description, boolean adminProtected){
        super(logger, keyword, description, adminProtected);
    }


    /*
    https://developer.github.com/v3/issues/#create-an-issue
    {
        "title": "Found a bug",
        "body": "I'm having a problem with this.",
        "assignees": [
            "octocat"
        ],
        "milestone": 1,
        "labels": [
            "bug"
        ]
    }
     */

    @Override
    public void function(Message message) {
        String rawMessage = message.getContentRaw();
        String[] split = rawMessage.split(" ");
        String body = split[1];


    }
}
