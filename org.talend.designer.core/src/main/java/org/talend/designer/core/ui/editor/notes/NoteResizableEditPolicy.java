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
package org.talend.designer.core.ui.editor.notes;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.talend.designer.core.ui.editor.cmd.ResizeNoteCommand;
import org.talend.designer.core.ui.editor.process.Process;

/**
 */
public class NoteResizableEditPolicy extends ResizableEditPolicy {

    @Override
    protected Command getResizeCommand(ChangeBoundsRequest request) {
        Object parent = getHost().getParent().getModel();
        if (!(parent instanceof Process)) {
            return null;
        }

        Note note = (Note) getHost().getModel();
        if (note.isReadOnly()) {
            return null;
        }

        return new ResizeNoteCommand(note, new Dimension(note.getSize().width + request.getSizeDelta().width, note
                .getSize().height
                + request.getSizeDelta().height));
    }

}
