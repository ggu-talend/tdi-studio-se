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
package org.talend.repository.model.migration.filters;

import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.repository.model.migration.ComponentUtilities;

/**
 * Filter components by property. Property and value are specified in constructor.<br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40 +0000 (ven., 29 sept. 2006) nrousseau $
 * 
 */
public class PropertyComponentFilter extends NameComponentFilter implements IComponentFilter {

    private String property;

    private String value;

    public PropertyComponentFilter(String name, String property, String value) {
        super(name);
        this.property = property;
        this.value = value;
    }

    @Override
    public boolean accept(NodeType node) {
        boolean toReturn = (name == null ? true : super.accept(node));
        if (toReturn) {
            String pValue = ComponentUtilities.getNodePropertyValue(node, property);
            toReturn = pValue.equals(value);
        }
        return toReturn;
    }

}
