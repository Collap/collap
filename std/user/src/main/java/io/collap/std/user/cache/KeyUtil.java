package io.collap.std.user.cache;

import io.collap.std.user.UserModule;
import io.collap.std.user.entity.User;

public class KeyUtil {

    public static String userProfile (User user) {
        return userProfile (user.getId ());
    }

    public static String userProfile (long id) {
        return UserModule.ARTIFACT_NAME + ":user.Profile:" + id;
    }

}
