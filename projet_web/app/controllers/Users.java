package controllers;
 
import play.*;
import play.mvc.*;
import models.User;
@Check("admin")
@With(Secure.class) 
public class Users extends CRUD {

    public static void authorize(Long id) {
        User user = User.findById(id);
        user.authorized = !user.authorized;
        user.save();
        flash.success("Authorization changed for " + user);
        Admin.index();

    }

}
