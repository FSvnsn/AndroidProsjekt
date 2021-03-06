package no.hiof.oleedvao.bardun.bruker;

public class User {
    private String name;
    private String email;
    private String description;
    private String imageId;
    private int age;
    private Boolean sendNotification;

    public User(){
    }

    public User(String name, String email, int age, String description, String imageId, Boolean sendNotification){
        this.name = name;
        this.email = email;
        this.age = age;
        this.description = description;
        this.imageId = imageId;
        this.sendNotification = sendNotification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Boolean getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }
}
