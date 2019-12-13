package projekti;

/**
 * Seuraamiseen liittyviä tietokantakyselyjä.
 *
 * @author Salla Koskinen
 */
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    Follow findByFollowerAndFollowee(Account follower, Account followee);
    
}
