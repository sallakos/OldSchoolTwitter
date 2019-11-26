package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Mallintaa sovelluksen viestiä.
 * @author Salla Koskinen
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message extends AbstractPersistable<Long> {
    
    @ManyToOne
    private Account owner; // Viestin lähettäjä.
    
    private LocalDateTime timeSent; // Viestin lähetysaika.
    private String message; // Viestin sisältö.
    
    // Moni käyttäjä voi tykätä viestistä ja käyttäjä voi tykätä monesta viestistä.
    @ManyToMany
    private List<Account> likes; // Käyttäjät, jotka ovat tykänneet.
    
    // Oma konstruktori, jolla voidaan luoda uusi viesti.
    public Message(String message, Account owner) {
        this.message = message;
        this.owner = owner;
        this.likes = new ArrayList<>();
        this.timeSent = LocalDateTime.now();
    }
    
}
