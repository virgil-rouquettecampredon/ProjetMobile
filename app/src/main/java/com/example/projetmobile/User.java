package com.example.projetmobile;

public class User {

    private String pseudo;
    private String password;
    private String email;
    private Long elo;

    public User() {
    }

    public User(String pseudo, String password, String email) {
        this.pseudo = pseudo;
        this.password = password;
        this.email = email;
        this.elo = new Long(1000);

    }

    public String getPseudo() {
        return pseudo;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Long getElo() {
        return elo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
