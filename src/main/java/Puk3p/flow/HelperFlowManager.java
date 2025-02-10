package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class HelperFlowManager {

    public static Modal createGeneralQuestionsModal() {
        TextInput nameInput = TextInput.create("helper_name", "Cum te numești și ce vârstă ai?", TextInputStyle.SHORT)
                .setPlaceholder("Introdu numele tău complet și vârsta reală.")
                .setRequired(true)
                .build();

        TextInput nicknameInput = TextInput.create("helper_nickname", "Numele din joc(nickname)", TextInputStyle.SHORT)
                .setPlaceholder("Introdu nickname-ul tău.")
                .setRequired(true)
                .build();

        TextInput roleInput = TextInput.create("helper_role", "Ce înseamnă rolul de HELPER pentru tine?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie ce înseamnă pentru tine acest rol.")
                .setRequired(true)
                .build();

        TextInput rulesInput = TextInput.create("helper_rules", "Ai citit regulamentului server-ului?", TextInputStyle.SHORT)
                .setPlaceholder("Răspunde cu ce ai citit în regulament.")
                .setRequired(true)
                .build();

        TextInput contributionInput = TextInput.create("helper_contribution", "Ce aspect important vei aduce echipei staff?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie contribuția ta.")
                .setRequired(true)
                .build();

        return Modal.create("helper_general_questions", "Cerere Helper - Întrebări")
                .addComponents(
                        ActionRow.of(nameInput),
                        ActionRow.of(nicknameInput),
                        ActionRow.of(roleInput),
                        ActionRow.of(rulesInput),
                        ActionRow.of(contributionInput)
                ).build();

    }

    public static Modal createScenarioQuestionsModal() {
        TextInput chatScenarioInput = TextInput.create("helper_chat_scenario", "SCENARIU: Jucător te înjură pe chat", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie cum ai răspunde")
                .setRequired(true)
                .build();

        TextInput abuseScenarioInput = TextInput.create("helper_abuse_scenario", "SCENARIU: Staff abuzează pentru prieten", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie cum ai gestiona situația")
                .setRequired(true)
                .build();

        TextInput hackScenarioInput = TextInput.create("helper_hack_scenario", "SCENARIU: Jucător acuză de hack alt jucător", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie ce ai face")
                .setRequired(true)
                .build();

        TextInput eventScenarioInput = TextInput.create("helper_event_scenario", "SCENARIU: Probleme la un eveniment", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie soluția ta")
                .setRequired(true)
                .build();

        TextInput staffScenarioInput = TextInput.create("helper_staff_scenario", "SCENARIU: Staff sancționează fără motiv", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie reacția ta")
                .setRequired(true)
                .build();

        return Modal.create("helper_scenario_questions", "Cerere Helper - Scenarii")
                .addComponents(
                        ActionRow.of(chatScenarioInput),
                        ActionRow.of(abuseScenarioInput),
                        ActionRow.of(hackScenarioInput),
                        ActionRow.of(eventScenarioInput),
                        ActionRow.of(staffScenarioInput)
                ).build();
    }
}