// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2007 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.sqlbuilder.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryService;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;
import org.talend.repository.ui.views.IRepositoryView;
import org.talend.sqlbuilder.Messages;
import org.talend.sqlbuilder.ui.SQLBuilderDialog;
import org.talend.sqlbuilder.util.ConnectionParameters;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40 +0000 (ven., 29 sept. 2006) nrousseau $
 * 
 */
public class EditQueriesAction extends AContextualAction {

    public void run() {
        IStructuredSelection selection = (IStructuredSelection) getSelection();
        RepositoryNode node = (RepositoryNode) selection.getFirstElement();

        ConnectionParameters connParameters = new ConnectionParameters();
        connParameters.setRepositoryName(node.getObject().getLabel());

        Shell parentShell = new Shell(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                IRepositoryView.VIEW_ID).getSite().getShell().getDisplay());
        SQLBuilderDialog dial = new SQLBuilderDialog(parentShell);
        connParameters.setQuery(""); //$NON-NLS-1$
        connParameters.setNodeReadOnly(false);
        dial.setConnParameters(connParameters);
        dial.open();

    }

    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = !selection.isEmpty() && selection.size() == 1;
        if (canWork) {
            Object o = selection.getFirstElement();
            RepositoryNode node = (RepositoryNode) o;
            switch (node.getType()) {
            case REPOSITORY_ELEMENT:
                if (node.getObjectType() != ERepositoryObjectType.METADATA_CONNECTIONS) {
                    canWork = false;
                } else {
                    IRepositoryService service = (IRepositoryService) GlobalServiceRegister.getDefault().getService(
                            IRepositoryService.class);
                    IProxyRepositoryFactory repFactory = service.getProxyRepositoryFactory();
                    if (repFactory.isPotentiallyEditable(node.getObject())) {
                        this.setText(Messages.getString("EditQueriesAction.textEditQueries")); //$NON-NLS-1$
                    } else {
                        this.setText(Messages.getString("EditQueriesAction.textOpenQueries")); //$NON-NLS-1$
                    }
                }
                break;
            default:
                canWork = false;
            }
        }
        setEnabled(canWork);
    }
}
