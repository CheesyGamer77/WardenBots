package pw.cheesygamer77.wardenbots.internal;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

public final class Hasher {
    public static @NotNull String hashify(@NotNull String value) {
        return new DigestUtils("SHA3-256").digestAsHex(value);
    }

    public static @NotNull String hashify(@NotNull Long value) {
        return hashify(String.valueOf(value));
    }
}
