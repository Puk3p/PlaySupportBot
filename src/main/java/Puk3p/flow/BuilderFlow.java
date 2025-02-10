package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class BuilderFlow {

    public static Modal createNameInputModal() {
        return Modal.create("builder_name_modal", "Cerere Media")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createBuilderApplicationModal() {
        TextInput builderName = TextInput.create("builder_name", "Cum te numești?", TextInputStyle.SHORT)
                .setPlaceholder("Introdu numele tău complet.")
                .setRequired(true)
                .build();

        TextInput builderAge = TextInput.create("builder_age", "Vârsta", TextInputStyle.SHORT)
                .setPlaceholder("Introduceți vârsta reală.")
                .setRequired(true)
                .build();

        TextInput builderExperience = TextInput.create("builder_experience", "Experiență în building", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Explicați experiența în detaliu.")
                .setRequired(true)
                .build();

        TextInput builderKnowledge = TextInput.create("builder_knowledge", "Cunoștiințe WorldEdit / VoxelSniper / CS", TextInputStyle.SHORT)
                .setPlaceholder("Explică în detaliu cunoștințele tale.")
                .setRequired(true)
                .build();

        return Modal.create("builder_general_application", "Cerere Builder - General")
                .addComponents(
                        ActionRow.of(builderName),
                        ActionRow.of(builderAge),
                        ActionRow.of(builderExperience),
                        ActionRow.of(builderKnowledge)
                ).build();
    }

    public static Modal createDetailedBuilderModal() {
        TextInput buildClassInput = TextInput.create("build_class", "Clasa de build pentru care optezi", TextInputStyle.SHORT)
                .setPlaceholder("Structura, TerraForming, Organic")
                .setRequired(true)
                .build();

        TextInput portfolioInput = TextInput.create("portfolio_link", "Portofoliu construcții", TextInputStyle.SHORT)
                .setPlaceholder("Adaugă link-ul către portofoliul tău.")
                .setRequired(true)
                .build();

        TextInput descriptionInput = TextInput.create("self_description", "O mică descriere despre tine", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Scrie câteva lucruri despre tine.")
                .setRequired(true)
                .build();

        TextInput additionalNotesInput = TextInput.create("additional_notes", "Alte precizări", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Adaugă alte detalii relevante.")
                .setRequired(false)
                .build();

        return Modal.create("detailed_builder_application", "Cerere Builder - Detalii")
                .addComponents(
                        ActionRow.of(buildClassInput),
                        ActionRow.of(portfolioInput),
                        ActionRow.of(descriptionInput),
                        ActionRow.of(additionalNotesInput)
                ).build();
    }


}
