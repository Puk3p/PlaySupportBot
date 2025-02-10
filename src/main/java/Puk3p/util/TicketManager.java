package Puk3p.util;

import Puk3p.embed.TicketEmbed;
import Puk3p.flow.*;
import Puk3p.model.TicketData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Puk3p.embed.TicketEmbed.*;

public class TicketManager extends ListenerAdapter {

    private final Map<String, Map<String, String>> userResponses = new HashMap<>();
    private final Map<String, TicketData> ticketDataMap = new HashMap<>();
    private final StaffManager staffManager;
    private final Map<String, Set<String>> ticketVotes = new HashMap<>();
    private final Map<String, List<String>> proVotes = new HashMap<>();
    private final Map<String, List<String>> contraVotes = new HashMap<>();
    private final Map<String, String> voteMessageMap = new HashMap<>();
    private final Map<String, List<TicketData>> categorizedTickets = new HashMap<>();
    private final String ID_CATEGORIE = "878190445115637770";
    private static final Map<String, Long> lastWarnTime = new HashMap<>();
    private static final long WARNING_COOLDOWN = 10 * 60 * 1000;
    private static final Set<String> completedForms = new HashSet<>();


    public TicketManager(StaffManager staffManager) {

        this.staffManager = staffManager;
        System.out.println("TicketManager instance created: " + this);

    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        String userId = event.getUser().getId();

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);

        System.out.println("onModalInteraction invoked. Modal ID: " + modalId);
        System.out.println("User responses: " + responses);

        switch (modalId) {
            case "helper_general_questions": handleHelperGeneralQuestions(event); break;
            case "helper_scenario_questions": handleHelperScenarioQuestions(event, responses); break;
            case "vote_pro_modal": case "vote_contra_modal": handleVoteModal(event, modalId); break;
            case "unban_name_modal": handleUnbanNameModal(event); break;
            case "unban_reason_modal": handleUnbanReasonModal(event); break;
            case "complete_form_modal": handleCompleteFormModal(event); break;
            case "recover_password_modal": handleRecoverPasswordModal(event); break;
            case "recover_password_form_1": handleFirstRecoverPasswordForm(event); break;
            case "recover_password_form_2": handleSecondRecoverPasswordForm(event); break;
            case "recover_grade_donnor_modal": handleRecoverGradeDonnorModal(event); break;
            case "donator_recovery_modal": handleDonnorRecoverModal(event); break;
            case "recover_grade_staff_modal": handleRecoverGradeStaffModal(event); break;
            case "staff_recovery_modal": handleStaffRecoverModal(event); break;
            case "probleme_generale_modal": handleGeneralProblemsModal(event); break;
            case "general_problems_modal": handleGeneralProblemsModalV2(event); break;
            case "donations_problems_modal": handleDonationsProblemsModal(event); break;
            case "donation_problems_modal": handleDonationProblemsModal(event); break;
            case "complaints_name_modal": handleComplaintNameModal(event); break;
            case "complaint_staff_modal": handleComplaintStaffModal(event); break;
            case "questions_modal": handleQuestionsModal(event); break;
            case "questions_final_modal": handleQuestionsFinalModal(event); break;
            case "media_name_modal": handleMediaNext(event); break;
            case "media_general_application": handleMediaApplication(event); break;
            case "media_detailed_application": handleMediaDetailedApplication(event, responses); break;
            case "builder_name_modal": handleBuilderModal(event); break;
            case "builder_general_application": handleBuilderGeneralApp(event); break;
            case "detailed_builder_application": handleBuilderDetailedForm(event); break;
            case "translator_name_modal": handleTranslatorModal(event); break;
            case "translator_general_application": handleTranslatorApplication(event); break;
            case "developer_name_modal": handleDeveloperModal(event); break;
            case "developer_general_application": handleDeveloperApplication(event); break;
            default: event.reply("Acțiune necunoscută pentru acest modal.").setEphemeral(true).queue(); break;
        }
    }

    private void handleDeveloperApplication(ModalInteractionEvent event) {
        String userID = event.getUser().getId();

        userResponses.putIfAbsent(userID, new HashMap<>());
        Map<String, String> responses = userResponses.get(userID);

        responses.put("developerExperience", event.getValue("developer_experience").getAsString());
        responses.put("developerProjects", event.getValue("developer_projects").getAsString());
        responses.put("relevantsTehnique", event.getValue("relevants_tehnique").getAsString());
        responses.put("teamWork", event.getValue("team_work").getAsString());
        responses.put("intrebariSuplimentare", event.getValue("intrebari_suplimentare").getAsString());

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            System.err.println("Canalul sau mesajul asociat lipsește. Verifică dacă au fost salvate corect.");
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Eroare: Nu am putut obține guild-ul asociat.").setEphemeral(true).queue();
            return;
        }

        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed responseEmbed = TicketEmbed.createDeveloperApplicationEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents()
                    .queue(success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza cererea în cel mai scurt timp.")
                                                .setEphemeral(true).queue(),
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu informațiile completate.").setEphemeral(true).queue();
                                            System.err.println("[handleDeveloperApplication] Eroare la trimiterea embed-ului: " + error.getMessage());
                                        }
                                );
                    }, error -> {
                        event.reply("Eroare la actualizarea mesajului principal.").setEphemeral(true).queue();
                        System.err.println("[handleDeveloperApplication] Eroare la editarea mesajului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleDeveloperApplication] Eroare la preluarea mesajului: " + error.getMessage());
        });
    }

    private void handleDeveloperModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "Cerere Developer";
        String category = "Cereri Developer";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel  = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-developer-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru cerere developer")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );
                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();

                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_developer", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleDeveloperModal] Ticket creat cu succes: " + channel.getId());
                                }, error -> {
                                    System.err.println("[handleDeveloperModal] Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleDeveloperModal] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleDeveloperModal] Eroare la găsirea membrului: " + error.getMessage());
        });
    }

    private void handleTranslatorApplication(ModalInteractionEvent event) {
        String userID = event.getUser().getId();

        userResponses.putIfAbsent(userID, new HashMap<>());
        Map<String, String> responses = userResponses.get(userID);

        responses.put("translatorName", event.getValue("translator_name").getAsString());
        responses.put("translatorExperience", event.getValue("translator_experience").getAsString());
        responses.put("translatorLanguages", event.getValue("translator_languages").getAsString());
        responses.put("translatorTehnical", event.getValue("translator_tehnical").getAsString());
        responses.put("translatorHistory", event.getValue("translator_history").getAsString());

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            System.err.println("Canalul sau mesajul asociat lipsește. Verifică dacă au fost salvate corect.");
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Eroare: Nu am putut obține guild-ul asociat.").setEphemeral(true).queue();
            return;
        }

        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed responseEmbed = TicketEmbed.createTranslatorApplicationEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents()
                    .queue(success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza cererea în cel mai scurt timp.")
                                                .setEphemeral(true).queue(),
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu informațiile completate.").setEphemeral(true).queue();
                                            System.err.println("[handleTranslatorApplication] Eroare la trimiterea embed-ului: " + error.getMessage());
                                        }
                                );
                    }, error -> {
                        event.reply("Eroare la actualizarea mesajului principal.").setEphemeral(true).queue();
                        System.err.println("[handleTranslatorApplication] Eroare la editarea mesajului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleTranslatorApplication] Eroare la preluarea mesajului: " + error.getMessage());
        });
    }


    private void handleTranslatorModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "Cerere Traducător";
        String category = "Cereri Traducător";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-traducător-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru cerere traducător")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );

                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);

                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();

                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_translator", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleTranslatorModal] Ticket creat cu succes: " + channel.getId());
                                }, error -> System.err.println("[handleTranslatorModal] Eroare la trimiterea mesajului în canal: " + error.getMessage()));
                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleTranslatorModal] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleTranslatorModal] Eroare la găsirea membrului: " + error.getMessage());
        });
    }

    private void handleBuilderDetailedForm(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        Map<String, String> responses = userResponses.get(userId);

        responses.put("builderClass", event.getValue("build_class").getAsString());
        responses.put("builderPortofolio", event.getValue("portfolio_link").getAsString());
        responses.put("builderDescription", event.getValue("self_description").getAsString());
        responses.put("builderNotes", event.getValue("additional_notes").getAsString());

        System.out.println("[handleBuilderDetailedForm] Răspunsuri salvate pentru formularul 2: " + responses);

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var channel = event.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }
        completedForms.add(channelId);

        MessageEmbed responseEmbed = TicketEmbed.createBuilderResponseEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents().queue(
                    success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> {
                                            event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza cererea în cel mai scurt timp.")
                                                    .setEphemeral(true).queue();
                                        },
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu detaliile formularului.").setEphemeral(true).queue();
                                            System.err.println("[handleBuilderDetailedForm] Eroare la trimiterea mesajului: " + error.getMessage());
                                        }
                                );
                    },
                    error -> {
                        event.reply("Eroare la eliminarea butoanelor din mesajul principal.").setEphemeral(true).queue();
                        System.err.println("[handleBuilderDetailedForm] Eroare la editarea mesajului: " + error.getMessage());
                    }
            );
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleBuilderDetailedForm] Eroare la găsirea mesajului: " + error.getMessage());
        });
    }

    private void handleBuilderGeneralApp(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);

        responses.put("builderName", event.getValue("builder_name").getAsString());
        responses.put("builderAge", event.getValue("builder_age").getAsString());
        responses.put("builderExperience", event.getValue("builder_experience").getAsString());
        responses.put("builderKnowledge", event.getValue("builder_knowledge").getAsString());

        System.out.println("[handleBuilderGeneralApp] Răspunsuri salvate pentru formularul 1: " + responses);

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var channel = event.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents(
                    ActionRow.of(
                            Button.primary("continue_builder_form", "Continuă Formularul ➡️"),
                            Button.danger("close_ticket", "Închide Ticket-ul ❌")
                    )
            ).queue(
                    success -> event.reply("✅ Răspunsurile dvs. au fost salvate. Continuați cu formularul următor.").setEphemeral(true).queue(),
                    error -> event.reply("Eroare la actualizarea butoanelor.").setEphemeral(true).queue()
            );
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleBuilderGeneralApp] Eroare la găsirea mesajului: " + error.getMessage());
        });
    }

    private void handleBuilderModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "Cerere Builder";
        String category = "Cereri Builder";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-builder-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru cerere builder")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );
                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();
                        var headBuilderRole = guild.getRoleById(UserRole.HEAD_BUILDER.getId());
                        if (headBuilderRole != null) {
                            channel.upsertPermissionOverride(headBuilderRole)
                                    .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                    .queue();
                        } else {
                            System.err.println("[handleBuilderModal] Nu am găsit rolul HEAD_BUILDER.");
                        }
                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_builder", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleBuilderModal] Ticket creat cu succes: " + channel.getId());

                                    event.getJDA().addEventListener(new ListenerAdapter() {
                                        @Override
                                        public void onMessageReceived(MessageReceivedEvent msgEvent) {
                                            String userId = msgEvent.getAuthor().getId();
                                            String channelId = msgEvent.getChannel().getId();

                                            if (completedForms.contains(channelId)) {
                                                return;
                                            }

                                            if (ticketDataMap.containsKey(channelId) && msgEvent.getAuthor().getId().equals(ticketDataMap.get(channelId).getUserId())) {
                                                long currentTime = System.currentTimeMillis();

                                                if (lastWarnTime.containsKey(userId) && (currentTime - lastWarnTime.get(userId)) < WARNING_COOLDOWN) {
                                                    msgEvent.getMessage().delete().queue();
                                                    return;
                                                }

                                                msgEvent.getMessage().delete().queue();
                                                MessageEmbed warnEmbed = TicketEmbed.createFormCompletionReminder();
                                                msgEvent.getChannel().sendMessageEmbeds(warnEmbed).queue(embedMessage -> {
                                                    embedMessage.delete().queueAfter(10, TimeUnit.MINUTES);
                                                });

                                                lastWarnTime.put(userId, currentTime);
                                            }
                                        }
                                    });
                                }, error -> {
                                    System.err.println("[handleBuilderModal] Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleBuilderModal] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleRecoverGradeStaffModal] Eroare la găsirea membrului: " + error.getMessage());
        });
    }

    private void handleMediaDetailedApplication(ModalInteractionEvent event, Map<String, String> responses) {
        System.out.println("Interacțiunea pentru `details modal app media` a început.");

        responses.put("mediaLinkVideo", event.getValue("media_video_link").getAsString());
        responses.put("mediaContribution", event.getValue("media_contribution").getAsString());
        responses.put("mediaDiscord", event.getValue("media_discord").getAsString());
        responses.put("mediaExperience", event.getValue("media_experience").getAsString());
        responses.put("mediaAddComm", event.getValue("media_additional_comments").getAsString());

        MessageEmbed resultEmbed = TicketEmbed.createCompleteMediaApplicationEmbed(responses);

        String helperMessageId = responses.get("messageId");
        String channelId = responses.get("channelId");

        if (channelId != null && helperMessageId != null) {
            var channel = event.getGuild().getTextChannelById(channelId);
            if (channel != null) {
                channel.retrieveMessageById(helperMessageId).queue(originalMessage -> {
                    originalMessage.editMessageComponents(
                            ActionRow.of(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                    ).queue(success -> {
                        System.out.println("Mesajul a fost actualizat pentru a păstra doar butonul de închidere.");
                    }, error -> {
                        System.err.println("Eroare la actualizarea mesajului: " + error.getMessage());
                    });
                }, error -> {
                    System.err.println("Eroare la găsirea mesajului: " + error.getMessage());
                });
            } else {
                System.err.println("Canalul specificat nu a fost găsit.");
            }
        } else {
            System.err.println("ID-ul mesajului sau al canalului lipsește!");
        }

        event.replyEmbeds(resultEmbed).queue();
    }





    private void handleMediaApplication(ModalInteractionEvent event) {
        String userId = event.getUser().getId();

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);

        responses.put("nameMedia", event.getValue("media_name").getAsString());
        responses.put("ageMedia", event.getValue("media_age").getAsString());
        responses.put("platformMedia", event.getValue("media_platform").getAsString());
        responses.put("profileLinkMedia", event.getValue("media_profile_link").getAsString());

        String helperMessageId = responses.get("messageId");
        if (helperMessageId == null) {
            System.err.println("messageId pentru helper este null! Asigură-te că este salvat corect.");
            event.reply("A apărut o eroare la actualizarea mesajului.").setEphemeral(true).queue();
            return;
        }

        var channel = event.getChannel();
        if (channel instanceof TextChannel textChannel) {
            textChannel.retrieveMessageById(helperMessageId).queue(originalMessage -> {
                List<Button> buttons = List.of(
                        Button.primary("continue_details_media", "Continuă cu detaliile 📖"),
                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                );
                originalMessage.editMessageComponents(
                        ActionRow.of(buttons)
                ).queue(success -> {
                    System.out.println("Butoanele au fost actualizate cu succes!");
                }, error -> {
                    System.err.println("Eroare la actualizarea butoanelor: " + error.getMessage());
                });
            }, error -> {
                System.err.println("Eroare la preluarea mesajului pentru actualizare: " + error.getMessage());
            });
        } else {
            System.err.println("Canalul nu este un TextChannel sau este null.");
        }

        event.reply("Ai completat prima parte a cererii.")
                .setEphemeral(true)
                .queue();
    }


    private void handleMediaNext(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "Cerere Media";
        String category = "Cereri Media";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-media-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket Media")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );
                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();

                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_media", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleMediaNext] Ticket creat cu succes: " + channel.getId());
                                }, error -> {
                                    System.err.println("[handleMediaNext] Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleMediaNext] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleMediaNext] Eroare la găsirea membrului: " + error.getMessage());
        });
    }

    private void handleQuestionsFinalModal(ModalInteractionEvent event) {
        String userID = event.getUser().getId();

        userResponses.putIfAbsent(userID, new HashMap<>());
        Map<String, String> responses = userResponses.get(userID);

        responses.put("sectionAsk", event.getValue("section_ask").getAsString());
        responses.put("askReason", event.getValue("ask_reason").getAsString());
        responses.put("categoryAsk", event.getValue("category_ask").getAsString());
        responses.put("anotherAsks", event.getValue("another_asks").getAsString());

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            System.err.println("Canalul sau mesajul asociat lipsește. Verifică dacă au fost salvate corect.");
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Eroare: Nu am putut obține guild-ul asociat.").setEphemeral(true).queue();
            return;
        }

        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed responseEmbed = TicketEmbed.createQuestionsResponseEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents()
                    .queue(success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza cererea în cel mai scurt timp.")
                                                .setEphemeral(true).queue(),
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu informațiile completate.").setEphemeral(true).queue();
                                            System.err.println("[handleQuestionsFinalModal] Eroare la trimiterea embed-ului: " + error.getMessage());
                                        }
                                );
                    }, error -> {
                        event.reply("Eroare la actualizarea mesajului principal.").setEphemeral(true).queue();
                        System.err.println("[handleQuestionsFinalModal] Eroare la editarea mesajului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleQuestionsFinalModal] Eroare la preluarea mesajului: " + error.getMessage());
        });
    }

    private void handleQuestionsModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "întrebări Generale";
        String category = "Întrebări Generale";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-întrebări-generale-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru întrebări generale")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );
                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();

                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_questions", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleQuestionsModal] Ticket creat cu succes: " + channel.getId());
                                }, error -> {
                                    System.err.println("[handleQuestionsModal] Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleQuestionsModal] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleQuestionsModal] Eroare la găsirea membrului: " + error.getMessage());
        });
    }

    private void handleComplaintNameModal(ModalInteractionEvent event) {
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("❌ Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        String userId = event.getUser().getId();
        userResponses.putIfAbsent(userId, new HashMap<>());
        userResponses.get(userId).put("userName", userName);

        StringSelectMenu staffMenu = createStaffSelectionMenu("complaint_staff", "Selectează staff-ul reclamat");

        if (staffMenu == null) {
            event.reply("⚠️ Nu există membri staff înregistrați în acest moment.").setEphemeral(true).queue();
            return;
        }

        event.reply("✅ Numele tău a fost înregistrat: " + userName + ". Selectează staff-ul reclamat.")
                .addActionRow(staffMenu)
                .setEphemeral(true)
                .queue();
    }


    private void handleComplaintStaffModal(ModalInteractionEvent event) {
        String userID = event.getUser().getId();

        userResponses.putIfAbsent(userID, new HashMap<>());
        Map<String, String> responses = userResponses.get(userID);

        responses.put("sectionAbused", event.getValue("section_abused").getAsString());
        responses.put("reasonForComplaint", event.getValue("reason_complaint").getAsString());
        responses.put("proof", event.getValue("proof").getAsString());
        responses.put("additionalComments", event.getValue("additional_comments").getAsString());

        System.out.println("[DEBUG] SectionAbused: " + event.getValue("section_abused").getAsString());
        System.out.println("[DEBUG] ReasonForComplaint: " + event.getValue("reason_complaint").getAsString());
        System.out.println("[DEBUG] Proof: " + event.getValue("proof").getAsString());
        System.out.println("[DEBUG] AdditionalComments: " + event.getValue("additional_comments").getAsString());


        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            System.err.println("Canalul sau mesajul asociat lipsește. Verifică dacă au fost salvate corect.");
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Eroare: Nu am putut obține guild-ul asociat.").setEphemeral(true).queue();
            return;
        }

        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed responseEmbed = TicketEmbed.createComplaintResponseEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents()
                    .queue(success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza reclamația în cel mai scurt timp.")
                                                .setEphemeral(true).queue(),
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu informațiile completate.").setEphemeral(true).queue();
                                            System.err.println("[handleComplaintStaffModal] Eroare la trimiterea embed-ului: " + error.getMessage());
                                        }
                                );
                    }, error -> {
                        event.reply("Eroare la actualizarea mesajului principal.").setEphemeral(true).queue();
                        System.err.println("[handleComplaintStaffModal] Eroare la editarea mesajului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleComplaintStaffModal] Eroare la preluarea mesajului: " + error.getMessage());
        });
    }



    private void handleDonationProblemsModal(ModalInteractionEvent event) {
        String userID = event.getUser().getId();

        userResponses.putIfAbsent(userID, new HashMap<>());
        Map<String, String> responses = userResponses.get(userID);

        responses.put("whatBought", event.getValue("what_bought").getAsString());
        responses.put("typeDonation", event.getValue("type_donation").getAsString());
        responses.put("dateDonation", event.getValue("date_donation").getAsString());
        responses.put("proofDonation", event.getValue("proofDonation").getAsString());
        responses.put("problemDonation", event.getValue("problemDonation").getAsString());

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            System.err.println("Canalul sau mesajul asociat lipsește. Verifică dacă au fost salvate corect.");
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Eroare: Nu am putut obține guild-ul asociat.").setEphemeral(true).queue();
            return;
        }

        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed responseEmbed = TicketEmbed.createDonationProblemsResponseEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents()
                    .queue(success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> {
                                            if (responses.get("proofDonation").equalsIgnoreCase("da")) {
                                                MessageEmbed donationInstructionsEmbed = TicketEmbed.createDonationInstructionsEmbed();
                                                channel.sendMessage("Hey " + event.getUser().getAsMention() + "! Am observat că ai completat cu **'da'** la întrebarea despre donație.\n" +
                                                                "Te rugăm să urmezi instrucțiunile din mesajul de mai jos pentru a ne furniza dovezile necesare:")
                                                        .queueAfter(10, TimeUnit.SECONDS,
                                                                message -> channel.sendMessageEmbeds(donationInstructionsEmbed).queue());
                                            }
                                            event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza cererea în cel mai scurt timp.")
                                                    .setEphemeral(true).queue();
                                        },
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu informațiile completate.").setEphemeral(true).queue();
                                            System.err.println("[handleDonationProblemsModal] Eroare la trimiterea embed-ului: " + error.getMessage());
                                        }
                                );
                    }, error -> {
                        event.reply("Eroare la actualizarea mesajului principal.").setEphemeral(true).queue();
                        System.err.println("[handleDonationProblemsModal] Eroare la editarea mesajului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleDonationProblemsModal] Eroare la preluarea mesajului: " + error.getMessage());
        });
    }

    private void handleDonationsProblemsModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "Probleme Donații";
        String category = "Probleme Donații";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-probleme-donații-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru probleme donații")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );
                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();

                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_donations_problems", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleDonationsProblemsModal] Ticket creat cu succes: " + channel.getId());
                                }, error -> {
                                    System.err.println("[handleDonationsProblemsModal] Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleDonationsProblemsModal] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleDonationsProblemsModal] Eroare la găsirea membrului: " + error.getMessage());
        });
    }

    private void handleGeneralProblemsModalV2(ModalInteractionEvent event) {
        String userID = event.getUser().getId();

        userResponses.putIfAbsent(userID, new HashMap<>());
        Map<String, String> responses = userResponses.get(userID);

        responses.put("reasonProblem", event.getValue("reason_problem").getAsString());
        responses.put("dateProblem", event.getValue("date_problem").getAsString());
        responses.put("versionUsed", event.getValue("version_used").getAsString());
        responses.put("proofUsed", event.getValue("proof_used").getAsString());

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            System.err.println("Canalul sau mesajul asociat lipsește. Verifică dacă au fost salvate corect.");
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Eroare: Nu am putut obține guild-ul asociat.").setEphemeral(true).queue();
            return;
        }

        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed responseEmbed = TicketEmbed.createGeneralProblemsResponseEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents()
                    .queue(success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza cererea în cel mai scurt timp.")
                                                .setEphemeral(true).queue(),
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu informațiile completate.").setEphemeral(true).queue();
                                            System.err.println("[handleGeneralProblemsModalV2] Eroare la trimiterea embed-ului: " + error.getMessage());
                                        }
                                );
                    }, error -> {
                        event.reply("Eroare la actualizarea mesajului principal.").setEphemeral(true).queue();
                        System.err.println("[handleGeneralProblemsModalV2] Eroare la editarea mesajului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleGeneralProblemsModalV2] Eroare la preluarea mesajului: " + error.getMessage());
        });
    }


    private void handleGeneralProblemsModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "Probleme Generale";
        String category = "Probleme Generale";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-probleme-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru probleme generale")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );
                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();

                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_general_problems", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleGeneralProblemsModal] Ticket creat cu succes: " + channel.getId());
                                }, error -> {
                                    System.err.println("[handleGeneralProblemsModal] Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleGeneralProblemsModal] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleGeneralProblemsModal] Eroare la găsirea membrului: " + error.getMessage());
        });
    }

    private void handleQuestionsOrIssues(ButtonInteractionEvent event) {
        System.out.println("[handleQuestionsOrIssues] Triggered by user: " + event.getUser().getName());

        event.deferReply(true).queue(success -> {

            MessageEmbed embed = createQuestionsOrIssuesTypeEmbed();

            event.getHook().sendMessageEmbeds(embed)
                    .addActionRow(
                            Button.primary("probleme_generale", "Probleme \uD83E\uDD1D"),
                            Button.danger("probleme_donatii", "Probleme Donații \uD83D\uDEE0\uFE0F"),
                            Button.success("reclamatii", "Reclamații \u303d\ufe0f"),
                            Button.secondary("questions_general", "Întrebări \ud83d\udcee")
                    )
                    .setEphemeral(true)
                    .queue();
        }, failure -> {
            System.err.println("[handleQuestionsOrIssues] Failed to defer reply: " + failure.getMessage());
        });
    }

    private void handleStaffRecoverModal(ModalInteractionEvent event) {
        String userID = event.getUser().getId();

        userResponses.putIfAbsent(userID, new HashMap<>());
        Map<String, String> responses = userResponses.get(userID);

        responses.put("rank", event.getValue("staff_rank").getAsString());
        responses.put("reason", event.getValue("staff_reason").getAsString());
        responses.put("date", event.getValue("staff_date").getAsString());
        responses.put("talked", event.getValue("talked_retire").getAsString());

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            System.err.println("Canalul sau mesajul asociat lipsește. Verifică dacă au fost salvate corect.");
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Eroare: Nu am putut obține guild-ul asociat.").setEphemeral(true).queue();
            return;
        }

        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed responseEmbed = TicketEmbed.createStaffRecoveryResponseEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents()
                    .queue(success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza cererea în cel mai scurt timp.")
                                                .setEphemeral(true).queue(),
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu informațiile completate.").setEphemeral(true).queue();
                                            System.err.println("[handleStaffRecoverModal] Eroare la trimiterea embed-ului: " + error.getMessage());
                                        }
                                );
                    }, error -> {
                        event.reply("Eroare la actualizarea mesajului principal.").setEphemeral(true).queue();
                        System.err.println("[handleStaffRecoverModal] Eroare la editarea mesajului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleDonnorRecoverModal] Eroare la preluarea mesajului: " + error.getMessage());
        });
    }

    private void handleRecoverGradeStaffModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "Recuperare Grad Staff";
        String category = "Recuperări Staff";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-recuperare-staff-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru recuperare grad staff")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );
                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();

                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_recover_grade_staff", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleRecoverGradeStaffModal] Ticket creat cu succes: " + channel.getId());
                                }, error -> {
                                    System.err.println("[handleRecoverGradeStaffModal] Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleRecoverGradeStaffModal] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleRecoverGradeStaffModal] Eroare la găsirea membrului: " + error.getMessage());
        });
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        System.out.println("[onButtonInteraction] Triggered button: " + buttonId);

        if (event.isAcknowledged()) {
            System.err.println("[onButtonInteraction] Interacțiunea a fost deja procesată.");
            event.reply("Această acțiune a fost deja procesată.").setEphemeral(true).queue();
            return;
        }

        try {
            switch (buttonId) {
                case "vote_pro": case "vote_contra": handleVote(event, buttonId); break;
                case "request_helper": handleRequestHelper(event); break;
                case "start_helper_form": handleStartHelperForm(event); break;
                case "continue_scenarios": handleContinueScenarios(event); break;
                case "close_ticket": handleCloseTicket(event); break;
                case "helper": handleHelperRequest(event); break;
                case "unban": handleUnbanRequest(event); break;
                case "step_2": handleUnbanStepTwo(event); break;
                case "complete_form": handleCompleteForm(event); break;
                case "recover_password": handleRecoverPassword(event); break;
                case "complete_form_recover_password": handleCompleteFormRecoverPassword(event); break;
                case "continue_recover_form": handleContinueRecoverForm(event); break;
                case "recover_rank": handleRecoverRank(event); break;
                case "recover_grade_donnor": handleStartRecoverGradeDonnor(event); break;
                case "recover_grade_staff": handleStartRecoverGradeStaff(event); break;
                case "complete_form_recover_grade_donnor": handleCompleteFormGradeDonnor(event); break;
                case "complete_form_recover_grade_staff": handleCompleteFormGradeStaff(event); break;
                case "questions_or_issues": handleQuestionsOrIssues(event); break;
                case "probleme_generale": handleGeneralProblems(event); break;
                case "complete_form_general_problems": handleCompleteFormGeneralProblems(event); break;
                case "probleme_donatii": handleDonationsProblems(event); break;
                case "complete_form_donations_problems": handleCompleteFormDonationsProblems(event); break;
                case "reclamatii": handleComplaints(event); break;
                case "complete_complaint_form": handleCompleteComplaintForm(event); break;
                case "questions_general": handleQuestionsGeneral(event); break;
                case "complete_form_questions": handleCompleteFormQuestions(event); break;
                case "media": handleMedia(event); break;
                case "create_media_ticket": handleCreateMediaTicket(event); break;
                case "complete_form_media": handleFormMedia(event); break;
                case "continue_details_media": handleDetailsMedia(event); break;
                case "builder": handleBuidler(event); break;
                case "complete_form_builder": handleBuilderForm(event); break;
                case "continue_builder_form": handleContinueBuilderForm(event); break;
                case "traducator": handleTraducator(event); break;
                case "complete_form_translator": handleCompleteTranslator(event); break;
                case "developer": handleDeveloper(event); break;
                case "complete_form_developer": handleCompleteDeveloper(event); break;
                default:
                    System.err.println("[onButtonInteraction] Necunoscut: " + buttonId);
                    event.reply("Acțiune necunoscută.").setEphemeral(true).queue();
                    break;
            }
        } catch (Exception e) {
            System.err.println("[onButtonInteraction] Eroare: " + e.getMessage());
            e.printStackTrace();
            event.reply("A apărut o eroare neașteptată.").setEphemeral(true).queue();
        }
    }

    private void handleCompleteDeveloper(ButtonInteractionEvent event) {
        System.out.println("[handleCompleteDeveloper] Butonul 'complete_form_developer' a fost apăsat.");

        Modal firstModal = DeveloperFlow.createDeveloperApplicationModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleCompleteDeveloper] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleCompleteDeveloper] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleDeveloper(ButtonInteractionEvent event) {
        System.out.println("[handleDeveloper] Butonul 'developer' a fost apăsat.");

        Modal nameModal = DeveloperFlow.createNameInputModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleDeveloper] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleDeveloper] Eroare la afișarea modalului: " + error.getMessage())
        );
    }

    private void handleCompleteTranslator(ButtonInteractionEvent event) {
        System.out.println("[handleCompleteTranslator] Butonul 'complete_form_translator' a fost apăsat.");

        Modal firstModal = TranslatorFlow.createTranslatorApplicationModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleCompleteTranslator] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleCompleteTranslator] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleTraducator(ButtonInteractionEvent event) {
        System.out.println("[handleTraducator] Butonul 'traducator' a fost apăsat.");

        Modal nameModal = TranslatorFlow.createNameInputModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleTraducator] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleTraducator] Eroare la afișarea modalului: " + error.getMessage())
        );
    }

    private void handleContinueBuilderForm(ButtonInteractionEvent event) {
        System.out.println("[handleContinueBuilderForm] Butonul 'continue_builder_form' a fost apăsat.");

        Modal secondModal = BuilderFlow.createDetailedBuilderModal();

        event.replyModal(secondModal).queue(
                success -> System.out.println("[handleContinueBuilderForm] Al doilea formular a fost afișat cu succes."),
                error -> System.err.println("[handleContinueBuilderForm] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleBuilderForm(ButtonInteractionEvent event) {
        System.out.println("[handleBuilderForm] Butonul 'complete_form_builder' a fost apăsat.");

        Modal firstModal = BuilderFlow.createBuilderApplicationModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleBuilderForm] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleBuilderForm] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleBuidler(ButtonInteractionEvent event) {
        System.out.println("[handleBuidler] Butonul 'builder' a fost apăsat.");

        Modal nameModal = BuilderFlow.createNameInputModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleBuidler] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleBuidler] Eroare la afișarea modalului: " + error.getMessage())
        );
    }

    private void handleDetailsMedia(ButtonInteractionEvent event) {
        System.out.println("Interacțiunea pentru `continue_details_media` a început.");

        try {
            Modal detailsModal = MediaFlow.createMediaDetailedModal();

            event.replyModal(detailsModal).queue(
                    success -> System.out.println("Modalul pentru `continue_details_media` a fost afișat cu succes."),
                    error -> System.err.println("Eroare la afișarea modalului pentru `continue_details_media`: " + error.getMessage())
            );
        } catch (Exception e) {
            System.err.println("Eroare neașteptată în `handleDetailsMedia`: " + e.getMessage());
            event.reply("A apărut o eroare la procesarea cererii tale. Te rugăm să încerci din nou.").setEphemeral(true).queue();
        }
    }

    private void handleFormMedia(ButtonInteractionEvent event) {
        System.out.println("[handleFormMedia] Butonul 'complete_form_general_problems' a fost apăsat.");

        Modal firstModal = MediaFlow.createMediaApplicationModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleFormMedia] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleFormMedia] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleCreateMediaTicket(ButtonInteractionEvent event) {
        System.out.println("[handleCreateMediaTicket] Butonul 'questions_general' a fost apăsat.");

        Modal nameModal = MediaFlow.createNameInputModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleCreateMediaTicket] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleCreateMediaTicket] Eroare la afișarea modalului: " + error.getMessage())
        );

    }

    private void handleMedia(ButtonInteractionEvent event) {
        System.out.println("[handleMedia] Butonul 'media' a fost apăsat.");

        MessageEmbed promotionEmbed = createPromotionRequestEmbed();

        event.replyEmbeds(promotionEmbed)
                .addActionRow(
                        Button.success("create_media_ticket", "Creează Ticket-ul 🎫")
                )
                .setEphemeral(true)
                .queue();
    }

    private void handleCompleteFormQuestions(ButtonInteractionEvent event) {
        System.out.println("[handleCompleteFormQuestions] Butonul 'complete_form_questions' a fost apăsat.");

        Modal firstModal = QuestionsFlow.createQuestionsModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleCompleteFormQuestions] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleCompleteFormQuestions] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleQuestionsGeneral(ButtonInteractionEvent event) {
        System.out.println("[handleQuestionsGeneral] Butonul 'questions_general' a fost apăsat.");

        Modal nameModal = QuestionsFlow.createNameInputModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleQuestionsGeneral] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleQuestionsGeneral] Eroare la afișarea modalului: " + error.getMessage())
        );

    }

    private void handleCompleteComplaintForm(ButtonInteractionEvent event) {
        System.out.println("[handleCompleteComplaintForm] Butonul 'complete_form_general_problems' a fost apăsat.");

        Modal firstModal = ComplaintFlow.createComplaintStaffModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleCompleteComplaintForm] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleCompleteComplaintForm] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleComplaints(ButtonInteractionEvent event) {
        System.out.println("[handleComplaints] Butonul 'reclamatii' a fost apăsat.");

        Modal nameModal = ComplaintFlow.createNameInputModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleComplaints] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleComplaints] Eroare la afișarea modalului: " + error.getMessage())
        );

    }

    private void handleCompleteFormDonationsProblems(ButtonInteractionEvent event) {
        System.out.println("[handleCompleteFormDonationsProblems] Butonul 'complete_form_general_problems' a fost apăsat.");

        Modal firstModal = DonationProblemsFlow.createDonationsProblemsModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleCompleteFormDonationsProblems] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleCompleteFormDonationsProblems] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleDonationsProblems(ButtonInteractionEvent event) {
        System.out.println("[handleDonationsProblems] Butonul 'probleme_generale' a fost apăsat.");

        Modal nameModal = DonationProblemsFlow.createNameInputModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleDonationsProblems] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleDonationsProblems] Eroare la afișarea modalului: " + error.getMessage())
        );

    }

    private void handleCompleteFormGeneralProblems(ButtonInteractionEvent event) {
        System.out.println("[handleCompleteFormGeneralProblems] Butonul 'complete_form_general_problems' a fost apăsat.");

        Modal firstModal = GeneralProblemsFlow.createGeneralProblemsModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleCompleteFormGeneralProblems] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleCompleteFormGeneralProblems] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleGeneralProblems(ButtonInteractionEvent event) {
        System.out.println("[handleGeneralProblems] Butonul 'probleme_generale' a fost apăsat.");

        Modal nameModal = GeneralProblemsFlow.createNameInputModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleGeneralProblems] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleGeneralProblems] Eroare la afișarea modalului: " + error.getMessage())
        );

    }


    private void handleCompleteFormGradeStaff(ButtonInteractionEvent event) {
        System.out.println("[handleCompleteFormGradeStaff] Butonul 'complete_form_recover_grade_staff' a fost apăsat.");

        Modal firstModal = RecoverGrade.createRecoverGradeStaffModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleCompleteFormGradeStaff] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleCompleteFormGradeStaff] Eroare la afișarea formularului: " + error.getMessage())
        );
    }


    private void handleStartRecoverGradeStaff(ButtonInteractionEvent event) {
        System.out.println("[handleStartRecoverGradeStaff] Butonul 'recover_grade_staff' a fost apăsat.");

        Modal nameModal = RecoverGrade.createNameInputStaffModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleStartRecoverGradeStaff] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleStartRecoverGradeStaff] Eroare la afișarea modalului: " + error.getMessage())
        );
    }


    private void handleDonnorRecoverModal(ModalInteractionEvent event) {
        String userID = event.getUser().getId();

        userResponses.putIfAbsent(userID, new HashMap<>());
        Map<String, String> responses = userResponses.get(userID);

        responses.put("reason", event.getValue("donator_reason").getAsString());
        responses.put("premium", event.getValue("donator_premium").getAsString());
        responses.put("donation", event.getValue("donator_donation").getAsString());
        responses.put("rank", event.getValue("donator_rank").getAsString());
        responses.put("server", event.getValue("donator_servers").getAsString());

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            System.err.println("Canalul sau mesajul asociat lipsește. Verifică dacă au fost salvate corect.");
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Eroare: Nu am putut obține guild-ul asociat.").setEphemeral(true).queue();
            return;
        }

        var channel = guild.getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed responseEmbed = TicketEmbed.createDonnorRecoveryResponseEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents()
                    .queue(success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> {
                                            if (responses.get("donation").equalsIgnoreCase("da")) {
                                                MessageEmbed donationInstructionsEmbed = TicketEmbed.createDonationInstructionsEmbed();
                                                channel.sendMessage("Hey " + event.getUser().getAsMention() + "! Am observat că ai completat cu **'da'** la întrebarea despre donație.\n" +
                                                                "Te rugăm să urmezi instrucțiunile din mesajul de mai jos pentru a ne furniza dovezile necesare:")
                                                        .queueAfter(10, TimeUnit.SECONDS,
                                                                message -> channel.sendMessageEmbeds(donationInstructionsEmbed).queue()
                                                        );
                                            }
                                            event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza cererea în cel mai scurt timp.")
                                                    .setEphemeral(true).queue();
                                        },
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu informațiile completate.").setEphemeral(true).queue();
                                            System.err.println("[handleDonnorRecoverModal] Eroare la trimiterea embed-ului: " + error.getMessage());
                                        }
                                );
                    }, error -> {
                        event.reply("Eroare la actualizarea mesajului principal.").setEphemeral(true).queue();
                        System.err.println("[handleDonnorRecoverModal] Eroare la editarea mesajului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleDonnorRecoverModal] Eroare la preluarea mesajului: " + error.getMessage());
        });
    }




    private void handleCompleteFormGradeDonnor(ButtonInteractionEvent event) {
        System.out.println("[handleCompleteFormGradeDonnor] Butonul 'complete_form_recover_grade_donnor' a fost apăsat.");

        Modal firstModal = RecoverGrade.createRecoverGradeDonnorModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleCompleteFormGradeDonnor] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleCompleteFormGradeDonnor] Eroare la afișarea formularului: " + error.getMessage())
        );
    }

    private void handleStartRecoverGradeDonnor(ButtonInteractionEvent event) {
        System.out.println("[handleStartRecoverGradeDonnor] Butonul 'recover_grade_donnor' a fost apăsat.");

        Modal nameModal = RecoverGrade.createNameInputDonnorModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleStartRecoverGradeDonnor] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleStartRecoverGradeDonnor] Eroare la afișarea modalului: " + error.getMessage())
        );
    }

    private void handleRecoverGradeDonnorModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "Recuperare Grad Donator";
        String category = "Recuperări Donator";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-recuperare-donator-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru recuperare grad donator")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );
                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();

                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_recover_grade_donnor", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleRecoverGradeDonnorModal] Ticket creat cu succes: " + channel.getId());
                                }, error -> {
                                    System.err.println("[handleRecoverGradeDonnorModal] Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleRecoverGradeDonnorModal] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleRecoverGradeDonnorModal] Eroare la găsirea membrului: " + error.getMessage());
        });
    }

    private void handleRecoverRank(ButtonInteractionEvent event) {
        System.out.print("[handleRecoverRank] Butonul 'recover_rank' a fost apasat.");

        event.deferReply(true).queue(success -> {

            MessageEmbed embed = createRecoverRankTypeEmbed();
            event.getHook().sendMessageEmbeds(embed)
                    .addActionRow(
                            Button.success("recover_grade_donnor", "Donator \ud83d\udcb5"),
                            Button.success("recover_grade_staff", "Staff \ud83e\uddd1\u200d\ud83d\udcbc")
                    )
                    .setEphemeral(true)
                    .queue();
        }, failure -> {
            System.err.println("[handleRecoverRank] Failed to defer reply: " + failure.getMessage());
        });
    }

    private void handleContinueRecoverForm(ButtonInteractionEvent event) {
        System.out.println("[handleContinueRecoverForm] Butonul 'continue_recover_form' a fost apăsat.");

        Modal secondModal = RecoverFlowManager.createSecondRecoverPasswordModal();

        event.replyModal(secondModal).queue(
                success -> System.out.println("[handleContinueRecoverForm] Al doilea formular a fost afișat cu succes."),
                error -> System.err.println("[handleContinueRecoverForm] Eroare la afișarea formularului: " + error.getMessage())
        );
    }


    private void handleSecondRecoverPasswordForm(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        Map<String, String> responses = userResponses.get(userId);

        responses.put("premiumStatus", event.getValue("premium_status").getAsString());
        responses.put("donationInfo", event.getValue("donation_info").getAsString());
        responses.put("serverList", event.getValue("server_list").getAsString());
        responses.put("firstConnectionDate", event.getValue("first_connection_date").getAsString());

        System.out.println("[handleSecondRecoverPasswordForm] Răspunsuri salvate pentru formularul 2: " + responses);

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var channel = event.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        MessageEmbed responseEmbed = TicketEmbed.createRecoverPasswordResponseEmbed(responses);

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents().queue(
                    success -> {
                        channel.sendMessageEmbeds(responseEmbed)
                                .setActionRow(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                                .queue(
                                        successMessage -> {
                                            event.reply("✅ Toate informațiile necesare au fost completate. Echipa noastră va analiza cererea în cel mai scurt timp.")
                                                    .setEphemeral(true).queue();
                                        },
                                        error -> {
                                            event.reply("Eroare la trimiterea mesajului cu detaliile formularului.").setEphemeral(true).queue();
                                            System.err.println("[handleSecondRecoverPasswordForm] Eroare la trimiterea mesajului: " + error.getMessage());
                                        }
                                );
                    },
                    error -> {
                        event.reply("Eroare la eliminarea butoanelor din mesajul principal.").setEphemeral(true).queue();
                        System.err.println("[handleSecondRecoverPasswordForm] Eroare la editarea mesajului: " + error.getMessage());
                    }
            );
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleSecondRecoverPasswordForm] Eroare la găsirea mesajului: " + error.getMessage());
        });
    }

    private void handleFirstRecoverPasswordForm(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);

        responses.put("accountCreationDate", event.getValue("account_creation_date").getAsString());
        responses.put("lastLoginDate", event.getValue("last_login_date").getAsString());
        responses.put("associatedEmail", event.getValue("associated_email").getAsString());
        responses.put("visualProof", event.getValue("visual_proof").getAsString());

        System.out.println("[handleFirstRecoverPasswordForm] Răspunsuri salvate pentru formularul 1: " + responses);

        String channelId = responses.get("channelId");
        String messageId = responses.get("messageId");

        if (channelId == null || messageId == null) {
            event.reply("Eroare: Nu am putut găsi canalul sau mesajul asociat ticket-ului.").setEphemeral(true).queue();
            return;
        }

        var channel = event.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            event.reply("Eroare: Canalul asociat ticket-ului nu există.").setEphemeral(true).queue();
            return;
        }

        channel.retrieveMessageById(messageId).queue(originalMessage -> {
            originalMessage.editMessageComponents(
                    ActionRow.of(
                            Button.primary("continue_recover_form", "Continuă Formularul ➡️"),
                            Button.danger("close_ticket", "Închide Ticket-ul ❌")
                    )
            ).queue(
                    success -> event.reply("✅ Răspunsurile dvs. au fost salvate. Continuați cu formularul următor.").setEphemeral(true).queue(),
                    error -> event.reply("Eroare la actualizarea butoanelor.").setEphemeral(true).queue()
            );
        }, error -> {
            event.reply("Eroare: Nu am putut găsi mesajul asociat ticket-ului.").setEphemeral(true).queue();
            System.err.println("[handleFirstRecoverPasswordForm] Eroare la găsirea mesajului: " + error.getMessage());
        });
    }




    private void handleCompleteFormRecoverPassword(ButtonInteractionEvent event) {
        System.out.println("[handleCompleteFormRecoverPassword] Butonul 'complete_form_recover_password' a fost apăsat.");

        Modal firstModal = RecoverFlowManager.createFirstRecoverPasswordModal();
        event.replyModal(firstModal).queue(
                success -> System.out.println("[handleCompleteFormRecoverPassword] Primul formular a fost afișat cu succes."),
                error -> System.err.println("[handleCompleteFormRecoverPassword] Eroare la afișarea formularului: " + error.getMessage())
        );
    }


    private void handleRecoverPasswordModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        String categoryId = ID_CATEGORIE;
        String reason = "Recuperare Parolă";
        String category = "Recuperări Parolă";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            var categoryChannel = guild.getCategoryById(categoryId);
            if (category == null) {
                event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                return;
            }

            guild.createTextChannel("ticket-recuperare-parolă-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru recuperare parolă")
                    .queue(channel -> {
                        String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                userName,
                                reason,
                                "Neasignat",
                                formattedTimestamp,
                                category,
                                event.getUser().getId()
                        );
                        categorizedTickets.putIfAbsent(category, new ArrayList<>());
                        categorizedTickets.get(category).add(ticketData);

                        ticketDataMap.put(channel.getId(), ticketData);
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();
                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();

                        MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("complete_form_recover_password", "Completează Formularul 📃"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    responses.put("messageId", message.getId());
                                    responses.put("channelId", channel.getId());
                                    System.out.println("[handleRecoverPasswordModal] Ticket creat cu succes: " + channel.getId());
                                }, error -> {
                                    System.err.println("[handleRecoverPasswordModal] Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                    }, error -> {
                        event.reply("A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                        System.err.println("[handleRecoverPasswordModal] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, error -> {
            event.reply("Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[handleRecoverPasswordModal] Eroare la găsirea membrului: " + error.getMessage());
        });
    }


    private void handleRecoverPassword(ButtonInteractionEvent event) {
        System.out.println("[handleRecoverPassword] Butonul 'recover_password' a fost apăsat.");

        Modal nameModal = RecoverFlowManager.createNameInputModal();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleRecoverPassword] Modalul pentru recuperare parolă a fost afișat cu succes."),
                error -> System.err.println("[handleRecoverPassword] Eroare la afișarea modalului: " + error.getMessage())
        );
    }



    private void handleCompleteFormModal(ModalInteractionEvent event) {
        String userId = event.getUser().getId();
        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);

        responses.put("Screenshot Info", event.getValue("screenshot_info").getAsString());
        responses.put("Warnings", event.getValue("past_warnings").getAsString());
        responses.put("Ban Time", event.getValue("ban_time").getAsString());
        responses.put("Activity", event.getValue("ban_activity").getAsString());
        responses.put("Mods", event.getValue("mods_list").getAsString());

        String summary = String.format(
                "**Formular completat:**\n" +
                        "**Screenshot:** %s\n" +
                        "**Avertismente anterioare:** %s\n" +
                        "**Data/Ora banului:** %s\n" +
                        "**Activitate:** %s\n" +
                        "**Moduri instalate:** %s",
                responses.get("Screenshot Info"),
                responses.get("Warnings"),
                responses.get("Ban Time"),
                responses.get("Activity"),
                responses.get("Mods")
        );

        String userNameForm = responses.getOrDefault("userName", "N/A");

        String staffMemberId = responses.get("selectedStaff");
        String staffMemberName;
        if (staffMemberId != null) {
            StaffManager.StaffMember selectedStaff = staffManager.getStaffList().stream()
                    .filter(staff -> staff.idDiscord().equals(staffMemberId))
                    .findFirst()
                    .orElse(null);
            if (selectedStaff != null) {
                staffMemberName = selectedStaff.nickname();
            } else {
                staffMemberName = "Neasignat";
            }
        } else {
            staffMemberName = "Neasignat";
        }

        String messageId = responses.get("messageId");
        String channelId = responses.get("channelId");

        if (messageId != null && channelId != null) {
            var channel = event.getGuild().getTextChannelById(channelId);
            if (channel != null) {
                channel.retrieveMessageById(messageId).queue(originalMessage -> {
                    List<MessageEmbed> existingEmbeds = originalMessage.getEmbeds();

                    MessageEmbed newEmbed = TicketEmbed.createTicketDetailsEmbedWithForm(userNameForm, responses, staffMemberName);

                    List<MessageEmbed> updatedEmbeds = new ArrayList<>(existingEmbeds);
                    updatedEmbeds.add(newEmbed);

                    channel.editMessageEmbedsById(
                            messageId,
                            updatedEmbeds
                    ).setActionRow(
                            TicketEmbed.createEditActionButtons()
                    ).queue(
                            success -> System.out.println("Mesaj actualizat cu succes!"),
                            failure -> System.err.println("Eroare la actualizarea mesajului: " + failure.getMessage())
                    );
                }, failure -> System.err.println("Eroare la preluarea mesajului original: " + failure.getMessage()));
            } else {
                System.err.println("Canalul cu ID " + channelId + " nu a fost găsit!");
            }
        } else {
            System.err.println("messageId sau channelId sunt nule!");
        }

        event.reply("✅ Mulțumim pentru completarea formularului!\n" + summary)
                .queue(message -> message.deleteOriginal().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS));
    }


    private void handleCompleteForm(ButtonInteractionEvent event) {
        try {
            Modal modal = createModelForm();

            event.replyModal(modal).queue(
                    success -> System.out.println("[handleCompleteForm] Modal afișat cu succes."),
                    error -> System.err.println("[handleCompleteForm] Eroare la afișarea modalului: " + error.getMessage())
            );
        } catch (Exception e) {
            System.err.println("[handleCompleteForm] Eroare neașteptată: " + e.getMessage());
            e.printStackTrace();

            event.reply("A apărut o eroare neașteptată la procesarea cererii.").setEphemeral(true).queue();
        }
    }


    private void handleUnbanReasonModal(ModalInteractionEvent event) {
        String reason = event.getValue("unban_reason").getAsString();

        if (reason == null || reason.trim().isEmpty()) {
            event.reply("Motivul introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        String userId = event.getUser().getId();
        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);

        responses.put("reason", reason);

        try {
            createTicket(event, responses);
            System.out.println("[handleUnbanReasonModal] Ticket-ul a fost creat cu succes pentru user: " + userId);
        } catch (Exception e) {
            System.err.println("[handleUnbanReasonModal] Eroare la crearea ticket-ului: " + e.getMessage());
            e.printStackTrace();
            event.reply("A apărut o eroare la procesarea cererii tale. Te rugăm să încerci din nou.").setEphemeral(true).queue();
        }
    }


    private void handleUnbanNameModal(ModalInteractionEvent event) {
        String userName = event.getValue("user_name").getAsString();

        if (userName == null || userName.trim().isEmpty()) {
            event.reply("Numele introdus nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        String userId = event.getUser().getId();
        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);
        responses.put("userName", userName);

        event.reply("Numele tău a fost înregistrat: " + userName + ". Selectează staff-ul care ți-a dat ban.")
                .setEphemeral(true)
                .queue();

        StringSelectMenu staffMenu = createStaffSelectionMenu("unban_staff", "Selectează staff-ul care ți-a dat ban");

        if (staffMenu == null) {
            event.getHook().sendMessage("Nu există staff înregistrat în acest moment.").setEphemeral(true).queue();
            return;
        }

        event.getHook().sendMessage("Selectează staff-ul care ți-a dat ban:")
                .addActionRow(staffMenu)
                .setEphemeral(true)
                .queue();
    }


    private StringSelectMenu createStaffSelectionMenu(String menuId, String placeholder) {
        List<StaffManager.StaffMember> staffList = staffManager.getStaffList();

        if (staffList.isEmpty()) {
            return null;
        }

        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create(menuId)
                .setPlaceholder(placeholder);

        for (StaffManager.StaffMember staff : staffList) {
            if (staff.idDiscord() != null && !staff.idDiscord().isEmpty() &&
                    staff.nickname() != null && !staff.nickname().isEmpty()) {
                menuBuilder.addOption(staff.nickname(), staff.idDiscord());
            }
        }

        return menuBuilder.build();
    }



    private void handleUnbanRequest(ButtonInteractionEvent event) {
        System.out.println("[handleUnbanRequest] Începe procesul de cerere unban.");

        TextInput nameInput = TextInput.create("user_name", "Numele tău din joc", TextInputStyle.SHORT)
                .setPlaceholder("Introdu numele tău din joc")
                .setRequired(true)
                .build();

        Modal nameModal = Modal.create("unban_name_modal", "Cerere Unban - Introdu numele din joc")
                .addComponents(ActionRow.of(nameInput))
                .build();

        event.replyModal(nameModal).queue(
                success -> System.out.println("[handleUnbanRequest] Modalul pentru cerere unban a fost afișat cu succes."),
                error -> System.err.println("[handleUnbanRequest] Eroare la afișarea modalului: " + error.getMessage())
        );
    }

    private void handleUnbanStepTwo(ButtonInteractionEvent event) {
        System.out.println("[handleUnbanStepTwo] Începe selecția staff-ului pentru unban.");

        StringSelectMenu staffMenu = createStaffSelectionMenu("unban_staff", "Selectează staff-ul care ți-a dat ban");

        if (staffMenu == null) {
            event.reply("Nu există staff înregistrat în acest moment.").setEphemeral(true).queue();
            System.err.println("[handleUnbanStepTwo] Lista de staff este goală.");
            return;
        }

        event.reply("Selectează staff-ul care ți-a dat ban.")
                .addActionRow(staffMenu)
                .setEphemeral(true)
                .queue(
                        success -> System.out.println("[handleUnbanStepTwo] Meniul de selecție staff a fost afișat cu succes."),
                        error -> System.err.println("[handleUnbanStepTwo] Eroare la afișarea meniului: " + error.getMessage())
                );
    }



    private void handleCloseTicket(ButtonInteractionEvent event) {
        var channel = event.getChannel();

        if (channel != null && channel instanceof TextChannel) {
            TextChannel textChannel = (TextChannel) channel;

            TicketData ticketData = ticketDataMap.get(textChannel.getId());
            if (ticketData == null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("⚠️ Eroare")
                        .setDescription("Nu s-au găsit date despre acest tichet.")
                        .setColor(Color.RED)
                        .build()).setEphemeral(true).queue();
                return;
            }

            String categoryFolder = "tickets/" + ticketData.getCategory();
            File folder = new File(categoryFolder);
            if (!folder.exists() && !folder.mkdirs()) {
                System.err.println("Eroare la crearea folderului pentru categoria: " + ticketData.getCategory());
            }

            String pdfPath = PDFGenerator.generateTicketPDF(
                    ticketData.getUser(),
                    ticketData.getReason(),
                    ticketData.getStaff(),
                    ticketData.getTimestamp().toString(),
                    textChannel,
                    categoryFolder
            );

            if (pdfPath != null) {
                System.out.println("PDF generat și salvat la: " + pdfPath);
            } else {
                System.err.println("Eroare la generarea PDF-ului.");
            }

            ArchiveManager.createArchiveIfNecessary(categoryFolder, ticketData.getCategory());

            Color[] colors = {
                    Color.RED, Color.ORANGE, Color.YELLOW,
                    Color.GREEN, Color.CYAN, Color.BLUE,
                    Color.MAGENTA, Color.PINK, Color.GRAY, Color.BLACK
            };

            event.replyEmbeds(TicketEmbed.createClosingTicketEmbed(10, Color.ORANGE))
                    .queue(message -> {
                        for (int i = 9; i >= 0; i--) {
                            int secondsLeft = i;
                            Color currentColor = colors[9 - i];

                            message.editOriginalEmbeds(TicketEmbed.createClosingTicketEmbed(secondsLeft, currentColor))
                                    .queueAfter(10 - secondsLeft, java.util.concurrent.TimeUnit.SECONDS,
                                            null,
                                            error -> System.err.println("Eroare la actualizarea mesajului: " + error.getMessage()));
                        }

                        textChannel.delete().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS,
                                success -> {
                                    System.out.println("Canalul a fost șters cu succes.");
                                    ticketDataMap.remove(textChannel.getId());
                                },
                                error -> System.err.println("Eroare la ștergerea canalului: " + error.getMessage()));
                    }, error -> {
                        System.err.println("Eroare la trimiterea răspunsului: " + error.getMessage());
                    });
        } else {
            String channelId = channel != null ? channel.getId() : "N/A";
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("⚠️ Eroare")
                    .setDescription("Nu s-au găsit date despre acest tichet. Verifică dacă ticket-ul a fost creat corect.")
                    .addField("ID Canal", channelId, false)
                    .addField("Chei disponibile", ticketDataMap.keySet().toString(), false)
                    .setColor(Color.RED)
                    .build()).setEphemeral(true).queue();
        }
    }


    private void handleVoteModal(ModalInteractionEvent event, String modalId) {
        final String iconURL= "https://cdn.discordapp.com/attachments/1147467499256938557/1322898549331071047/a_be31be745c02dab8a165276c44a58dc9.png?ex=67728cc8&is=67713b48&hm=c35c02d80aecd1e6c4215f9faf6ea0a0871bb41d7e0773009ff049c1638e0415&";
        System.out.println("[handleVoteModal] Procesare pentru modal: " + modalId);

        String reasonVote = event.getValue("vote_reason").getAsString();
        String staffNameVote = event.getValue("staff_name").getAsString();
        TextChannel channelVote = event.getChannel().asTextChannel();
        String staffId = event.getUser().getId();

        if (channelVote == null) {
            event.reply("Canalul asociat acestui vot nu a fost găsit.").setEphemeral(true).queue();
            System.err.println("[handleVoteModal] Canalul este null.");
            return;
        }

        ticketVotes.putIfAbsent(channelVote.getId(), new HashSet<>());
        Set<String> voters = ticketVotes.get(channelVote.getId());

        if (voters.contains(staffId)) {
            event.reply("⛔ Ai votat deja pentru acest ticket.").setEphemeral(true).queue();
            return;
        }

        voters.add(staffId);

        boolean isProVote = modalId.equals("vote_pro_modal");
        if (isProVote) {
            proVotes.putIfAbsent(channelVote.getId(), new ArrayList<>());
            proVotes.get(channelVote.getId()).add(String.format("%s\n```%s```", staffNameVote, reasonVote));
        } else {
            contraVotes.putIfAbsent(channelVote.getId(), new ArrayList<>());
            contraVotes.get(channelVote.getId()).add(String.format("%s\n```%s```", staffNameVote, reasonVote));
        }

        List<String> proVotesList = proVotes.getOrDefault(channelVote.getId(), List.of());
        List<String> contraVotesList = contraVotes.getOrDefault(channelVote.getId(), List.of());

        String proVotesText = proVotesList.isEmpty() ? "*Nicio opinie încă.*" : String.join("\n\n", proVotesList);
        String contraVotesText = contraVotesList.isEmpty() ? "*Nicio opinie încă.*" : String.join("\n\n", contraVotesList);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("\uD83C\uDFE1 **Rezultate Voturi**");
        embedBuilder.setColor(new Color(126, 65, 229));
        embedBuilder.setDescription("📊 **Situația voturilor pentru acest ticket:**");
        embedBuilder.addField("✅ **PRO**", proVotesText, true);
        embedBuilder.addField("\u200B", "\u200B", true);
        embedBuilder.addField("❌ **CONTRA**", contraVotesText, true);
        embedBuilder.setFooter("React rapid pentru a vota! | Sistemul nostru de vot", iconURL);

        String voteMessageId = voteMessageMap.get(channelVote.getId());

        if (voteMessageId != null) {
            channelVote.retrieveMessageById(voteMessageId).queue(message -> {
                message.editMessageEmbeds(embedBuilder.build()).queue(success -> {
                    System.out.println("[handleVoteModal] Mesajul de vot a fost actualizat.");
                }, error -> {
                    System.err.println("[handleVoteModal] Eroare la actualizarea mesajului: " + error.getMessage());
                });
            });
        } else {
            channelVote.sendMessageEmbeds(embedBuilder.build()).queue(sentMessage -> {
                voteMessageMap.put(channelVote.getId(), sentMessage.getId());
                System.out.println("[handleVoteModal] Mesaj de vot creat.");
            }, error -> {
                System.err.println("[handleVoteModal] Eroare la trimiterea mesajului de vot: " + error.getMessage());
            });
        }

        event.reply("✅ Votul tău a fost înregistrat cu succes!").setEphemeral(true).queue();
    }


    private void handleVote(ButtonInteractionEvent event, String buttonId) {
        String voteType = buttonId.equals("vote_pro") ? "Pro" : "Contra";
        String modalId = buttonId.equals("vote_pro") ? "vote_pro_modal" : "vote_contra_modal";

        Modal modal = Modal.create(modalId, "Motivul votului - " + voteType)
                .addComponents(
                        ActionRow.of(
                                TextInput.create("staff_name", "Numele tău (Staff)", TextInputStyle.SHORT)
                                        .setPlaceholder("Introdu numele tău.")
                                        .setRequired(true)
                                        .build()
                        ),
                        ActionRow.of(
                                TextInput.create("vote_reason", "Argumentează-ți votul?", TextInputStyle.PARAGRAPH)
                                        .setPlaceholder("Introdu detalii aici.")
                                        .setRequired(true)
                                        .build()
                        )
                )
                .build();

        event.replyModal(modal).queue();
    }

    private void handleHelperScenarioQuestions(ModalInteractionEvent event, Map<String, String> responses) {
        System.out.println("Interacțiunea pentru `helper_scenario_questions` a început.");

        responses.put("chatScenario", event.getValue("helper_chat_scenario").getAsString());
        responses.put("abuseScenario", event.getValue("helper_abuse_scenario").getAsString());
        responses.put("hackScenario", event.getValue("helper_hack_scenario").getAsString());
        responses.put("eventScenario", event.getValue("helper_event_scenario").getAsString());
        responses.put("staffScenario", event.getValue("helper_staff_scenario").getAsString());

        MessageEmbed resultEmbed = TicketEmbed.createHelperCompletedEmbed(responses);

        String helperMessageId = responses.get("messageId");
        String channelId = responses.get("channelId");

        if (channelId != null && helperMessageId != null) {
            var guild = event.getGuild();
            var channel = guild.getTextChannelById(channelId);

            if (channel != null) {

                String userId = event.getUser().getId();
                channel.upsertPermissionOverride(guild.getMemberById(userId))
                        .deny(Permission.MESSAGE_SEND)
                        .queue(success -> System.out.println("Accesul de a scrie a fost eliminat pentru utilizator."),
                                error -> System.err.println("Eroare la eliminarea accesului utilizatorului: " + error.getMessage()));

                guild.getMembers().stream()
                        .filter(member -> member.getRoles().stream()
                                .anyMatch(role -> role.getId().equals(UserRole.STAFF.getId())))
                        .forEach(staffMember -> {
                            channel.upsertPermissionOverride(staffMember)
                                    .grant(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY)
                                    .queue(success -> System.out.println("Accesul de a scrie a fost acordat staff-ului."),
                                            error -> System.err.println("Eroare la acordarea accesului staff-ului: " + error.getMessage()));
                        });

                channel.retrieveMessageById(helperMessageId).queue(originalMessage -> {
                    originalMessage.editMessageComponents(
                            ActionRow.of(Button.danger("close_ticket", "Închide Ticket-ul ❌"))
                    ).queue(success -> {
                        System.out.println("Mesajul a fost actualizat pentru a păstra doar butonul de închidere.");
                    }, error -> {
                        System.err.println("Eroare la actualizarea mesajului: " + error.getMessage());
                    });
                }, error -> {
                    System.err.println("Eroare la găsirea mesajului: " + error.getMessage());
                });
            } else {
                System.err.println("Canalul specificat nu a fost găsit.");
            }
        } else {
            System.err.println("ID-ul mesajului sau al canalului lipsește!");
        }

        event.replyEmbeds(resultEmbed).queue();
    }


    private void handleContinueScenarios(ButtonInteractionEvent event) {
        System.out.println("Interacțiunea pentru `continue_scenarios` a început.");

        try {
            Modal scenarioModal = HelperFlowManager.createScenarioQuestionsModal();

            event.replyModal(scenarioModal).queue(
                    success -> System.out.println("Modalul pentru `continue_scenarios` a fost afișat cu succes."),
                    error -> System.err.println("Eroare la afișarea modalului pentru `continue_scenarios`: " + error.getMessage())
            );
        } catch (Exception e) {
            System.err.println("Eroare neașteptată în `handleContinueScenarios`: " + e.getMessage());
            event.reply("A apărut o eroare la procesarea cererii tale. Te rugăm să încerci din nou.").setEphemeral(true).queue();
        }
    }


    private void handleHelperGeneralQuestions(ModalInteractionEvent event) {
        String userId = event.getUser().getId();

        userResponses.putIfAbsent(userId, new HashMap<>());
        Map<String, String> responses = userResponses.get(userId);

        responses.put("name", event.getValue("helper_name").getAsString());
        responses.put("nickname", event.getValue("helper_nickname").getAsString());
        responses.put("role", event.getValue("helper_role").getAsString());
        responses.put("rules", event.getValue("helper_rules").getAsString());
        responses.put("contribution", event.getValue("helper_contribution").getAsString());

        String helperMessageId = responses.get("messageId");
        if (helperMessageId == null) {
            System.err.println("messageId pentru helper este null! Asigură-te că este salvat corect.");
            event.reply("A apărut o eroare la actualizarea mesajului.").setEphemeral(true).queue();
            return;
        }

        var channel = event.getChannel();
        if (channel instanceof TextChannel textChannel) {
            textChannel.retrieveMessageById(helperMessageId).queue(originalMessage -> {
                List<Button> buttons = List.of(
                        Button.primary("continue_scenarios", "Continuă cu scenariile 📖"),
                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                );

                originalMessage.editMessageComponents(
                        ActionRow.of(buttons)
                ).queue(success -> {
                    System.out.println("Butoanele au fost actualizate cu succes!");
                }, error -> {
                    System.err.println("Eroare la actualizarea butoanelor: " + error.getMessage());
                });
            }, error -> {
                System.err.println("Eroare la preluarea mesajului pentru actualizare: " + error.getMessage());
            });
        } else {
            System.err.println("Canalul nu este un TextChannel sau este null.");
        }


        event.reply("Ai completat prima parte a cererii. Apasă pe butonul de mai jos pentru a continua cu scenariile.")
                .addActionRow(Button.primary("continue_scenarios", "Continuă cu scenariile 📖"))
                .setEphemeral(true)
                .queue();
    }


    private void handleStartHelperForm(ButtonInteractionEvent event) {
        if (event.isAcknowledged()) {
            System.err.println("Interacțiunea a fost deja procesată.");
            return;
        }

        Modal generalModal = HelperFlowManager.createGeneralQuestionsModal();

        event.replyModal(generalModal).queue(success -> {
            System.out.println("Modalul pentru `start_helper_form` a fost afișat cu succes.");
        }, error -> {
            System.err.println("Eroare la afișarea modalului pentru `start_helper_form`: " + error.getMessage());
        });
    }

    private void handleHelperRequest(ButtonInteractionEvent event) {
        var member = event.getMember();
        if (member == null) {
            event.reply("Nu am putut găsi membrul asociat cu această interacțiune.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut găsi serverul (guild-ul).").setEphemeral(true).queue();
            return;
        }

        String channelName = "ticket-helper-" + member.getEffectiveName().toLowerCase().replaceAll("\\s+", "-");
        String ticketCategory = "Cereri Helper";;
        String staffRoleId = UserRole.STAFF.getId();
        String opRoleId = UserRole.OPERATOR.getId();

        var categoryChannel = guild.getCategoryById(ID_CATEGORIE);
        if (categoryChannel == null) {
            event.reply("Categoria specificată pentru ticket-uri nu există.").setEphemeral(true).queue();
            return;
        }


        event.deferReply(true).queue(success -> {
            guild.createTextChannel(channelName)
                    .setParent(categoryChannel)
                    .setTopic("Ticket pentru cererea de Helper.")
                    .queue(channel -> {
                        channel.upsertPermissionOverride(guild.getPublicRole())
                                .deny(Permission.VIEW_CHANNEL)
                                .queue();

                        channel.upsertPermissionOverride(member)
                                .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                .queue();


                        var staffRole = guild.getRoleById(staffRoleId);
                        var opRole = guild.getRoleById(opRoleId);

                        if (staffRole != null) {
                            channel.upsertPermissionOverride(staffRole)
                                    .grant(Permission.VIEW_CHANNEL)
                                    .deny(Permission.MESSAGE_SEND)
                                    .queue();
                        if (opRole != null) {
                            channel.upsertPermissionOverride(opRole)
                                    .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                    .queue();
                        }
                            } else {
                            System.err.println("[handleHelperRequest] Nu am găsit rolul Staff cu ID: " + staffRoleId);
                        }

                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        TicketData ticketData = new TicketData(
                                member.getEffectiveName(),
                                "Cerere Helper",
                                "Neasignat",
                                timestamp,
                                ticketCategory,
                                event.getUser().getId()
                        );
                        ticketDataMap.put(channel.getId(), ticketData);

                        categorizedTickets.putIfAbsent(ticketCategory, new ArrayList<>());
                        categorizedTickets.get(ticketCategory).add(ticketData);

                        MessageEmbed instructionsEmbed = createInstructionsEmbed();
                        channel.sendMessageEmbeds(instructionsEmbed)
                                .setActionRow(
                                        Button.primary("start_helper_form", "Completează Formularul 📋"),
                                        Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                )
                                .queue(message -> {
                                    userResponses.putIfAbsent(member.getId(), new HashMap<>());
                                    userResponses.get(member.getId()).put("messageId", message.getId());
                                    userResponses.get(member.getId()).put("channelId", channel.getId());

                                    System.out.println("TicketData adăugat pentru canalul: " + channel.getId());
                                    System.out.println("Mesaj creat cu ID: " + message.getId());
                                }, error -> {
                                    System.err.println("Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                });

                        event.getHook().sendMessage("✅ Ticket-ul tău a fost creat: " + channel.getAsMention())
                                .setEphemeral(true)
                                .queue();
                    }, error -> {
                        event.getHook().sendMessage("A apărut o eroare la crearea ticket-ului.").setEphemeral(true).queue();
                        System.err.println("[handleHelperRequest] Eroare la crearea canalului: " + error.getMessage());
                    });
        }, failure -> {
            System.err.println("[handleHelperRequest] Failed to defer reply: " + failure.getMessage());
        });
    }



    private void handleRequestHelper(ButtonInteractionEvent event) {
        System.out.println("[handleRequestHelper] Triggered by user: " + event.getUser().getName());

        event.deferReply(true).queue(success -> {

            MessageEmbed embed = createRequestHelperEmbed();

            event.getHook().sendMessageEmbeds(embed)
                    .addActionRow(
                            Button.success("helper", "Helper \uD83E\uDD1D"),
                            Button.success("builder", "Builder \uD83D\uDEE0\uFE0F"),
                            Button.success("traducator", "Traducător \u270D\uFE0F"),
                            Button.success("media", "Media \uD83D\uDCF9"),
                            Button.success("unban", "Unban \uD83D\uDD13")
                    )
                    .addActionRow(
                            Button.success("developer", "Developer \uD83C\uDFC7")
                    )
                    .setEphemeral(true)
                    .queue();
        }, failure -> System.err.println("[handleRequestHelper] Failed to defer reply: " + failure.getMessage()));
    }


    public static List<Button> createMainButtons() {
        return List.of(
                Button.success("request_helper", "Cerere \uD83D\uDCC3"),
                Button.primary("recover_rank", "Recuperare Grad \uD83D\uDCC2"),
                Button.danger("recover_password", "Recuperare Parolă \ud83d\udd12"),
                Button.secondary("questions_or_issues", "Probleme/Întrebări/Reclamații \ud83d\udcf3")
        );
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("unban_staff")) {
            if (event.getValues().isEmpty()) {
                event.reply("Nu ai selectat niciun staff.").setEphemeral(true).queue();
                return;
            }

            String selectedStaffId = event.getValues().get(0);
            String userId = event.getUser().getId();

            userResponses.putIfAbsent(userId, new HashMap<>());
            userResponses.get(userId).put("selectedStaff", selectedStaffId);

            StaffManager.StaffMember selectedStaff = staffManager.getStaffList().stream()
                    .filter(staff -> selectedStaffId.equals(staff.idDiscord()))
                    .findFirst()
                    .orElse(null);

            if (selectedStaff != null) {
                TextInput reasonInput = TextInput.create("unban_reason", "Motivul cererii", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Introduceți motivul pentru care ați fost banat.")
                        .setRequired(true)
                        .build();

                Modal modal = Modal.create("unban_reason_modal", "Cerere Unban - Motiv")
                        .addActionRow(reasonInput)
                        .build();

                event.replyModal(modal).queue();

            } else {
                event.deferReply(true).queue();
                event.getHook().sendMessage("A apărut o eroare: staff-ul selectat nu este valid.")
                        .queue();
            }
        } else if (event.getComponentId().equals("complaint_staff")) {
            if (event.getValues().isEmpty()) {
                event.reply("❌ Nu ai selectat niciun membru staff.").setEphemeral(true).queue();
                return;
            }
            String userId = event.getUser().getId();
            String selectedStaffId = event.getValues().get(0);

            StaffManager.StaffMember selectedStaff = staffManager.getStaffList().stream()
                    .filter(staff -> selectedStaffId.equals(staff.idDiscord()))
                    .findFirst()
                    .orElse(null);

            if (selectedStaff == null) {
                event.reply("⚠️ Staff-ul selectat nu este valid. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                return;
            }

            String selectedStaffName = selectedStaff.nickname();

            userResponses.putIfAbsent(userId, new HashMap<>());
            userResponses.get(userId).put("selectedStaff", selectedStaffName);

            String userName = userResponses.get(userId).get("userName");

            if (userName == null || userName.trim().isEmpty()) {
                event.reply("❌ Nu am găsit informații despre utilizator. Te rugăm să reîncerci.").setEphemeral(true).queue();
                return;
            }

            createComplaintChannel(event, userName, selectedStaffName, selectedStaffId);
        }
    }

    private void createComplaintChannel(StringSelectInteractionEvent event, String userName, String staffName, String staffId) {
        String userId = event.getUser().getId();
        userResponses.putIfAbsent(userId, new HashMap<>());

        String categoryId = ID_CATEGORIE;
        String category = "Reclamații";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("⚠️ Nu am putut obține guild-ul. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            guild.retrieveMemberById(staffId).queue(staffMember -> {
                var categoryChannel = guild.getCategoryById(categoryId);
                if (categoryChannel == null) {
                    event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                    return;
                }

                guild.createTextChannel("ticket-reclamție-" + userName.toLowerCase().replaceAll("\\s+", "-"))
                        .setParent(categoryChannel)
                        .setTopic("Reclamație împotriva staff-ului: " + staffName)
                        .queue(channel -> {
                            String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                            TicketData ticketData = new TicketData(
                                    userName,
                                    "Reclamație: " + staffName,
                                    "Neasignat",
                                    formattedTimestamp,
                                    category,
                                    event.getUser().getId()
                            );

                            categorizedTickets.putIfAbsent(category, new ArrayList<>());
                            categorizedTickets.get(category).add(ticketData);

                            ticketDataMap.put(channel.getId(), ticketData);
                            userResponses.get(userId).put("channelId", channel.getId());

                            channel.upsertPermissionOverride(guild.getPublicRole())
                                    .deny(Permission.VIEW_CHANNEL)
                                    .queue();

                            channel.upsertPermissionOverride(member)
                                    .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                    .queue();

                            channel.upsertPermissionOverride(staffMember)
                                    .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                    .queue();

                            MessageEmbed instructionsEmbed = TicketEmbed.createInstructionsEmbed();

                            channel.sendMessageEmbeds(instructionsEmbed)
                                    .setActionRow(
                                            Button.primary("complete_complaint_form", "Completează Formularul 📋"),
                                            Button.danger("close_ticket", "Închide Ticket-ul ❌")
                                    )
                                    .queue(message -> {
                                        userResponses.get(userId).put("messageId", message.getId());
                                    });

                            event.reply("✅ Ticket-ul tău a fost creat: " + channel.getAsMention()).setEphemeral(true).queue();
                        }, error -> {
                            event.reply("⚠️ A apărut o eroare la crearea ticket-ului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                            System.err.println("[createComplaintChannel] Eroare la crearea canalului: " + error.getMessage());
                        });
            }, error -> {
                event.reply("⚠️ Nu am putut găsi staff-ul selectat. Te rugăm să încerci din nou.").setEphemeral(true).queue();
                System.err.println("[createComplaintChannel] Eroare la găsirea staff-ului: " + error.getMessage());
            });
        }, error -> {
            event.reply("⚠️ Nu am putut găsi membrul asociat contului. Te rugăm să încerci din nou.").setEphemeral(true).queue();
            System.err.println("[createComplaintChannel] Eroare la găsirea membrului: " + error.getMessage());
        });
    }




    private void createTicket(ModalInteractionEvent event, Map<String, String> responses) {
        String userName = responses.get("userName");
        String reason = responses.get("reason");
        String selectedStaffId = responses.get("selectedStaff");
        String urRoleId = UserRole.UR_TEAM.getId();

        if (userName == null || reason == null || selectedStaffId == null) {
            event.reply("Lipsește informația necesară pentru a crea ticket-ul.").setEphemeral(true).queue();
            return;
        }
        String categoryId = ID_CATEGORIE;
        String category = "Cereri Unban";

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Nu am putut obține guild-ul.").setEphemeral(true).queue();
            return;
        }

        guild.retrieveMember(event.getUser()).queue(member -> {
            guild.retrieveMemberById(selectedStaffId).queue(staffMember -> {
                if (member == null || staffMember == null) {
                    event.reply("Nu am putut găsi membrii în guild.").setEphemeral(true).queue();
                    return;
                }

                var categoryChannel = guild.getCategoryById(categoryId);
                if (categoryChannel == null) {
                    event.reply("Categoria specificată pentru ticket-uri nu există. Te rugăm să contactezi staff-ul.").setEphemeral(true).queue();
                    return;
                }

                guild.createTextChannel("ticket-unban-" + userName)
                        .setParent(categoryChannel)
                        .queue(channel -> {
                            String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                            TicketData ticketData = new TicketData(
                                    userName,
                                    reason,
                                    staffMember.getEffectiveName(),
                                    formattedTimestamp,
                                    category,
                                    event.getUser().getId()
                            );
                            categorizedTickets.putIfAbsent(category, new ArrayList<>());
                            categorizedTickets.get(category).add(ticketData);

                            ticketDataMap.put(channel.getId(), ticketData);

                            channel.upsertPermissionOverride(guild.getPublicRole())
                                    .deny(Permission.VIEW_CHANNEL)
                                    .queue();

                            channel.upsertPermissionOverride(member)
                                    .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                    .queue();

                            channel.upsertPermissionOverride(staffMember)
                                    .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                    .queue();


                            var urTeamRole = guild.getRoleById(urRoleId);
                            if (urTeamRole != null) {
                                channel.upsertPermissionOverride(urTeamRole)
                                        .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                        .queue();
                            } else {
                                System.err.println("Nu am găsit rolul UR Team cu ID: " + urRoleId);
                            }

                            MessageEmbed detailsEmbed = TicketEmbed.createTicketDetailsEmbed(userName, reason, staffMember.getEffectiveName());
                            MessageEmbed instructionsEmbed = createInstructionsEmbed();

                            channel.sendMessageEmbeds(detailsEmbed, instructionsEmbed)
                                    .setActionRow(TicketEmbed.createActionButtons())
                                    .queue(message -> {
                                        responses.put("messageId", message.getId());
                                        responses.put("channelId", channel.getId());
                                        userResponses.put(member.getId(), responses);
                                        System.out.println("Mesaj creat cu ID: " + message.getId());
                                        System.out.println("Canal creat cu ID: " + channel.getId());
                                    }, error -> {
                                        System.err.println("Eroare la trimiterea mesajului în canal: " + error.getMessage());
                                    });

                            event.reply("Ticket-ul tău a fost creat: " + channel.getAsMention())
                                    .setEphemeral(true)
                                    .queue();

                            userResponses.remove(member.getId());
                        }, error -> {
                            event.reply("A apărut o eroare la crearea ticket-ului.").setEphemeral(true).queue();
                            System.err.println("Eroare la crearea canalului: " + error.getMessage());
                        });
            }, error -> {
                event.reply("Nu am putut găsi staff-ul selectat.").setEphemeral(true).queue();
                System.err.println("Eroare la găsirea staff-ului: " + error.getMessage());
            });
        }, error -> {
            event.reply("Nu am putut găsi membrul care a creat ticket-ul.").setEphemeral(true).queue();
            System.err.println("Eroare la găsirea membrului utilizator: " + error.getMessage());
        });
    }
}