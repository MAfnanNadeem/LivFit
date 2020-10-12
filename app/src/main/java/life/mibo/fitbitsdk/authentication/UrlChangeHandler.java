package life.mibo.fitbitsdk.authentication;

/**
 * Created by jboggess on 9/14/16.
 */
public interface UrlChangeHandler {
    boolean onUrlChanged(String newUrl);
    void onLoadError(int errorCode, CharSequence description);
}
