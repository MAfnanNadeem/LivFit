package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class TopBadge {

    @Expose
    private String badgeGradientEndColor;
    @Expose
    private String badgeGradientStartColor;
    @Expose
    private String badgeType;
    @Expose
    private String category;
    @Expose
    private List<Object> cheers;
    @Expose
    private String dateTime;
    @Expose
    private String description;
    @Expose
    private String earnedMessage;
    @Expose
    private String encodedId;
    @Expose
    private String image100px;
    @Expose
    private String image125px;
    @Expose
    private String image300px;
    @Expose
    private String image50px;
    @Expose
    private String image75px;
    @Expose
    private String marketingDescription;
    @Expose
    private String mobileDescription;
    @Expose
    private String name;
    @Expose
    private String shareImage640px;
    @Expose
    private String shareText;
    @Expose
    private String shortDescription;
    @Expose
    private String shortName;
    @Expose
    private Long timesAchieved;
    @Expose
    private Long value;

    public String getBadgeGradientEndColor() {
        return badgeGradientEndColor;
    }

    public void setBadgeGradientEndColor(String badgeGradientEndColor) {
        this.badgeGradientEndColor = badgeGradientEndColor;
    }

    public String getBadgeGradientStartColor() {
        return badgeGradientStartColor;
    }

    public void setBadgeGradientStartColor(String badgeGradientStartColor) {
        this.badgeGradientStartColor = badgeGradientStartColor;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Object> getCheers() {
        return cheers;
    }

    public void setCheers(List<Object> cheers) {
        this.cheers = cheers;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEarnedMessage() {
        return earnedMessage;
    }

    public void setEarnedMessage(String earnedMessage) {
        this.earnedMessage = earnedMessage;
    }

    public String getEncodedId() {
        return encodedId;
    }

    public void setEncodedId(String encodedId) {
        this.encodedId = encodedId;
    }

    public String getImage100px() {
        return image100px;
    }

    public void setImage100px(String image100px) {
        this.image100px = image100px;
    }

    public String getImage125px() {
        return image125px;
    }

    public void setImage125px(String image125px) {
        this.image125px = image125px;
    }

    public String getImage300px() {
        return image300px;
    }

    public void setImage300px(String image300px) {
        this.image300px = image300px;
    }

    public String getImage50px() {
        return image50px;
    }

    public void setImage50px(String image50px) {
        this.image50px = image50px;
    }

    public String getImage75px() {
        return image75px;
    }

    public void setImage75px(String image75px) {
        this.image75px = image75px;
    }

    public String getMarketingDescription() {
        return marketingDescription;
    }

    public void setMarketingDescription(String marketingDescription) {
        this.marketingDescription = marketingDescription;
    }

    public String getMobileDescription() {
        return mobileDescription;
    }

    public void setMobileDescription(String mobileDescription) {
        this.mobileDescription = mobileDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShareImage640px() {
        return shareImage640px;
    }

    public void setShareImage640px(String shareImage640px) {
        this.shareImage640px = shareImage640px;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getTimesAchieved() {
        return timesAchieved;
    }

    public void setTimesAchieved(Long timesAchieved) {
        this.timesAchieved = timesAchieved;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

}
