# Digitaler Anhang zur Master-Thesis

## Thema
**Regelbasierte Prüfung von E-Rezepten zur Vermeidung von Retaxation bei Leistungserbringern der gesetzlichen Krankenversicherung**

## Autor
**Martin Kubbillum**

## Abgabedatum
**25. Oktober 2024**

---

Die folgenden Artefakte sind auf dem beiliegenden USB-Stick abgelegt.

### Anlage 10.1: Digitale Ausfertigung der Master-Thesis
- **Dateipfad**: [/Kubbillum_Martin__Master-Thesis.pdf](./Kubbillum_Martin__Master-Thesis.pdf)

### Anlage 10.2: Verteidigung der Master-Thesis (Präsentation und Probe)
- **Dateipfade**:
	- [/Kubbillum_Martin__Master-Thesis__Verteidigung.pdf](./Kubbillum_Martin__Master-Thesis__Verteidigung.pdf)   
	- [/Kubbillum_Martin__Master-Thesis__Verteidigung__Probe.mp4](https://github.com/kumatec84/wings.master-thesis/raw/refs/heads/main/Kubbillum_Martin__Master-Thesis__Verteidigung__Probe.mp4) 

### Anlage 10.3: Installationsanleitung und Docker-Container-Definition für IBM Business Automation Manager Open Editions
Eine Multi-Container-Definition ([docker-compose.yaml](./Quelltext/IBM-BAMOE-Docker-Compose/docker-compose.yaml)) für die lokale Ausführung von IBM Business Automation Manager Open Editions (BAMOE) Version 8 sowie eine zugehörige Installationsanleitung (separate [README.md](./Quelltext/IBM-BAMOE-Docker-Compose/README.md)) werden bereitgestellt. In der Installationsanleitung wird auch kurz der Aufruf von Swagger UI beschrieben.
- **Dateipfade**:
	- [/Quelltext/IBM-BAMOE-Docker-Compose/](./Quelltext/IBM-BAMOE-Docker-Compose/)   
	- [/Quelltext/IBM-BAMOE-Docker-Compose/README.md](./Quelltext/IBM-BAMOE-Docker-Compose/README.md)  
	- [/Quelltext/IBM-BAMOE-Docker-Compose/docker-compose.yaml](./Quelltext/IBM-BAMOE-Docker-Compose/docker-compose.yaml)
- **Quelle**: In Anlehnung an Wuthenow, 13. Juli 2023, Fork des GitHub-Repositories

### Anlage 10.4: DMN-Modelle der Rezeptprüfung
Die exportierten Entscheidungsmodelle aus Business Central können importiert und wiederverwendet werden. Die jeweils zugehörigen mit Business Central erzeugten Dokumentationen im PDF-Format werden ebenfalls mitgeliefert.
- **Dateipfade**:
	- [/Quelltext/DMN-Modelle/](./Quelltext/DMN-Modelle/)  
	- [/Quelltext/DMN-Modelle/Vorlage_Rezeptpruefung.dmn](./Quelltext/DMN-Modelle/Vorlage_Rezeptpruefung.dmn)  
	- [/Quelltext/DMN-Modelle/Vorlage_Rezeptpruefung_Dokumentation.pdf](./Quelltext/DMN-Modelle/Vorlage_Rezeptpruefung_Dokumentation.pdf)  
	- [/Quelltext/DMN-Modelle/Verstoss_gegen_Substitutionsausschlussliste.dmn](./Quelltext/DMN-Modelle/Verstoss_gegen_Substitutionsausschlussliste.dmn)  
	- [/Quelltext/DMN-Modelle/Verstoss_gegen_Substitutionsausschlussliste_Dokumentation.pdf](./Quelltext/DMN-Modelle/Verstoss_gegen_Substitutionsausschlussliste_Dokumentation.pdf)

### Anlage 10.5: Testdaten
Zum Testen des Decision Service *Verstoss_gegen_Substitutionsausschlussliste* werden für alle vier Fälle (vgl.  Kapitel 6.2.4) sowohl der Testdatensatz als auch der Antwortdatensatz als JSON-Objekt bereitgestellt. Im JSON-Attribut *_comment* werden die Datensätze kurz beschrieben.
- **Dateipfade**:  
	- [/Quelltext/Testdaten/](./Quelltext/Testdaten/)  
	- **Testdatensätze:**  
	- [/Quelltext/Testdaten/Testdatensatz_1.json](./Quelltext/Testdaten/Testdatensatz_1.json)  
	- [/Quelltext/Testdaten/Testdatensatz_2.json](./Quelltext/Testdaten/Testdatensatz_2.json)  
	- [/Quelltext/Testdaten/Testdatensatz_3.json](./Quelltext/Testdaten/Testdatensatz_3.json)  
	- [/Quelltext/Testdaten/Testdatensatz_4.json](./Quelltext/Testdaten/Testdatensatz_4.json)  
	- **Antwortdatensätze:**  
	- [/Quelltext/Testdaten/Decison_Service_Response_1.json](./Quelltext/Testdaten/Decison_Service_Response_1.json)  
	- [/Quelltext/Testdaten/Decison_Service_Response_2.json](./Quelltext/Testdaten/Decison_Service_Response_2.json)  
	- [/Quelltext/Testdaten/Decison_Service_Response_3.json](./Quelltext/Testdaten/Decison_Service_Response_3.json)  
	- [/Quelltext/Testdaten/Decison_Service_Response_4.json](./Quelltext/Testdaten/Decison_Service_Response_4.json)  
		
### Anlage 10.6: Literaturquellen
Web-Seiten und unveröffentlichte Dokumente, die als Literaturquelle verwendet werden, liegen der Arbeit als PDF bei.
- **Dateipfad**: [/Literaturquellen/](./Literaturquellen/)

### Anlage 10.7: Implementierungsleitfaden der Schnittstellen zwischen Apotheke und Apothekenrechenzentren für das gematik E-Rezept
- **Dateipfad**: [/Literaturquellen/VDARZ 2022 - Implementierungsleitfaden der Schnittstellen zwischen Apotheke.pdf](./Literaturquellen/VDARZ%202022%20-%20Implementierungsleitfaden%20der%20Schnittstellen%20zwischen%20Apotheke.pdf)
- **Quelle**: Bundesverband Deutscher Apothekenrechenzentren e. V., 2022

### Anlage 10.8: DAP Arbeitsbuch - III.A.4 Substitutionsausschlussliste 
- **Dateipfad**: [/Literaturquellen/Brüggen, Dunkel et al 2024 - DAP-Arbeitsbuch - Substitutionsausschlussliste.pdf](./Literaturquellen/Brueggen,%20Dunkel%20et%20al%202024%20-%20DAP-Arbeitsbuch%20-%20Substitutionsausschlussliste.pdf)
- **Quelle**: Brüggen et al., 2024, S. III.4.3–III.4.7
