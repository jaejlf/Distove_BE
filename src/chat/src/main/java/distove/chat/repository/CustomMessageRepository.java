package distove.chat.repository;

import distove.chat.entity.Message;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomMessageRepository {

    /*
    Parent
    */
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
    Optional<Message> findNextByCursor(Long channelId, LocalDateTime cursorCreatedAt);

    @Aggregation(pipeline = {
            "{ '$match': { " +
                    "'channelId' : ?0," +
                    "'parentId' : null," +
                    "'createdAt' : { $lt :  ?1} } }",
            "{ '$sort' : { 'createdAt' : -1 } }",
            "{ '$limit' : 1 }"})
    Optional<Message> findPreviousByCursor(Long channelId, LocalDateTime cursorCreatedAt);

    /*
    Reply
    */
    @Aggregation(pipeline = {
            "{ '$match': " +
                    "{ 'parentId' : ?0 } }",
            "{ '$sort' : { 'createdAt' : -1 } }"})
    List<Message> findAllRepliesByParentId(String parentId);

    /*
    Unread
    */
    @Query(value = "{ 'channelId' : ?0, 'createdAt' : { '$gt' : ?1 }, 'parentId' : null }", sort = "{ 'createdAt' : -1 }", count = true)
    int countUnreadMessage(Long channelId, LocalDateTime latestConnectedAt);

    @Aggregation(pipeline = {
            "{ '$match': { 'channelId' : ?0, " +
                    "'parentId' : null," +
                    "'createdAt' : { $gt :  ?1} } }",
            "{ '$sort' : { 'createdAt' : 1 } }",
            "{ '$limit' : 1 }"})
    Message findFirstUnreadMessage(Long channelId, LocalDateTime latestConnectedAt);


}
