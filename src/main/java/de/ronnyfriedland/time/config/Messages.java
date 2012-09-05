package de.ronnyfriedland.time.config;

/**
 * Textbausteine für die Anwendung.
 * 
 * @author Ronny Friedland
 */
public enum Messages {
    /** Titel der Anwendung */
    TITLE("title"),
    /** Startdatum */
    START_DATE("startdate"),
    /** Zeitspanne */
    PERIOD_OF_TIME("periodoftime"),
    /** Datum */
    DATE("date"),
    /** Projektname */
    DURATION("duration"),
    /** Projektname */
    PROJECT_NAME("projectname"),
    /** Beschreibung */
    DESCRIPTION("description"),
    /** neues Projekt */
    NEW_PROJECT("newproject"),
    /** neuer Eintrag */
    NEW_ENTRY("newentry"),
    /** neuer Eintrag */
    NEW_EXPORT("newexport"),
    /** heute erstelle Einträge */
    LAST_ENTRIES("lastentries"),
    /** Daten importieren */
    IMPORT_DATA("importdata"),
    /** Daten exportieren */
    EXPORT_DATA("exportdata"),
    /** Beenden */
    EXIT("exit"),
    /** Neuen Eintrag erstellen */
    CREATE_NEW_ENTRY("createnewentry"),
    /** Neuen Eintrag erstellen */
    CREATE_NEW_PROJECT("createnewproject"),
    /** Exportieren */
    EXPORT("export"),
    /** Projektliste aktualisieren */
    REFRESH_PROJECT("refreshprojects"),
    /** Alle Projekte anzeigen (auch deaktivierte) */
    SHOW_DISABLED_PROJECT("showdisabledprojects"),
    /** Vorschau */
    PREVIEW("preview"),
    /** Speichern */
    SAVE("save"),
    /** Löschen */
    DELETE("delete"),
    /** Löschen */
    DISABLE("disable"),
    /** Ja */
    YES("yes"),
    /** Nein */
    NO("no"),
    /** Text für Kontierungserinnerung (Popup) */
    MESSAGE_POPUP("messagepopup"),
    /** Abfrage, ob Import wirklich durchgeführt werden soll (Popup) */
    IMPORT_CONFIRM("importconfirm"),
    /** Text Erfolgsmeldung Import (Popup) */
    IMPORT_SUCCESSFUL("importsuccessful"),
    /** Text Erfolgsmeldung Export (Popup) */
    EXPORT_SUCCESSFUL("exportsuccessful"),
    /** Löschen nach Export ? */
    EXPORT_DELETE("exportdelete"),
    /** Hinweistext für Export */
    EXPORT_DESCRIPTION("exportdescription", PREVIEW.getMessage(), EXPORT.getMessage()), HELP("help");

    private String message;
    private String[] properties;

    private Messages(final String aMessage, final String... aProperties) {
        this.message = aMessage;
        this.properties = aProperties;
    }

    /**
     * Liefert die Fehlermeldung.
     * 
     * @param customProperties
     *            replace tokens with given properties
     * @return Fehlermeldung with replaced tokens
     */
    public String getMessage(final String... customProperties) {
        return MessageProperties.getString(message, 0 == customProperties.length ? properties : customProperties);
    }
}
