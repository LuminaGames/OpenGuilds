package lol.vedant.openguilds.guild;

import org.bukkit.entity.Player;

public class GuildPlayer {

    private final Player player;

    public GuildPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
