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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * This action shows the alphabetically sorted pull-down menu of all registered
 * views. Selecting the menu item shows and activates the view. The menu item
 * for the currently active view is shown checked and disabled.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class ShowViewsPulldownMenuAction implements
		IWorkbenchWindowPulldownDelegate {

	private Menu showViewsPulldownMenu;

	public Menu getMenu(Control parent) {
		if (showViewsPulldownMenu == null) {
			// Build the menu
			showViewsPulldownMenu = createViewsMenu(parent,
					showViewsPulldownMenu);
		}
		// Determine the active view and use it to enable and check
		// the menu items
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPartReference activePartReference = workbench
				.getActiveWorkbenchWindow().getActivePage()
				.getActivePartReference();
		String id = null;
		if (activePartReference instanceof IViewReference) {
			id = activePartReference.getId();
		}
		MenuItem[] items = showViewsPulldownMenu.getItems();
		for (MenuItem item : items) {
			if (id == null) {
				// No view is active
				item.setEnabled(true);
			} else {
				// Check and disable the menuItem for the active view
				boolean equals = id.equals(item.getData());
				item.setEnabled(!equals);
			}
		}
		return showViewsPulldownMenu;
	}

	private static Menu createViewsMenu(Control parent, Menu menu) {
		if (menu == null) {
			menu = new Menu(parent);
			IViewRegistry viewsRegistry = PlatformUI.getWorkbench()
					.getViewRegistry();
			// Get all views
			IViewDescriptor[] viewDescriptors = viewsRegistry.getViews();

			// Sort alphabetically by label
			Arrays.sort(viewDescriptors, new Comparator<IViewDescriptor>() {
				public int compare(IViewDescriptor vd1, IViewDescriptor vd2) {
					return vd1.getLabel().compareTo(vd2.getLabel());
				}
			});

			// Configure the menu items for each View
			for (IViewDescriptor viewDescriptor : viewDescriptors) {
				MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
				menuItem.setText(viewDescriptor.getLabel());
				menuItem.setImage(viewDescriptor.getImageDescriptor()
						.createImage());
				menuItem.setData(viewDescriptor.getId());
				// Handle selection
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						IWorkbench workbench = PlatformUI.getWorkbench();
						try {
							IViewDescriptor viewWithId = workbench
									.getViewRegistry().find(
											(String) e.widget.getData());
							if (viewWithId != null) {
								IWorkbenchPage activePage = workbench
										.getActiveWorkbenchWindow()
										.getActivePage();
								IViewPart view = activePage.showView(viewWithId
										.getId(), null,
										IWorkbenchPage.VIEW_CREATE);
								activePage.activate(view);
							} else {
								// may be delete this menuItem ?
							}
						} catch (PartInitException pie) {
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
		if (showViewsPulldownMenu != null) {
			showViewsPulldownMenu.dispose();
		}
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
