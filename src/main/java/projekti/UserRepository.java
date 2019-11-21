package projekti;

/**
 * 
 * @author Salla Koskinen
 */
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
//    @EntityGraph(attributePaths = {"followers", "followees", "messages"})
//    List<User> findAll();
    
    @EntityGraph(value = "User.allUserData")
    User findByUsername(String username);
    
}