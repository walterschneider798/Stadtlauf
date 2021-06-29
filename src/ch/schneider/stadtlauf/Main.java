package ch.schneider.stadtlauf;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

//Walter Schneider

public class Main {

	static final LocalTime ELITE_START_TIME = LocalTime.of(15, 0);
	static final String ELITE_RANKING_FILENAME = "\\elite.rl.txt";
	static final LocalTime SENIOREN_START_TIME = LocalTime.of(14, 15);
	static final String SENIOREN_RANKING_FILENAME = "\\senioren.rl.txt";
	static final LocalTime JUNIOREN_START_TIME = LocalTime.of(14, 0);
	static final String JUNIOREN_RANKING_FILENAME = "\\junioren.rl.txt";
	static final String NAME_DATEINAME = "\\namen.ref.txt";
	static final String LISTEN_ORDNER_DATEINAME = "\\lists";
	static final String START_LISTEN_DATEINAME = "\\startliste.txt";
	static final String RESULTAT_LISTEN_DATEINAME = "\\messresultate.txt";
	static final String KATEGORIE_HEADER = "Kategorie";

	public static void main(String[] args) throws IOException {
		String pfad = System.getProperty("user.dir");
		String startPfad = pfad + LISTEN_ORDNER_DATEINAME + START_LISTEN_DATEINAME;
		System.out.println(startPfad);
		String endPfad = pfad + LISTEN_ORDNER_DATEINAME + RESULTAT_LISTEN_DATEINAME;
		System.out.println(endPfad);
		File anfangsDatei = new File(startPfad);
		File endDatei = new File(endPfad);
		List<Laeufer> runners = getLaeufer(endDatei);
		runners = info(anfangsDatei, runners);
		writeResultate(runners, pfad);
	}

	static List<Laeufer> getLaeufer(File endDateiListe) {
		Scanner resultatScanner = null;
		try {
			resultatScanner = new Scanner(endDateiListe);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		resultatScanner.useDelimiter("\n");
		List<Laeufer> laeufer = new ArrayList<>();
		while (resultatScanner.hasNext()) {
			String text = resultatScanner.next();
			if (text.length() >= 12) {
				String substring1 = text.substring(0, 3).replaceAll(" ", "");
				String substring2 = text.substring(4, 12);
				int startNummer = Integer.parseInt(substring1);
				LocalTime endYeit = LocalTime.parse(substring2);
				Laeufer runner = new Laeufer(startNummer, endYeit);
				laeufer.add(runner);
			}
		}
		return laeufer;
	}

	static List<Laeufer> info(File startListFile, List<Laeufer> runners) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(startListFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		scanner.useDelimiter("\n");

		while (scanner.hasNext()) {
			String reihen = scanner.next();
			if (reihen.length() >= 7) {
				String startNummerSubstring = reihen.substring(0, 3).replaceAll(" ", "");
				String katSubstring = reihen.substring(4, 5);
				String nameSubstring = reihen.substring(6, reihen.length() - 1);
				int startNummer = Integer.parseInt(startNummerSubstring);
				int katNummer = Integer.parseInt(katSubstring);
				if (runners.stream().filter(x -> x.getStartnummer() == startNummer).findFirst().isPresent()) {
					Laeufer runner = runners.stream().filter(y -> y.getStartnummer() == startNummer).findFirst().get();
					runner.setKat(katNummer);
					runner.setName(nameSubstring);
				}
			}
		}

		return runners;
	}

	static void writeResultate(List<Laeufer> laeufer, String pfad) throws IOException {
		FileWriter juniorenFileWriter = new FileWriter(pfad + JUNIOREN_RANKING_FILENAME, false);
		FileWriter seniorenFileWriter = new FileWriter(pfad + SENIOREN_RANKING_FILENAME, false);
		FileWriter eliteFileWriter = new FileWriter(pfad + ELITE_RANKING_FILENAME, false);
		FileWriter nameFileWriter = new FileWriter(pfad + NAME_DATEINAME, false);
		String rang = "Rang \t Startnummer \t Laufzeit \t Name";
		String name = "Startnummer \t Name \t Kategorie \t Rang \t Laufzeit";
		String trennlinie = "-----";
		String rankingHeader = rang + "\n" + trennlinie + "\n";
		juniorenFileWriter.write(rankingHeader);
		seniorenFileWriter.write(rankingHeader);
		String Header = name + "\n" + trennlinie + "\n";
		eliteFileWriter.write(rankingHeader);
		nameFileWriter.write(Header);
		int aktuellerJRank = 0;
		int aktuellerSRank = 0;
		int aktuellerERank = 0;
		laeufer.sort(Comparator.comparing(Laeufer::getEndzeit));
		for (Laeufer lauefer : laeufer) {
			FileWriter writerKat = null;
			LocalTime zeit = null;
			switch (lauefer.getKat()) {
			case 1:
				writerKat = juniorenFileWriter;
				lauefer.setRang_Kat(++aktuellerJRank);
				zeit = subtractLocalTime(JUNIOREN_START_TIME, lauefer.getEndzeit());
				break;
			case 2:
				writerKat = seniorenFileWriter;
				lauefer.setRang_Kat(++aktuellerSRank);
				zeit = subtractLocalTime(SENIOREN_START_TIME, lauefer.getEndzeit());
				break;
			case 3:
				writerKat = eliteFileWriter;
				zeit = subtractLocalTime(ELITE_START_TIME, lauefer.getEndzeit());
				lauefer.setRang_Kat(++aktuellerERank);
			}
			String zeitFormatiert = zeit.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
			String rangZeile = lauefer.getRang_Kat() + "\t" + lauefer.getStartnummer() + "\t" + zeitFormatiert
					+ "\t" + lauefer.getName() + "\n";
			writerKat.write(rangZeile);
		}
		juniorenFileWriter.close();
		seniorenFileWriter.close();
		eliteFileWriter.close();
		laeufer.sort(Comparator.comparing(Laeufer::getName));
		for (Laeufer lauf : laeufer) {
			LocalTime laufZeit = null;
			switch (lauf.getKat()) {
			case 1:
				laufZeit = subtractLocalTime(JUNIOREN_START_TIME, lauf.getEndzeit());
				break;
			case 2:
				laufZeit = subtractLocalTime(SENIOREN_START_TIME, lauf.getEndzeit());
			case 3:
				laufZeit = subtractLocalTime(ELITE_START_TIME, lauf.getEndzeit());
				break;
			}
			String nameZeile = lauf.getStartnummer() + "\t\t" + lauf.getName() + "\t\t" + lauf.getKat() + "\t\t"
					+ lauf.getRang_Kat() + "\t" + laufZeit + "\n";
			nameFileWriter.write(nameZeile);
		}
		nameFileWriter.close();
	}

	static LocalTime subtractLocalTime(LocalTime startZeit, LocalTime endZeit) {
		LocalTime resultat = endZeit;
		resultat = resultat.minusHours(startZeit.getHour());
		resultat = resultat.minusMinutes(startZeit.getMinute());
		resultat = resultat.minusSeconds(startZeit.getSecond());
		return resultat;
	}
}
