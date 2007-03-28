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

package org.talend.sqlbuilder.sqleditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * SQL color manager.
 * <br/>
 *
 * $Id: talend-code-templates.xml 1 2006-09-29 17:06:40 +0000 (Fri, 29 Sep 2006) nrousseau $
 *
 */
public class SQLColorManager implements IColorManager, IColorManagerExtension {

	protected Map fKeyTable = new HashMap(10);

	protected Map fDisplayTable = new HashMap(2);

	public SQLColorManager() {
	}

	void dispose(Display display) {
		Map colorTable = (Map) fDisplayTable.get(display);
		if (colorTable != null) {
			Iterator e = colorTable.values().iterator();
			while (e.hasNext()) {
				((Color) e.next()).dispose();
			}
		}
	}

	/*
	 * @see IColorManager#getColor(RGB)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Color getColor(RGB rgb) {

		if (rgb == null) {
			return null;
		}
		final Display display = Display.getCurrent();
		Map colorTable = (Map) fDisplayTable.get(display);
		if (colorTable == null) {
			colorTable = new HashMap(10);
			fDisplayTable.put(display, colorTable);
			display.disposeExec(new Runnable() {
				public void run() {
					dispose(display);
				}
			});
		}

		Color color = (Color) colorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			colorTable.put(rgb, color);
		}

		return color;
	}

	/*
	 * @see IColorManager#dispose
	 */
	public void dispose() {
		// nothing to dispose
	}

	/*
	 * @see IColorManager#getColor(String)
	 */
	public Color getColor(String key) {

		if (key == null) {
			return null;
		}
		RGB rgb = (RGB) fKeyTable.get(key);
		return getColor(rgb);
	}

	/*
	 * @see IColorManagerExtension#bindColor(String, RGB)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public void bindColor(String key, RGB rgb) {
		Object value = fKeyTable.get(key);
		if (value != null) {
			throw new UnsupportedOperationException();
		}
		fKeyTable.put(key, rgb);
	}

	/*
	 * @see IColorManagerExtension#unbindColor(String)
	 */
	public void unbindColor(String key) {
		fKeyTable.remove(key);
	}
}
