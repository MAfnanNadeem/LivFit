package life.mibo.hardware.models.program;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import life.mibo.hardware.encryption.MCrypt;
import life.mibo.hardware.models.program.parameters.CustomParameter;


/**
 * Created by Fer on 22/03/2019.
 */

public class Block {

    @SerializedName("Id")
    private String id  = "";

    @SerializedName("ShortName")
    private String shortName  = "";

    @SerializedName("Waveform")
    private CustomParameter waveform = new CustomParameter();
    @SerializedName("BlockDuration")
    private CustomParameter blockDuration = new CustomParameter();
    @SerializedName("PulseWidth")
    private CustomParameter pulseWidth = new CustomParameter();
    @SerializedName("Frequency")
    private CustomParameter frequency = new CustomParameter();
    @SerializedName("UpRampDuration")
    private CustomParameter upRampDuration = new CustomParameter();
    @SerializedName("ActionDuration")
    private CustomParameter actionDuration = new CustomParameter();
    @SerializedName("DownRampDuration")
    private CustomParameter downRampDuration = new CustomParameter();
    @SerializedName("PauseDuration")
    private CustomParameter pauseDuration = new CustomParameter();

    public Block(Block blockToClone){
        this.id = blockToClone.id;
        this.shortName = blockToClone.shortName;
        this.waveform  = new CustomParameter(blockToClone.waveform );
        this.blockDuration = new CustomParameter(blockToClone.blockDuration);
        this.pulseWidth = new CustomParameter(blockToClone.pulseWidth);
        this.frequency = new CustomParameter(blockToClone.frequency);
        this.upRampDuration = new CustomParameter(blockToClone.upRampDuration);
        this.actionDuration = new CustomParameter(blockToClone.actionDuration);
        this.downRampDuration = new CustomParameter(blockToClone.downRampDuration);
        this.pauseDuration = new CustomParameter(blockToClone.pauseDuration);
    }
    public Block(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public CustomParameter getWaveform() {
        return waveform;
    }

    public void setWaveform(String waveform) {
        this.waveform.setValue(waveform);
    }

    public CustomParameter getBlockDuration() {
        return blockDuration;
    }

    public void setBlockDuration(int mseconds) {
        if(this.blockDuration == null)
            blockDuration = new CustomParameter();
        this.blockDuration.setValue(mseconds+"");
    }

    public CustomParameter getPulseWidth() {
        return pulseWidth;
    }

    public void setPulseWidth(int useconds) {
        if(this.pulseWidth == null)
            pulseWidth = new CustomParameter();
        this.pulseWidth.setValue(useconds+"");
    }

    public CustomParameter getFrequency() {
        return frequency;
    }

    public void setFrequency(int hz) {
        if(this.frequency == null)
            frequency = new CustomParameter();
        this.frequency.setValue(hz+"");
    }

    public CustomParameter getUpRampDuration() {
        return upRampDuration;
    }

    public void setUpRampDuration(int mseconds) {
        if(this.upRampDuration == null)
            upRampDuration = new CustomParameter();
        this.upRampDuration.setValue(mseconds+"");
    }

    public CustomParameter getActionDuration() {
        return actionDuration;
    }

    public void setActionDuration(int mseconds) {
        if(this.actionDuration == null)
            actionDuration = new CustomParameter();
        this.actionDuration.setValue(mseconds+"");
    }

    public CustomParameter getDownRampDuration() {
        return downRampDuration;
    }

    public void setDownRampDuration(int mseconds) {
        if(this.downRampDuration == null)
            downRampDuration = new CustomParameter();
        this.downRampDuration.setValue(mseconds+"");
    }

    public CustomParameter getPauseDuration() {
        return pauseDuration;
    }

    public void setPauseDuration(int mseconds) {
        if(this.pauseDuration == null)
            pauseDuration = new CustomParameter();
        this.pauseDuration.setValue(mseconds+"");
    }

    public void setBlockDataFromJSONAPI(JSONObject blockData) {
        MCrypt mcrypt = new MCrypt();
        try {

            setId(blockData.getInt("Id")+"");
            setShortName(blockData.getString("ShortName"));
            setWaveform(blockData.getJSONObject("Parameter").getJSONObject("Waveform").getString("value"));

            setPulseWidth(Integer.parseInt(blockData.getJSONObject("Parameter").getJSONObject("PulseWidth").getString("value")));
            setFrequency(Integer.parseInt(blockData.getJSONObject("Parameter").getJSONObject("Frequency").getString("value")));
            if(blockData.getJSONObject("Parameter").getJSONObject("UpRampDuration").getString("unit").equals("ms")){
                setUpRampDuration((int) Double.parseDouble(blockData.getJSONObject("Parameter").getJSONObject("UpRampDuration").getString("value")));
            } else  {
                setUpRampDuration((int)(Double.parseDouble(blockData.getJSONObject("Parameter").getJSONObject("UpRampDuration").getString("value"))*1000.0));
            }
            if(blockData.getJSONObject("Parameter").getJSONObject("ActionDuration").getString("unit").equals("ms")){
                setActionDuration((int) Double.parseDouble(blockData.getJSONObject("Parameter").getJSONObject("ActionDuration").getString("value")));
            } else  {
                setActionDuration((int)(Double.parseDouble(blockData.getJSONObject("Parameter").getJSONObject("ActionDuration").getString("value"))*1000.0));
            }
            if(blockData.getJSONObject("Parameter").getJSONObject("DownRampDuration").getString("unit").equals("ms")){
                setDownRampDuration((int) Double.parseDouble(blockData.getJSONObject("Parameter").getJSONObject("DownRampDuration").getString("value")));
            } else  {
                setDownRampDuration((int)(Double.parseDouble(blockData.getJSONObject("Parameter").getJSONObject("DownRampDuration").getString("value"))*1000.0));
            }
            if(blockData.getJSONObject("Parameter").getJSONObject("PauseDuration").getString("unit").equals("ms")){
                setPauseDuration((int) Double.parseDouble(blockData.getJSONObject("Parameter").getJSONObject("PauseDuration").getString("value")));
            } else  {
                setPauseDuration((int)(Double.parseDouble(blockData.getJSONObject("Parameter").getJSONObject("PauseDuration").getString("value"))*1000.0));
            }
            if (blockData.getJSONObject("Parameter").getJSONObject("BlockDuration").getString("value").equals("Automatic")) {
                setBlockDuration((Integer.parseInt(getDownRampDuration().getValue()))+
                        (Integer.parseInt(getUpRampDuration().getValue()))+
                        (Integer.parseInt(getActionDuration().getValue()))+
                        (Integer.parseInt(getPauseDuration().getValue())));
            } else {
                setBlockDuration(Integer.parseInt(blockData.getJSONObject("Parameter").getJSONObject("BlockDuration").getString("value"))*1000);
            }
            if(blockData.getJSONObject("Parameter").getJSONObject("Waveform").getString("value").equals("Constant")){
                setActionDuration(Integer.parseInt(getBlockDuration().getValue()));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
