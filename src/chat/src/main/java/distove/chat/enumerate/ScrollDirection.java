package distove.chat.enumerate;

import distove.chat.exception.DistoveException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static distove.chat.exception.ErrorCode.SCROLL_REQUEST_ERROR;

@Getter
@AllArgsConstructor
public enum ScrollDirection {

    DEFAULT(-1),
    DOWN(0),
    UP(1);

    private final int scroll;

    public static ScrollDirection getScrollDirection(Integer scroll) {
        return scroll != null ? Arrays.stream(ScrollDirection.values())
                .filter(direction -> direction.scroll == scroll)
                .findFirst()
                .orElseThrow(() -> new DistoveException(SCROLL_REQUEST_ERROR))
                : DEFAULT;
    }

}
