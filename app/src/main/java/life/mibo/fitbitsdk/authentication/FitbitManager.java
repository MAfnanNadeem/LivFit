package life.mibo.fitbitsdk.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import life.mibo.fitbitsdk.authentication.ui.FitbitLoginActivity;
import life.mibo.fitbitsdk.service.FitbitService;
import life.mibo.fitbitsdk.service.models.auth.OAuthAccessToken;
import life.mibo.fitbitsdk.service.storage.OAuthAccessTokenStorage;
import life.mibo.fitbitsdk.service.storage.SharedPreferenceTokenStorage;
import life.mibo.hardware.core.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jboggess on 9/14/16.
 * fitbitsdk from git@github.com:lucasiturbide/FitbitAndroidSDK.git
 */
public class FitbitManager {

    private static final String TAG = FitbitManager.class.getSimpleName();

    public static final String FITBIT_SHARED_PREFERENCE_NAME = "mibofitbit";
    public static final int FITBIT_LOGIN_REQUEST_CODE = 41;
    private static boolean configured = false;
    private static FitbitConfiguration authenticationConfiguration;
    private static OAuthAccessToken currentAccessToken;
    private static OAuthAccessTokenStorage tokenStorage;
    private static FitbitService apiService;

    public static void configure(Context context, FitbitConfiguration authenticationConfiguration) {
        FitbitManager.authenticationConfiguration = authenticationConfiguration;
        SharedPreferences preferences = context.getSharedPreferences(FITBIT_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        tokenStorage = new SharedPreferenceTokenStorage(preferences);
        configured = true;
        apiService = new FitbitService(preferences, authenticationConfiguration.getClientCredentials());
    }

    public static synchronized OAuthAccessToken getCurrentAccessToken() {
        checkPreconditions();
        if (currentAccessToken == null) {
            currentAccessToken = tokenStorage.getToken();
        }
        return currentAccessToken;
    }

    public static synchronized void setCurrentAccessToken(OAuthAccessToken currentAccessToken) {
        checkPreconditions();
        FitbitManager.currentAccessToken = currentAccessToken;

        //Save to shared tokenStorage
        tokenStorage.setToken(currentAccessToken);
    }

    public static void login(Activity activity) {
        Log.i(TAG, "login");

        Set<Scope> scopes = new HashSet<>();
        scopes.addAll(authenticationConfiguration.getRequiredScopes());
        scopes.addAll(authenticationConfiguration.getOptionalScopes());

        Intent intent = FitbitLoginActivity.createIntent(
                activity,
                authenticationConfiguration.getClientCredentials(),
                scopes);

        activity.startActivityForResult(intent, FITBIT_LOGIN_REQUEST_CODE);
    }

    public static void login(androidx.fragment.app.Fragment fragment) {
        log("login");

        Set<Scope> scopes = new HashSet<>();
        scopes.addAll(authenticationConfiguration.getRequiredScopes());
        scopes.addAll(authenticationConfiguration.getOptionalScopes());

        Intent intent = FitbitLoginActivity.createIntent(
                fragment.getContext(),
                authenticationConfiguration.getClientCredentials(),
                scopes);

        fragment.startActivityForResult(intent, FITBIT_LOGIN_REQUEST_CODE);
    }

    public static boolean onActivityResult(int requestCode, int resultCode, Intent data, @NonNull AuthenticationHandler authenticationHandler) {
        log("onActivityResult");
        checkPreconditions();
        switch (requestCode) {
            case (FITBIT_LOGIN_REQUEST_CODE): {
                if (resultCode == Activity.RESULT_OK) {
                    AuthenticationResult authenticationResult = data.getParcelableExtra(FitbitLoginActivity.AUTHENTICATION_RESULT_KEY);

                    if (authenticationResult.isSuccessful() && authenticationResult.getAccessToken() != null) {
                        Set<Scope> grantedScopes = new HashSet<>(authenticationResult.getAccessToken().getScopes());
                        Set<Scope> requiredScopes = new HashSet<>(authenticationConfiguration.getRequiredScopes());

                        requiredScopes.removeAll(grantedScopes);
                        if (requiredScopes.size() > 0) {
                            authenticationResult = AuthenticationResult.missingRequiredScopes(requiredScopes);
                        } else {
                            setCurrentAccessToken(authenticationResult.getAccessToken());
                        }
                    }

                    authenticationHandler.onAuthFinished(authenticationResult);
                } else {
                    authenticationHandler.onAuthFinished(AuthenticationResult.dismissed());
                }
                return true;
            }
        }

        return false;
    }

    public static boolean isLoggedIn() {
        try {
            log("isLoggedIn " + configured);
            if (configured) {
                OAuthAccessToken currentAccessToken = getCurrentAccessToken();
                log("isLoggedIn currentAccessToken " + currentAccessToken);
                if(currentAccessToken != null){
                    log("isLoggedIn currentAccessToken " + currentAccessToken.needsRefresh());
                    return !currentAccessToken.needsRefresh();
                }

            }
        } catch (Exception e) {

        }
        return false;
    }

    public static void logout(final Activity contextActivity) {
        logout(contextActivity, null);
    }

    public static void logout(final Activity contextActivity, @Nullable final LogoutTaskCompletionHandler logoutTaskCompletionHandler) {
        log("logout");
        checkPreconditions();
        if (!isLoggedIn()) {
            if (logoutTaskCompletionHandler != null) {
                logoutTaskCompletionHandler.logoutSuccess();
            }
            return;
        }

        final ClientCredentials clientCredentials = getAuthenticationConfiguration().getClientCredentials();
        String tokenString = String.format("%s:%s", clientCredentials.getClientId(), clientCredentials.getClientSecret());
        String currentToken = getCurrentAccessToken().getAccess_token() != null ? getCurrentAccessToken().getAccess_token() : "";
        apiService.getTokenService().revokeToken(currentToken).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                log("revokeToken onResponse");

                if (response.isSuccessful()) {
                    Intent beforeLoginActivity = authenticationConfiguration.getBeforeLoginActivity();
//                    if (beforeLoginActivity != null) {
//                        contextActivity.startActivity(beforeLoginActivity);
//                    }
                    if (logoutTaskCompletionHandler != null) {
                        logoutTaskCompletionHandler.logoutSuccess();
                    }
                } else {
                    logoutError(response.message(), contextActivity, logoutTaskCompletionHandler);
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                log("revokeToken onFailure");
                logoutError("", contextActivity, logoutTaskCompletionHandler);
            }
        });
        setCurrentAccessToken(null);
    }

    private static void logoutError(String message, Activity contextActivity, @Nullable LogoutTaskCompletionHandler logoutTaskCompletionHandler) {
        log("revokeToken onFailure");
        Intent beforeLoginActivity = authenticationConfiguration.getBeforeLoginActivity();
        if (beforeLoginActivity != null) {
            log("logoutError startActivity beforeLoginActivity");
            contextActivity.startActivity(beforeLoginActivity);
        }
        if (logoutTaskCompletionHandler != null) {
            logoutTaskCompletionHandler.logoutError(message);
        }
    }

    public static FitbitConfiguration getAuthenticationConfiguration() {
        checkPreconditions();
        return authenticationConfiguration;
    }

    public static @Nullable
    FitbitService getService() {
        return apiService;
    }

    private static void checkPreconditions() {
        if (!configured) {
            throw new IllegalArgumentException("You must call `configure` on FitbitManager before using its methods!");
        }
    }

    public static void log(String msg) {
        Logger.e(TAG, msg);
    }

}
