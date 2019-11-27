package projekti;

/**
 * 
 * @author Salla Koskinen
 */
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
       
}