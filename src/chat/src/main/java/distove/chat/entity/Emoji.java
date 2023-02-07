package distove.chat.entity;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "emoji")
public class Emoji {

    @Id
    private String id;
    private String unicode; //1F601
    private String description; //grinning face with smiling eyes

     //뭔가 닉네임같은 거 넣으면 좋을듯 description 너무 길어
}
