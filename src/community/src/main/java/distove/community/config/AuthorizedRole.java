package distove.community.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizedRole {

    Auth name();

    enum Auth {
        CAN_DELETE_SERVER, CAN_MANAGE_SERVER, CAN_MANAGE_CHANNEL, CAN_UPDATE_MEMBER_ROLE
    }

}