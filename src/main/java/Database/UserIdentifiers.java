package Database;

public enum UserIdentifiers {
    DiscordUser("Discord_Tag", DiscordMember.class, null);
    public final String identifier;
    public final Class dataType;
    public final Object defaultValue;

    UserIdentifiers(String identifier, Class dataType, Object defaultValue) {
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
    }
}
