package de.ronnyfriedland.time.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.ronnyfriedland.time.entity.Entry;

/**
 * Controller für den Datenexport in eine XSLS-Arbeitsmappe.
 *
 * @author Ronny Friedland
 */
public final class ExportController {

    /** Name of the first tab */
    public static final String TAB_OVERVIEW = "Überblick";

    /**
     * Lädt oder erzeugt eine neue Arbeitsmappe
     *
     * @param path der Pfad zur Datei
     * @param file Dateiname
     * @return {@link Workbook} Instanz
     * @throws IOException Fehler beim Dateisystemzugriff
     */
    public Workbook loadOrCreateWorkbook(final String path, final String file) throws IOException {
        Workbook wb;
        if (!new File(path).exists()) {
            new File(path).mkdir();
        }
        if (!new File(path, file).exists()) {
            wb = new XSSFWorkbook();
        } else {
            wb = new XSSFWorkbook(new FileInputStream(new File(path, file)));
        }
        return wb;
    }

    /**
     * Speichern der Daten in einem Arbeitsblatt
     *
     * @param wb {@link Workbook} Instanz
     * @param sheetName Name des Arbeitsblatts
     * @param entries die Daten
     * @return das erzeugte Arbeitsblatt
     * @throws IOException Fehler beim Dateisystemzugriff
     */
    public Sheet loadOrCreateSheet(final Workbook wb, final String sheetName, final Collection<Entry> entries)
            throws IOException {
        Sheet sheet = wb.getSheet(sheetName);
        if (null == sheet) {
            sheet = wb.createSheet(sheetName);
        }

        // Clear
        int i = 0;
        do {
            Row currentRow = sheet.getRow(i);
            if (null == currentRow) {
                break;
            } else {
                sheet.removeRow(currentRow);
            }
            i++;
        } while (true);
        Iterator<Row> rowIter = sheet.rowIterator();
        while (rowIter.hasNext()) {
            Row row = rowIter.next();
            sheet.removeRow(row);
        }

        Row headerRow = sheet.createRow(0);
        formatHeaderCell(wb, headerRow.createCell(0), "Datum");
        formatHeaderCell(wb, headerRow.createCell(1), "Projekt");
        formatHeaderCell(wb, headerRow.createCell(2), "Beschreibung");
        formatHeaderCell(wb, headerRow.createCell(3), "Dauer (in h)");

        i = 1;
        for (Entry entry : entries) {
            Row row = sheet.createRow(i);

            row.createCell(0).setCellValue(entry.getDateString());
            row.createCell(1).setCellValue(entry.getProject().getName());
            row.createCell(2).setCellValue(entry.getDescription());
            row.createCell(3).setCellValue(entry.getDuration());

            i++;
        }
        return sheet;
    }

    /**
     * Fügt ein Arbeitsblatt zur Übersichtsseite hinzu
     *
     * @param wb {@link Workbook} Instanz
     * @param sheetName Name des Arbeitsblatts
     * @throws IOException Fehler beim Dateisystemzugriff
     */
    public void addSheetToOverview(final Workbook wb, final String sheetName) throws IOException {
        Sheet overview = wb.getSheet(TAB_OVERVIEW);
        if (null == overview) {
            overview = wb.createSheet(TAB_OVERVIEW);
            wb.setSheetOrder(TAB_OVERVIEW, 0);

            Row headerRow = overview.createRow(0);
            formatHeaderCell(wb, headerRow.createCell(0), "Tabname");
            formatHeaderCell(wb, headerRow.createCell(1), "Zeitpunkt");
        }
        int lastRow = 1;
        do {
            Row currentRow = overview.getRow(lastRow);
            if (null == currentRow) {
                break;
            }
            lastRow++;
        } while (true);

        Row row = overview.createRow(lastRow);

        Hyperlink link = wb.getCreationHelper().createHyperlink(Hyperlink.LINK_DOCUMENT);
        link.setAddress(String.format("'%1$s'!A1", sheetName));
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(sheetName);
        cell0.setHyperlink(link);

        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy hh:mm:ss"));
        Cell cell1 = row.createCell(1);
        cell1.setCellValue(Calendar.getInstance().getTime());
        cell1.setCellStyle(cellStyle);
    }

    /**
     * Abspeichern einer Arbeitsmappe
     *
     * @param wb {@link Workbook} Instanz
     * @param path Pfad zur Datei
     * @param file Dateiname
     * @throws IOException Fehler beim Dateisystemzugriff
     */
    public void persistWorkbook(final Workbook wb, final String path, final String file) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(new File(path, file));
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }

    private void formatHeaderCell(final Workbook wb, final Cell cell, final String cellValue) {
        CellStyle style = wb.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cell.setCellValue(cellValue);
        cell.setCellStyle(style);
    }
}
