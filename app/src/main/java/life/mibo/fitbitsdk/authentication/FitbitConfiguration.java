package life.mibo.fitbitsdk.authentication;

import android.content.Intent;

import java.util.Set;

/**
 * Created by jboggess on 9/26/16.
 */

public class FitbitConfiguration {

    private ClientCredentials clientCredentials;
    private Set<Scope> requiredScopes;
    private Set<Scope> optionalScopes;
    private Intent beforeLoginActivity;
    private boolean logoutOnAuthFailure;
    private String encryptionKey;
    private Long tokenExpiresIn;

    FitbitConfiguration() {
        //Package only!
    }

    public ClientCredentials getClientCredentials() {
        return clientCredentials;
    }

    public void setClientCredentials(String clientId, String clientSecret, String redirectUrl) {
        setClientCredentials(new ClientCredentials(clientId, clientSecret, redirectUrl));
    }

    void setClientCredentials(ClientCredentials clientCredentials) {
        this.clientCredentials = clientCredentials;
    }

    public Set<Scope> getRequiredScopes() {
        return requiredScopes;
    }

    void setRequiredScopes(Set<Scope> requiredScopes) {
        this.requiredScopes = requiredScopes;
    }

    public Set<Scope> getOptionalScopes() {
        return optionalScopes;
    }

    void setOptionalScopes(Set<Scope> optionalScopes) {
        this.optionalScopes = optionalScopes;
    }

    public Intent getBeforeLoginActivity() {
        return beforeLoginActivity;
    }

    void setBeforeLoginActivity(Intent beforeLoginActivity) {
        this.beforeLoginActivity = beforeLoginActivity;
    }

    public boolean isLogoutOnAuthFailure() {
        return logoutOnAuthFailure;
    }

    public void setLogoutOnAuthFailure(boolean logoutOnAuthFailure) {
        this.logoutOnAuthFailure = logoutOnAuthFailure;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public Long getTokenExpiresIn() {
        return tokenExpiresIn;
    }

    void setTokenExpiresIn(Long tokenExpiresIn) {
        this.tokenExpiresIn = tokenExpiresIn;
    }
}
