package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;
 
@Entity
public class User extends Model {
    
    @Email
    @Required
    public String email;

    @Required
    public String password;

    public String fullname;
    public boolean isAdmin;
    public boolean authorized;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Basket> baskets;
    
    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }
    
    public static User connect(String email, String password) {
        User user = find("byEmailAndPassword", email, password).first();
        if (!user.isAdmin || (user.isAdmin && user.authorized) ) {
            return user;
        } else {
            return null;
        }
    }
 
    public String toString() {
        return email;
    }
}
