package Puk3p.util;

import Puk3p.embed.TicketEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.JDA;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.security.SecureRandom;
import java.util.List;

public class SlashCommandManager extends ListenerAdapter {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@";
    private static final SecureRandom RANDOM = new SecureRandom();


    public static void registerSlashCommands(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash("accept-unban", "Acceptă o cerere de unban")
                        .addOption(OptionType.STRING, "user", "Numele utilizatorului", true)
                        .addOption(OptionType.STRING, "staff", "Numele staff-ului care acceptă", false),

                Commands.slash("deny-unban", "Respinge o cerere de unban")
                        .addOption(OptionType.STRING, "user", "Numele utilizatorului", true)
                        .addOption(OptionType.STRING, "staff", "Numele staff-ului care respinge", false),

                Commands.slash("vote", "Votează pentru o decizie"),
                Commands.slash("change-password", "Schimba parola unui jucator")
                        .addOption(OptionType.STRING, "user", "Numele utilizatorului", true),
                Commands.slash("accept-recover-staff", "Acceptă o cerere de recuperare staff")
                        .addOption(OptionType.STRING, "user", "Numele utilizatorului", true)
                        .addOption(OptionType.STRING, "grad", "Introdu gradul utilizatorului", true)
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        switch (commandName) {
            case "accept-unban": //facut
                handleAcceptUnban(event);
                break;

            case "deny-unban": //facut
                handleDenyUnban(event);
                break;

            case "vote": //facut
                handleVoteCommand(event);
                break;

            case "change-password": //facut
                handleChangePassword(event);
                break;

            case "accept-recover-staff": //facut
                handleAcceptRecoverStaff(event);
                break;

            default:
                event.reply("Comanda nu este recunoscută.").setEphemeral(true).queue();
                break;
        }
    }

    private void handleAcceptRecoverStaff(SlashCommandInteractionEvent event) {

        List<UserRole> allowedRoles = List.of(
                UserRole.SERVER_OWNER,
                UserRole.ADMINISTRATOR,
                UserRole.MANAGER
        );

        var userOption = event.getOption("user");
        var gradOption = event.getOption("grad");

        if (!UserRole.isAuthorized(event, allowedRoles)) {
            event.reply("⛔ Nu ai permisiunea să folosești această comandă.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (userOption == null || gradOption == null) {
            event.reply("❌ Opțiunile `user` și `grad` sunt obligatorii.").setEphemeral(true).queue();
            return;
        }

        String user = userOption.getAsString();
        String grad = gradOption.getAsString().toLowerCase();

        List<String> validGrades = List.of("helper", "moderator", "admin");

        if (!validGrades.contains(grad)) {
            event.reply(String.format("❌ Gradul `%s` nu este valid. Grade permise: %s",
                            gradOption.getAsString(),
                            String.join(", ", validGrades.stream().map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).toList())))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String formattedGrad = grad.substring(0, 1).toUpperCase() + grad.substring(1);

        TextChannel channel = event.getChannel().asTextChannel();
        channel.sendMessageEmbeds(TicketEmbed.createStaffRecoveryAcceptEmbed(user, formattedGrad))
                .queue(
                        success -> {
                            executeRecoverStaff(user, formattedGrad);
                            System.out.println("[handleAcceptRecoverStaff] Mesaj de acceptare trimis cu succes.");
                        },

                        error -> System.err.println("[handleAcceptRecoverStaff] Eroare la trimiterea mesajului: " + error.getMessage())
                );

        event.reply(String.format("✅ Cererea de recuperare pentru utilizatorul `%s` cu gradul `%s` a fost acceptată!", user, formattedGrad))
                .setEphemeral(true)
                .queue();
    }


    private void executeRecoverStaff(String user, String grad) {
        String executeRecoverStaff = System.getenv("RECOVER_STAFF_IP");
        int portRecoverStaff = Integer.parseInt(System.getenv("RECOVER_STAFF_PORT"));
        String userNameRecoverStaff = System.getenv("RECOVER_STAFF_USER");
        String passwordRecoverStaff = System.getenv("RECOVER_STAFF_PASSWORD");
        SSHClient sshClient = new SSHClient(executeRecoverStaff, portRecoverStaff, userNameRecoverStaff,
                passwordRecoverStaff);

        try {
            String command = String.format("lp user %s parent add %s", user, grad);
            System.out.println("[SSHClient] Sending command: " + command);

            String response = sshClient.executeCommand(command);
            System.out.println("[SSHClient] Response: " + response);
        } catch (Exception e) {
            System.err.println("[SSHClient] Eroare la executarea comenzii: " + e.getMessage());
        }
    }

    private void handleVoteCommand(SlashCommandInteractionEvent event) {

        List<UserRole> allowedRoles = List.of(
                UserRole.SERVER_OWNER,
                UserRole.ADMINISTRATOR,
                UserRole.MANAGER,
                UserRole.STAFF
        );

        if (!UserRole.isAuthorized(event, allowedRoles)) {
            event.reply("⛔ Nu ai permisiunea să folosești această comandă.").setEphemeral(true).queue();
            return;
        }

        event.reply("Alegeți un vot:")
                .addActionRow(
                        Button.success("vote_pro", "✅ Pro"),
                        Button.danger("vote_contra", "❌ Contra")
                )
                .setEphemeral(true)
                .queue();
    }

    private void handleAcceptUnban(SlashCommandInteractionEvent event) {
        List<UserRole> allowedRoles = List.of(
                UserRole.SERVER_OWNER,
                UserRole.ADMINISTRATOR,
                UserRole.MANAGER,
                UserRole.UR_TEAM
        );

        if (!UserRole.isAuthorized(event, allowedRoles)) {
            event.reply("⛔ Nu ai permisiunea să folosești această comandă.").setEphemeral(true).queue();
            return;
        }

        var userOption = event.getOption("user");
        if (userOption == null) {
            event.reply("❌ Opțiunea `user` este obligatorie. Te rugăm să completezi comanda corect.")
                    .setEphemeral(true).queue();
            return;
        }

        String user = userOption.getAsString();
        String acceptUnbanIP = System.getenv("ACCEPT_UNBAN_IP");
        int portUnban = Integer.parseInt(System.getenv("ACCEPT_UNBAN_PORT"));
        String userNameUnban = System.getenv("ACCEPT_UNBAN_USER");
        String passwordUnban = System.getenv("ACCEPT_UNBAN_PASSWORD");

        SSHClient sshClient = new SSHClient(acceptUnbanIP, portUnban, userNameUnban,
                passwordUnban);


        try {
            System.out.println("[SSHClient] Connecting to SSH...");
            String command = "litebans:unban " + user + " -s";
            System.out.println("[SSHClient] Sending command: " + command);

            String response = sshClient.executeCommand(command);
            System.out.println("[SSHClient] Response: " + response);

            TextChannel channel = event.getChannel().asTextChannel();
            channel.sendMessageEmbeds(TicketEmbed.createUnbanNotificationEmbed(user)).queue();

            event.reply(String.format("✅ Utilizatorul `%s` a fost unbanat cu succes pe server!", user))
                    .setEphemeral(true).queue();
        } catch (Exception e) {
            System.err.println("[SSHClient] Error: " + e.getMessage());
            event.reply("❌ A apărut o eroare la conectarea prin SSH.").setEphemeral(true).queue();
        }
    }

    private void handleDenyUnban(SlashCommandInteractionEvent event) {

        List<UserRole> allowedRoles = List.of(
                UserRole.SERVER_OWNER,
                UserRole.ADMINISTRATOR,
                UserRole.MANAGER,
                UserRole.UR_TEAM
        );

        if (!UserRole.isAuthorized(event, allowedRoles)) {
            event.reply("⛔ Nu ai permisiunea să folosești această comandă.").setEphemeral(true).queue();
            return;
        }

        var userOption = event.getOption("user");
        if (userOption == null) {
            event.reply("❌ Opțiunea `user` este obligatorie. Te rugăm să completezi comanda corect.")
                    .setEphemeral(true).queue();
            return;
        }

        String user = userOption.getAsString();
        TextChannel channel = event.getChannel().asTextChannel();
        channel.sendMessageEmbeds(TicketEmbed.createBanNotificationEmbed(user, "Cerere respinsă")).queue();

        event.reply(String.format("❌ Cererea de unban a utilizatorului `%s` a fost respinsă.", user))
                .setEphemeral(true)
                .queue();
    }

    private void handleChangePassword(SlashCommandInteractionEvent event) {
        List<UserRole> allowedRoles = List.of(
                UserRole.SERVER_OWNER,
                UserRole.ADMINISTRATOR,
                UserRole.MANAGER
        );

        if (!UserRole.isAuthorized(event, allowedRoles)) {
            event.reply("⛔ Nu ai permisiunea să folosești această comandă.").setEphemeral(true).queue();
            return;
        }

        var userOption = event.getOption("user");
        if (userOption == null) {
            event.reply("❌ Opțiunea `user` este obligatorie. Te rugăm să completezi comanda corect.").setEphemeral(true).queue();
            return;
        }

        String user = userOption.getAsString();
        String newPassword = generateRandomPassword();

        String changePasswordIP = System.getenv("ACCEPT_CHANGE_PASSWORD_IP");
        int changePasswordPort = Integer.parseInt(System.getenv("ACCEPT_CHANGE_PASSWORD_PORT"));
        String changePasswordUser = System.getenv("ACCEPT_UNBAN_USER");
        String changePasswordPass = System.getenv("ACCEPT_UNBAN_PASSWORD");
        SSHClient sshClient = new SSHClient(changePasswordIP, changePasswordPort, changePasswordUser,
                changePasswordPass);

        try {
            System.out.println("[SSHClient] Connecting to SSH...");
            String command = "authme cp " + user + " " + newPassword;
            System.out.println("[SSHClient] Sending command: " + command);

            sshClient.executePasswordChangeCommand(command);
            System.out.println("[SSHClient] Password changed successfully.");

            TextChannel channel = event.getChannel().asTextChannel();
            channel.sendMessageEmbeds(TicketEmbed.createPasswordNotificationEmbed(user, newPassword)).queue();

            event.reply(String.format("✅ Parola pentru utilizatorul `%s` a fost schimbată cu succes!", user))
                    .setEphemeral(true).queue();
        } catch (Exception e) {
            System.err.println("[SSHClient] Error: " + e.getMessage());
            event.reply("❌ A apărut o eroare la conectarea prin SSH. Verificați logurile pentru detalii.").setEphemeral(true).queue();
        }
    }

    private String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}
