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
package org.talend.designer.core.model.components;

import java.util.StringTokenizer;

import org.talend.core.model.components.IMultipleComponentParameter;

/**
 * DOC nrousseau class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class MultipleComponentParameter implements IMultipleComponentParameter {

    String sourceComponent;

    String targetComponent;

    String sourceValue;

    String targetValue;

    public MultipleComponentParameter(String source, String target) {
        StringTokenizer token = new StringTokenizer(source, "."); //$NON-NLS-1$
        sourceComponent = token.nextToken();
        sourceValue = token.nextToken();

        token = new StringTokenizer(target, "."); //$NON-NLS-1$
        targetComponent = token.nextToken();
        targetValue = token.nextToken();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.components.IMultipleComponentParameter#getSourceComponent()
     */
    public String getSourceComponent() {
        return this.sourceComponent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.components.IMultipleComponentParameter#getSourceValue()
     */
    public String getSourceValue() {
        return this.sourceValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.components.IMultipleComponentParameter#getTargetComponent()
     */
    public String getTargetComponent() {
        return this.targetComponent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.model.components.IMultipleComponentParameter#getTargetValue()
     */
    public String getTargetValue() {
        return this.targetValue;
    }
}
