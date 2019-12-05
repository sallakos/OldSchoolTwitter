package projekti;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Salla Koskinen
 */
@Data
@AllArgsConstructor
public class AccountData {
    
    @NotEmpty
    @Size(min = 3, max = 30)
    private String name;
    
    @NotEmpty
    @Size(min = 3, max = 15)
    private String username;
    
    @NotEmpty
    @Size(min = 4, max = 100)
    private String password;
    
}
