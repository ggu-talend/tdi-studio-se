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
package org.talend.designer.codegen.model;

/**
 * Provider for current instance of CodeGeneratorInternalTemplatesFactory.
 * 
 * $Id$
 * 
 */
public final class CodeGeneratorInternalTemplatesFactoryProvider {

    /**
     * Default Constructor. Must not be used.
     */
    private CodeGeneratorInternalTemplatesFactoryProvider() {
    }

    private static CodeGeneratorInternalTemplatesFactory singleton = null;

    /**
     * Return the instance of CodeGeneratorInternalTemplatesFactory to use.
     * 
     * @return
     */
    public static CodeGeneratorInternalTemplatesFactory getInstance() {
        if (singleton == null) {
            singleton = new CodeGeneratorInternalTemplatesFactory();
        }
        return singleton;
    }

}
