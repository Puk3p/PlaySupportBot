package Puk3p.embed;

public enum Emoji {
    NPC("<:npc:1327795204517789706>"),
    MINECRAFT_CUBE("<a:minecraftcube:1327798787292397568>");

    private final String value;

    Emoji(String value) { this.value = value; }

    public String getValue() {
        return value;
    }
}
