package no.hiof.oleedvao.bardun.fragment;

public class Kommentar {
    private String brukernavn;
    private String kommentar;

    public Kommentar(){
    }

    public Kommentar(String brukernavn, String kommentar){
        this.brukernavn = brukernavn;
        this.kommentar = kommentar;
    }

    public String getBrukerNavn() {
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
}
