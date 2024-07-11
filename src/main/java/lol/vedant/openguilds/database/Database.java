package lol.vedant.openguilds.database;

import lol.vedant.openguilds.guild.Guild;

import java.util.UUID;

public interface Database {

    void init();

    boolean isInGuild(UUID player);

    Guild getGuild(int id);

    Guild getGuildByPlayer(UUID player);


}
