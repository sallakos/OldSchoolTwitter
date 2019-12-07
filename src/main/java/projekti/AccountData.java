package projekti;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Hyödynnetään rekisteröitymisen yhteydessä.
 *
 * @author Salla Koskinen
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountData extends AbstractPersistable<Long> {
    
    @NotEmpty(message = "Kenttä ei saa olla tyhjä!")
    @Size(min = 3, max = 45, message = "Nimen tulee olla 3-45 merkkiä pitkä.")
    private String name;
    
    @NotEmpty(message = "Kenttä ei saa olla tyhjä!")
    @Size(min = 3, max = 15, message = "Käyttäjänimen tulee olla 3-15 merkkiä pitkä.")
    private String username;
    
    @NotEmpty(message = "Kenttä ei saa olla tyhjä!")
    @Size(min = 4, max = 100, message = "Salasanan tulee olla 4-100 merkkiä pitkä.")
    private String password;
    
}
