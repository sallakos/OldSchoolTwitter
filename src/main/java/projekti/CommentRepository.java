package projekti;

/**
 * Kommentteihin liittyvät tietokantakyselyt.
 * 
 * @author Salla Koskinen
 */
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @Query(value = "SELECT Comment.id, Comment.comment, Comment.time_sent, Comment.commenter_id FROM MESSAGE " +
                   "LEFT JOIN Message_comments ON Message_comments.message_id = Message.id " +
                   "LEFT JOIN Comment ON Comment.id = comments_id " +
                   "WHERE message.id = :id " +
                   "ORDER BY time_sent " +
                   "LIMIT 10", nativeQuery = true)
    List<Comment> findByMessageId(@Param("id") Long id);
       
}