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
package org.talend.sqlbuilder.erdiagram.ui.figures;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40 +0000 (ææäº, 29 ä¹æ 2006) nrousseau $
 * 
 */
public class ColorUtilities {

    /**
     * It is refined method in org.eclipse.dreaw2d.FigureUtilities which produces Color leak.
     */
    private static final float RGB_VALUE_MULTIPLIER = 0.6f;

    public static Color darker(Color color) {
        return getColor((int) (color.getRed() * RGB_VALUE_MULTIPLIER), (int) (color.getGreen() * RGB_VALUE_MULTIPLIER),
                (int) (color.getBlue() * RGB_VALUE_MULTIPLIER));
    }

    public static Color lighter(Color color) {
        int red = Math.min(255, (int) (color.getRed() * 1.2));
        int green = Math.min(255, (int) (color.getGreen() * 1.2));
        int blue = Math.min(255, (int) (color.getBlue() * 1.2));
        return getColor(red, green, blue);
    }

    public static Color lighter(Color color, float factor) {
        int red = Math.min(255, (int) (color.getRed() * factor));
        int green = Math.min(255, (int) (color.getGreen() * factor));
        int blue = Math.min(255, (int) (color.getBlue() * factor));
        return getColor(red, green, blue);
    }

    public static Color getColor(int red, int green, int blue) {
        return getColor(new RGB(red, green, blue));
    }

    public static Color getColor(RGB rgb) {
        return EditorsPlugin.getDefault().getSharedTextColors().getColor(rgb);
    }

    /**
     * DOC admin Comment method "getSelectStatement".
     * 
     * @param tables
     * @param columns
     * @param wheres
     * @return
     */
    public static String getSelectStatement(List<String> tables, List<String> columns, List<String> wheres) {
        String sql;
        sql = "select "; //$NON-NLS-1$
        for (String string : columns) {
            sql = sql + string + ","; //$NON-NLS-1$
        }
        sql = sql.substring(0, sql.length() - 1) + " from "; //$NON-NLS-1$
        for (String string : tables) {
            sql = sql + string + ","; //$NON-NLS-1$
        }
        sql = sql.substring(0, sql.length() - 1) + " where "; //$NON-NLS-1$
        for (String string : wheres) {
            sql = sql + string + ","; //$NON-NLS-1$
        }
        return sql;
    }
}
