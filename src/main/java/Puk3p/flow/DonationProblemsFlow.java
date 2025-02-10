package Puk3p.flow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class DonationProblemsFlow {

    public static Modal createNameInputModal() {
        return Modal.create("donations_problems_modal", "Recuperare Parolă - Nume Cont")
                .addActionRow(
                        TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                                .setPlaceholder("Introduceți numele contului")
                                .setRequired(true)
                                .build()
                )
                .build();
    }

    public static Modal createDonationsProblemsModal() {
        TextInput itemBought = TextInput.create("what_bought", "Ce ați donat?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Exemplu: bani, resurse, grad.")
                .setRequired(true)
                .build();

        TextInput donationType = TextInput.create("type_donation", "Cum ați făcut donația?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Exemplu: PayPal, PaysafeCard, SMS,")
                .setRequired(true)
                .build();

        TextInput dateDonation = TextInput.create("date_donation", "Când ați realizat donația", TextInputStyle.SHORT)
                .setPlaceholder("Exemplu: 01.01.2025.")
                .setRequired(true)
                .build();

        TextInput proofDonation = TextInput.create("proofDonation", "Ai primit confirmarea tranzacției?", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Răspunde cu 'da' sau 'nu'.")
                .setRequired(true)
                .build();

        TextInput problemDonation = TextInput.create("problemDonation", "Ce problemă ai întâmpinat?",TextInputStyle.PARAGRAPH)
                .setPlaceholder("Exemplu: Nu am primit ce am cumpărat.")
                .setRequired(true)
                .build();

        return Modal.create("donation_problems_modal", "Probleme Donații")
                .addComponents(
                        ActionRow.of(itemBought),
                        ActionRow.of(donationType),
                        ActionRow.of(dateDonation),
                        ActionRow.of(proofDonation),
                        ActionRow.of(problemDonation)
                ).build();
    }

}
