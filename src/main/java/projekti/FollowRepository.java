package projekti;

/**
 * Seuraamisen repo.
 *
 * @author Salla Koskinen
 */
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    Follow findByFollowerAndFollowee(Account follower, Account followee);
       
}
