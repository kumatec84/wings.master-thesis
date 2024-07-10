package de.kubbillum.masterthesis.rxcheckwrapper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class EAbgabedaten {

	// a_datum
	private String aDatum;

	public String getADatum() {

		if (aDatum == null) {
			aDatum = "00000000";
		} else if (aDatum.length() > 8) {
			// Trim the string to 8 characters
			aDatum = aDatum.substring(0, 8);
			// Replace the last 2 characters with '00'
			aDatum = aDatum.substring(0, 6) + "00";
		}

		// Get the last two characters
		String lastTwoChars = aDatum.substring(6);

		// Check if the last two characters are strictly greater than 31
		if (Integer.parseInt(lastTwoChars) > 31) {
			// Replace the last 2 characters with '00'
			aDatum = aDatum.substring(0, 6) + "00";
		}

		return aDatum;
	}

	// h_datum
	private String hDatum;

	public String getHDatum() {
		if (hDatum == null) {
			hDatum = "00000000";
		}

		return hDatum;
	}

	// h_zaehler
	private String hZaehler;

	public String getHZaehler() {
		if (hZaehler == null) {
			hZaehler = "";
		}

		return hZaehler;
	}

	// zaehler_einheit
	private String zaehlerEinheit;

	public String getZaehlerEinheit() {
		if (zaehlerEinheit == null) {
			zaehlerEinheit = "";
		}

		return zaehlerEinheit;
	}

	// zaehler_abr_pos
	private String zaehlerAbrPos;

	public List<String> zaehlerAbrPosList = Collections.emptyList();


	public String getZaehlerAbrPos() {
		if (zaehlerAbrPos == null) {
			zaehlerAbrPos = "";
		}

		return zaehlerAbrPos;
	}

	// g_zuz_vo
	private Integer gZuzVo;

	public Integer getGZuzVo() {
		if (gZuzVo == null) {
			gZuzVo = 0;
		}
		return gZuzVo;
	}

	// g_avp_vo
	private Integer gAvpVo;

	public Integer getGAvpVo() {
		if (gAvpVo == null) {
			gAvpVo = 0;
		}
		return gAvpVo;
	}

	// vertrag_kn
	private String vertragKn;

	public String getVertragKn() {
		if (vertragKn == null) {
			vertragKn = "";
		}
		return vertragKn;
	}

	// rz_aend
	private String rzAend;

	public String getRzAend() {
		if (rzAend == null) {
			rzAend = "";
		}
		return rzAend;
	}

	// zaehler_abr_zeile
	private String zaehlerAbrZeile;

	public String getZaehlerAbrZeile() {
		if (zaehlerAbrZeile == null) {
			zaehlerAbrZeile = "";
		}
		return zaehlerAbrZeile;
	}

	private String pzn;

	public String getPzn() {
		if (pzn == null) {
			pzn = "";
		}
		return pzn;
	}

	// r_pzn
	private String rPzn;

	public List<String> rPznList = Collections.emptyList();

	public String getRPzn() {
		if (rPzn == null) {
			rPzn = "";
		}
		return rPzn;
	}

	// chargen_bez
	private String chargenBez;

	public String getChargenBez() {
		if (chargenBez == null) {
			chargenBez = "";
		}
		return chargenBez;
	}

	private String menge;

	public String getMenge() {
		if (menge == null) {
			menge = "";
		}
		return menge;
	}

	// menge_kn
	private String mengeKn;

	public String getMengeKn() {
		if (mengeKn == null) {
			mengeKn = "";
		}
		return mengeKn;
	}

	// r_menge
	public List<String> rMengeList = Collections.emptyList();

	private String rMenge;

	public String getRMenge() {
		if (rMenge == null) {
			rMenge = "";
		} else if (rMenge.contains(".")) {
			rMenge = rMenge.replace(".", ",");
		}
		return rMenge;
	}

	public void setRMenge(String rMenge2) {
		this.rMenge = rMenge2;
	}

	// vk_kn
	public List<String> vkKnList = Collections.emptyList();
	;

	private String vkKn;

	public String getVkKn() {
		if (vkKn == null) {
			vkKn = "";
		}
		return vkKn;
	}

	// r_vk
	public List<String> rVkList = Collections.emptyList();
	private String rVk;

	public String getRVK() {
		if (rVk == null) {
			rVk = "";
		}
		return rVk;
	}

	// apo_vo
	private Integer apoVo;

	public Integer getApoVo() {
		if (apoVo == null) {
			apoVo = 0;
		}
		return apoVo;
	}

	// k_vers_zuz
	private Integer kVersZuz;

	public Integer getKVersZuz() {
		if (kVersZuz == null) {
			kVersZuz = 0;
		}
		return kVersZuz;
	}

	// k_vers_mehrk
	private Integer kVersMehrk;

	public Integer getKVersMehrk() {
		if (kVersMehrk == null) {
			kVersMehrk = 0;
		}
		return kVersMehrk;
	}

	// k_vers_eigenb
	private Integer kVersEigenb;

	public Integer getKVersEigenb() {
		if (kVersEigenb == null) {
			kVersEigenb = 0;
		}
		return kVersEigenb;
	}

	// s_markt
	private Integer sMarkt;

	public Integer getSMarkt() {
		if (sMarkt == null) {
			sMarkt = 0;
		}
		return sMarkt;
	}

	// s_rabatt
	private Integer sRabatt;

	public Integer getSRabatt() {
		if (sRabatt == null) {
			sRabatt = 0;
		}
		return sRabatt;
	}

	// s_preisg
	private Integer sPreisg;

	public Integer getSPreisg() {
		if (sPreisg == null) {
			sPreisg = 0;
		}
		return sPreisg;
	}

	// s_import_fam
	private String sImportFam;

	public String getSImportFam() {
		if (sImportFam == null) {
			sImportFam = "0";
		}
		return sImportFam;
	}

	// s_mehrkosten_uebern
	private String sMehrkostenUebern;

	public String getSMehrkostenUebern() {
		if (sMehrkostenUebern == null) {
			sMehrkostenUebern = ""; // Falls mehrfachZ null ist, gib eine leere Zeichenkette zurück
		}
		return sMehrkostenUebern; // Andernfalls gib eine leere Zeichenkette zurück
	}

	// s_wunsch_am
	private String sWunschAm;

	public String getSWunschAm() {
		if (sWunschAm == null) {
			sWunschAm = ""; // Falls mehrfachZ null ist, gib eine leere Zeichenkette zurück
		}
		return sWunschAm; // Andernfalls gib eine leere Zeichenkette zurück
	}

	// s_wirkstoffvo
	private String sWirkstoffvo;

	public String getSWirkstoffvo() {
		if (sWirkstoffvo == null) {
			sWirkstoffvo = ""; // Falls mehrfachZ null ist, gib eine leere Zeichenkette zurück
		}
		return sWirkstoffvo; // Andernfalls gib eine leere Zeichenkette zurück
	}

	// s_ersatzvo
	private String sErsatzvo;

	public String getSErsatzvo() {
		if (sErsatzvo == null) {
			sErsatzvo = ""; // Falls mehrfachZ null ist, gib eine leere Zeichenkette zurück
		}
		return sErsatzvo; // Andernfalls gib eine leere Zeichenkette zurück
	}

	// s_kuenstl_bef
	private String sKuenstlBef;

	public String getSKuenstlBef() {
		if (sKuenstlBef == null) {
			sKuenstlBef = ""; // Falls mehrfachZ null ist, gib eine leere Zeichenkette zurück
		}
		return sKuenstlBef; // Andernfalls gib eine leere Zeichenkette zurück
	}

	// s_e_imp_fam
	private String sEImpFam;

	public String getSEImpFam() {
		if (sEImpFam == null) {
			sEImpFam = ""; // Falls mehrfachZ null ist, gib eine leere Zeichenkette zurück
		}
		return sEImpFam; // Andernfalls gib eine leere Zeichenkette zurück

	}

	// s_notdienst
	private String sNotdienst;

	public String getSNotdienst() {
		if (sNotdienst == null) {
			sNotdienst = ""; // Falls mehrfachZ null ist, gib eine leere Zeichenkette zurück
		}
		return sNotdienst; // Andernfalls gib eine leere Zeichenkette zurück

	}

	// s_zuzahlungsstatus
	private String sZuzahlungsStatus;

	public String getSZuzahlungsStatus() {
		if (sZuzahlungsStatus == null) {
			sZuzahlungsStatus = ""; // Falls mehrfachZ null ist, gib eine leere Zeichenkette zurück
		}
		return sZuzahlungsStatus; // Andernfalls gib eine leere Zeichenkette zurück
	}

	// http://fhir.abda.de/eRezeptAbgabedaten/StructureDefinition/DAV-EX-ERP-ZusatzdatenHerstellung
	public String medDispenseZDUrl;

	public String getMedDispenseZDUrl() {
		if (medDispenseZDUrl == null) {
			medDispenseZDUrl = ""; // Falls mehrfachZ null ist, gib eine leere Zeichenkette zurück
		}
		return medDispenseZDUrl; // Andernfalls gib eine leere Zeichenkette zurück
	}

	// ________________Helper

	public static String genInt(Integer input) {
		if (input == null) {
			return String.format("%08d", 0);
		}

		return String.format("%08d", input);
	}
}
