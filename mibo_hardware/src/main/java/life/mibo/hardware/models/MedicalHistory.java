package life.mibo.hardware.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fer on 18/03/2019.
 */

public class MedicalHistory {

    private String height = "0";
    private String weight = "70";
    private String blood_group = "";
    private String smoker = "";
    private String alcohol = "";
    private String pintPerWeek = "";
    private String diabetes = "";
    private String diabetesType = "";
    private String heartDisease = "";
    private String heartDiseaseDesc = "";
    private String chestPains = "";
    private String chestPainsDesc = "";
    private String shortnessOfBreath = "";
    private String shortnessOfBreathDesc = "";
    private String brokenBones = "";
    private String brokenBonesDesc = "";
    private String allergies = "";
    private String allergiesDesc = "";
    private String recentChildbirth = "";
    private String recentChildbirthDesc = "";
    private String heartMurmurprivate = "";
    private String heartMurmurDesc = "";
    private String pneumonia = "";
    private String pneumoniaDesc = "";
    private String epilepsySeizures = "";
    private String epilepsySeizuresDesc = "";
    private String tachycardia = "";
    private String tachycardiaDesc = "";
    private String oedema = "";
    private String oedemaDesc = "";
    private String heartAttack = "";
    private String heartAttackDesc = "";
    private String recentSurgery = "";
    private String recentSurgeryDesc = "";
    private String palpitations = "";
    private String palpitationsDesc = "";
    private String bloodPressure = "";
    private String bloodPressureDesc = "";
    private String asthma = "";
    private String asthmaDesc = "";
    private String muscleJointProblems = "";
    private String muscleJointProblemsDesc = "";
    private String physicalDisabilities = "";
    private String physicalDisabilitiesDesc = "";
    private String mentalDisabilities = "";
    private String mentalDisabilitiesDesc = "";
    private String ulcers = "";
    private String ulcersDesc = "";
    private String heartRateMonitors = "";
    private String heartRateMonitorsDesc = "";
    private String currentlyPregnant = "";
    private String currentlyPregnantDesc = "";
    private String anyOther = "";
    private String anyOtherDesc = "";
    private String medicalHistory = "";

    public MedicalHistory() {
    }

    public MedicalHistory(String height, String weight, String blood_group, String smoker,
                          String alcohol, String pintPerWeek, String diabetes, String diabetesType,
                          String heartDisease, String heartDiseaseDesc, String chestPains,
                          String chestPainsDesc, String shortnessOfBreath,
                          String shortnessOfBreathDesc, String brokenBones, String brokenBonesDesc,
                          String allergies, String allergiesDesc, String recentChildbirth,
                          String recentChildbirthDesc, String heartMurmurprivate,
                          String heartMurmurDesc, String pneumonia, String pneumoniaDesc,
                          String epilepsySeizures, String epilepsySeizuresDesc, String tachycardia,
                          String tachycardiaDesc, String oedema, String oedemaDesc,
                          String heartAttack, String heartAttackDesc, String recentSurgery,
                          String recentSurgeryDesc, String palpitations, String palpitationsDesc,
                          String bloodPressure, String bloodPressureDesc, String asthma,
                          String asthmaDesc, String muscleJointProblems,
                          String muscleJointProblemsDesc, String physicalDisabilities,
                          String physicalDisabilitiesDesc, String mentalDisabilities,
                          String mentalDisabilitiesDesc, String ulcers, String ulcersDesc,
                          String heartRateMonitors, String heartRateMonitorsDesc,
                          String currentlyPregnant, String currentlyPregnantDesc, String anyOther,
                          String anyOtherDesc, String medicalHistory) {
        this.height = height;
        this.weight = weight;
        this.blood_group = blood_group;
        this.smoker = smoker;
        this.alcohol = alcohol;
        this.pintPerWeek = pintPerWeek;
        this.diabetes = diabetes;
        this.diabetesType = diabetesType;
        this.heartDisease = heartDisease;
        this.heartDiseaseDesc = heartDiseaseDesc;
        this.chestPains = chestPains;
        this.chestPainsDesc = chestPainsDesc;
        this.shortnessOfBreath = shortnessOfBreath;
        this.shortnessOfBreathDesc = shortnessOfBreathDesc;
        this.brokenBones = brokenBones;
        this.brokenBonesDesc = brokenBonesDesc;
        this.allergies = allergies;
        this.allergiesDesc = allergiesDesc;
        this.recentChildbirth = recentChildbirth;
        this.recentChildbirthDesc = recentChildbirthDesc;
        this.heartMurmurprivate = heartMurmurprivate;
        this.heartMurmurDesc = heartMurmurDesc;
        this.pneumonia = pneumonia;
        this.pneumoniaDesc = pneumoniaDesc;
        this.epilepsySeizures = epilepsySeizures;
        this.epilepsySeizuresDesc = epilepsySeizuresDesc;
        this.tachycardia = tachycardia;
        this.tachycardiaDesc = tachycardiaDesc;
        this.oedema = oedema;
        this.oedemaDesc = oedemaDesc;
        this.heartAttack = heartAttack;
        this.heartAttackDesc = heartAttackDesc;
        this.recentSurgery = recentSurgery;
        this.recentSurgeryDesc = recentSurgeryDesc;
        this.palpitations = palpitations;
        this.palpitationsDesc = palpitationsDesc;
        this.bloodPressure = bloodPressure;
        this.bloodPressureDesc = bloodPressureDesc;
        this.asthma = asthma;
        this.asthmaDesc = asthmaDesc;
        this.muscleJointProblems = muscleJointProblems;
        this.muscleJointProblemsDesc = muscleJointProblemsDesc;
        this.physicalDisabilities = physicalDisabilities;
        this.physicalDisabilitiesDesc = physicalDisabilitiesDesc;
        this.mentalDisabilities = mentalDisabilities;
        this.mentalDisabilitiesDesc = mentalDisabilitiesDesc;
        this.ulcers = ulcers;
        this.ulcersDesc = ulcersDesc;
        this.heartRateMonitors = heartRateMonitors;
        this.heartRateMonitorsDesc = heartRateMonitorsDesc;
        this.currentlyPregnant = currentlyPregnant;
        this.currentlyPregnantDesc = currentlyPregnantDesc;
        this.anyOther = anyOther;
        this.anyOtherDesc = anyOtherDesc;
        this.medicalHistory = medicalHistory;
    }

    public JSONObject toJSON () {
        JSONObject medicalHistory = new JSONObject();

        try {
            medicalHistory.accumulate("height", getheight());
            medicalHistory.accumulate("weight", getWeight());
            medicalHistory.accumulate("blood_group", getBlood_group());
            medicalHistory.accumulate("smoker", getSmoker());
            medicalHistory.accumulate("alcohol", getAlcohol());
            medicalHistory.accumulate("pintPerWeek", getPintPerWeek());
            medicalHistory.accumulate("diabetes", getDiabetes());
            medicalHistory.accumulate("diabetesType", getDiabetesType());
            medicalHistory.accumulate("heartDisease", getHeartDisease());
            medicalHistory.accumulate("heartDiseaseDesc", getHeartDiseaseDesc());
            medicalHistory.accumulate("chestPains", getChestPains());
            medicalHistory.accumulate("chestPainsDesc", getChestPainsDesc());
            medicalHistory.accumulate("shortnessOfBreath", getShortnessOfBreath());
            medicalHistory.accumulate("shortnessOfBreathDesc", getShortnessOfBreathDesc());
            medicalHistory.accumulate("brokenBones", getBrokenBones());
            medicalHistory.accumulate("brokenBonesDesc", getBrokenBonesDesc());
            medicalHistory.accumulate("allergies", getAllergies());
            medicalHistory.accumulate("allergiesDesc", getAllergiesDesc());
            medicalHistory.accumulate("recentChildbirth", getRecentChildbirth());
            medicalHistory.accumulate("recentChildbirthDesc", getRecentChildbirthDesc());
            medicalHistory.accumulate("heartMurmurprivate", getHeartMurmurprivate());
            medicalHistory.accumulate("heartMurmurDesc", getHeartMurmurDesc());
            medicalHistory.accumulate("pneumonia", getPneumonia());
            medicalHistory.accumulate("pneumoniaDesc", getPneumoniaDesc());
            medicalHistory.accumulate("epilepsySeizures", getEpilepsySeizures());
            medicalHistory.accumulate("epilepsySeizuresDesc", getEpilepsySeizuresDesc());
            medicalHistory.accumulate("tachycardia", getTachycardia());
            medicalHistory.accumulate("tachycardiaDesc", getTachycardiaDesc());
            medicalHistory.accumulate("oedema", getOedema());
            medicalHistory.accumulate("oedemaDesc", getOedemaDesc());
            medicalHistory.accumulate("heartAttack", getHeartAttack());
            medicalHistory.accumulate("heartAttackDesc", getHeartAttackDesc());
            medicalHistory.accumulate("recentSurgery", getRecentSurgery());
            medicalHistory.accumulate("recentSurgeryDesc", getRecentSurgeryDesc());
            medicalHistory.accumulate("palpitations", getPalpitations());
            medicalHistory.accumulate("palpitationsDesc", getPalpitationsDesc());
            medicalHistory.accumulate("bloodPressure", getBloodPressure());
            medicalHistory.accumulate("bloodPressureDesc", getBloodPressureDesc());
            medicalHistory.accumulate("asthma", getAsthma());
            medicalHistory.accumulate("asthmaDesc", getAsthmaDesc());
            medicalHistory.accumulate("muscleJointProblems", getMuscleJointProblems());
            medicalHistory.accumulate("muscleJointProblemsDesc", getMuscleJointProblemsDesc());
            medicalHistory.accumulate("physicalDisabilities", getPhysicalDisabilities());
            medicalHistory.accumulate("physicalDisabilitiesDesc", getPhysicalDisabilitiesDesc());
            medicalHistory.accumulate("mentalDisabilities", getMentalDisabilities());
            medicalHistory.accumulate("mentalDisabilitiesDesc", getMentalDisabilitiesDesc());
            medicalHistory.accumulate("ulcers", getUlcers());
            medicalHistory.accumulate("ulcersDesc", getUlcersDesc());
            medicalHistory.accumulate("heartRateMonitors", getHeartRateMonitors());
            medicalHistory.accumulate("heartRateMonitorsDesc", getHeartRateMonitorsDesc());
            medicalHistory.accumulate("currentlyPregnant", getCurrentlyPregnant());
            medicalHistory.accumulate("currentlyPregnantDesc", getCurrentlyPregnantDesc());
            medicalHistory.accumulate("anyOther", getAnyOther());
            medicalHistory.accumulate("anyOtherDesc", getAnyOtherDesc());
            medicalHistory.accumulate("medicalHistory", getMedicalHistory());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return medicalHistory;
    }

    public void setHeight(String value) {
        height = value;
    }

    public void setWeight(String value) {
        weight = value;
    }

    public void setBlood_group(String value) {
        blood_group = value;
    }

    public void setSmoker(String value) {
        smoker = value;
    }

    public void setAlcohol(String value) {
        alcohol = value;
    }

    public void setPintPerWeek(String value) {
        pintPerWeek = value;
    }

    public void setDiabetes(String value) {
        diabetes = value;
    }

    public void setDiabetesType(String value) {
        diabetesType = value;
    }

    public void setHeartDisease(String value) {
        heartDisease = value;
    }

    public void setHeartDiseaseDesc(String value) {
        heartDiseaseDesc = value;
    }

    public void setChestPains(String value) {
        chestPains = value;
    }

    public void setChestPainsDesc(String value) {
        chestPainsDesc = value;
    }

    public void setShortnessOfBreath(String value) {
        shortnessOfBreath = value;
    }

    public void setShortnessOfBreathDesc(String value) {
        shortnessOfBreathDesc = value;
    }

    public void setBrokenBones(String value) {
        brokenBones = value;
    }

    public void setBrokenBonesDesc(String value) {
        brokenBonesDesc = value;
    }

    public void setAllergies(String value) {
        allergies = value;
    }

    public void setAllergiesDesc(String value) {
        allergiesDesc = value;
    }

    public void setRecentChildbirth(String value) {
        recentChildbirth = value;
    }

    public void setRecentChildbirthDesc(String value) {
        recentChildbirthDesc = value;
    }

    public void setHeartMurmurprivate(String value) {
        heartMurmurprivate = value;
    }

    public void setHeartMurmurDesc(String value) {
        heartMurmurDesc = value;
    }

    public void setPneumonia(String value) {
        pneumonia = value;
    }

    public void setPneumoniaDesc(String value) {
        pneumoniaDesc = value;
    }

    public void setEpilepsySeizures(String value) {
        epilepsySeizures = value;
    }

    public void setEpilepsySeizuresDesc(String value) {
        epilepsySeizuresDesc = value;
    }

    public void setTachycardia(String value) {
        tachycardia = value;
    }

    public void setTachycardiaDesc(String value) {
        tachycardiaDesc = value;
    }

    public void setOedema(String value) {
        oedema = value;
    }

    public void setOedemaDesc(String value) {
        oedemaDesc = value;
    }

    public void setHeartAttack(String value) {
        heartAttack = value;
    }

    public void setHeartAttackDesc(String value) {
        heartAttackDesc = value;
    }

    public void setRecentSurgery(String value) {
        recentSurgery = value;
    }

    public void setRecentSurgeryDesc(String value) {
        recentSurgeryDesc = value;
    }

    public void setPalpitations(String value) {
        palpitations = value;
    }

    public void setPalpitationsDesc(String value) {
        palpitationsDesc = value;
    }

    public void setBloodPressure(String value) {
        bloodPressure = value;
    }

    public void setBloodPressureDesc(String value) {
        bloodPressureDesc = value;
    }

    public void setAsthma(String value) {
        asthma = value;
    }

    public void setAsthmaDesc(String value) {
        asthmaDesc = value;
    }

    public void setMuscleJointProblems(String value) {
        muscleJointProblems = value;
    }

    public void setMuscleJointProblemsDesc(String value) {
        muscleJointProblemsDesc = value;
    }

    public void setPhysicalDisabilities(String value) {
        physicalDisabilities = value;
    }

    public void setPhysicalDisabilitiesDesc(String value) {
        physicalDisabilitiesDesc = value;
    }

    public void setMentalDisabilities(String value) {
        mentalDisabilities = value;
    }

    public void setMentalDisabilitiesDesc(String value) {
        mentalDisabilitiesDesc = value;
    }

    public void setUlcers(String value) {
        ulcers = value;
    }

    public void setUlcersDesc(String value) {
        ulcersDesc = value;
    }

    public void setHeartRateMonitors(String value) {
        heartRateMonitors = value;
    }

    public void setHeartRateMonitorsDesc(String value) {
        heartRateMonitorsDesc = value;
    }

    public void setCurrentlyPregnant(String value) {
        currentlyPregnant = value;
    }

    public void setCurrentlyPregnantDesc(String value) {
        currentlyPregnantDesc = value;
    }

    public void setAnyOther(String value) {
        anyOther = value;
    }

    public void setAnyOtherDesc(String value) {
        anyOtherDesc = value;
    }

    public void setMedicalHistory(String value) {
        medicalHistory = value;
    }


    public String getheight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public String getSmoker() {
        return smoker;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public String getPintPerWeek() {
        return pintPerWeek;
    }

    public String getDiabetes() {
        return diabetes;
    }

    public String getDiabetesType() {
        return diabetesType;
    }

    public String getHeartDisease() {
        return heartDisease;
    }

    public String getHeartDiseaseDesc() {
        return heartDiseaseDesc;
    }

    public String getChestPains() {
        return chestPains;
    }

    public String getChestPainsDesc() {
        return chestPainsDesc;
    }

    public String getShortnessOfBreath() {
        return shortnessOfBreath;
    }

    public String getShortnessOfBreathDesc() {
        return shortnessOfBreathDesc;
    }

    public String getBrokenBones() {
        return brokenBones;
    }

    public String getBrokenBonesDesc() {
        return brokenBonesDesc;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getAllergiesDesc() {
        return allergiesDesc;
    }

    public String getRecentChildbirth() {
        return recentChildbirth;
    }

    public String getRecentChildbirthDesc() {
        return recentChildbirthDesc;
    }

    public String getHeartMurmurprivate() {
        return heartMurmurprivate;
    }

    public String getHeartMurmurDesc() {
        return heartMurmurDesc;
    }

    public String getPneumonia() {
        return pneumonia;
    }

    public String getPneumoniaDesc() {
        return pneumoniaDesc;
    }

    public String getEpilepsySeizures() {
        return epilepsySeizures;
    }

    public String getEpilepsySeizuresDesc() {
        return epilepsySeizuresDesc;
    }

    public String getTachycardia() {
        return tachycardia;
    }

    public String getTachycardiaDesc() {
        return tachycardiaDesc;
    }

    public String getOedema() {
        return oedema;
    }

    public String getOedemaDesc() {
        return oedemaDesc;
    }

    public String getHeartAttack() {
        return heartAttack;
    }

    public String getHeartAttackDesc() {
        return heartAttackDesc;
    }

    public String getRecentSurgery() {
        return recentSurgery;
    }

    public String getRecentSurgeryDesc() {
        return recentSurgeryDesc;
    }

    public String getPalpitations() {
        return palpitations;
    }

    public String getPalpitationsDesc() {
        return palpitationsDesc;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public String getBloodPressureDesc() {
        return bloodPressureDesc;
    }

    public String getAsthma() {
        return asthma;
    }

    public String getAsthmaDesc() {
        return asthmaDesc;
    }

    public String getMuscleJointProblems() {
        return muscleJointProblems;
    }

    public String getMuscleJointProblemsDesc() {
        return muscleJointProblemsDesc;
    }

    public String getPhysicalDisabilities() {
        return physicalDisabilities;
    }

    public String getPhysicalDisabilitiesDesc() {
        return physicalDisabilitiesDesc;
    }

    public String getMentalDisabilities() {
        return mentalDisabilities;
    }

    public String getMentalDisabilitiesDesc() {
        return mentalDisabilitiesDesc;
    }

    public String getUlcers() {
        return ulcers;
    }

    public String getUlcersDesc() {
        return ulcersDesc;
    }

    public String getHeartRateMonitors() {
        return heartRateMonitors;
    }

    public String getHeartRateMonitorsDesc() {
        return heartRateMonitorsDesc;
    }

    public String getCurrentlyPregnant() {
        return currentlyPregnant;
    }

    public String getCurrentlyPregnantDesc() {
        return currentlyPregnantDesc;
    }

    public String getAnyOther() {
        return anyOther;
    }

    public String getAnyOtherDesc() {
        return anyOtherDesc;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }
}
