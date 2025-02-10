package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class RecoverFlowManager {

    public static Modal createNameInputModal() {
        return Modal.create("recover_password_modal", "Recuperare Parolă - Nume Cont")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createFirstRecoverPasswordModal() {
        return Modal.create("recover_password_form_1", "Recuperare Parolă - Formular 1")
                .addActionRow(
                        TextInput.create("account_creation_date", "Data Creării Contului", TextInputStyle.SHORT)
                                .setPlaceholder("Ex: Iunie 2020")
                                .setRequired(true)
                                .build()
                )
                .addActionRow(
                        TextInput.create("last_login_date", "Ultima Dată de Conectare", TextInputStyle.SHORT)
                                .setPlaceholder("Ex: 25 Decembrie 2023")
                                .setRequired(true)
                                .build()
                )
                .addActionRow(
                        TextInput.create("associated_email", "Adresa de Email Asociată", TextInputStyle.SHORT)
                                .setPlaceholder("Ex: exemplu@email.com daca aveti")
                                .setRequired(true)
                                .build()
                )
                .addActionRow(
                        TextInput.create("visual_proof", "Dovadă Vizuală", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Descrieți sau încărcați o poză/filmare cu contul dumneavoastră.")
                                .setRequired(true)
                                .build()
                )
                .build();
    }
    
    public static Modal createSecondRecoverPasswordModal() {
        return Modal.create("recover_password_form_2", "Recuperare Parolă - Formular 2")
                .addActionRow(
                        TextInput.create("premium_status", "Cont Premium", TextInputStyle.SHORT)
                                .setPlaceholder("Este contul Premium? Specificați detalii.")
                                .setRequired(true)
                                .build()
                )
                .addActionRow(
                        TextInput.create("donation_info", "Detalii Donatii", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Număr comandă/metodă plată")
                                .setRequired(true)
                                .build()
                )
                .addActionRow(
                        TextInput.create("server_list", "Servere Adăugate", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Servere adăugate în listă, cele din Ro.")
                                .setRequired(true)
                                .build()
                )
                .addActionRow(
                        TextInput.create("first_connection_date", "Data Primei Conectări", TextInputStyle.SHORT)
                                .setPlaceholder("Ex: Ianuarie 2020")
                                .setRequired(true)
                                .build()
                )
                .build();
    }
}
