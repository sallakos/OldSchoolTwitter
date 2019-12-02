package projekti;

/**
 * 
 * @author Salla Koskinen
 */
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Haetaan 25 uusinta käyttäjän ja käyttäjän seuraamien käyttäjien viestiä.
    @Query(value = "SELECT DISTINCT Message.id, message, time_sent, owner_id FROM MESSAGE " +
                   "JOIN Account ON Account.id = owner_id " +
                   "JOIN Follow ON followee_id = Account.id " +
                   "WHERE (follower_id = :id OR owner_id = :id) AND Follow.pending = false " +
                   "ORDER BY time_sent DESC LIMIT 25", nativeQuery = true)
    List<Message> getAllMessages(@Param("id") Long id);
    
    // Haetaan 25 uusinta käyttäjän viestiä.
    @Query(value = "SELECT * FROM Message WHERE owner_id = :id ORDER BY time_sent DESC LIMIT 25", nativeQuery = true)
    List<Message> getUserMessages(@Param("id") Long id);
    
//    @Query(value = "SELECT comment.id, comment.comment, comment.time_sent, comment.commenter_id FROM Message " +
//                   "JOIN Message_comments ON Message.id = message_id " +
//                   "JOIN Comment ON Comment.id = comments_id " +
//                   "WHERE Message.id = :id " +
//                   "ORDER BY time_sent DESC " +
//                   "LIMIT 10", nativeQuery = true)
//    List<Comment> getMessageComments(@Param("id") Long id);
    
}