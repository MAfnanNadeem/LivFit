package life.mibo.fitbitsdk.authentication;

import android.content.Intent;

import java.util.HashSet;

/**
 * Created by jboggess on 9/26/16.
 */

public class FitbitConfigurationBuilder {

    private FitbitConfiguration authenticationConfiguration;
    private boolean hasSetClientCredentials = false;

    public FitbitConfigurationBuilder() {
        authenticationConfiguration = new FitbitConfiguration();

        //Set default values
        authenticationConfiguration.setRequiredScopes(new HashSet<Scope>());
        authenticationConfiguration.setOptionalScopes(new HashSet<Scope>());
        authenticationConfiguration.setLogoutOnAuthFailure(false);
    }


    public FitbitConfigurationBuilder setClientCredentials(ClientCredentials clientCredentials) {
        this.authenticationConfiguration.setClientCredentials(clientCredentials);
        hasSetClientCredentials = clientCredentials != null && clientCredentials.isComplete();
        return this;
    }

    public FitbitConfigurationBuilder addRequiredScopes(Scope... requiredScopes) {
        for (Scope scope : requiredScopes) {
            authenticationConfiguration.getRequiredScopes().add(scope);
        }
        return this;
    }

    public FitbitConfigurationBuilder addOptionalScopes(Scope... optionalScopes) {
        for (Scope scope : optionalScopes) {
            authenticationConfiguration.getOptionalScopes().add(scope);
        }
        return this;
    }

    public FitbitConfigurationBuilder setBeforeLoginActivity(Intent beforeLoginActivity) {
        authenticationConfiguration.setBeforeLoginActivity(beforeLoginActivity);
        beforeLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        beforeLoginActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        beforeLoginActivity.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return this;
    }

    public FitbitConfigurationBuilder setLogoutOnAuthFailure(boolean loginOnAuthFailure) {
        authenticationConfiguration.setLogoutOnAuthFailure(loginOnAuthFailure);
        return this;
    }

    public FitbitConfigurationBuilder setTokenExpiresIn(Long tokenExpiresIn) {
        authenticationConfiguration.setTokenExpiresIn(tokenExpiresIn);
        return this;
    }

    public FitbitConfiguration build() {
        if (!hasSetClientCredentials) {
            throw new IllegalArgumentException("Error: client credentials not set! You must set client credentials with valid client id, client secret, and redirect url");
        } else if (authenticationConfiguration.getRequiredScopes().size()
                + authenticationConfiguration.getOptionalScopes().size() == 0) {
            throw new IllegalArgumentException("You must specify at least one oauth2 scope in `requiredScopes` or `optionalScopes`");
//        } else if (TextUtils.isEmpty(authenticationConfiguration.getEncryptionKey())) {
//            throw new IllegalArgumentException("Encryption Key must not be blank!");
        } else if (authenticationConfiguration.isLogoutOnAuthFailure() && authenticationConfiguration.getBeforeLoginActivity() == null) {
            throw new IllegalArgumentException("`BeforeLoginActivity must be set if auto-logout on auth failures");
        }

        return authenticationConfiguration;
    }


}
