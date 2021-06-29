package ch.schneider.stadtlauf;

import java.time.LocalTime;

public class Laeufer {
    private String name;
    private int startNummer;
    private int kat;
    private int rang_kat;
    private LocalTime endZeit;
    public Laeufer(int startNummer, LocalTime endZeit) {
        this.startNummer = startNummer;
        this.endZeit = endZeit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartnummer() {
        return startNummer;
    }

    public void setStartNumber(int startNummer) {
        this.startNummer = startNummer;
    }

    public int getKat() {
        return kat;
    }

    public void setKat(int kat) {
        this.kat = kat;
    }

    public int getRang_Kat() {
        return rang_kat;
    }

    public void setRang_Kat(int rang_kat) {
        this.rang_kat = rang_kat;
    }

    public LocalTime getEndzeit() {
        return endZeit;
    }

    public void setEndZeit(LocalTime endZeit) {
        this.endZeit = endZeit;
    }
}
