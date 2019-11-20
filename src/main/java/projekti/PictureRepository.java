package projekti;

/**
 * 
 * @author Salla Koskinen
 */
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    
}