package no.hiof.oleedvao.bardun.teltplass;

public class Kommentar {
    private String date;
    private String brukernavn;
    private String kommentar;

    public Kommentar(){
    }

    public Kommentar(String date ,String brukernavn, String kommentar){
        this.date = date;
        this.brukernavn = brukernavn;
        this.kommentar = kommentar;
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    public void setBrukernavn(String brukernavn) {
        this.brukernavn = brukernavn;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
