package de.ronnyfriedland.time.ui;

import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Button;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;

import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.ui.dialog.NewProjectFrame;

public class NewProjectFrameTest extends UISpecTestCase {

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		setAdapter(new MainClassAdapter(NewProjectFrame.class, new String[0]));
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		Collection<Project> projects = EntityController.getInstance().findAll(Project.class, false);
		for (Project project : projects) {
			EntityController.getInstance().deleteDetached(project);
		}
	}

	@Test
	public void testValidation() throws Exception {
		Window window = getMainWindow();
		TextBox nameField = window.getInputTextBox("name");
		TextBox descriptionField = window.getInputTextBox("description");
		Button saveButton = window.getButton(Messages.SAVE.getMessage());

		Assert.assertNotNull(descriptionField);
		Assert.assertNotNull(saveButton);

		saveButton.triggerClick().run();
		Assert.assertTrue(EntityController.getInstance().findAll(Project.class, false).isEmpty());

		nameField.setText("testproject");
		descriptionField.setText("test");

		saveButton.triggerClick().run();
		Assert.assertFalse(EntityController.getInstance().findAll(Project.class, false).isEmpty());
	}
}
