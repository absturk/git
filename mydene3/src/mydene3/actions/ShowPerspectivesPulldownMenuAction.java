package mydene3.actions;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * This action shows the alphabetically sorted pull-down menu of all registered
 * perspectives. Selecting the menu item shows and activates the perspective.
 * The menu item for the currently active perspective is shown checked and
IWorkbenchWindowPulldownDelegate * disabled.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class ShowPerspectivesPulldownMenuAction implements
	IWorkbenchWindowPulldownDelegate {

	private Menu showPerspectivesPulldownMenu;

	public Menu getMenu(Control parent) {
		if (showPerspectivesPulldownMenu == null) {
			// Build the menu
			showPerspectivesPulldownMenu = createPerspectiveMenu(parent,
					showPerspectivesPulldownMenu);
		}
		// Determine active perspective id
		IWorkbench workbench = PlatformUI.getWorkbench();
		IPerspectiveDescriptor perspective = workbench
				.getActiveWorkbenchWindow().getActivePage().getPerspective();
		String id = null;
		if (perspective != null) {
			id = perspective.getId();
		}
		MenuItem[] items = showPerspectivesPulldownMenu.getItems();
		for (MenuItem item : items) {
			if (id == null) {
				// No perspective is active
				item.setEnabled(true);
			} else {
				// Check and disable the menuItem for the active perspective
				boolean equals = item.getData().equals(id);
				item.setEnabled(!equals);
			}
		}
		return showPerspectivesPulldownMenu;
	}

	private static Menu createPerspectiveMenu(Control parent, Menu menu) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (menu == null) {
			menu = new Menu(parent);
			IPerspectiveRegistry perspectiveRegistry = PlatformUI
					.getWorkbench().getPerspectiveRegistry();
			// Get all perspectives
			IPerspectiveDescriptor[] perspectiveDescriptors = perspectiveRegistry
					.getPerspectives();

			// Sort alphabetically by label
			Arrays.sort(perspectiveDescriptors,
					new Comparator<IPerspectiveDescriptor>() {
						public int compare(IPerspectiveDescriptor pd1,
								IPerspectiveDescriptor pd2) {
							return pd1.getLabel().compareTo(pd2.getLabel());
						}
					});

			// Configure the menu items for each Perspective
			for (IPerspectiveDescriptor perspectiveDescriptor : perspectiveDescriptors) {
				final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
				menuItem.setText(perspectiveDescriptor.getLabel());
				menuItem.setImage(perspectiveDescriptor.getImageDescriptor()
						.createImage());
				menuItem.setData(perspectiveDescriptor.getId());

				// Handle selection
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						try {
							IPerspectiveDescriptor perspectiveDescriptorWithId = workbench
									.getPerspectiveRegistry()
									.findPerspectiveWithId(
											(String) e.widget.getData());
							if (perspectiveDescriptorWithId != null) {
								workbench.showPerspective(
										perspectiveDescriptorWithId.getId(),
										workbench.getActiveWorkbenchWindow());
							} else {
								// may be delete this menuItem ?
							}
						} catch (WorkbenchException we) {
						}
					}
				});
			}
		} else {
			// Delete children
		}

		return menu;
	}

	public void dispose() {
		if (showPerspectivesPulldownMenu != null) {
			showPerspectivesPulldownMenu.dispose();
		}
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}