package projekti;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Mallintaa käyttäjän seuraamista.
 *
 * @author Salla Koskinen
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends AbstractPersistable<Long> {

    @ManyToOne
    private Account follower;
    
    @ManyToOne
    private Account followee;
    
    private LocalDateTime startOfFollow;
    
    private boolean pending;
    
}
