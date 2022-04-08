package Database;

import org.bson.codecs.Codec;

import java.lang.reflect.InvocationTargetException;

public enum UserIdentifiers implements DiscordIdentifiers{
    Timezone("Timezone", String.class, "LOL");

    private final String identifier;
    private final Class<?> dataType;
    private final Object defaultValue;

    UserIdentifiers(String identifier, Class<?> dataType, Object defaultValue){
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Class<?> getDataType() {
        return dataType;
    }

    @Override
    public Object getDefaultValue() {
        if(!(defaultValue instanceof Codec<?>))
            return defaultValue;
        else {
            try {
                return dataType.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        }
    }
}
