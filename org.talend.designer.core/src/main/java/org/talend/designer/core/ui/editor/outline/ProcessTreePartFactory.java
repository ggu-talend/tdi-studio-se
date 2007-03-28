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
package org.talend.designer.core.ui.editor.outline;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.talend.designer.core.model.components.NodeReturn;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.process.Process;

/**
 * This class is the main factory of the Tree in the Outline View. <br/>
 * 
 * $Id$
 * 
 */
public class ProcessTreePartFactory implements EditPartFactory {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
     */
    public EditPart createEditPart(EditPart context, Object model) {
        if (model instanceof Process) {
            return new ProcessTreeEditPart(model);
        } else if (model instanceof Node) {
            return new NodeTreeEditPart(model);
        } else if (model instanceof NodeReturn) {
            return new NodeReturnsTreeEditPart(model);
        } else {
            return null;
        }
    }
}
