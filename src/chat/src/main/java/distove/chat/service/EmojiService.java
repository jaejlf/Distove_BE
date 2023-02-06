package distove.chat.service;

import distove.chat.entity.Emoji;
import distove.chat.repository.EmojiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmojiService {
    private final EmojiRepository emojiRepository;
    public List<Emoji> getEmojis(){
        List<Emoji> emojis = emojiRepository.findAll();
        return emojis;
    }
}
