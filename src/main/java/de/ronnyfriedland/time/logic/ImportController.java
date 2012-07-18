package de.ronnyfriedland.time.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.EntryState;
import de.ronnyfriedland.time.entity.EntryState.State;
import de.ronnyfriedland.time.entity.Project;

/**
 * Controller für den Datenimport aus einer XSLS-Arbeitsmappe.
 * 
 * @author Ronny Friedland
 */
public final class ImportController {

    public static final String TAB_OVERVIEW = "Überblick";

    public Workbook loadWorkbook(final String path, final String file) throws IOException {
        if (!new File(path).exists()) {
            throw new FileNotFoundException(String.format("Path %s not found.", path));
        }
        if (!new File(path, file).exists()) {
            throw new FileNotFoundException(String.format("Improt file %s not found.", file));
        }
        return new XSSFWorkbook(new FileInputStream(new File(path, file)));
    }

    public Collection<Entry> loadSheet(final Workbook wb) throws IOException {
        Collection<Entry> entries = new ArrayList<Entry>();

        int numberOfSheets = wb.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = wb.getSheetAt(i);
            if (!TAB_OVERVIEW.equalsIgnoreCase(sheet.getSheetName())) { // skip
                                                                        // overview
                                                                        // tab
                Iterator<Row> rowIter = sheet.iterator();
                while (rowIter.hasNext()) {
                    Row row = rowIter.next();
                    if (row.getRowNum() > 0) { // skip first row
                        Entry e = new Entry();
                        e.setDateString(row.getCell(0).getStringCellValue());
                        e.setDescription(row.getCell(2).getStringCellValue());
                        e.setDuration(row.getCell(3).getStringCellValue());
                        e.setState(new EntryState(Calendar.getInstance().getTime(), State.FIXED));
                        Project p = new Project();
                        p.setEnabled(true);
                        p.setName(row.getCell(1).getStringCellValue());
                        p.addEntry(e);
                        e.setProject(p);
                        entries.add(e);
                    }
                }
            }
        }
        return entries;
    }

}
