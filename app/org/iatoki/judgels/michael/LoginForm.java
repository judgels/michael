package org.iatoki.judgels.michael;

import play.data.validation.Constraints;

public class LoginForm {
    @Constraints.Required
    public String username;

    @Constraints.Required
    public String password;

    public String validate() {
        if ((MichaelProperties.getInstance().getMichaelUsername().equals(username)) && (MichaelProperties.getInstance().getMichaelPassword().equals(password))) {
            return null;
        } else {
            return "Username atau sandi salah.";
        }
    }
}
