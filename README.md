**Scoping-Dokument: Universitätsmanagement-Plattform**

## **1. Einführung**
### **Projektübersicht**
Die Universitätsmanagement-Plattform ist ein webbasiertes System, das akademische Abläufe innerhalb einer Universität optimiert. Die Plattform erleichtert die Verwaltung von Zeitplänen, Prüfungen und die Verfolgung der studentischen Leistungen und ermöglicht eine nahtlose Interaktion zwischen Administratoren, Professoren und Studierenden.

### **Zweck und Ziele**
Das Ziel dieses Projekts ist die Digitalisierung und Zentralisierung der Universitätsabläufe, um die Effizienz zu steigern, manuelle Arbeit zu reduzieren und die Kommunikation zwischen den Beteiligten zu verbessern. Die Hauptziele sind:
- Implementierung eines **Zeitplansystems**, das von Universitätsadministratoren verwaltet wird und für Studierende und Professoren zugänglich ist.
- Bereitstellung eines **Notensystems**, in dem Professoren Notizen für Studierende eingeben können und Studierende ihre akademischen Fortschritte einsehen können.

### **Projektumfang**
Die Plattform wird folgende Funktionen abdecken:
- **Benutzerauthentifizierung & rollenbasierter Zugriff** (Administrator, Professoren, Studierende).
- **Zeitplansystem** zur Verwaltung von Kurs- und Veranstaltungsterminen.
- **Notensystem**, in dem Professoren Notizen eingeben und Studierende diese einsehen können.
- **Prüfungssystem** zur Organisation und Verwaltung von Prüfungen. (Überlegen ob Notensystem und Prüfungssystem ein system werden können)
- **Datenbankintegration** zur effizienten Speicherung und Abrufung von Daten.

## **2. User Journey**
### **Nutzungsprofile (Stakeholder)**
Die Plattform wird von verschiedenen Benutzergruppen genutzt, die jeweils unterschiedliche Funktionen und Verantwortlichkeiten haben. Universitätsadministratoren, die in der Regel Professoren sind, verwalten das Zeitplansystem und übernehmen die Benutzerverwaltung. Sie legen Kurse und Veranstaltungen an, planen Prüfungen und stellen sicher, dass Noten korrekt eingetragen werden. Professoren nutzen das System zur Verwaltung ihrer Kurse, zur Eingabe von Noten und zur Organisation von Prüfungen. Studierende greifen auf das System zu, um ihren Stundenplan einzusehen, ihre Noten zu überprüfen und sich für Prüfungen anzumelden.


| **Stakeholder-Typ** | **Stakeholder** | **Rolle und Verantwortlichkeiten** |
|----------------------|----------------|--------------------------------|
| **Intern** | Universitätsadministratoren (Professoren) | Verwaltung des Zeitplansystems, Benutzerverwaltung. |
| **Intern** | Professoren | Eingabe von Notizen, Zugriff auf Zeitpläne, Verwaltung von Prüfungen. |
| **Intern** | Studierende | Einsicht in Zeitpläne, Notizen und Teilnahme an Prüfungen. |

### **User Journey**

Die Benutzerreise beschreibt die verschiedenen Phasen, die ein Nutzer beim Interagieren mit der Plattform durchläuft. 

In der **Onboarding-Phase** erhalten neue Benutzer ihre Anmeldeinformationen von der Universitätsverwaltung. Nach dem ersten Login können sie ihr Passwort ändern und persönliche Einstellungen anpassen. Eine Einführungstour oder eine Dokumentation hilft ihnen, sich mit den Funktionen der Plattform vertraut zu machen.

Während der **Nutzungsphase** greifen die verschiedenen Benutzergruppen auf die Plattform zu, um ihre jeweiligen Aufgaben zu erledigen. Universitätsadministratoren verwalten das Zeitplansystem, indem sie Kurse und Veranstaltungen erstellen oder aktualisieren. Sie legen Prüfungen an und überprüfen die von den Professoren eingetragenen Noten. Zudem sind sie für die Verwaltung der Benutzerkonten verantwortlich und können neue Studierende oder Professoren anlegen sowie bestehende Konten bearbeiten.

Professoren nutzen die Plattform, um ihre Kurszeiten und Prüfungstermine einzusehen. Sie können Noten für Studierende eingeben und Prüfungsaufgaben erstellen. Zudem haben sie die Möglichkeit, ihre Vorlesungstermine bei Bedarf anzupassen und Prüfungen zu bewerten. Studierende wiederum können sich in das System einloggen, um ihren persönlichen Stundenplan einzusehen, ihre Prüfungen zu verwalten und ihre Noten abzurufen. Falls Prüfungen online stattfinden, können sie über das System daran teilnehmen und erhalten nach der Bewertung ihre Ergebnisse direkt in der Plattform.

In der **Support- und Feedback-Phase** haben alle Benutzer die Möglichkeit, technische Probleme zu melden oder Verbesserungsvorschläge einzureichen. Ein integriertes Ticketsystem hilft bei der schnellen Behebung von Problemen, und regelmäßige Umfragen zur Benutzerfreundlichkeit ermöglichen eine kontinuierliche Optimierung der Plattform. Darüber hinaus können Benutzer auf einen FAQ-Bereich zugreifen, der häufig gestellte Fragen beantwortet.
#### **1. Universitätsadministratoren (Professoren)**
- **Anmeldung** auf der Plattform.
- **Navigation zum Zeitplansystem**, um Kurspläne zu erstellen, zu aktualisieren oder zu löschen.
- **Verwaltung von Benutzern**, einschließlich Hinzufügen, Aktualisieren oder Entfernen von Professoren und Studierenden.
- **Überwachung des Notensystems**, um sicherzustellen, dass Professoren Notizen korrekt eingeben.
- **Überprüfung der Prüfungsplanung**, um sie mit dem akademischen Kalender abzustimmen.

#### **2. Professoren**
- **Anmeldung** auf der Plattform.
- **Einsicht in zugewiesene Zeitpläne**, um bevorstehende Kurse und Prüfungen zu überprüfen.
- **Eingabe von Notizen für Studierende** im Notensystem.
- **Planung und Verwaltung von Prüfungen** für Studierende.
- **Aktualisierung von Zeitplänen, falls autorisiert.**

#### **3. Studierende**
- **Anmeldung** auf der Plattform.
- **Einsicht in Kurspläne**, um ihre akademischen Aktivitäten zu planen.
- **Zugriff auf akademische Notizen**, die von Professoren eingegeben wurden.
- **Überprüfung von Prüfungsterminen und -details.**
- **Teilnahme an Online-Prüfungen, falls zutreffend.**




