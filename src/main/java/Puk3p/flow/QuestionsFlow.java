package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class QuestionsFlow {

    public static Modal createNameInputModal() {
        return Modal.create("questions_modal", "Întrebări generale")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createQuestionsModal() {

        TextInput sectionAsk = TextInput.create("section_ask", "Întrebarea are legătură cu o secțiune anume?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Dacă da, introdu numele secțiunii.")
                .setRequired(true)
                .build();

        TextInput askReason = TextInput.create("ask_reason", "Care este întrebarea ta?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Formulează întrebarea cât mai clar.")
                .setRequired(true)
                .build();

        TextInput category_ask = TextInput.create("category_ask", "Unde s-ar încadra întrebarea?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Exemplu: tehnică, donație, reguli, sugestii")
                .setRequired(true)
                .build();

        TextInput anotherAsks = TextInput.create("another_asks", "Mai ai alte întrebări legate de subiect?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Introdu aici detaliile")
                .setRequired(false)
                .build();

        return Modal.create("questions_final_modal", "Întrebări Generale")
                .addComponents(
                        ActionRow.of(sectionAsk),
                        ActionRow.of(askReason),
                        ActionRow.of(category_ask),
                        ActionRow.of(anotherAsks)
                ).build();
    }
}
