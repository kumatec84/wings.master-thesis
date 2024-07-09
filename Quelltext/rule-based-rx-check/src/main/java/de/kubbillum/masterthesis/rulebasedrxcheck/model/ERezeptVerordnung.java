package de.kubbillum.masterthesis.rulebasedrxcheck.model;
public class ERezeptVerordnung {

    private String verordnenderArzt;
    private String patientenId;
    private String patientenName;
    private String patientenGeburtsdatum;
    private String patientenAdresse;
    private String krankenkasseId;
    private String krankenkasseName;

    // Medikamentendaten
    private String medikamentName;
    private String medikamentPzn;
    private String darreichungsform;
    private String dosierung;
    private int packungsgroesse;
    private String anwendungsdauer;
    private String hinweise;

    // Rezeptdetails
    private String rezeptTyp; // Kassenrezept, Privatrezept, etc.
    private boolean autIdem;
    private boolean retaxierungVermeiden;

    // Getter und Setter sind dank Lombok @Data Annotation vorhanden

    // Optional: Validierungsmethoden
    public boolean isValid() {
        return verordnungId != null && !verordnungId.isEmpty()
            && !verordnenderArzt.isEmpty()
            && patientenId != null && !patientenId.isEmpty()
            && medikamentName != null && !medikamentName.isEmpty()
            && medikamentPzn != null && !medikamentPzn.isEmpty();
    }

    @Override
    public String toString() {
        return "ERezeptVerordnung{" +
                "verordnungId='" + verordnungId + '\'' +
                ", patientenId='" + patientenId + '\'' +
                ", patientenName='" + patientenName + '\'' +
                ", patientenGeburtsdatum='" + patientenGeburtsdatum + '\'' +
                ", patientenAdresse='" + patientenAdresse + '\'' +
                ", krankenkasseId='" + krankenkasseId + '\'' +
                ", krankenkasseName='" + krankenkasseName + '\'' +
                ", medikamentName='" + medikamentName + '\'' +
                ", medikamentPzn='" + medikamentPzn + '\'' +
                ", darreichungsform='" + darreichungsform + '\'' +
                ", dosierung='" + dosierung + '\'' +
                ", packungsgroesse=" + packungsgroesse +
                ", anwendungsdauer='" + anwendungsdauer + '\'' +
                ", hinweise='" + hinweise + '\'' +
                ", rezeptTyp='" + rezeptTyp + '\'' +
                ", autIdem=" + autIdem +
                ", retaxierungVermeiden=" + retaxierungVermeiden +
                '}';
    }
    
 // Verordnungsdaten
    private String verordnungId;
    public String getVerordnungId() {
		return verordnungId;
	}

	public void setVerordnungId(String verordnungId) {
		this.verordnungId = verordnungId;
	}


	public String getPatientenId() {
		return patientenId;
	}

	public void setPatientenId(String patientenId) {
		this.patientenId = patientenId;
	}

	public String getPatientenName() {
		return patientenName;
	}

	public void setPatientenName(String patientenName) {
		this.patientenName = patientenName;
	}

	public String getPatientenGeburtsdatum() {
		return patientenGeburtsdatum;
	}

	public void setPatientenGeburtsdatum(String patientenGeburtsdatum) {
		this.patientenGeburtsdatum = patientenGeburtsdatum;
	}

	public String getPatientenAdresse() {
		return patientenAdresse;
	}

	public void setPatientenAdresse(String patientenAdresse) {
		this.patientenAdresse = patientenAdresse;
	}

	public String getKrankenkasseId() {
		return krankenkasseId;
	}

	public void setKrankenkasseId(String krankenkasseId) {
		this.krankenkasseId = krankenkasseId;
	}

	public String getKrankenkasseName() {
		return krankenkasseName;
	}

	public void setKrankenkasseName(String krankenkasseName) {
		this.krankenkasseName = krankenkasseName;
	}

	public String getMedikamentName() {
		return medikamentName;
	}

	public void setMedikamentName(String medikamentName) {
		this.medikamentName = medikamentName;
	}

	public String getMedikamentPzn() {
		return medikamentPzn;
	}

	public void setMedikamentPzn(String medikamentPzn) {
		this.medikamentPzn = medikamentPzn;
	}

	public String getDarreichungsform() {
		return darreichungsform;
	}

	public void setDarreichungsform(String darreichungsform) {
		this.darreichungsform = darreichungsform;
	}

	public String getDosierung() {
		return dosierung;
	}

	public void setDosierung(String dosierung) {
		this.dosierung = dosierung;
	}

	public int getPackungsgroesse() {
		return packungsgroesse;
	}

	public void setPackungsgroesse(int packungsgroesse) {
		this.packungsgroesse = packungsgroesse;
	}

	public String getAnwendungsdauer() {
		return anwendungsdauer;
	}

	public void setAnwendungsdauer(String anwendungsdauer) {
		this.anwendungsdauer = anwendungsdauer;
	}

	public String getHinweise() {
		return hinweise;
	}

	public void setHinweise(String hinweise) {
		this.hinweise = hinweise;
	}

	public String getRezeptTyp() {
		return rezeptTyp;
	}

	public void setRezeptTyp(String rezeptTyp) {
		this.rezeptTyp = rezeptTyp;
	}

	public boolean isAutIdem() {
		return autIdem;
	}

	public void setAutIdem(boolean autIdem) {
		this.autIdem = autIdem;
	}

	public boolean isRetaxierungVermeiden() {
		return retaxierungVermeiden;
	}

	public void setRetaxierungVermeiden(boolean retaxierungVermeiden) {
		this.retaxierungVermeiden = retaxierungVermeiden;
	}

}
