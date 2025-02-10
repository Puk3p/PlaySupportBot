package Puk3p.util;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public enum UserRole {
    SERVER_OWNER("369235198304321555"),
    ADMINISTRATOR("447295712892420096"),
    MANAGER("628611473140219905"),
    UR_TEAM("1327757813791068201"),
    STAFF("369229269957607428"),
    HEAD_BUILDER("524819368375353354"),
    OPERATOR("369229141624619029");

    private final String id;
    UserRole(String id) { this.id = id; }

    public String getId() { return id; }

    public static boolean isAuthorized(SlashCommandInteractionEvent event, List<UserRole> allowedRoles) {
        if (event.getMember() == null) return false;
        return event.getMember().getRoles().stream()
                .anyMatch(role ->
                        allowedRoles.stream()
                                .anyMatch(allowedRole -> allowedRole.getId().equals(role.getId()))
                );
    }

}
