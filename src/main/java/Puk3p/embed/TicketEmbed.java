package Puk3p.embed;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class TicketEmbed {
    private static final String iconURL= "https://cdn.discordapp.com/attachments/1147467499256938557/1322898549331071047/a_be31be745c02dab8a165276c44a58dc9.png?ex=67728cc8&is=67713b48&hm=c35c02d80aecd1e6c4215f9faf6ea0a0871bb41d7e0773009ff049c1638e0415&";

    public static String generateFooter(String ticketType) {
        return switch (ticketType.toLowerCase()) {
            case "support" -> "© Play Squad | Contactează staff-ul pentru suport suplimentar\n© Puk3p | .callmegeorge";
            case "recover_password" -> "© Play Squad | Parola ta a fost resetată cu succes. Contactează-ne dacă întâmpini probleme.\n© Puk3p | .callmegeorge";
            case "recover_grade" -> "© Play Squad | Cererea ta pentru recuperarea gradului a fost procesată cu succes.\n© Puk3p | .callmegeorge";
            case "unban" -> "© Play Squad | Sistem de Cereri Unban. Respectă regulile pentru a evita sancțiuni viitoare.\n© Puk3p | .callmegeorge";
            case "staff_request" -> "© Play Squad | Cerere Staff - Apreciem dorința ta de a contribui la comunitate.\n© Puk3p | .callmegeorge";
            case "donation" -> "© Play Squad | Mulțumim pentru sprijinul acordat comunității noastre! Fiecare donație contează.\n© Puk3p | .callmegeorge";
            case "helper_application" -> "© Play Squad | Îți mulțumim pentru interesul de a te alătura echipei noastre.\n© Puk3p | .callmegeorge";
            case "ban_notification" -> "© Play Squad | Sancțiunea ta rămâne activă. Contactează staff-ul dacă ai întrebări.\n© Puk3p | .callmegeorge";
            case "unban_notification" -> "© Play Squad | Felicitări pentru unban! Bucură-te de joc și respectă regulile serverului.\n© Puk3p | .callmegeorge";
            case "instructions" -> "© Play Squad | Urmează pașii de mai sus pentru o soluționare rapidă.\n© Puk3p | .callmegeorge";
            case "ticket_details" -> "© Play Squad | Contactează staff-ul pentru asistență suplimentară.\n© Puk3p | .callmegeorge";
            case "questions_or_issues_emb" -> "💡 Echipa PlaySupport este aici pentru tine!\n © Puk3p | .callmegeorge";
            case "general_problems" -> "© Play Squad | Suntem aici să te ajutăm!\n© Puk3p | .callmegeorge";
            case "media_req" -> "© Play Squad | Cerințe principale\n© Puk3p | .callmegeorge";
            case "builder" -> "© Play Squad | Îți mulțumim pentru dedicație!\n© Puk3p | .callmegeorge";
            default -> "© Play Squad | Sistem de Tickete - Contactează staff-ul pentru mai multe informații.\n © Puk3p | .callmegeorge";
        };
    }


    public static MessageEmbed createTicketEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("📩 Play Support")
                .setDescription(Emoji.NPC.getValue() + " Te rugăm să citești informațiile și regulile de mai jos înainte de a deschide un ticket. " + Emoji.MINECRAFT_CUBE.getValue() + "\n\u200B")
                .setColor(new Color(255, 0, 0))
                .addField("📖 Introducere",
                        "```#Ticketele sunt pentru probleme sau întrebări care nu pot fi rezolvate în privat cu staff-ul sau membrii administrației.```\n\u200B",
                        false)
                .addField("ℹ️ Informații",
                        "```" +
                                "✔️ Ticketele sunt private și vizibile doar pentru staff-ul autorizat și utilizatorul care le-a creat.\n" +
                                "✔️ Fiecare ticket va avea un nume unic generat automat:\n" +
                                "   ➤ Ex: #ticket-{numele-tău}-{motivul}\n" +
                                "✔️ Staff-ul va răspunde cât mai repede posibil, în funcție de volumul de cereri.\n" +
                                "✔️ Toate răspunsurile și informațiile oferite în cadrul ticketelor vor fi tratate cu confidențialitate maximă.\n" +
                                "✔️ După închiderea unui ticket, acesta va fi arhivat automat și nu va mai putea fi accesat.\n" +
                                "```\n\u200B",
                        false)
                .addField("⚠️ Reguli",
                        "```" +
                                "1️⃣ Creează un ticket doar dacă problema sau întrebarea ta necesită asistență directă din partea staff-ului.\n" +
                                "2️⃣ Nu abuza de sistemul de tickete (ex: cereri repetitive, spam, motive nejustificate).\n" +
                                "3️⃣ Respectă limbajul și conduita în cadrul ticketelor. Înjurăturile sau insultele nu vor fi tolerate.\n" +
                                "4️⃣ Asigură-te că furnizezi toate informațiile relevante pentru problema ta. Informațiile incomplete pot întârzia rezolvarea.\n" +
                                "5️⃣ Nu partaja informații confidențiale sau sensibile în cadrul ticketelor (ex: parole, date personale).\n" +
                                "❌ Încălcarea regulilor poate duce la închiderea ticketului fără notificare și sancțiuni suplimentare!" +
                                "```\n\u200B",
                        false)
                .setFooter(generateFooter("support"), iconURL);

        return embed.build();
    }


    public static MessageEmbed createTicketDetailsEmbed(String userName, String reason, String staffMemberName) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("📩    Detalii Ticket  |  PlaySquad")
                .setColor(new Color(0, 255, 127))
                .addField("👤 Utilizator", String.format("`%s`", userName), false)
                .addField("❓ Motiv", String.format("`%s`", reason), false)
                .addField("🎯 Staff asignat", String.format("`%s`", staffMemberName), false)
                .setFooter(generateFooter("ticket_details"), iconURL);

        return embed.build();
    }

    public static MessageEmbed createInstructionsEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("🗄️    Detalii Ticket  |  PlaySquad")
                .setDescription("‎ \nℹ️  **Informatii**\n```\n🎉 Mulțumim pentru răbdare!\n" +
                        "Echipa noastră va fi alături de dumneavoastră în cel mai scurt timp posibil pentru a vă oferi sprijinul necesar. " +
                        "Între timp, pentru a ne asigura că vă putem ajuta cât mai repede, vă rugăm să completați următorul model:\n" +
                        "```\n" +
                        "📭 **Model**\n```\n" +
                        "✨ Pentru a putea rezolva cererea dumneavoastră cât mai rapid și eficient, " +
                        "vă rugăm să completați formularul de mai jos.\n👉 " +
                        "Apăsați pe butonul de mai jos pentru a începe completarea formularului necesar!\n" +
                        "Echipa noastră vă mulțumește pentru colaborare și vă asigură că veți primi ajutorul " +
                        "de care aveți nevoie în cel mai scurt timp posibil. 💙\n```")
                .setColor(new Color(0, 128, 255)) // Albastru
                .setFooter(generateFooter("instructions"), iconURL);

        return embed.build();
    }

    public static List<Button> createActionButtons() {
        return List.of(
                Button.primary("complete_form", "Completare Model \ud83c\udf1e"),
                Button.danger("close_ticket", "Închidere Ticket \ud83c\udff3\ufe0f")
        );
    }


    public static Modal createModelForm() {
        TextInput screenshotInput = TextInput.create("screenshot_info", "Screenshot cu F3 ON", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Pune un screenshot după completare.")
                .setRequired(true)
                .build();

        TextInput warningsInput = TextInput.create("past_warnings", "Avertismente anterioare?", TextInputStyle.SHORT)
                .setPlaceholder("Răspuns scurt: 'Da' sau 'Nu'.")
                .setRequired(true)
                .build();

        TextInput banTimeInput = TextInput.create("ban_time", "Data/Ora banului", TextInputStyle.SHORT)
                .setPlaceholder("Exemplu: '10.12.2024, ora 15:30'.")
                .setRequired(true)
                .build();

        TextInput activityInput = TextInput.create("ban_activity", "\n" +
                        "Activitate la momentul banului (Ce făceai?)", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie ce făcea utilizatorul în acel moment.")
                .setRequired(true)
                .build();

        TextInput modsInput = TextInput.create("mods_list", "Moduri instalate", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Enumeră modurile instalate: 'Optifine, ReplayMod, etc.'.")
                .setRequired(true)
                .build();

        return Modal.create("complete_form_modal", "Cerere Unban")
                .addComponents(
                        ActionRow.of(screenshotInput),
                        ActionRow.of(warningsInput),
                        ActionRow.of(banTimeInput),
                        ActionRow.of(activityInput),
                        ActionRow.of(modsInput)
                ).build();
    }

    public static List<Button> createEditActionButtons() {
        return List.of(
                Button.danger("close_ticket", "Închidere Ticket 🚫")
        );
    }

    public static MessageEmbed createHelperCompletedEmbed(Map<String, String> responses) {
        return new EmbedBuilder()
                .setTitle("📋 Cerere Helper Completată")
                .setColor(Color.BLUE)
                .addField("👤 Nume", "```" + responses.getOrDefault("name", "N/A") + "```", false)
                .addField("🎮 Nickname", "```" + responses.getOrDefault("nickname", "N/A") + "```", false)
                .addField("📝 Rol și Responsabilități", "```" + responses.getOrDefault("role", "N/A") + "```", false)
                .addField("📜 Regulament Citit", "```" + responses.getOrDefault("rules", "N/A") + "```", false)
                .addField("🌟 Contribuție", "```" + responses.getOrDefault("contribution", "N/A") + "```", false)
                .addField("💬 Scenariu - Chat", "```" + responses.getOrDefault("chatScenario", "N/A") + "```", false)
                .addField("⚠️ Scenariu - Abuz Staff", "```" + responses.getOrDefault("abuseScenario", "N/A") + "```", false)
                .addField("🔍 Scenariu - Hack", "```" + responses.getOrDefault("hackScenario", "N/A") + "```", false)
                .addField("📅 Scenariu - Eveniment", "```" + responses.getOrDefault("eventScenario", "N/A") + "```", false)
                .addField("🙋 Scenariu - Staff Sancțiuni", "```" + responses.getOrDefault("staffScenario", "N/A") + "```", false)
                .setFooter(generateFooter("staff_request"), iconURL)
                .setTimestamp(Instant.now())
                .build();
    }



    public static MessageEmbed createTicketDetailsEmbedWithForm(String userName, Map<String, String> responses, String staffMemberName) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("\uD83D\uDCE9   Detalii Ticket | PlaySquad")
                .setDescription("**Formular completat:**")
                .setColor(new Color(209, 0, 255)) // Elegant Turcoaz
                .addField("👤 Utilizator", String.format("`%s`", userName), true)
                .addField("🎯 Staff Asignat", staffMemberName != null && !staffMemberName.isEmpty() && !staffMemberName.equals("Neasignat")
                        ? String.format("`%s`", staffMemberName)
                        : "*Neasignat*", true)
                .addField("📷 Screenshot", String.format("`%s`", responses.getOrDefault("Screenshot Info", "N/A")), false)
                .addField("⚠️ Avertismente Anterioare", String.format("`%s`", responses.getOrDefault("Warnings", "N/A")), false)
                .addField("🕒 Data/Ora Banului", String.format("`%s`", responses.getOrDefault("Ban Time", "N/A")), false)
                .addField("📄 Activitate", String.format("`%s`", responses.getOrDefault("Activity", "N/A")), false)
                .addField("🔧 Moduri Instalate", String.format("`%s`", responses.getOrDefault("Mods", "N/A")), false)
                .setFooter(generateFooter("ticket_details"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createBanNotificationEmbed(String userName, String reason) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("❌ Cerere Unban Respinsă")
                .setDescription("Cererea de unban a utilizatorului a fost respinsă. Sancțiunea rămâne activă.")
                .setColor(new Color(255, 0, 0))
                .addField("👤 Utilizator", String.format("```%s```", userName), false)
                .addField("⚠️ Motiv Respins", String.format("```%s```", reason), false)
                .addField("🙏 Mulțumim", "```" +
                        "Vă mulțumim pentru înțelegere și pentru că respectați regulile serverului." +
                        "```", false)
                .setImage("https://media1.tenor.com/m/O7_ZgFSBGZIAAAAd/angry-cat-sour-cat.gif")
                .setFooter(generateFooter("ban_notification"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createUnbanNotificationEmbed(String userName) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("✅ Cerere Unban Acceptată")
                .setDescription("Un utilizator a fost reabilitat pe server.")
                .setColor(new Color(0, 255, 0))
                .addField("👤 Utilizator", String.format("```%s```", userName), false)
                .addField("🙏 Mulțumim", "```" +
                        "Bun venit înapoi! Vă mulțumim pentru că \nfaceți parte din comunitatea noastră." +
                        "```", false)
                .setImage("https://media1.tenor.com/m/fitGu2TwtHoAAAAd/cat-hyppy.gif")
                .setFooter(generateFooter("unban_notification"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createClosingTicketEmbed(int secondsLeft, Color color) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("⏳ Închidere Ticket")
                .setDescription(secondsLeft == 0
                        ? "Ticket-ul se va închide acum... 🚪"
                        : String.format("Ticket-ul se va închide în **%d secunde**.", secondsLeft))
                .setColor(color)
                .setImage("https://i.redd.it/origin-of-this-cat-template-meme-v0-1j06grkaqfgb1.jpg?width=1920&format=pjpg&auto=webp&s=2a3f46e6a56075290391f8b3ead6874eb98ca5c6")
                .addField("🎉 Mulțumim pentru colaborare!", "```" +
                                "Suntem încântați că faceți parte din comunitatea noastră! 💙\n" +
                                "Pentru suport suplimentar, contactați staff-ul." + "```",
                        false)
                .addField("ℹ️ Informații Server", "```" +
                                "➤ IP Server: Play.Mc-Ro.Ro\n" +
                                "➤ Versiune Minecraft: 1.16+\n" + "```",
                        false)
                .setFooter(generateFooter("instructions"), iconURL);

        return embed.build();
    }

    public static MessageEmbed createRecoverPasswordResponseEmbed(Map<String, String> responses) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🔐 Recuperare Parolă Completată")
                .setColor(new Color(0, 153, 255)) // Albastru elegant
                .setDescription("✅ **Cererea ta pentru recuperarea parolei a fost procesată cu succes!**\n\n" +
                        "Echipa noastră va analiza răspunsurile tale și te va contacta în cel mai scurt timp posibil.")
                .addField("👤 Nume Utilizator", "```" + responses.getOrDefault("userName", "N/A") + "```", false)
                .addField("📅 Data Creării Contului", "```" + responses.getOrDefault("accountCreationDate", "N/A") + "```", false)
                .addField("🕒 Ultima Conectare", "```" + responses.getOrDefault("lastLoginDate", "N/A") + "```", false)
                .addField("📧 Adresa de Email Asociată", "```" + responses.getOrDefault("associatedEmail", "N/A") + "```", false)
                .addField("📸 Dovadă Vizuală", "```" + responses.getOrDefault("visualProof", "Nu a fost furnizată.") + "```", false)
                .addField("💎 Cont Premium", "```" + responses.getOrDefault("premiumStatus", "N/A") + "```", false)
                .addField("💳 Donații", "```" + responses.getOrDefault("donationInfo", "Nu există informații despre donații.") + "```", false)
                .addField("🗺️ Servere Adăugate", "```" + responses.getOrDefault("serverList", "Nu a fost specificat.") + "```", false)
                .addField("⏳ Prima Conectare", "```" + responses.getOrDefault("firstConnectionDate", "N/A") + "```", false)
                .setFooter(generateFooter("recover_password"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createPasswordNotificationEmbed(String userName, String newPassword) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🔐 Parolă Schimbată cu Succes!")
                .setColor(new Color(0, 153, 255))
                .setDescription("✅ **Noua parolă a fost generată cu succes!**\n\n")
                .setThumbnail("https://media.tenor.com/cGzc08rXDCwAAAAi/cat.gif")
                .addField("👤 Utilizator", "```" + userName + "```", false)
                .addField("🔑 Parola Nouă", "```" + newPassword + "```", false)
                .addField("🔒 **Recomandare IMPORTANTĂ:**",
                        "```" +
                                "1️⃣ NU partaja parola cu nimeni pentru a-ți proteja contul.\n" +
                                "2️⃣ Este important să îți schimbi parola cât mai repede posibil.\n" +
                                "3️⃣ Folosește comanda în lobby pentru a seta o parolă nouă:\n" +
                                "➤ /cp [parolaVECHE] [parolaNOUA]\n" +
                                "```",
                        false)
                .setFooter(generateFooter("recover_password"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createDonnorRecoveryResponseEmbed(Map<String, String> responses) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🎖️ Recuperare Grad Donator Completată")
                .setColor(new Color(0, 255, 118))
                .setDescription("✅ **Cererea ta pentru recuperarea gradului de donator a fost procesată cu succes!**")
                .addField("👤 Nume Utilizator", "```" + responses.getOrDefault("userName", "N/A") + "```", false)
                .addField("🎯 Cum ați pierdut gradul?", "```" + responses.getOrDefault("reason", "N/A") + "```", false)
                .addField("💎 Este contul Premium?", "```" + responses.getOrDefault("premium", "N/A") + "```", false)
                .addField("💳 Ați efectuat donații?", "```" + responses.getOrDefault("donation", "N/A") + "```", false)
                .addField("🏅 Grad anterior", "```" + responses.getOrDefault("rank", "N/A") + "```", false)
                .addField("🌐 Servere jucate", "```" + responses.getOrDefault("server", "N/A") + "```", false)
                .setFooter(generateFooter("recover_grade"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }


    public static MessageEmbed createDonationInstructionsEmbed() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("📧 Instrucțiuni pentru Confirmarea Donației")
                .setColor(new Color(0, 255, 118))
                .setDescription("✅ **Vă rugăm să urmați pașii de mai jos pentru confirmarea donației**")
                .addField("1️⃣ Găsiți Chitanța", "```Verificați email-ul de pe care ați efectuat tranzacția pentru a găsi chitanta aferentă.```", false)
                .addField("2️⃣ Trimiteți Dovada", "```Includeți chitanța în acest canal.```", false)
                .addField("ℹ️ Informație Adițională", "```Dacă nu mai găsiți chitanta, vă rugăm să ne comunicați data aproximativă și email-ul aferent tranzacției.```", false)
                .setFooter(generateFooter("donation"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createStaffRecoveryResponseEmbed(Map<String, String> responses) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🎖️ Recuperare Grad Staff Completată")
                .setColor(new Color(0, 255, 118))
                .setDescription("✅ **Cererea ta pentru recuperarea gradului de staff a fost procesată cu succes!**")
                .addField("👤 Nume Utilizator", "```" + responses.getOrDefault("userName", "N/A") + "```", false)
                .addField("🎯 Ce grad ați deținut?", "```" + responses.getOrDefault("rank", "N/A") + "```", false)
                .addField("❓ De ce v-ați retras din funcție?", "```" + responses.getOrDefault("reason", "N/A") + "```", false)
                .addField("\uD83D\uDCC5 Când v-ați retras?", "```" + responses.getOrDefault("date", "N/A") + "```", false)
                .addField("\uD83E\uDD1D Cu cine ați vorbit pentru a te retrage?", "```" + responses.getOrDefault("talked", "N/A") + "```", false)
                .setFooter(generateFooter("recover_grade"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createStaffRecoveryAcceptEmbed(String userName, String staffRole) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("✅ Cerere Recuperare Staff Acceptată")
                .setDescription(String.format("Cererea utilizatorului **%s** pentru recuperarea gradului de staff a fost acceptată. Felicitări și bine ai revenit în echipă!", userName))
                .setColor(new Color(255, 141, 0, 255))
                .addField("👤 Utilizator", String.format("```%s```", userName), false)
                .addField("🎯 Grad Atribuit", String.format("```%s```", staffRole), false)
                .addField("🙏 Mulțumim", "```Vă mulțumim pentru dedicare și contribuția adusă comunității!```", false)
                .setImage("https://media1.tenor.com/m/nQP9sNn2IfIAAAAd/sdfh.gif")
                .setFooter(generateFooter("recover_staff"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createQuestionsOrIssuesTypeEmbed() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🎉 Salutare! Cum te putem ajuta astăzi?")
                .setDescription("Alege tipul de cerere pe care dorești să o creezi. Suntem aici să te ajutăm cu orice problemă sau întrebări ai avea. 💙\n"
                        + "👇 **Apasă pe unul dintre butoanele de mai jos pentru a continua:**")
                .setColor(new Color(0, 204, 255))
                .addField("🔧 Probleme", "```Pentru orice problemă tehnică sau întrebări generale.```", false)
                .addField("💳 Probleme Donații", "```Ai o problemă cu o donație? Suntem aici să te ajutăm!```", false)
                .addField("✍️ Reclamații", "```Dorești să faci o reclamație? Nu ezita să ne spui.```", false)
                .addField("\ud83d\uded1 Întrebări", "```Ai o întrebare sau ai nevoie de clarificări? Suntem bucuroși să răspundem!```", false)
                .setThumbnail("https://media.tenor.com/zGIMaKCYD-EAAAAi/happy.gif")
                .setFooter(generateFooter("questions_or_issues_emb"), iconURL)
                .setTimestamp(Instant.now());
        return embed.build();
    }

    public static MessageEmbed createRecoverRankTypeEmbed() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🎉 Salutare! Alege tipul de cerere de recuperare:")
                .setDescription("Suntem aici să te ajutăm să îți recuperezi gradul pierdut! Selectează una dintre opțiunile de mai jos pentru a începe. 💪\n"
                        + "👇 **Apasă pe butonul corespunzător pentru continuare:**")
                .setColor(new Color(34, 139, 34))
                .addField("💚 Donator", "```Recuperează-ți gradul de donator. Apasă pe butonul dedicat.```", false)
                .addField("👔 Staff", "```Recuperează-ți gradul de staff. Apasă pe butonul dedicat.```", false)
                .setFooter(generateFooter("ticket_details"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createRequestHelperEmbed() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🔍 Aplică pentru un Rol!")
                .setDescription("Suntem încântați că dorești să contribui la comunitatea noastră! Alege tipul de cerere pe care dorești să o depui din lista de mai jos. 💙\n\n"
                        + "👇 **Apasă pe butonul corespunzător pentru a continua:**")
                .setColor(new Color(161, 255, 78))
                .addField("🤝 Helper", "```Fii parte din echipa noastră și ajută membrii comunității. Apasă pentru a aplica.```", false)
                .addField("🔧 Builder", "```Arată-ți abilitățile de construcție și ajută la crearea unor proiecte uimitoare. Apasă pentru a aplica.```", false)
                .addField("✍️ Traducător", "```Ajută-ne să facem serverul accesibil pentru toată lumea. Apasă pentru a aplica.```", false)
                .addField("🎥 Media", "```Contribuie la crearea de conținut vizual pentru comunitate. Apasă pentru a aplica.```", false)
                .addField("🔓 Unban", "```Depune o cerere pentru a fi reabilitat pe server. Apasă pentru a aplica.```", false)
                .addField("🏆 Developer", "```Contribuie la dezvoltarea și îmbunătățirea serverului nostru. Apasă pentru a aplica.```", false)
                .setFooter("© Play Squad | Suntem aici să te ajutăm să găsești rolul perfect pentru tine!\n© Puk3p | .callmegeorge", iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createGeneralProblemsResponseEmbed(Map<String, String> responses) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("🛠️ Probleme Generale - Detalii Ticket")
                .setDescription("Mulțumim pentru că ne-ai oferit informațiile necesare! Echipa noastră va analiza situația și va reveni cu un răspuns cât mai curând posibil. 💙")
                .setColor(new Color(131, 0, 255)) // Albastru prietenos
                .addField("❓ Problema raportată:", "```" + responses.getOrDefault("reasonProblem", "N/A") + "```", false)
                .addField("🕒 Când a apărut problema:", "```" + responses.getOrDefault("dateProblem", "N/A") + "```", false)
                .addField("📦 Versiunea utilizată:", "```" + responses.getOrDefault("versionUsed", "N/A") + "```", false)
                .addField("📸 Dovezi furnizate:", "```" + responses.getOrDefault("proofUsed", "N/A") + "```", false)
                .setThumbnail("https://media.tenor.com/FYsjyvi3C7kAAAAi/rupert-cat.gif")
                .setFooter(generateFooter("general_problems"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createDonationProblemsResponseEmbed(Map<String, String> responses) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("💳 Probleme Donații")
                .setDescription("Am înregistrat detaliile problemei tale. Echipa noastră va analiza cererea în cel mai scurt timp. Mai jos sunt informațiile pe care ni le-ai oferit:")
                .setColor(new Color(255, 215, 0))
                .addField("📦 Ce ai donat?", "```" + responses.getOrDefault("whatBought", "N/A") + "```", false)
                .addField("💳 Metoda de donație", "```" + responses.getOrDefault("typeDonation", "N/A") + "```", false)
                .addField("📅 Data donației", "```" + responses.getOrDefault("dateDonation", "N/A") + "```", false)
                .addField("✅ Confirmare tranzacție", "```" + responses.getOrDefault("proofDonation", "N/A") + "```", false)
                .addField("⚠️ Problemă întâmpinată", "```" + responses.getOrDefault("problemDonation", "N/A") + "```", false)
                .setThumbnail("https://media1.tenor.com/m/YlBfgZ3_INcAAAAd/cat-kitty.gif")
                .setFooter(generateFooter("general_problems"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createComplaintResponseEmbed(Map<String, String> responses) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("📋 Reclamație Staff")
                .setDescription("Am înregistrat detaliile reclamației tale. Echipa noastră va analiza cererea în cel mai scurt timp. Mai jos sunt informațiile pe care ni le-ai oferit:")
                .setColor(new Color(255, 69, 0)) // Portocaliu intens
                .addField("👤 Nickname-ul tău", "```" + responses.getOrDefault("userName", "N/A") + "```", false)
                .addField("👥 Nickname-ul staff-ului reclamat", "```" + responses.getOrDefault("selectedStaff", "N/A") + "```", false)
                .addField("📂 Secțiunea abuzată", "```" + responses.getOrDefault("sectionAbused", "N/A") + "```", false)
                .addField("⚠️ Motivul reclamației", "```" + responses.getOrDefault("reasonForComplaint", "N/A") + "```", false)
                .addField("📸 Dovezi furnizate", "```" + responses.getOrDefault("proof", "Nu există dovezi atașate.") + "```", false)
                .addField("💬 Comentarii adiționale", "```" + responses.getOrDefault("additionalComments", "N/A") + "```", false)
                .setThumbnail("https://media.tenor.com/cute-cat.gif") // Imagine reprezentativă
                .setFooter(generateFooter("complaint_response"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createQuestionsResponseEmbed(Map<String, String> responses) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("❓ Întrebări Generale")
                .setDescription("Am înregistrat întrebările tale. Echipa noastră va analiza cererea și îți va răspunde cât mai curând posibil. Mai jos sunt detaliile pe care ni le-ai oferit:")
                .setColor(new Color(204, 115, 76))
                .addField("👤 Nickname-ul tău", "```" + responses.getOrDefault("userName", "N/A") + "```", false)
                .addField("📂 Secțiunea asociată întrebării", "```" + responses.getOrDefault("sectionAsk", "N/A") + "```", false)
                .addField("❔ Întrebarea principală", "```" + responses.getOrDefault("askReason", "N/A") + "```", false)
                .addField("📋 Categoria întrebării", "```" + responses.getOrDefault("categoryAsk", "N/A") + "```", false)
                .addField("💬 Alte întrebări suplimentare", "```" + responses.getOrDefault("anotherAsks", "Nu există alte întrebări.") + "```", false)
                .setThumbnail("https://media1.tenor.com/m/DM7SdBiQKhEAAAAd/cat-underwater.gif")
                .setFooter(generateFooter("questions_response"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createPromotionRequestEmbed() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("📢 Cerințe pentru Cererea de Media")
                .setDescription("Dacă dorești să ne ajuți promovând serverul nostru, te rugăm să te asiguri că îndeplinești următoarele cerințe." +
                        " Dacă îndeplinești cerințele, apasă pe butonul de mai jos pentru a crea un ticket.")
                .setColor(new Color(143, 105, 208))
                .addField("🎥 Videoclip de promovare", "```Un videoclip în care promovezi serverul nostru. Asigură-te că este de calitate și atractiv.```", false)
                .addField("🧠 Comportament adecvat", "```Un comportament controlat și adecvat, care reflectă valorile comunității noastre.```", false)
                .addField("🌟 Minim 500 de abonați/urmăritori", "```Trebuie să ai minim 500 de abonați sau urmăritori și o influență semnificativă în cadrul videoclipurilor sau TikTok-urilor tale.```", false)
                .setThumbnail("https://media1.tenor.com/m/32bh8yl7ZmcAAAAd/cat-wtf.gif")
                .setFooter(generateFooter("media_req"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }


    public static MessageEmbed createCompleteMediaApplicationEmbed(Map<String, String> responses) {
        return new EmbedBuilder()
                .setTitle("📋 Cerere Media - Completată")
                .setDescription("✅ **Cererea ta pentru media fost procesată cu succes!**")
                .setColor(new Color(255, 0, 164))
                .addField("👤 Nume", "```" + responses.getOrDefault("nameMedia", "N/A") + "```", false)
                .addField("🎂 Vârsta", "```" + responses.getOrDefault("ageMedia", "N/A") + "```", false)
                .addField("🌐 Platforma", "```" + responses.getOrDefault("platformMedia", "N/A") + "```", false)
                .addField("🔗 Link către Profil", "```" + responses.getOrDefault("profileLinkMedia", "N/A") + "```", false)
                .addField("📹 Link Videoclip", "```" + responses.getOrDefault("mediaLinkVideo", "N/A") + "```", false)
                .addField("🤝 Cum contribui?", "```" + responses.getOrDefault("mediaContribution", "N/A") + "```", false)
                .addField("💬 Ești pe Discord?", "```" + responses.getOrDefault("mediaDiscord", "N/A") + "```", false)
                .addField("📆 Experiență", "```" + responses.getOrDefault("mediaExperience", "N/A") + "```", false)
                .addField("📄 Comentarii Adiționale", "```" + responses.getOrDefault("mediaAddComm", "N/A") + "```", false)
                .setThumbnail("https://media1.tenor.com/m/uyrB9E4GThcAAAAd/cat-kitten.gif")
                .setFooter(generateFooter("media_request"), iconURL)
                .setTimestamp(Instant.now())
                .build();
    }
    public static MessageEmbed createBuilderResponseEmbed(Map<String, String> responses) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("\uD83C\uDFE0 Cerere Builder Completată")
                .setDescription("Echipa noastră va analiza cererea ta. Mai jos sunt informațiile completate în formular.")
                .setColor(new Color(168, 137, 64))
                .addField("👤 Nume", "```" + responses.getOrDefault("builderName", "N/A") + "```", false)
                .addField("🎂 Vârstă", "```" + responses.getOrDefault("builderAge", "N/A") + "```", false)
                .addField("🏗️ Experiență în Building", "```" + responses.getOrDefault("builderExperience", "N/A") + "```", false)
                .addField("📚 Cunoștințe WorldEdit / VoxelSniper / CS", "```" + responses.getOrDefault("builderKnowledge", "N/A") + "```", false)
                .addField("🏛️ Clasa de Build Optată", "```" + responses.getOrDefault("builderClass", "N/A") + "```", false)
                .addField("🖼️ Portofoliu", "[Accesează portofoliul](" + responses.getOrDefault("builderPortofolio", "N/A") + ")", false)
                .addField("✍️ Descriere Personală", "```" + responses.getOrDefault("builderDescription", "N/A") + "```", false)
                .addField("📋 Alte Precizări", "```" + responses.getOrDefault("builderNotes", "N/A") + "```", false)
                .setThumbnail("https://media1.tenor.com/m/F_q07fVK6OEAAAAd/sweet-engineer.gif")
                .setFooter(generateFooter("builder"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createTranslatorApplicationEmbed(Map<String, String> responses) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("\uD83D\uDCDD Cerere Traducător Completată")
                .setDescription("Detaliile cererii de traducător completate de utilizator sunt afișate mai jos:")
                .setColor(new Color(206, 255, 81))
                .addField("👤 Nume", "```" + responses.getOrDefault("translatorName", "N/A") + "```", false)
                .addField("📚 Experiență în traduceri", "```" + responses.getOrDefault("translatorExperience", "N/A") + "```", false)
                .addField("🌐 Limbi cunoscute și traducere posibilă", "```" + responses.getOrDefault("translatorLanguages", "N/A") + "```", false)
                .addField("🛠️ Experiență cu fișiere YAML/JSON/TXT", "```" + responses.getOrDefault("translatorTehnical", "N/A") + "```", false)
                .addField("📂 Traduceri anterioare", "```" + responses.getOrDefault("translatorHistory", "N/A") + "```", false)
                .setFooter(generateFooter("builder"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

    public static MessageEmbed createDeveloperApplicationEmbed(Map<String, String> responses) {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDCBB Cerere Developer Completată")
                .setColor(Color.CYAN)
                .addField("👤 Nickname-ul tău", "```" + responses.getOrDefault("userName", "N/A") + "```", false)
                .addField("👨‍💻 Experiență în programare", "```" + responses.getOrDefault("developerExperience", "N/A") + "```", false)
                .addField("📂 Proiecte realizate", "```" + responses.getOrDefault("developerProjects", "N/A") + "```", false)
                .addField("🛠️ Tehnologii relevante", "```" + responses.getOrDefault("relevantsTehnique", "N/A") + "```", false)
                .addField("🤝 Lucru în echipă", "```" + responses.getOrDefault("teamWork", "N/A") + "```", false)
                .addField("❓ Întrebări suplimentare", "```" + responses.getOrDefault("intrebariSuplimentare", "N/A") + "```", false)
                .setThumbnail("https://media1.tenor.com/m/XPRG-4ujVMIAAAAd/cat-work-in-progress.gif")
                .setFooter(generateFooter("builder"), iconURL)
                .setTimestamp(Instant.now())
                .build();
    }

    public static MessageEmbed createFormCompletionReminder() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("⏳ Completează Formularul")
                .setDescription("❗ Te rugăm să completezi formularul înainte de a scrie în acest canal.\n\n"
                        + "Fă clic pe butonul **'Completează Formularul 📃'** pentru a începe procesul.\n\n"
                        + "Dacă întâmpini probleme, te rugăm să contactezi staff-ul.")
                .setColor(new Color(255, 69, 0))
                .setThumbnail("https://media1.tenor.com/m/cuW62FiZhBwAAAAd/cat-stare-blinking-loop-lopped-confused-ashley.gif")
                .setFooter(generateFooter("instructions"), iconURL)
                .setTimestamp(Instant.now());

        return embed.build();
    }

}