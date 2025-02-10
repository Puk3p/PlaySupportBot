package Puk3p.util;

import Puk3p.embed.TicketEmbed;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.List;

public class Main extends Plugin {
    private JDA jda;
    private StaffManager staffManager;

    @Override
    public void onEnable() {
        getLogger().info("[DiscordBot] Pornit...");

        try {
            if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
                throw new RuntimeException("❌ Nu s-a putut crea directorul pluginului: " + getDataFolder().getAbsolutePath());
            }

            staffManager = new StaffManager(getDataFolder());
            staffManager.loadStaff();

            initializeDiscordBot();

            getProxy().getPluginManager().registerCommand(this, new ReloadStaffCommand());

            getLogger().info("[DiscordBot] Activ și funcțional!");
        } catch (Exception e) {
            getLogger().severe("[DiscordBot] Eroare la inițializare: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public void onDisable() {
        getLogger().info("[DiscordBot] Oprit...");

        if (jda != null) {
            try {
                jda.shutdown();
                getLogger().info("[DiscordBot] Botul Discord a fost închis cu succes!");
            } catch (Exception e) {
                getLogger().severe("[DiscordBot] Eroare la închiderea botului Discord: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void initializeDiscordBot() throws Exception {
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("DiscordBot token nu este setat în variabilele de mediu!");
        }

        jda = JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(
                        new TicketManager(staffManager),
                        new SlashCommandManager()
                )
                .build();
        jda.awaitReady();

        jda.getPresence().setActivity(Activity.competing("Play.Mc-Ro.Ro!"));

        SlashCommandManager.registerSlashCommands(jda);

        List<TextChannel> channels = jda.getTextChannelsByName("\uD83C\uDFAB┇tickete", true);
        if (!channels.isEmpty()) {
            TextChannel channel = channels.get(0);
            channel.sendMessageEmbeds(TicketEmbed.createTicketEmbed())
                    .setActionRow(TicketManager.createMainButtons())
                    .queue();
        } else {
            getLogger().warning("[DiscordBot] Canalul 'ticket' nu a fost găsit!");
        }

        getLogger().info("[DiscordBot] Botul Discord este online și funcțional!");
    }

    public class ReloadStaffCommand extends Command {
        public ReloadStaffCommand() {
            super("reloadstaff", "staff.reload", "rs");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            try {
                staffManager.loadStaff();
                sender.sendMessage("[DiscordBot] Lista de staff a fost reîncărcată cu succes!");
                getLogger().info("[DiscordBot] Lista de staff a fost reîncărcată.");
            } catch (Exception e) {
                sender.sendMessage("[DiscordBot] Eroare la reîncărcarea listei de staff: " + e.getMessage());
                getLogger().severe("[DiscordBot] Eroare la reîncărcarea listei de staff.");
                e.printStackTrace();
            }
        }
    }
}
