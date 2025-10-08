package com.example.myapplication.main_func.caregiver;

public class FamilyMember {
    private String lansiaId;
    private String name;
    private String age;
    private String gender;
    private String religion;
    private String relation;
    private String complaint;

    public FamilyMember(String lansiaId, String name, String age) {
        this.lansiaId = lansiaId;
        this.name = name;
        this.age = age;
    }

    // Getters and Setters
    public String getLansiaId() { return lansiaId; }
    public String getName() { return name; }
    public String getAge() { return age; }
    public String getGender() { return gender; }
    public String getReligion() { return religion; }
    public String getRelation() { return relation; }
    public String getComplaint() { return complaint; }

    public void setGender(String gender) { this.gender = gender; }
    public void setReligion(String religion) { this.religion = religion; }
    public void setRelation(String relation) { this.relation = relation; }
    public void setComplaint(String complaint) { this.complaint = complaint; }
}
