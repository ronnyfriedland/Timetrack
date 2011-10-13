package de.ronnyfriedland.time.config;

/**
 * @author ronnyfriedland
 */
public enum Messages {
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
    TODAY_ENTRIES("Heute erstellt"),
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
    /** Speichern */
    SAVE("Speichern");

    public String getText() {
        return text;
    }

    private String text;

    private Messages(String aText) {
        this.text = aText;
    }
}
