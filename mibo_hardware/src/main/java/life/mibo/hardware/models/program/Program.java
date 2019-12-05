package life.mibo.hardware.models.program;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import life.mibo.hardware.encryption.MCrypt;
import life.mibo.hardware.models.program.parameters.Duration;


/**
 * Created by Fer on 16/03/2019.
 */

public class Program {

    @SerializedName("Id")
    private String id = "";

    @SerializedName("Name")
    private String name = "New Program";

    @SerializedName("Description")
    private String description = "";

    @SerializedName("BorgRating")
    private int borgRating = 6;

    @SerializedName("Category")
    private String category = "";

    @SerializedName("Type")
    private String type = "";

    @SerializedName("AccessType")
    private String accessType = "Private";

    @SerializedName("CreatedBy")
    private int createdBy = 0;

    @SerializedName("Duration")
    private Duration duration = new Duration();

    @SerializedName("Blocks")
    private ArrayList<Block> blocks = new ArrayList<Block>();

    public Program(){

    }

    public Program(Program programToCopy){
        this.id = programToCopy.id;
        this.name = programToCopy.name;
        this.description = programToCopy.description;
        this.borgRating = programToCopy.borgRating;
        this.category = programToCopy.category;
        this.type = programToCopy.type;
        this.accessType = programToCopy.accessType;
        this.createdBy = programToCopy.createdBy;
        this.duration = new Duration(programToCopy.duration);
        this.blocks = new ArrayList<Block>();
        for(Block b : programToCopy.blocks){
            this.blocks.add(new Block(b));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBorgRating() {
        return borgRating;
    }

    public void setBorgRating(int borgRating) {
        this.borgRating = borgRating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(int seconds) {
        if(this.duration == null)
            this.duration = new Duration();
        this.duration.setValue(seconds+"");
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(Block[] blocks) {
        this.blocks = new ArrayList<Block>(Arrays.asList(blocks));
    }

    public void addBlock(Block block){
        if(block == null){
            blocks.add(initNewBlock());
        } else {
            blocks.add(block);
        }
    }

    public void removeBlock(int index){
        blocks.remove(index);
    }

    private Block initNewBlock(){
        Block newBlock = new Block();
        newBlock.setBlockDuration(0);
        newBlock.setPauseDuration(0);
        newBlock.setActionDuration(0);
        newBlock.setUpRampDuration(0);
        newBlock.setDownRampDuration(0);
        newBlock.setPulseWidth(200);
        newBlock.setFrequency(25);
        return newBlock;
    }

    public void setProgramDataFromJSONAPI(JSONObject programData) {
        MCrypt mcrypt = new MCrypt();
        try {

            setId(programData.getInt("Id")+"");
            setName(programData.getString("Name"));
            setDescription(programData.getString("Description"));
            setBorgRating(programData.getInt("BorgRating"));
            setCategory(programData.getString("Category"));
            setType(programData.getString("Type"));
            setAccessType(programData.getString("AccessType"));
            setCreatedBy(programData.getInt("CreatedBy"));
            setDuration(Integer.parseInt(programData.getJSONObject("Duration").getString("value")));


            int UNPARSED_BLOCKS_LENGHT = programData.getJSONArray("Blocks").length();
            Block[] fetchedBlocks = new Block[UNPARSED_BLOCKS_LENGHT];
            for(int i = 0; i < UNPARSED_BLOCKS_LENGHT; i++) {
                Block block = new Block();
                try {
                    JSONObject unparsedBlock = programData.getJSONArray("Blocks").getJSONObject(i);
                    block.setBlockDataFromJSONAPI(unparsedBlock);

                    fetchedBlocks[i] = block;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            this.setBlocks(fetchedBlocks);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
