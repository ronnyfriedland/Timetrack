package de.ronnyfriedland.time.config;

/**
 * @author ronnyfriedland
 */
public enum Messages {
    /** Titel der Anwendung */
    TITLE("Timetable"),
    /** Startdatum */
    START_DATE("Startdatum"),
    /** Zeitspanne */
    PERIOD_OF_TIME("Zeitspanne"),
    /** Projektname */
    DATE("Datum"),
    /** Projektname */
    DURATION("Dauer (in h)"),
    /** Projektname */
    PROJECT_NAME("Projektname"),
    /** Beschreibung */
    DESCRIPTION("Beschreibung"),
    /** neues Projekt */
    NEW_PROJECT("Neues Projekt"),
    /** neuer Eintrag */
    NEW_ENTRY("Neuer Eintrag"),
    /** neuer Eintrag */
    NEW_EXPORT("Neuer Export"),
    /** heute erstelle Einträge */
    LAST_ENTRIES("Zuletzt erstellt"),
    /** Daten exportieren */
    EXPORT_DATA("Daten exportieren"),
    /** Beenden */
    EXIT("Beenden"),
    /** Neuen Eintrag erstellen */
    CREATE_NEW_ENTRY("Neuen Eintrag anlegen"),
    /** Neuen Eintrag erstellen */
    CREATE_NEW_PROJECT("Neues Projekt anlegen"),
    /** Exportieren */
    EXPORT("Exportieren"),
    /** Vorschau */
    PREVIEW("Vorschau"),
    /** Speichern */
    SAVE("Speichern"),
    /** Löschen */
    DELETE("Löschen"),
    /** Text für Kontierungserinnerung (Popup) */
    MESSAGE_POPUP("Bitte an das Kontieren denken."),
    /** Text Erfolgsmeldung Export (Popup) */
    EXPORT_SUCCESSFUL("Der Export wurde erfolgreich durchgeführt.\nOrdner: %s"),
    /** Hinweistext für Export */
    EXPORT_DESCRIPTION("Über den Kalender wird der Startzeitpunkt ausgewählt. Über den Regler wird die Zeitspanne "
            + "(in Tagen) eingestellt. Der Button " + PREVIEW.getText()
            + " aktualisiert die Vorschaudaten in der Tabelle. "
            + "Gleichzeitig werden die Daten in die Zwischenablage kopiert. Erst durch den Button " + EXPORT.getText()
            + " werden die Daten in eine Excel-Arbeitsmappe exportiert.");

    public String getText() {
        return text;
    }

    private String text;

    private Messages(String aText) {
        this.text = aText;
    }
}
