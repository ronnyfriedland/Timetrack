package de.ronnyfriedland.time.dataimport;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.ImportController;

/**
 * Klasse für den Import von Daten aus einer Arbeitsmappe in die Anwendung.<br/>
 * <strong>ACHTUNG: ES WERDEN ALLE BISHERIGEN DATEN GELÖSCHT !</strong>
 * 
 * @author Ronny Friedland
 */
public class DatabaseImport {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new IllegalArgumentException(String.format("Usage: java <path to import file>"));
		}
		File importFile = new File(args[0]);
		if (!importFile.exists()) {
			throw new IllegalArgumentException(String.format("File %1$s does not exist.", args[0]));
		}

		ImportController controller = new ImportController();
		Workbook wb = controller.loadWorkbook(importFile.getParentFile().getAbsolutePath(), importFile.getName());
		Collection<Entry> entries = controller.loadSheet(wb);

		EntityController.getInstance().deleteAll(Entry.class);
		EntityController.getInstance().deleteAll(Project.class);

		for (Entry entry : entries) {
			Project project = entry.getProject();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(Project.PARAM_NAME, project.getName());
			try {
				Project savedProject = EntityController.getInstance().findSingleResultByParameter(Project.class,
				        Project.QUERY_FINDBYNAME, parameters);
				project = savedProject;
				project.addEntry(entry);
			} catch (Exception e) {
				EntityController.getInstance().create(project);
			}
			entry.setProject(project);
			EntityController.getInstance().create(entry);
		}
	}
}
