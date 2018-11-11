package no.hiof.oleedvao.bardun;

public class Teltplass {
    private String latLng;
    private String navn;
    private String beskrivelse;
    private int underlag;
    private int utsikt;
    private int avstand;
    private Boolean skog;
    private Boolean fjell;
    private Boolean fiske;
    private String imageId;

    public Teltplass(){

    }

    public Teltplass(String latLng, String navn, String beskrivelse, String imageId){
        this.latLng = latLng;
        this.navn = navn;
        this.beskrivelse = beskrivelse;
        this.imageId = imageId;
    }

    public Teltplass(String latLng, String navn, String beskrivelse, int underlag, int utsikt, int avstand, Boolean skog, Boolean fjell, Boolean fiske, String imageId){
        this.latLng = latLng;
        this.navn = navn;
        this.beskrivelse = beskrivelse;
        this.underlag = underlag;
        this.utsikt = utsikt;
        this.avstand = avstand;
        this.skog = skog;
        this.fjell = fjell;
        this.fiske = fiske;
        this.imageId = imageId;
    }

    //Gettere og settere
    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public Integer getUnderlag() {
        return underlag;
    }

    public void setUnderlag(Integer underlag) {
        this.underlag = underlag;
    }

    public Integer getUtsikt() {
        return utsikt;
    }

    public void setUtsikt(Integer utsikt) {
        this.utsikt = utsikt;
    }

    public Integer getAvstand() {
        return avstand;
    }

    public void setAvstand(Integer avstand) {
        this.avstand = avstand;
    }

    public Boolean getSkog() {
        return skog;
    }

    public void setSkog(Boolean skog) {
        this.skog = skog;
    }

    public Boolean getFjell() {
        return fjell;
    }

    public void setFjell(Boolean fjell) {
        this.fjell = fjell;
    }

    public Boolean getFiske() {
        return fiske;
    }

    public void setFiske(Boolean fiske) {
        this.fiske = fiske;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
