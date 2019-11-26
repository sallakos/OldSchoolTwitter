package projekti;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Mallintaa sovelluksen kuvaa.
 * @author Salla Koskinen
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Picture extends AbstractPersistable<Long> {
    
    @Lob
    private byte[] picture; // Kuva.
    private String description; // Teksti.
    
    // Kuva kuuluu yhdelle käyttäjälle, jolla voi olla monta kuvaa.
    @ManyToOne
    private Account owner; // Kuvan omistaja.
    
    // Moni käyttäjä voi tykätä kuvasta ja käyttäjä voi tykätä monesta kuvasta.
    @ManyToMany
    private List<Account> likes; // Käyttäjät, jotka ovat tykänneet.
    
}
