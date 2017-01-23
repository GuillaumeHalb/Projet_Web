package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;
 
@Entity
public class Review extends Model {
 
    @Required
    public String author;
    
    @Required
    public Date postedAt;
     
    @Required
    public int mark;
    
    @ManyToOne
    @Required
    public Advice advice; 
    
    public Review(Advice advice, String author, int mark) {
        this.advice = advice;
        this.author = author;
        this.mark = mark;
        this.postedAt = new Date();
    }
 
}
