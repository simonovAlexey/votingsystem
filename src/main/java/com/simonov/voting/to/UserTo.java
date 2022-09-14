package com.simonov.voting.to;

import com.simonov.voting.HasIdAndEmail;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.StringJoiner;

@Value
@EqualsAndHashCode(callSuper = true)
public class UserTo extends BaseTo implements HasIdAndEmail {

    @Size(min = 2, max = 255)
    String name;

    @Email
    @Size(max = 255)
    String email;

    @Size(min = 5, max = 64)
    String password;


    public UserTo(Integer id, String name, String email, String password) {
        super(id);
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static UserTo of(Integer id, String name, String email, String password) {
        return new UserTo(id, name, email, password);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "UserTo[", "]")
                .add("id=" + id)
                .add("email= '" + email + "'")
                .add("name= '" + name + "'")
                .toString();
    }


}
