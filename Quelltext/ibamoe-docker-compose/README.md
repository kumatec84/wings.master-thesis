# Installation und Ausführung des Prototyps für die Master-Thesis
Diese Anleitung geht davon aus, dass das Repository auf dem UBS-Stick mit dem Digitalen Anhang der Master-Thesis zur Verfügung gestellt wird.

## Voraussetzungen
Eine lokale Installation von Docker wird vorausgesetzt.

### Lokale Installation von IBM Business Automation Manager Open Editions (BAMOE)
- **Schritt 1 - Container-Definition bereitstellen:**  
Kopieren Sie den Ordner `bamoe-docker-compose` auf ein lokales Laufwerk, z.B. nach `C:\dev\ibamoe-docker-compose`.  
*Hinweis: Wurde das Repository von GitHub geclont, sollte das Verzeichnis bereits auf einem lokalen Laufwerk liegen.* 


- **Schritt 2 - In das Verzeichnis mit der Container-Definition wechseln:**

```
cd C:\dev\ibamoe-docker-compose
```

- **Schritt 3 - Aufbauen der Container-Umgebung:**  
Anhand der Multi-Container-Definition `docker-compose.yaml` werden mit *Docker Compose* werden Images heruntergeladen, anschließend die Container gebaut und gestartet.  
*Hinweis: Die Images haben eine Größe von mehreren Gigabyte, wodurch der Download mehrere Minuten in Anspruch nehmen kann.*
  
```
docker compose up
```

- **Die BAMOE Test-Umgebung sollte jetzt laufen und die folgende Konsolenausgabe zu sehen sein:**

```
  IBM Business Automation Manager Open Edition 8.0.3
  PostgreSQL Database with the SQL to create it
  a fake email server - not required
  KIE Sandbox - the new method for editing DMN models and BPMN models
  KIE Extended Services to run DMN models as you make modifications in KIE Sandbox
  GitHub CORS Proxy image to be able to do local GitHub communication with GitHub through CORS
```

- **Schritt 4 - Business Central aufrufen**  
Die Web-Oberfläche Business Central kann jetzt lokal aufgerufen werden.  
**Zugangslink**: [http://localhost:8080/business-central/kie-wb.jsp](http://localhost:8080/business-central/kie-wb.jsp )    
**Benutzername**: bamAdmin  
**Passwort**: ibmpam1!  