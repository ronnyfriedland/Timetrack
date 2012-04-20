package de.ronnyfriedland.time.ui;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Button;
import org.uispec4j.ListBox;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;

import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.ui.dialog.NewEntryFrame;

public class NewEntryFrameTest extends UISpecTestCase {

	private Project project;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();

		project = new Project();
		project.setDescription("testproject");
		project.setName("testproject");
		EntityController.getInstance().create(project);

		setAdapter(new MainClassAdapter(NewEntryFrame.class, new String[0]));
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		Collection<Entry> entries = EntityController.getInstance().findAll(Entry.class, false);
		for (Entry entry : entries) {
			EntityController.getInstance().deleteDetached(entry);
		}
		if (null != project) {
			EntityController.getInstance().deleteDetached(project);
		}
	}

	@Test
	public void testValidation() throws Exception {
		Window window = getMainWindow();
		TextBox dateField = window.getInputTextBox("date");
		TextBox descriptionField = window.getInputTextBox("description");
		TextBox durationField = window.getInputTextBox("duration");
		ListBox projectField = window.getListBox("projects");
		Button saveButton = window.getButton(Messages.SAVE.getMessage());

		Assert.assertNotNull(dateField);
		Assert.assertNotNull(descriptionField);
		Assert.assertNotNull(durationField);
		Assert.assertNotNull(projectField);
		Assert.assertNotNull(saveButton);

		saveButton.triggerClick().run();
		Assert.assertTrue(EntityController.getInstance().findAll(Entry.class, false).isEmpty());

		dateField.setText("01.01.2011");
		descriptionField.setText("test");
		durationField.setText("3a");
		projectField.selectIndex(0);

		saveButton.triggerClick().run();
		Assert.assertTrue(EntityController.getInstance().findAll(Entry.class, false).isEmpty());

		dateField.setText("01.01.2011");
		descriptionField.setText("test");
		durationField.setText("3");
		projectField.selectIndex(0);

		saveButton.triggerClick().run();
		Assert.assertEquals(1, EntityController.getInstance().findAll(Entry.class, false).size());

		dateField.setText("01.01.2011");
		descriptionField.setText("test");
		durationField.setText("3,123 ");
		projectField.selectIndex(0);

		saveButton.triggerClick().run();
		Assert.assertEquals(2, EntityController.getInstance().findAll(Entry.class, false).size());

	}
}
