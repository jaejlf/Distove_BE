package distove.community.dto.response;

import lombok.Getter;

@Getter
public class ServerDto {
    private Long id;
    private String name;

    public ServerDto(Long id, String name) {
        this.id = id;
        this.name = name;

    }
}
