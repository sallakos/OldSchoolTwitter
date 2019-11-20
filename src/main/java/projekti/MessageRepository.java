package projekti;

/**
 * 
 * @author Salla Koskinen
 */
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
       
}