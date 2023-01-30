package distove.community.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultRoleName {

    OWNER("소유자"),
    MANAGER("관리자"),
    MEMBER("멤버");

    private final String name;

}
