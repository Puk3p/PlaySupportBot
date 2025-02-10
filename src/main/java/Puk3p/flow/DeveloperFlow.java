package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class DeveloperFlow {

    public static Modal createNameInputModal() {
        return Modal.create("developer_name_modal", "Cerere Traducător")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createDeveloperApplicationModal() {
        TextInput developerExperience = TextInput.create("developer_experience", "Ce experiență tehnică ai?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Introdu detalii, cât mai amănunțite.")
                .setRequired(true)
                .build();

        TextInput projects = TextInput.create("developer_projects", "Ce proiecte proprii deți (sau în echipă)?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Lasă link-uri către ele (GitHub, GitLab etc.).")
                .setRequired(true)
                .build();

        TextInput relevantsTehnique = TextInput.create("relevants_tehnique", "Ești familiarizat cu Spigot API?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Oferă-ți o notă la nivelul de cunoaștere a Spigot API")
                .setRequired(true)
                .build();

        TextInput teamWork = TextInput.create("team_work", "Ești confortabil să lucrezi într-o echipă?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Ce așteptări ai de la noi? ")
                .setRequired(true)
                .build();

        TextInput intrebariSuplm = TextInput.create("intrebari_suplimentare", "Ai întrbări legate de acest rol?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Dacă da, oferă câteva explicații legate de nelămurirea ta.")
                .setRequired(true)
                .build();


        return Modal.create("developer_general_application", "Cerere Traducător - General")
                .addComponents(
                        ActionRow.of(developerExperience),
                        ActionRow.of(projects),
                        ActionRow.of(relevantsTehnique),
                        ActionRow.of(teamWork),
                        ActionRow.of(intrebariSuplm)
                ).build();
    }


}
