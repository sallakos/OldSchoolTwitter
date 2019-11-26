package projekti;

/**
 * 
 * @author Salla Koskinen
 */
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
//    @EntityGraph(attributePaths = {"followers", "followees", "messages"})
//    List<User> findAll();
    
    @EntityGraph(value = "User.allUserData")
    Account findByUsername(String username);
    
}