package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class GeneralProblemsFlow {

    public static Modal createNameInputModal() {
        return Modal.create("probleme_generale_modal", "Recuperare Parolă - Nume Cont")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createGeneralProblemsModal() {
        TextInput reasonInput = TextInput.create("reason_problem", "Ce problemă aveți?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie problema pe scurt.")
                .setRequired(true)
                .build();

        TextInput dateAppeared = TextInput.create("date_problem", "Când ai întâmpinat problema?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("La ce dată și oră a apărut problema, aproximativ.")
                .setRequired(true)
                .build();

        TextInput versionUsed = TextInput.create("version_used", "Pe ce versiune erai când a apărut prorblema?", TextInputStyle.SHORT)
                .setPlaceholder("Introdu aici versiunea și ce Launcher folosești.")
                .setRequired(true)
                .build();

        TextInput proofNeed = TextInput.create("proof_used", "Ne poți oferi screenshot-uri sau dovezi?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Răspunde cu 'da' sau 'nu'.")
                .setRequired(true)
                .build();

        return Modal.create("general_problems_modal", "Probleme Generale")
                .addComponents(
                        ActionRow.of(reasonInput),
                        ActionRow.of(dateAppeared),
                        ActionRow.of(versionUsed),
                        ActionRow.of(proofNeed)
                ).build();
    }
}
