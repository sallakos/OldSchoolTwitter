package projekti;

/**
 * 
 * @author Salla Koskinen
 */
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Haetaan 25 uusinta käyttäjän ja käyttäjän seuraamien käyttäjien viestiä.
    @Query(value = "SELECT DISTINCT Message.id, message, time_sent, owner_id FROM MESSAGE " +
                   "JOIN Account ON Account.id = owner_id " +
                   "LEFT JOIN Follow ON followee_id = Account.id " +
                   "WHERE owner_id = :id OR (follower_id = :id AND Follow.pending = false) " +
                   "ORDER BY time_sent DESC LIMIT 25", nativeQuery = true)
    List<Message> getAllMessages(@Param("id") Long id);
    
    // Haetaan 25 uusinta käyttäjän viestiä.
    @Query(value = "SELECT * FROM Message WHERE owner_id = :id ORDER BY time_sent DESC LIMIT 25", nativeQuery = true)
    List<Message> getUserMessages(@Param("id") Long id);
    
}