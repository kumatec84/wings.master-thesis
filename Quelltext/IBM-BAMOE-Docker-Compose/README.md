# Installation und Ausführung des Prototyps für die Master-Thesis

## Thema
**Regelbasierte Prüfung von E-Rezepten zur Vermeidung von Retaxation bei Leistungserbringern der gesetzlichen Krankenversicherung**

## Autor
**Martin Kubbillum**

## Voraussetzungen
Eine lokale Installation von Docker wird vorausgesetzt.

## Lokale Installation von IBM Business Automation Manager Open Editions (BAMOE)
- **Schritt 1 von 4 - Container-Definition bereitstellen:**  
Kopieren Sie den Ordner `IBM-BAMOE-Docker-Compose` auf ein lokales Laufwerk, z.B. nach `C:\Dev\IBM-BAMOE-Docker-Compose`.  
*Hinweis: Wurde das Repository nicht über den digitalen Anhang der Master-Thesis (USB-Stick) bezogen, sondern von GitHub geklont, sollte das Verzeichnis bereits auf einem lokalen Laufwerk liegen.* 


- **Schritt 2 von 4 - In das Verzeichnis mit der Container-Definition wechseln:**

```
cd C:\Dev\IBM-BAMOE-Docker-Compose
```

- **Schritt 3 von 4 - Aufbauen der Container-Umgebung:**  
Anhand der Multi-Container-Definition `docker-compose.yaml` werden mit *Docker Compose* die Images heruntergeladen und anschließend die Container gebaut und gestartet.  
*Hinweis: Die Images haben eine Größe von mehreren Gigabyte, wodurch der Download mehrere Minuten in Anspruch nehmen kann.*
  
```
docker compose up
```

- **Die BAMOE Test-Umgebung sollte jetzt laufen und die folgenden Konsolenausgaben zu sehen sein:**

```
  IBM Business Automation Manager Open Edition 8.0.3
  PostgreSQL Database with the SQL to create it
  a fake email server - not required
  KIE Sandbox - the new method for editing DMN models and BPMN models
  KIE Extended Services to run DMN models as you make modifications in KIE Sandbox
  GitHub CORS Proxy image to be able to do local GitHub communication with GitHub through CORS
```

- **Schritt 4 von 4 - Business Central aufrufen und anmelden:**  
Die Web-Oberfläche von Business Central kann jetzt lokal aufgerufen werden.  
**Zugangslink**: [http://localhost:8080/business-central/kie-wb.jsp](http://localhost:8080/business-central/kie-wb.jsp)  
Eine Anmeldung ist mit den folgenden Zugangsdaten möglich:  
**Benutzername**: bamAdmin  
**Passwort**: ibmpam1!  

# Swagger UI zum Test des Decision Service
Die Web-Oberfläche von Swagger zum Testen der RESTful Decision Services ist nach dem Start von BAMOE ebenfalls verfügbar.  
Dort befinden sich im Abschnitt *DMN models*  die Defintion der Endpunkte zum Test der DMN Services.
- **Aufruf von Swagger UI**  
**Zugangslink**: [http://localhost:8080/kie-server/docs/](http://localhost:8080/kie-server/docs/)  
Zum Absetzen der Test-Anfragen ist eine Authentifizierung mit den Zugangsdaten von BAMOE notwendig.  
**Benutzername**: bamAdmin  
**Passwort**: ibmpam1!  
containerID: Substitutionsausschlussliste_1.0.0-SNAPSHOT  
modelID: Verstoss_gegen_Substitutionsausschlussliste