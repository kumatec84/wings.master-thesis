# Digitaler Anhang zur Master-Thesis

## Thema
**Regelbasierte Prüfung von E-Rezepten zur Vermeidung von Retaxation bei Leistungserbringern der gesetzlichen Krankenversicherung**

## Autor
**Martin Kubbillum**

## Abgabedatum
**25. Oktober 2024**

---

Die folgenden Artefakte sind auf dem beiliegenden USB-Stick abgelegt.

### Anlage 9.1: Digitale Ausfertigung der Master-Thesis
- **Dateipfad**: [/Kubbillum_Martin__Master-Thesis.pdf](./Kubbillum_Martin__Master-Thesis.pdf)

### Anlage 9.2: Installationsanleitung und Docker-Container-Definition für IBM Business Automation Manager Open Editions
Eine Multi-Container-Definition ([docker-compose.yaml](./Quelltext/IBM-BAMOE-Docker-Compose/docker-compose.yaml)) für die lokale Ausführung von IBM Business Automation Manager Open Editions (BAMOE) Version 8 sowie eine zugehörige Installationsanleitung (separate [README.md](./Quelltext/IBM-BAMOE-Docker-Compose/README.md)) werden bereitgestellt. In der Installationsanleitung wird auch kurz der Aufruf von Swagger UI beschrieben.
- **Dateipfade**:
	- [/Quelltext/IBM-BAMOE-Docker-Compose/](./Quelltext/IBM-BAMOE-Docker-Compose/)   
	- [/Quelltext/IBM-BAMOE-Docker-Compose/README.md](./Quelltext/IBM-BAMOE-Docker-Compose/README.md)  
	- [/Quelltext/IBM-BAMOE-Docker-Compose/docker-compose.yaml](./Quelltext/IBM-BAMOE-Docker-Compose/docker-compose.yaml)
- **Quelle**: In Anlehnung an Wuthenow, 13. Juli 2023, Fork des GitHub-Repositories

### Anlage 9.3: DMN-Modelle der Rezeptprüfung
Die exportierten Entscheidungsmodelle aus Business Central können importiert und wiederverwendet werden. Die jeweils zugehörigen mit Business Central erzeugten Dokumentationen im PDF-Format werden ebenfalls mitgeliefert.
- **Dateipfade**:
	- [/Quelltext/DMN-Modelle/](./Quelltext/DMN-Modelle/)  
	- [/Quelltext/DMN-Modelle/Vorlage_Rezeptpruefung.dmn](./Quelltext/DMN-Modelle/Vorlage_Rezeptpruefung.dmn)  
	- [/Quelltext/DMN-Modelle/Vorlage_Rezeptpruefung_Dokumentation.pdf](./Quelltext/DMN-Modelle/Vorlage_Rezeptpruefung_Dokumentation.pdf)  
	- [/Quelltext/DMN-Modelle/Verstoss_gegen_Substitutionsausschlussliste.dmn](./Quelltext/DMN-Modelle/Verstoss_gegen_Substitutionsausschlussliste.dmn)  
	- [/Quelltext/DMN-Modelle/Verstoss_gegen_Substitutionsausschlussliste_Dokumentation.pdf](./Quelltext/DMN-Modelle/Verstoss_gegen_Substitutionsausschlussliste_Dokumentation.pdf)

### Anlage 9.4: Testdaten
Zum Test des Decision Service wird für jeden der 4 Fälle in der Entscheidungstabelle der Decision *Verstoss_gegen_Substitutionsausschlussliste* (vgl. Kapitel 6.2.4) ein Testdatensatz als JSON-Objekt bereitgestellt. In dem JSON-Attribut *_comment* wird jeweils der Datensatz kurz beschrieben.
- **Dateipfade**:  
	- [/Quelltext/Testdaten/](./Quelltext/Testdaten/)  
	- [/Quelltext/Testdaten/eRezept_1.json](./Quelltext/Testdaten/eRezept_1.json)  
	- [/Quelltext/Testdaten/eRezept_2.json](./Quelltext/Testdaten/eRezept_2.json)  
	- [/Quelltext/Testdaten/eRezept_3.json](./Quelltext/Testdaten/eRezept_3.json)  
	- [/Quelltext/Testdaten/eRezept_4.json](./Quelltext/Testdaten/eRezept_4.json)
	
### Anlage 9.5: Literaturquellen
Web-Seiten und unveröffentlichte Dokumente, die als Literaturquelle verwendet werden, liegen der Arbeit als PDF bei.
- **Dateipfad**: [/Literaturquellen/](./Literaturquellen/)

### Anlage 9.6: Implementierungsleitfaden der Schnittstellen zwischen Apotheke und Apothekenrechenzentren für das gematik E-Rezept
- **Dateipfad**: [/Literaturquellen/VDARZ 2022 - Implementierungsleitfaden der Schnittstellen zwischen Apotheke.pdf](./Literaturquellen/VDARZ%202022%20-%20Implementierungsleitfaden%20der%20Schnittstellen%20zwischen%20Apotheke.pdf)
- **Quelle**: Bundesverband Deutscher Apothekenrechenzentren e. V., 2022

### Anlage 9.7: Substitutionsausschlussliste
Das Dokument ist der Teil B der Anlage VII zur Arzneimittel-Richtlinie und enthält die von der Ersetzung durch ein wirkstoffgleiches Arzneimittel ausgeschlossenen Arzneimittel gemäß § 129 Absatz 1a Satz 2 SGB V.
- **Dateipfad**: [/Literaturquellen/Gemeinsamer Bundesausschuss 15072024 - Teil B der Anlage VII zum Abschnitt M.pdf](./Literaturquellen/Gemeinsamer%20Bundesausschuss%2015072024%20-%20Teil%20B%20der%20Anlage%20VII%20zum%20Abschnitt%20M.pdf)
- **Quelle**: Quelle: Gemeinsamer Bundesausschuss, 2024, S. 51–52

### Anlage 9.8: DAP Arbeitsbuch - III.A.4 Substitutionsausschussliste 
- **Dateipfad**: [/Literaturquellen/Brüggen, Dunkel et al 2024 - DAP-Arbeitsbuch - Substitutionsausschussliste.pdf](./Literaturquellen/Brueggen,%20Dunkel%20et%20al%202024%20-%20DAP-Arbeitsbuch%20-%20Substitutionsausschussliste.pdf)
- **Quelle**: Brüggen et al., 2024, S. III.4.3–III.4.7