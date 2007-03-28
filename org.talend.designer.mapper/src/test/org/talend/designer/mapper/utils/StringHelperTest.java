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
package org.talend.designer.mapper.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.talend.commons.utils.data.text.StringHelper;

/**
 * DOC amaumont class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class StringHelperTest {

    /**
     * Test method for
     * {@link org.talend.designer.mapper.utils.StringHelper#replacePrms(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testReplacePrms() {
        assertEquals("abcdef", StringHelper.replacePrms("a{0}{1}d{2}f", new Object[] { "b", "c", "e" }));
        assertEquals("ab\\{c\\}def", StringHelper.replacePrms("a{0}\\{{1}\\}d{2}f", new Object[] { "b", "c", "e" }));
    }

}
