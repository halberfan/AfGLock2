package de.afgmedia.afglock2.commands;

import de.afgmedia.afglock2.locks.group.LockGroup;
import de.afgmedia.afglock2.locks.settings.AllowSetting;
import de.afgmedia.afglock2.locks.settings.DenySetting;
import de.afgmedia.afglock2.locks.settings.InfoSetting;
import de.afgmedia.afglock2.locks.settings.RemoveSetting;
import de.afgmedia.afglock2.main.AfGLock;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CMDlock implements CommandExecutor {

    private AfGLock plugin;

    public CMDlock(AfGLock plugin)
    {
        this.plugin = plugin;
        plugin.getCommand("lock").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
    {

        if (!(cs instanceof Player)) {
            cs.sendMessage("§cDieser Command ist nur für Spieler!");
            return true;
        }

        Player p = (Player) cs;

        if (args.length < 1) {
            p.sendMessage(help());
            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("info")) {

                InfoSetting setting = new InfoSetting(p);
                plugin.getProtectionManager().setProtectionSetting(p, setting);
                p.sendMessage("§cBitte klick jetzt auf eine Sicherung");
            } else if(args[0].equalsIgnoreCase("delete")) {
                RemoveSetting setting = new RemoveSetting(p);
                plugin.getProtectionManager().setProtectionSetting(p, setting);
                p.sendMessage("§cBitte klick jetzt auf eine Sicherung");
                return true;
            }
            else p.sendMessage(help());

        }
        else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("add")) {

                String name = args[1];
                //Is Group
                if(name.startsWith("$")) {
                    name = name.replace("$", "");
                    LockGroup group = plugin.getProtectionManager().getLockGroups().get(name);

                    if(group == null) {
                        p.sendMessage("§cDiese Gruppe gibt es nicht!");
                        return true;
                    }

                    if(!group.isMember(p.getUniqueId())) {
                        p.sendMessage("§cDu gehörst gar nicht zu der Gruppe, naja, ist ja deine Sache!");
                    }

                    AllowSetting allowSetting = new AllowSetting(AllowSetting.AllowSettingType.GROUP);
                    allowSetting.setGroup(name);
                    p.sendMessage("§cKlick jetzt auf eine Sicherung!");
                    plugin.getProtectionManager().setProtectionSetting(p, allowSetting);

                } else {

                    OfflinePlayer op = Bukkit.getOfflinePlayer(name);
                    if(op.getFirstPlayed() == 0) {
                        p.sendMessage("§cDieser Spieler hat hier noch nie gespielt, naja, ist ja deine Sache!");
                    }

                    AllowSetting allowSetting = new AllowSetting(AllowSetting.AllowSettingType.PLAYER);
                    allowSetting.setUuid(op.getUniqueId().toString());
                    p.sendMessage("§cKlick jetzt auf eine Sicherung!");
                    plugin.getProtectionManager().setProtectionSetting(p, allowSetting);

                }

            }
            else if (args[0].equalsIgnoreCase("remove")) {

                String name = args[1];
                //Is Group
                if(name.startsWith("$")) {
                    name = name.replace("$", "");
                    LockGroup group = plugin.getProtectionManager().getLockGroups().get(name);

                    if(group == null) {
                        p.sendMessage("§cDiese Gruppe gibt es nicht!");
                        return true;
                    }

                    DenySetting denySetting = new DenySetting(AllowSetting.AllowSettingType.GROUP);
                    denySetting.setGroup(name);
                    p.sendMessage("§cKlick jetzt auf eine Sicherung!");
                    plugin.getProtectionManager().setProtectionSetting(p, denySetting);

                } else {

                    OfflinePlayer op = Bukkit.getOfflinePlayer(name);

                    DenySetting denySetting = new DenySetting(AllowSetting.AllowSettingType.PLAYER);
                    denySetting.setUuid(op.getUniqueId().toString());
                    p.sendMessage("§cKlick jetzt auf eine Sicherung!");
                    plugin.getProtectionManager().setProtectionSetting(p, denySetting);

                }

            }
            else p.sendMessage(help());

        }
        else if (args.length == 3) {

            if (args[0].equalsIgnoreCase("group")) {

                if (args[1].equalsIgnoreCase("create")) {

                    final String name = args[2];
                    if(plugin.getProtectionManager().getLockGroups().get(name) != null) {
                        p.sendMessage("§cEs gibt bereits eine Gruppe mit diesem Namen!");
                        return true;
                    }

                    LockGroup lockGroup = new LockGroup(name, p.getUniqueId());

                    plugin.getProtectionManager().getLockGroups().put(name, lockGroup);
                    p.sendMessage("§cDu hast erfolgreich die Gruppe §e"+name+" §cerstellt!");
                }
                else if (args[1].equalsIgnoreCase("info")) {

                    String name = args[2];
                    LockGroup group = plugin.getProtectionManager().getLockGroups().get(name);
                    if(group == null) {
                        p.sendMessage("§cDiese Gruppe gibt es nicht!");
                        return true;
                    }

                    p.sendMessage("§e===========");
                    p.sendMessage("§eGruppe: §c"+group.getName());
                    p.sendMessage("§eBesitzer: §c"+Bukkit.getOfflinePlayer(group.getOwner()).getName());
                    p.sendMessage("§eMitglieder: ");
                    for (String s : group.getMembers()) {
                        UUID uuid = UUID.fromString(s);
                        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                        p.sendMessage("§c- "+op.getName());
                    }
                    p.sendMessage("§e===========");

                }

            }

        } else if(args.length == 4) {

            if(args[0].equalsIgnoreCase("group")) {

                if(args[1].equalsIgnoreCase("add")) {

                    final String player = args[2];
                    OfflinePlayer op = Bukkit.getOfflinePlayer(player);
                    LockGroup group = plugin.getProtectionManager().getLockGroups().get(args[3]);

                    if(group == null) {
                        p.sendMessage("§cDiese Gruppe gibt es nicht!");
                        return true;
                    }

                    if(!group.getOwner().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
                        p.sendMessage("§cDu bist nicht der Besitzer der Gruppe!");
                        return true;
                    }

                    if(!group.getMembers().contains(op.getUniqueId().toString())) {
                        group.addMember(op.getUniqueId());
                        p.sendMessage("§cDer Spieler wurde hinzugefügt?");
                    } else {
                        p.sendMessage("§cDer Spieler ist bereits in der Gruppe!");
                        return true;
                    }

                }
                else if (args[1].equalsIgnoreCase("remove")) {

                    final String player = args[2];
                    OfflinePlayer op = Bukkit.getOfflinePlayer(player);
                    LockGroup group = plugin.getProtectionManager().getLockGroups().get(args[3]);

                    if(group.getMembers().contains(op.getUniqueId().toString())) {
                        group.removeMember(op.getUniqueId());
                        p.sendMessage("§cDer Spieler wurde entfernt");
                    } else {
                        p.sendMessage("§cDer Spieler ist nicht in der Gruppe!");
                        return true;
                    }

                }

            }
        }

        return false;
    }

    private String help()
    {

        return "§b=====§c/lock§b=====\n" +
                "§e/lock info §cZeigt Informationen zu der Sicherung an\n" +
                "§e/lock delete §cLösche eine Sicherung\n"+
                "§e/lock add <$Gruppe/Spieler> §cFügt einen Spieler oder Gruppe hinzu\n" +
                "§e/lock remove <$Gruppe/Spieler> §cEntfernt einen Spieler oder Gruppe\n" +
                "§e/lock group create <Name> §cErstellt eine Gruppe\n" +
                "§e/lock group add <Spieler> <Gruppe> §cFügt einen Spieler zur Gruppe hinzu\n" +
                "§e/lock group remove <Spieler> <Gruppe> §cEntfernt einen Spieler von einer Gruppe\n" +
                "§e/lock group info <Gruppe> §cZeigt Informationen zur Gruppe\n" +
                "§eMit dem '$' - Zeichen zeigst, du dass es sich bei dem Command um eine Gruppe handlet" +
                "§eDu weißt null worum es hier geht? Hier findest du mehr Informationen: https://bit.ly/2MmNeK1";

    }

}
