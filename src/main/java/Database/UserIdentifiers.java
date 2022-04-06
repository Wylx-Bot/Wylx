package Database;

public enum UserIdentifiers {
    Timezone("Timezone", String.class, "lol you haven't set a timezone");

    public final String identifier;
    public final Class dataType;
    public final Object defaultValue;
    UserIdentifiers(String identifier, Class dataType, Object defaultValue) {
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
    }
}
