package projekti;

/**
 * Kuviin liittyvät tietokantakyselyt.
 * 
 * @author Salla Koskinen
 */
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    
    // Haetaan kommentit samalla kuin kuvat, niin Thymeleaf hakee vain tykkäykset.
    @EntityGraph(attributePaths = {"comments"})
    List<Picture> findByOwner(Account account);
    
}