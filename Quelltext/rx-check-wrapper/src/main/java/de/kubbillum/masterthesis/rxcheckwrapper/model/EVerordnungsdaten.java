package de.kubbillum.masterthesis.rxcheckwrapper.model;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EVerordnungsdaten {

	// la_nr
	private String laNr;

	public String getLaNr() {
		if (laNr == null || laNr.length() < 9) {
			int leadingZeros = 9 - (laNr != null ? laNr.length() : 0);
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < leadingZeros; i++) {
				result.append("0");
			}
			if (laNr != null) {
				result.append(laNr);
			}
			laNr = result.toString();
		} else if (!Character.isDigit(laNr.charAt(0))) {
			laNr = "null";
		}
		return laNr;
	}

	// la_nr_v
	private String laNrV;

	public String getLaNrV() {
		if (laNrV == null || laNrV.equals("") || !Character.isDigit(laNrV.charAt(0))) {
			laNrV = "";
		}
		return laNrV;
	}

	// bs_nr
	private String bsNr;

	public String getBsNr() {
	    if (bsNr == null || bsNr.length() < 9) {
	        int leadingZeros = 9 - (bsNr != null ? bsNr.length() : 0);
	        StringBuilder result = new StringBuilder();
	        for (int i = 0; i < leadingZeros; i++) {
	            result.append("0");
	        }
	        if (bsNr != null) {
	            result.append(bsNr);
	        }
	        bsNr = result.toString();
	    } else if (bsNr.length() > 9) {
	        bsNr = null;
	    }
	    return bsNr;
	}


	// asv_nr
	private String asvNr;

	public String getAsvNr() {
		if (asvNr == null) {
			asvNr = "";
		} 
		return asvNr;
	}

	// pat_nr
	private String patNr;

	// pat_geb
	private String patGeb;

	public String getPatGeb() {
		if (patGeb == null) {
			patGeb = "000000";
		}
		return patGeb;
	}

	// pat_vorname
	private String patVorname;

	// pat_nachname
	private String patNachname;

	// rezept_id
	private String rezeptId;

	// kostentr_ik1
	private Integer kostentrIk1;

	// kostentr_bg
	private String kostentrBg;

	public String getKostentrBg() {
		if (kostentrBg == null || kostentrBg.length() < 9) {
			kostentrBg = "";
		}
		return kostentrBg;

	}

	// vers_wop
	private String versWop;

	public String getVersWop() {
		if (versWop == null) {
			return ""; // Falls die Variable null ist, gib eine leere Zeichenkette zurück
		} else if (versWop.length() < 2) {
			// Führende Null hinzufügen, falls weniger als 2 Zeichen
			return "0" + versWop;
		} else {
			return versWop; // Ansonsten die ursprüngliche Zeichenkette zurückgeben
		}
	}

	// verst_status
	private String verstStatus;

	// bes_persongr
	private String besPersongr;

	public String getBesPersongr() {
		if (besPersongr == null) {
			return ""; // Falls die Variable null ist, gib eine leere Zeichenkette zurück
		} else if (besPersongr.length() < 2) {
			// Führende Null hinzufügen, falls weniger als 2 Zeichen
			return "0" + besPersongr;
		} else {
			return besPersongr; // Ansonsten die ursprüngliche Zeichenkette zurückgeben
		}
	}

	// dmp_status
	private String dmpStatus;

	public String getDmpStatus() {
		if (dmpStatus == null) {
			return ""; // Falls die Variable null ist, gib eine leere Zeichenkette zurück
		} else if (dmpStatus.length() < 2) {
			// Führende Null hinzufügen, falls weniger als 2 Zeichen
			return "0" + dmpStatus;
		} else {
			return dmpStatus; // Ansonsten die ursprüngliche Zeichenkette zurückgeben
		}
	}

	// rechts_kn
	private String rechtsKn;

	public String getRechtsKn() {
		if (rechtsKn == null) {
			return "null"; // Falls die Variable null ist, gib eine leere Zeichenkette zurück
		} else if (rechtsKn.length() < 2) {
			// Führende Null hinzufügen, falls weniger als 2 Zeichen
			return "0" + rechtsKn;
		} else {
			return rechtsKn; // Ansonsten die ursprüngliche Zeichenkette zurückgeben
		}
	}

	private String unfall;

	public String getUnfall() {
		if (unfall == null) {
			return ""; // Falls die Variable null oder leer ist, gib eine leere Zeichenkette zurück
		}
		return unfall;
	}

	// geb_befr
	private Integer gebBefr;

	public Integer getGebBefr() {
		if (gebBefr == null) {
			gebBefr = 0;
		}
		return gebBefr;
	}

	// v_datum
	private String vDatum;

	public String getVDatum() {
		   if (vDatum == null) {
			   vDatum = "00000000";
		    } else if (vDatum.length() > 8) {
		        // Trim the string to 8 characters
		    	vDatum = vDatum.substring(0, 8);
		        // Replace the last 2 characters with '00'
		    	vDatum = vDatum.substring(0, 6) + "00";
		    }
		return vDatum;
	}

	// v_kategorie
	private String vKategorie;

	public String getVKategorie() {
		if (vKategorie == null) {
			return "";
		}
			return vKategorie;
	}

	private Integer noctu;

	public Integer getNoctu() {
		if (noctu == null) {
			noctu = 0;
		}
		return noctu;
	}

	private String spr;

	public String getSpr() {
		if (spr == null) {
			spr = "";
		}
		return spr;
	}

	private Integer impf;

	private Integer bvg;

	// mehrfach_kn
	private Integer mehrfachKn;

	public Integer getMehrfachKn() {
		if (mehrfachKn == null) {
			mehrfachKn = 0;
		}
		return mehrfachKn;
	}

	// mehrfach_z
	private String mehrfachZ;

	public String getMehrfachZ() {
		if (mehrfachZ == null) {
			return "";
		}
		return mehrfachZ;

	}

	// mehrfach_n
	private String mehrfachN;

	public String getMehrfachN() {
		if (mehrfachN == null) {
			return "";
		}
		return mehrfachN;

	}

	// mehrfach_start
	private String mehrfachStart;

	public String getMehrfachStart() {
		if (mehrfachStart == null) {
			return "";
		}
		return mehrfachStart;

	}

	// mehrfach_ende
	private String mehrfachEnde;

	public String getMehrfachEnde() {
		if (mehrfachEnde == null) {
			return "";
		}
		return mehrfachEnde;

	}

	// aut_idem
	private Integer autIdem;

	public Integer getAutIdem() {
		if (autIdem == null) {
			autIdem = 0;
		}
		return autIdem;
	}

	// v_df
	private String vDf;

	public String getVDf() {
		if (vDf == null) {
			vDf = "";
		} else if (vDf.contains(";")) {
			vDf = vDf.replaceAll(";", " ");
		}
		return vDf;
	}

	// dosierung_kn
	private String dosierungKn;
	
	public String getDosierungKn() {
		if (dosierungKn == null) {
			dosierungKn = "";
		} else {
			dosierungKn = dosierungKn.replaceAll(";", " ").replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "").replaceAll("\"", "");

		}
		return dosierungKn;

	}

	private String dosierung;

	public String getDosierung() {
		if (dosierung == null) {
			dosierung = "";
		} else {
			dosierung = dosierung.replaceAll(";", " ").replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "").replaceAll("\"", "");

		}
		return dosierung;

	}

	// v_normgroesse
	private String vNormgroesse;

	public String getVNormgroesse() {
		if (vNormgroesse == null) {
			vNormgroesse = "";
		}
		return vNormgroesse;
	}

	// v_staerke
	private String vStaerke;

	// v_einheit
	private String vEinheit;

	public String getVEinheit() {
		if (vEinheit == null) {
			vEinheit = "";
		}
		return vEinheit;
	}

	// v_anz_packungen
	private String vAnzPackungen;

	// v_pzn
	private String vPzn;

	public String getVPzn() {
		if (vPzn == null) {
			vPzn = "";
		} else if (vPzn.contains("\"")) {
			vPzn = vPzn.replaceAll("\"", "");
		}
		return vPzn;
	}

	// v_wirkstoffnr
	private String vWirkstoffnr;

	public String getVWirkstoffnr() {
		if (vWirkstoffnr == null) {
			vWirkstoffnr = "";
		}
		return vWirkstoffnr;
	}

	// v_wirkstoff
	private String vWirkstoff;

	public String getVWirkstoff() {
		if (vWirkstoff == null) {
			vWirkstoff = "";
		} else if (vWirkstoff.contains("\"")) {
			vWirkstoff = vWirkstoff.replaceAll("\"", "");
		}
		return vWirkstoff;
	}

	// v_wirkstaerke
	private String vWirkstaerke;

	public String getVWirkstaerke() {
		if (vWirkstaerke == null) {
			vWirkstaerke = "";
		}
		return vWirkstaerke;
	}

	// v_wirkstaerke_einheit
	private String vWirkstaerkeEinheit;

	public String getVWirkstaerkeEinheit() {
		if (vWirkstaerkeEinheit == null) {
			vWirkstaerkeEinheit = "";
		}
		return vWirkstaerkeEinheit;
	}

	// v_rezeptur
	private String vRezeptur;

	public String getVRezeptur() {
		if (vRezeptur == null) {
			vRezeptur = "";
		}
		 else {
			vRezeptur = vRezeptur.replaceAll(";", " ").replaceAll("\"", "").replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");
		}
		return vRezeptur;
	}

	// v_rezeptur_menge
	private String vRezepturMenge;

	public String getVRezepturMenge() {
		if (vRezepturMenge == null) {
			vRezepturMenge = "";
		}
		return vRezepturMenge;
	}

	// v_rezeptur_einheit
	private String vRezepturEinheit;

	public String getVRezepturEinheit() {
		if (vRezepturEinheit == null) {
			vRezepturEinheit = "";
		}
		return vRezepturEinheit;
	}

	// Krankenkasse/Institut
	private String displayPayor;

	private String codeArzt;
	
}
