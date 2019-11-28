package projekti;

/**
 * 
 * @author Salla Koskinen
 */
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
//    @EntityGraph(attributePaths = {"followers", "followees", "messages"})
//    List<User> findAll();
    
    @EntityGraph(value = "Account.allUserData")
    Account findByUsername(String username);
    
//    @Query(value = "SELECT * FROM ACCOUNT" +
//                   "JOIN Message ON Message.owner_id = Account.id" +
//                   "JOIN Picture ON Picture.owner_id = Account.id" +
//                   "JOIN Follow ON Follow.follower_id = Account.id" +
//                   "WHERE username = :username", nativeQuery = true)
//    Account allUserData(@Param("username") String username);
//    
//    @Query(value = "SELECT * FROM ACCOUNT" +
//                   "JOIN Follow ON Follow.follower_id = Account.id" +
//                   "WHERE username LIKE :username", nativeQuery = true)
//    List<Follow> userFollowees(@Param("username") String username);
    
}