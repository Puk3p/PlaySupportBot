package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class ComplaintFlow {

    public static Modal createNameInputModal() {
        return Modal.create("complaints_name_modal", "Reclamație")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createComplaintStaffModal() {
        TextInput sectionAbused = TextInput.create("section_abused", "Secțiunea pe care a abuzat", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Exemplu: oneblock, survival, skyblock etc.")
                .setRequired(true)
                .build();

        TextInput reasonForComplaint = TextInput.create("reason_complaint", "Motivul pentru care îl reclami", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descrie motivul detaliat.")
                .setRequired(true)
                .build();

        TextInput proof = TextInput.create("proof", "Dovada", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Menționați dovezile disponibile, dacă există. Ex: screenshot-uri, video.")
                .setRequired(false)
                .build();

        TextInput additionalComments = TextInput.create("additional_comments", "Comentarii adiționale", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Adăugați alte detalii relevante.")
                .setRequired(false)
                .build();

        return Modal.create("complaint_staff_modal", "Reclamație Staff")
                .addComponents(
                        ActionRow.of(sectionAbused),
                        ActionRow.of(reasonForComplaint),
                        ActionRow.of(proof),
                        ActionRow.of(additionalComments)
                ).build();
    }

}
