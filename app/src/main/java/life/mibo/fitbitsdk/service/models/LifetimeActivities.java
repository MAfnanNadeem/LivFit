package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class LifetimeActivities {

    @Expose
    private Best best;
    @Expose
    private Lifetime lifetime;

    public Best getBest() {
        return best;
    }

    public void setBest(Best best) {
        this.best = best;
    }

    public Lifetime getLifetime() {
        return lifetime;
    }

    public void setLifetime(Lifetime lifetime) {
        this.lifetime = lifetime;
    }

}
