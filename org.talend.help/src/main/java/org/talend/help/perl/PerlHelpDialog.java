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
package org.talend.help.perl;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.help.i18n.Messages;
import org.talend.help.perl.ui.PerlHelpComposite;

/**
 * PerlHelpDialog.java.
 * 
 */
public class PerlHelpDialog extends TitleAreaDialog {

    private static Point size;

    private static Point location;

    public PerlHelpDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.DIALOG_TRIM | getDefaultOrientation() | SWT.RESIZE);
    }

    protected Point getInitialSize() {
        if (size != null) {
            return size;
        }
        return super.getInitialSize();
    }

    protected Point getInitialLocation(Point initialSize) {
        if (location != null) {
            return location;
        }
        return super.getInitialLocation(initialSize);
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        createDialogAreaContent(composite);
        super.setMessage(Messages.getString("PerlHelpDialog.perlHelpTitle")); //$NON-NLS-1$
        return composite;
    }

    private static final int HEIGHT_DIALOG = 350;

    private static final int WIDTH_DIALOG = 600;

    private void createDialogAreaContent(Composite parent) {
        PerlHelpComposite perlHelpComposite = new PerlHelpComposite(parent, SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = HEIGHT_DIALOG;
        gd.widthHint = WIDTH_DIALOG;
        perlHelpComposite.setLayoutData(gd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    public boolean close() {
        size = getShell().getSize();
        location = getShell().getLocation();
        return super.close();
    }
}
