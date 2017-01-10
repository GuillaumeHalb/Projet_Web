package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Advice extends Model {
 
    public String title;
    public Date postedAt;
    
    @Lob
    public String content;
    
    @ManyToOne
    public User author;
    
    @OneToMany(mappedBy="advice", cascade=CascadeType.ALL)
    public List<Comment> comments;
     
    public Advice(User author, String title, String content) { 
        this.comments = new ArrayList<Comment>();
        this.author = author;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
    }
    
    public Advice previous() {
        return Advice.find("postedAt < ? order by postedAt desc", postedAt).first();
    }
     
    public Advice next() {
        return Advice.find("postedAt > ? order by postedAt asc", postedAt).first();
    }

    
    //TODO : ajouter comme attribut la liste des éléments réferant ( actions etc )
    //TODO : voir pour le type d'investissement ( peut etre avec un enum); la plus ou moins value; indice de confiance

    //TODO : réfléchir pour la catégorie
    //TODO : modifier le conseil, effacer le conseil ( ou alors dans la classe user ?)
    
    public Advice addComment(String author, String content) {
        Comment newComment = new Comment(this, author, content).save();
        this.comments.add(newComment);
        this.save();
        return this;
    }

 
}