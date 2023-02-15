package distove.chat.repository;

import distove.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByParentId(String parentId);

    List<Message> findAllByChannelIdAndReplyNameIsNotNull(Long channelId);

    void deleteAllByParentId(String parentId);

    void deleteAllByChannelId(Long channelId);

//    @Query(value = "{ 'channelId' : ?0, 'parentId' : null }", sort = "{ 'createdAt' :  -1 }")
//    Page<Message> findAllParentByChannelId(Long channelId, Pageable pageable);

    @Query(value = "{ 'parentId' : ?0 }", sort = "{ 'createdAt' :  -1 }")
    Page<Message> findAllByChildByParentId(String parentId, Pageable pageable);

    @Query(value = "{ 'channelId' : ?0, 'createdAt' : { '$gt' : ?1 } }")
    List<Message> findUnreadMessage(Long channelId, LocalDateTime latestConnectedAt);

    @Aggregation(pipeline = {
            "{ '$match': { " +
                    "'channelId' : ?0," +
                    "'parentId' : null } }",
            "{ '$sort' : { 'createdAt' : -1 } }",
            "{ '$limit' : ?1 }"})
    List<Message> findAllParentByChannelId(Long channelId, int pageSize);

    @Aggregation(pipeline = {
            "{ '$match': { " +
                    "'channelId' : ?0," +
                    "'parentId' : null," +
                    "'createdAt' : { $gte :  ?1} } }",
            "{ '$sort' : { 'createdAt' : 1 } }",
            "{ '$limit' : ?2 }",
            "{ '$sort' : { 'createdAt' : -1 } }"})
    List<Message> findAllParentByChannelIdNext(Long channelId, LocalDateTime cursorCreatedAt, int pageSize);

    @Aggregation(pipeline = {
            "{ '$match': { " +
                    "'channelId' : ?0," +
                    "'parentId' : null," +
                    "'createdAt' : { $lte :  ?1} } }",
            "{ '$sort' : { 'createdAt' : -1 } }",
            "{ '$limit' : ?2 }"})
    List<Message> findAllParentByChannelIdPrevious(Long channelId, LocalDateTime cursorCreatedAt, int pageSize);

    @Aggregation(pipeline = {
            "{ '$match': { " +
                    "'channelId' : ?0," +
                    "'parentId' : null," +
                    "'createdAt' : { $gt :  ?1} } }",
            "{ '$sort' : { 'createdAt' : 1 } }",
            "{ '$limit' : 1 }",
            "{ '$sort' : { 'createdAt' : -1 } }"})
    Message findNextByCursor(Long channelId, LocalDateTime cursorCreatedAt);

    @Aggregation(pipeline = {
            "{ '$match': { " +
                    "'channelId' : ?0," +
                    "'parentId' : null," +
                    "'createdAt' : { $lt :  ?1} } }",
            "{ '$sort' : { 'createdAt' : -1 } }",
            "{ '$limit' : 1 }"})
    Message findPreviousByCursor(Long channelId, LocalDateTime cursorCreatedAt);

}
