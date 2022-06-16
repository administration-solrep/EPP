package fr.dila.st.api.model;

import java.util.Calendar;

public class TokenData {
    private final String token;
    private final Calendar expirationDate;

    public TokenData(String token, final Calendar expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return token;
    }

    public Calendar getExpirationDate() {
        return expirationDate;
    }
}
