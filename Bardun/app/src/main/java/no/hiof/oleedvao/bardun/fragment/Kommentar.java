package no.hiof.oleedvao.bardun.fragment;

public class Kommentar {
    private String brukerNavn;
    private String kommentar;

    public Kommentar(){
    }

    public Kommentar(String brukerNavn, String kommentar){
        this.brukerNavn = brukerNavn;
        this.kommentar = kommentar;
    }

    public String getBrukerNavn() {
        return brukerNavn;
    }

    public void setBrukerNavn(String brukerNavn) {
        this.brukerNavn = brukerNavn;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }
}
