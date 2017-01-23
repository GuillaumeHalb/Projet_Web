
package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.Model;
 



@Entity
public class Basket extends Model {
    
    public String name;
    
    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL)
    public List<Advice> advices;
    
    @ManyToOne
    public User user;
    
    public Basket(String name, User user) {
        this.name = name;
        advices = new ArrayList<Advice>();
        this.user = user;
    }
    
    public Basket addAdvice(Advice adv) {
            this.advices.add(adv);
            this.save();
            return this;
            
    }
    
    public String toString() {
        return name;
    }

    
}