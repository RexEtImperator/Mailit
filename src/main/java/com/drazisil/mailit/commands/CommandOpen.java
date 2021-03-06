/*
 * Mailit is a Minecraft Bukkit plugin to allow for player mailboxes
 * Copyright (C) 2020 Joe Becher (Drazisil)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.drazisil.mailit.commands;

import com.drazisil.mailit.MailPackage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;

import static com.drazisil.mailit.Mailit.plugin;
import static com.drazisil.mailit.PlayerUtil.isPlayer;
import static java.lang.String.format;

public class CommandOpen implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!isPlayer(sender)) {
            sender.sendMessage("This command can only be ran as a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) return false;

        int packageIdx = Integer.parseInt(args[0]);

        ArrayList<MailPackage> pkgs = null;
        try {
            pkgs = plugin.getMailboxManager()
                    .getPackagesByReceiver(player);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (pkgs == null) {
            player.sendMessage("You don't have any packages. It may still be in transit.");
            return true;
        }

        // Player entered an invalid index
        if (packageIdx >= pkgs.size()) {
            player.sendMessage("I'm sorry, we can't find a package with that id! Can you check the number again?");
            return true;
        }

        MailPackage mailPackage = pkgs.get(packageIdx);

        // Unable to locate package
        if (mailPackage == null) {
            player.sendMessage("I'm sorry, we can't find a package with that id! Can you check the number again?");
            return true;
        }

        player.sendMessage(format("Package %d is from %s",
                packageIdx,
                mailPackage.getFrom().getName()));

        plugin.getMailboxManager().open(mailPackage, player);

        return true;
    }
}
