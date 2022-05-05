package com.wylxbot.wylx.Database.DbElements;

import com.wylxbot.wylx.Commands.Roles.RolesUtil.RoleMenu;
import com.wylxbot.wylx.Database.Codecs.RoleMenuCodec;
import com.wylxbot.wylx.Database.DiscordIdentifiers;
import org.bson.codecs.Codec;

import java.util.function.Supplier;

// These identifiers are used in database access to ensure information is located correctly
public enum RoleMenuIdentifiers implements DiscordIdentifiers {
    // We never want to create a menu unless the user asked for one
    // Just return null if default is asked for
    ROLE_MENU("Role_Menu", RoleMenu.class, () -> null, new RoleMenuCodec());

    public final String identifier;
    public final Class<?> dataType;
    public final Codec<?> codec;
    public final Supplier<Object> defaultSupplier;

    RoleMenuIdentifiers(String identifier, Class<?> dataType, Supplier<Object> defaultSupplier, Codec<?> codec) {
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultSupplier = defaultSupplier;
        this.codec = codec;
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
    public Codec<?> getCodec() {
        return codec;
    }

    @Override
    public Object getDefaultValue() {
        return defaultSupplier.get();
    }
}

