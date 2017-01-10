package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;
 
@Entity
public class Comment extends Model {
 
    @Required
    public String author;
    
    @Required
    public Date postedAt;
     
    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @ManyToOne
    @Required
    public Advice advice; 
    
    public Comment(Advice advice, String author, String content) {
        this.advice = advice;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }
 
}
