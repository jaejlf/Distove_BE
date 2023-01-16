package distove.community.dto.response;

import distove.community.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ServerResponse {
    private Long id;
    private String name;
    private List<Category> categorise;


    public ServerResponse(Long id, String name, List<Category> categorise){
        this.id = id;
        this.name = name;
        this.categorise = categorise;

    }
}
