// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.codegen.ui.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.designer.codegen.components.ui.IComponentPreferenceConstant;
import org.talend.designer.codegen.ui.CodegenUiPlugin;

/**
 * DOC zli class global comment. Detailled comment
 */
public class ComponentPreferenceInitializer extends AbstractPreferenceInitializer {

    public ComponentPreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore preferenceStore = CodegenUiPlugin.getDefault().getPreferenceStore();

        preferenceStore.putValue(IComponentPreferenceConstant.LIMIT, "1000"); //$NON-NLS-1$
        preferenceStore.setDefault(IComponentPreferenceConstant.LIMIT, "1000"); //$NON-NLS-1$

        preferenceStore.putValue(IComponentPreferenceConstant.LINK_STYLE, LINK_STYLE.AUTO.toString());
    }
}