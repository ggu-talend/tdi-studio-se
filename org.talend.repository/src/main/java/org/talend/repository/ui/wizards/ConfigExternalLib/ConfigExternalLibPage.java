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
package org.talend.repository.ui.wizards.ConfigExternalLib;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.RoutineItem;
import org.talend.repository.model.RepositoryNode;

/**
 * Page of the Job Scripts Export Wizard. <br/>
 * 
 * @referto WizardArchiveFileResourceExportPage1 $Id: JobScriptsExportWizardPage.java 1 2006-12-13 下午03:09:07 bqian
 * 
 */
public abstract class ConfigExternalLibPage extends WizardPage {

    private IStructuredSelection selection;

    /**
     * Getter for selection.
     * 
     * @return the selection
     */
    public IStructuredSelection getSelection() {
        return this.selection;
    }

    /**
     * An abstract class for import external Lib.
     * 
     * @param pageName
     * @param title
     * @param titleImage
     */
    public ConfigExternalLibPage(String name, IStructuredSelection selection) {
        super(name);
        this.selection = selection;
    }

    /**
     * Subclasses should implement this for its own business.
     * 
     * @return
     */
    public boolean finish() {
        return true;
    }

    public RepositoryNode getSelectedRepositoryNode() {
        return (RepositoryNode) this.getSelection().getFirstElement();
    }

    public RoutineItem getSelectedRoutine() {
        RepositoryNode node = getSelectedRepositoryNode();
        Item item = node.getObject().getProperty().getItem();
        return (RoutineItem) item;
    }

}
