package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class TranslatorFlow {

    public static Modal createNameInputModal() {
        return Modal.create("translator_name_modal", "Cerere Traducător")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createTranslatorApplicationModal() {
        TextInput translatorName = TextInput.create("translator_name", "Cum te numești?", TextInputStyle.SHORT)
                .setPlaceholder("Introdu numele tău real.")
                .setRequired(true)
                .build();

        TextInput translatorExperience = TextInput.create("translator_experience", "Ce experiență ai în traduceri?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie experiența ta în traducerea de texte sau fișiere tehnice.")
                .setRequired(true)
                .build();

        TextInput translatorLanguages = TextInput.create("translator_languages", "Ce limbi vorbești și-n ce limbi poți traduce?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Introduceți aici limbile cunoscute.")
                .setRequired(true)
                .build();

        TextInput translatorTehnical = TextInput.create("translator_tehnical", "Ai mai lucrat cu fișiere YAML, JSON sau TXT?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Dacă da, descrie experiența ta.")
                .setRequired(true)
                .build();

        TextInput translatorHistory = TextInput.create("translator_history", "Te-ai mai ocupat de traducerea fișierelor?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Dacă da, oferă câteva exemple sau detalii.")
                .setRequired(true)
                .build();


        return Modal.create("translator_general_application", "Cerere Traducător - General")
                .addComponents(
                        ActionRow.of(translatorName),
                        ActionRow.of(translatorExperience),
                        ActionRow.of(translatorLanguages),
                        ActionRow.of(translatorTehnical),
                        ActionRow.of(translatorHistory)
                ).build();
    }

}
