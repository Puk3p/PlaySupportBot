package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class RecoverGrade {

    public static Modal createNameInputDonnorModal() {
        return Modal.create("recover_grade_donnor_modal", "Recuperare Grad Donator - Nume Cont")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createRecoverGradeDonnorModal() {
        TextInput reasonLost = TextInput.create("donator_reason", "Cum ați pierdut gradul de donator?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Introdu aici detaliile.")
                .setRequired(true)
                .build();

        TextInput premiumInput = TextInput.create("donator_premium", "Este contul dvs. Premium?", TextInputStyle.SHORT)
                .setPlaceholder("Răspunde cu 'Da' sau 'Nu'.")
                .setRequired(true)
                .build();

        TextInput donationInput = TextInput.create("donator_donation", "Ați efectuat donații pe magazinul online?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Răspunde cu 'Da' sau 'Nu'.")
                .setRequired(true)
                .build();

        TextInput rankInput = TextInput.create("donator_rank", "Ce grad ați avut anterior?", TextInputStyle.SHORT)
                .setPlaceholder("Exemplu: Flyer, Ultra, Vip, Supreme, Legend, Prime, Titan.")
                .setRequired(true)
                .build();

        TextInput serverInput = TextInput.create("donator_servers", "Servere pe care jucați (românești)", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Introdu aici.")
                .setRequired(true)
                .build();

        return Modal.create("donator_recovery_modal", "Recuperare Grad Donator")
                .addComponents(
                        ActionRow.of(reasonLost),
                        ActionRow.of(premiumInput),
                        ActionRow.of(donationInput),
                        ActionRow.of(rankInput),
                        ActionRow.of(serverInput)
                ).build();
    }


    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------
    public static Modal createNameInputStaffModal() {
        return Modal.create("recover_grade_staff_modal", "Recuperare Grad Staff - Nume Cont")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createRecoverGradeStaffModal() {
        TextInput gradeInput = TextInput.create("staff_rank", "Ce grad ați avut anterior?", TextInputStyle.SHORT)
                .setPlaceholder("Exemplu: Helper, Moderator, Supervizor, Admin,\n Operator, Manager, Administrator.")
                .setRequired(true)
                .build();

        TextInput reasonRetire = TextInput.create("staff_reason", "De ce ați ales să te retragi din funcție?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Introdu aici detaliile.")
                .setRequired(true)
                .build();

        TextInput dateRetire = TextInput.create("staff_date", "Când te-ai retras?", TextInputStyle.SHORT)
                .setPlaceholder("Introdu aici o dată aproximativă.")
                .setRequired(true)
                .build();

        TextInput talkedRetire = TextInput.create("talked_retire", "Cu cine ați vorbit pentru a te retrage?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Introdu aici numele superiorului cu care ai vorbit.")
                .setRequired(true)
                .build();

        return Modal.create("staff_recovery_modal", "Recuperare Grad Staff")
                .addComponents(
                        ActionRow.of(gradeInput),
                        ActionRow.of(reasonRetire),
                        ActionRow.of(dateRetire),
                        ActionRow.of(talkedRetire)
                ).build();
    }

}
