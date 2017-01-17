package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;
 
@Entity
public class Advice extends Model {
    @Required
    public String title;
    
    @Required
    public Date postedAt;
    
    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @Required
    @ManyToOne
    public User author;
    
    @OneToMany(mappedBy="advice", cascade=CascadeType.ALL)
    public List<Comment> comments;
    
    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Tag> tags;
    
    @ManyToOne
    public Basket basket;
     
    public Advice(User author, String title, String content) {
        this.comments = new ArrayList<Comment>();
        this.tags = new TreeSet<Tag>();
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
    
    public Advice tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }


    public static List<Advice> findTaggedWith(String tag) {
        return Advice.find(
            "select distinct p from Advice p join p.tags as t where t.name = ?", tag
        ).fetch();
    }

    
    public static List<Advice> findTaggedWith(String... tags) {
        return Advice.find(
                "select distinct p from Advice p join p.tags as t where t.name in (:tags) group by p.id, p.author, p.title, p.content,p.postedAt having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }
    
    public String toString(){
    	return title;
    }

}