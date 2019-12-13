package projekti;

/**
 * Käyttäjään liittyvät tietokantakyselyt.
 * 
 * @author Salla Koskinen
 */
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @EntityGraph(value = "Account.allUserData")
    Account findByUsername(String username);
    
    // Haetaan kaikki käyttäjät ja heidän kuvansa (vaikka niitä ei periaatteessa tarvita...)
    @EntityGraph(value = "Account.allUserData")
    @Override
    List<Account> findAll(Sort sort);
        
    @Query(value = "SELECT Account.id, name, password, username, profile_picture_id FROM Follow " +
                   "JOIN Account ON follower_id = Account.id " +
                   "WHERE followee_id = :id AND pending = true", nativeQuery = true)
    List<Account> findPendingRequests(@Param("id") Long id);
    
    @Query(value = "SELECT COUNT(Account.id) FROM Follow " +
                   "JOIN Account ON followee_id = Account.id " +
                   "WHERE Account.id = :id AND pending = true", nativeQuery = true)
    int numberOfPendingRequests(@Param("id") Long id);
    
    @Query(value = "SELECT * FROM Account " +
                   "JOIN Follow ON Follow.followee_id = Account.id " +
                   "WHERE Follow.follower_id = :id AND Follow.pending = false " + 
                   "ORDER BY Account.name", nativeQuery = true)
    List<Account> findUserFollowees(@Param("id") Long id);
       
    @Query(value = "SELECT * FROM Account " +
                   "JOIN Follow ON Follow.follower_id = Account.id " +
                   "WHERE Follow.followee_id = :id AND Follow.pending = false " + 
                   "ORDER BY Account.name", nativeQuery = true)
    List<Account> findUserFollowers(@Param("id") Long id);    
    
    @Query(value = "SELECT id FROM PICTURE " +
                   "WHERE owner_id = :id", nativeQuery = true)
    List<Long> findUserPictures(@Param("id") Long id);
    
}