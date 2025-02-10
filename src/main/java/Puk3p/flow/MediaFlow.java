package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class MediaFlow {

    public static Modal createNameInputModal() {
        return Modal.create("media_name_modal", "Cerere Media")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createMediaApplicationModal() {
        TextInput nameInput = TextInput.create("media_name", "Cum te numești?", TextInputStyle.SHORT)
                .setPlaceholder("Introdu numele tău complet.")
                .setRequired(true)
                .build();

        TextInput ageInput = TextInput.create("media_age", "Vârsta", TextInputStyle.SHORT)
                .setPlaceholder("Introduceți vârsta reală.")
                .setRequired(true)
                .build();

        TextInput platformInput = TextInput.create("media_platform", "Platforma principală", TextInputStyle.SHORT)
                .setPlaceholder("Exemplu: YouTube, TikTok, Twitch")
                .setRequired(true)
                .build();

        TextInput profileLinkInput = TextInput.create("media_profile_link", "Link către canal/profil", TextInputStyle.SHORT)
                .setPlaceholder("Introduceți URL-ul canalului/profilului dvs.")
                .setRequired(true)
                .build();

        return Modal.create("media_general_application", "Cerere Media - General")
                .addComponents(
                        ActionRow.of(nameInput),
                        ActionRow.of(ageInput),
                        ActionRow.of(platformInput),
                        ActionRow.of(profileLinkInput)
                ).build();
    }

    public static Modal createMediaDetailedModal() {
        TextInput videoLinkInput = TextInput.create("media_video_link", "Link-ul videoclipului de promovare", TextInputStyle.SHORT)
                .setPlaceholder("Introdu URL-ul videoclipului.")
                .setRequired(true)
                .build();

        TextInput contributionInput = TextInput.create("media_contribution", "Cum poți ajuta comunitatea?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie cum vei contribui la creșterea comunității.")
                .setRequired(true)
                .build();

        TextInput discordInput = TextInput.create("media_discord", "Ai intrat pe serverul nostru de Discord?", TextInputStyle.SHORT)
                .setPlaceholder("Răspunde cu 'da' sau 'nu'.")
                .setRequired(true)
                .build();

        TextInput experienceInput = TextInput.create("media_experience", "De cât timp creezi conținut?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie experiența ta în crearea de conținut.")
                .setRequired(true)
                .build();

        TextInput additionalCommentsInput = TextInput.create("media_additional_comments", "Comentarii adiționale", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Adaugă orice informații relevante.")
                .setRequired(false)
                .build();

        return Modal.create("media_detailed_application", "Cerere Media - Detalii")
                .addComponents(
                        ActionRow.of(videoLinkInput),
                        ActionRow.of(contributionInput),
                        ActionRow.of(discordInput),
                        ActionRow.of(experienceInput),
                        ActionRow.of(additionalCommentsInput)
                ).build();
    }
}
