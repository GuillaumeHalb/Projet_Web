
package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.Model;
 



@Entity
public class Basket extends Model {
    
    public String name;
    
    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL)
    public List<Advice> advices;
    
    public Basket(String name) {
        this.name = name;
        advices = new ArrayList<Advice>();
    }
    
    public Basket addAdvice(Advice adv) {
            this.advices.add(adv);
            this.save();
            return this;
            
    }
    
    public String toString() {
        return name;
    }


    public static Basket findOrCreateByName(String name) {
    	Basket basket = Basket.find("byName", name).first();
        if (basket == null) {
            basket = new Basket(name).save();
        }
        return basket;
    }
    
}