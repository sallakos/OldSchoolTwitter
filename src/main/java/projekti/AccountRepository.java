package projekti;

/**
 * 
 * @author Salla Koskinen
 */
import java.math.BigInteger;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @EntityGraph(value = "Account.allUserData")
    Account findByUsername(String username);
    
    @Query(value = "SELECT followee_id FROM ACCOUNT " +
                   "JOIN Follow ON follower_id = Account.id " + 
                   "WHERE Account.id = :id AND pending = true", nativeQuery = true)
    List<Long> findPendingRequestsId(@Param("id") Long id);
    
    @Query(value = "SELECT Account.id, name, password, username, profile_picture_id FROM Follow " +
                   "JOIN Account ON follower_id = Account.id " +
                   "WHERE followee_id = :id AND pending = true", nativeQuery = true)
    List<Account> findPendingRequests(@Param("id") Long id);
    
}