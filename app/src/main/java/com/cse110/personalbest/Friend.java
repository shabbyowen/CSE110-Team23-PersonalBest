package com.cse110.personalbest;

public class Friend {
    public String nickname;
    public String email;

    public Friend(String email) {
        this.email = email;
        this.nickname = "";
    }

    public Friend(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toString() {
        return this.email;
    }
}
