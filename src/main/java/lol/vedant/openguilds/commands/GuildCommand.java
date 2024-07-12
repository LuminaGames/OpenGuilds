package lol.vedant.openguilds.commands;

import lol.vedant.openguilds.menu.GuildMenu;
import me.despical.commandframework.Command;
import me.despical.commandframework.CommandArguments;
import org.bukkit.entity.Player;

public class GuildCommand {

    @Command(
            name = "guild",
            desc = "GUI for your guild",
            aliases = { " guilds" },
            senderType = Command.SenderType.PLAYER
    )
    public void execute(CommandArguments args) {

        Player player = (Player) args.getSender();
        new GuildMenu(27, "Guild Menu", player).open(player);

    }
}
