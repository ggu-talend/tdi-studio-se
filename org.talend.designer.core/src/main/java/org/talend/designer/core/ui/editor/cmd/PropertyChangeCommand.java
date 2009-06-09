// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.ui.editor.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.Element;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.IElementParameterDefaultValue;
import org.talend.core.model.properties.ProcessItem;
import org.talend.designer.core.i18n.Messages;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.components.EmfComponent;
import org.talend.designer.core.model.process.jobsettings.JobSettingsConstants;
import org.talend.designer.core.ui.editor.connections.Connection;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.preferences.StatsAndLogsConstants;
import org.talend.designer.core.ui.views.CodeView;
import org.talend.designer.core.ui.views.jobsettings.JobSettings;
import org.talend.designer.core.ui.views.properties.ComponentSettings;
import org.talend.designer.core.utils.DesignerUtilities;
import org.talend.designer.runprocess.ItemCacheManager;

/**
 * Command that changes a given property. It will call the set or get property value in an element. This element can be
 * either a node, a connection or a process. <br/>
 * 
 * $Id$
 * 
 */
public class PropertyChangeCommand extends Command {

    private final Element elem;

    private final String propName;

    private Object newValue;

    private Object oldValue;

    private boolean repositoryValueWasUsed;

    private boolean toUpdate;

    private final Map<IElementParameter, Object> oldElementValues;

    private ChangeMetadataCommand changeMetadataCommand;

    private String propertyTypeName;

    private final String updataComponentParamName;

    /**
     * The property is defined in an element, which can be either a node or a connection.
     * 
     * @param elem
     * @param propName
     * @param propValue
     */
    public PropertyChangeCommand(Element elem, String propName, Object propValue) {
        this.elem = elem;
        this.propName = propName;
        newValue = propValue;
        toUpdate = false;
        oldElementValues = new HashMap<IElementParameter, Object>();
        setLabel(Messages.getString("PropertyChangeCommand.Label")); //$NON-NLS-1$
        // for job settings extra (feature 2710)
        // if (JobSettingsConstants.isExtraParameter(propName) ||
        // propName.equals(EParameterName.IMPLICIT_TCONTEXTLOAD.getName())) {
        // propertyTypeName = JobSettingsConstants.getExtraParameterName(EParameterName.PROPERTY_TYPE.getName());
        // updataComponentParamName =
        // JobSettingsConstants.getExtraParameterName(EParameterName.UPDATE_COMPONENTS.getName());
        // } else {

        IElementParameter currentParam = elem.getElementParameter(propName);
        propertyTypeName = EParameterName.PROPERTY_TYPE.getName();
        for (IElementParameter param : elem.getElementParameters()) {
            if (param.getField().equals(EParameterFieldType.PROPERTY_TYPE)
                    && param.getCategory().equals(currentParam.getCategory())) {
                propertyTypeName = param.getName() + ":" + EParameterName.PROPERTY_TYPE.getName(); //$NON-NLS-1$
                break;
            }
        }
        updataComponentParamName = EParameterName.UPDATE_COMPONENTS.getName();
        // }
    }

    @Override
    public void execute() {
        IElementParameter currentParam = elem.getElementParameter(propName);
        oldElementValues.clear();

        if (currentParam == null) {
            return;
        }

        if (currentParam.isRepositoryValueUsed()) {
            if (currentParam.getField() == EParameterFieldType.MEMO_SQL) {
                Object queryStoreValue = elem.getPropertyValue(EParameterName.QUERYSTORE_TYPE.getName());
                if (!EmfComponent.BUILTIN.equals(queryStoreValue)) {
                    elem.setPropertyValue(EParameterName.QUERYSTORE_TYPE.getName(), EmfComponent.BUILTIN);
                }
                currentParam.setRepositoryValueUsed(false);
            } else {
                toUpdate = true;
                elem.setPropertyValue(propertyTypeName, EmfComponent.BUILTIN);
                for (IElementParameter param : elem.getElementParameters()) {
                    if (param.getCategory().equals(currentParam.getCategory())) {
                        param.setRepositoryValueUsed(false);
                    }
                }
            }

            repositoryValueWasUsed = true;
        } else {
            repositoryValueWasUsed = false;
        }

        oldValue = elem.getPropertyValue(propName);
        elem.setPropertyValue(propName, newValue);
        if (propName.contains(EParameterName.PROCESS_TYPE_PROCESS.getName())) {
            // newValue is the id of the job
            ProcessItem processItem = ItemCacheManager.getProcessItem((String) newValue);
            if (processItem != null) {
                currentParam.setLinkedRepositoryItem(processItem);
                currentParam.getParentParameter().setValue(processItem.getProperty().getLabel());
            }
        }
        if (propName.contains(EParameterName.PROCESS_TYPE_VERSION.getName())) {
            // newValue is the id of the job
            IElementParameter processIdParam = currentParam.getParentParameter().getChildParameters().get(
                    EParameterName.PROCESS_TYPE_PROCESS.getName());
            ProcessItem processItem = ItemCacheManager.getProcessItem((String) processIdParam.getValue(), (String) newValue);
            if (processItem != null) {
                processIdParam.setLinkedRepositoryItem(processItem);
                currentParam.getParentParameter().setValue(processItem.getProperty().getLabel());
            }
        }
        if (propName.contains(EParameterName.PROCESS_TYPE_CONTEXT.getName())) {
            if (elem instanceof Node) {
                Node node = (Node) elem;
                List<IContext> listContext = node.getProcess().getContextManager().getListContext();
                List<String> values = new ArrayList<String>();
                for (IContext context : listContext) {
                    values.add(context.getName());
                }
                currentParam.setListItemsDisplayName(values.toArray(new String[0]));
                currentParam.setListItemsValue(values.toArray(new String[0]));
                currentParam.setValue(newValue);
            }

        }
        String dbType = "";
        if (newValue instanceof String) {
            dbType = (String) newValue;
        }
        if (propName.equals(EParameterName.DB_TYPE.getName())) {
            IElementParameter elementParameter = elem.getElementParameter(EParameterName.DB_VERSION.getName());
            IElementParameter elementParameter2 = elem.getElementParameter(EParameterName.SCHEMA_DB.getName());
            setDbVersion(elementParameter, dbType);
            DesignerUtilities.setSchemaDB(elementParameter2, newValue);
        } else if (propName.equals(JobSettingsConstants.getExtraParameterName(EParameterName.DB_TYPE.getName()))) {//$NON-NLS-1$
            IElementParameter elementParameter = elem.getElementParameter(JobSettingsConstants
                    .getExtraParameterName(EParameterName.DB_VERSION.getName()));
            IElementParameter elementParameter2 = elem.getElementParameter(JobSettingsConstants
                    .getExtraParameterName(EParameterName.SCHEMA_DB.getName()));
            setDbVersion(elementParameter, dbType);
            DesignerUtilities.setSchemaDB(elementParameter2, newValue);
        }
        if (!toUpdate
                && (currentParam.getField().equals(EParameterFieldType.RADIO)
                        || currentParam.getField().equals(EParameterFieldType.CLOSED_LIST)
                        || currentParam.getField().equals(EParameterFieldType.CHECK) || currentParam.getField().equals(
                        EParameterFieldType.AS400_CHECK))) {
            toUpdate = false;
            for (int i = 0; i < elem.getElementParameters().size(); i++) {
                IElementParameter testedParam = elem.getElementParameters().get(i);

                String showIf = testedParam.getShowIf();
                String notShowIf = testedParam.getNotShowIf();

                if (showIf != null) {
                    if (showIf.contains(currentParam.getName())) {
                        toUpdate = true;
                    }
                } else {
                    if (notShowIf != null) {
                        if (notShowIf.contains(currentParam.getName())) {
                            toUpdate = true;
                        }
                    }
                }
                if (testedParam.getField() == EParameterFieldType.TABLE) {
                    String[] tmpShowIfs = testedParam.getListItemsShowIf();
                    if (tmpShowIfs != null) {
                        for (String show : tmpShowIfs) {
                            if (show != null && show.contains(currentParam.getName())) {
                                toUpdate = true;
                            }
                        }
                    }
                    tmpShowIfs = testedParam.getListItemsNotShowIf();
                    if (tmpShowIfs != null) {
                        for (String show : tmpShowIfs) {
                            if (show != null && show.contains(currentParam.getName())) {
                                toUpdate = true;
                            }
                        }
                    }
                }
                if (currentParam.getField().equals(EParameterFieldType.CLOSED_LIST)) {
                    if (testedParam.getListItemsShowIf() != null) {
                        for (int j = 0; j < testedParam.getListItemsShowIf().length && !toUpdate; j++) {
                            showIf = testedParam.getListItemsShowIf()[j];
                            notShowIf = testedParam.getListItemsNotShowIf()[j];
                            if (showIf != null) {
                                if (showIf.contains(currentParam.getName())) {
                                    toUpdate = true;
                                }
                            } else {
                                if (notShowIf != null) {
                                    if (notShowIf.contains(currentParam.getName())) {
                                        toUpdate = true;
                                    }
                                }
                            }
                        }
                    }
                }

                setDefaultValues(currentParam, testedParam);
            }
        }

        if (currentParam.getName().equals(EParameterName.PROCESS_TYPE_PROCESS.getName())) {
            toUpdate = true;
        }

        if (toUpdate) {
            elem.setPropertyValue(updataComponentParamName, new Boolean(true));
        }
        CodeView.refreshCodeView(elem);

        if (elem instanceof Node) {
            ((Node) elem).checkAndRefreshNode();
        }

        // See feature 3902
        if (needUpdateMonitorConnection()) {
            ((Connection) elem).setMonitorConnection((Boolean) currentParam.getValue());
        }
    }

    private void setDbVersion(IElementParameter elementParameter, String value) {
        if (value.indexOf("Access") != -1) {//$NON-NLS-1$
            elementParameter.setValue(StatsAndLogsConstants.ACCESS_VERSION_DRIVER[1]);
            elementParameter.setListItemsDisplayName(StatsAndLogsConstants.ACCESS_VERSION_DISPLAY);
            elementParameter.setListItemsValue(StatsAndLogsConstants.ACCESS_VERSION_DRIVER);
        } else if (value.indexOf("Oracle") != -1) {//$NON-NLS-1$
            elementParameter.setValue(StatsAndLogsConstants.ORACLE_VERSION_DRIVER[1]);
            elementParameter.setListItemsDisplayName(StatsAndLogsConstants.ORACLE_VERSION_DISPLAY);
            elementParameter.setListItemsValue(StatsAndLogsConstants.ORACLE_VERSION_DRIVER);
        } else if (value.indexOf("AS400") != -1) {//$NON-NLS-1$
            elementParameter.setValue(StatsAndLogsConstants.AS400_VERSION_DRIVER[1]);
            elementParameter.setListItemsDisplayName(StatsAndLogsConstants.AS400_VERSION_DISPLAY);
            elementParameter.setListItemsValue(StatsAndLogsConstants.AS400_VERSION_DRIVER);
        }
    }

    private boolean needUpdateMonitorConnection() {

        if (elem instanceof Connection) {
            if (propName.equals(EParameterName.MONITOR_CONNECTION.getName())) {
                return true;
            }
        }

        return false;
    }

    public void setUpdate(boolean update) {
        toUpdate = update;
    }

    /**
     * Set the values to default if needed.
     * 
     * @param currentParam Current parameter that has been modified in the interface
     * @param testedParam Tested parameter, to know if there is a link for the default values between this parameter and
     * the current.
     */
    private void setDefaultValues(IElementParameter currentParam, IElementParameter testedParam) {
        boolean contains = false;
        for (IElementParameterDefaultValue value : testedParam.getDefaultValues()) {
            if (value.getIfCondition() != null) {
                if (value.getIfCondition().contains(currentParam.getName())) {
                    contains = true;
                    break;
                }
            }
            if (value.getNotIfCondition() != null) {
                if (value.getNotIfCondition().contains(currentParam.getName())) {
                    contains = true;
                    break;
                }
            }
        }

        if (testedParam.getDefaultValues().size() > 0 && contains) {
            oldElementValues.put(testedParam, testedParam.getValue());

            // if the field is not a schema type, then use standard "set value".
            if (!testedParam.getField().equals(EParameterFieldType.SCHEMA_TYPE)) {
                String oldMapping = ""; //$NON-NLS-1$
                if (!testedParam.getField().equals(EParameterFieldType.CHECK)
                        && !testedParam.getField().equals(EParameterFieldType.RADIO)) {
                    oldMapping = (String) testedParam.getValue();
                }
                testedParam.setValueToDefault(elem.getElementParameters());
                if (testedParam.getField().equals(EParameterFieldType.MAPPING_TYPE)) {
                    String newMapping = (String) testedParam.getValue();
                    if (!oldMapping.equals(newMapping)) {
                        Node node = (Node) elem;
                        if (node.getMetadataList().size() > 0) {
                            // to change with:
                            // IMetadataTable metadataTable = node.getMetadataFromConnector(testedParam.getContext());
                            IMetadataTable metadataTable = node.getMetadataList().get(0);
                            metadataTable.setDbms(newMapping);
                        }
                    }
                }
            } else {
                // See issue 975, update the schema.
                Node node = (Node) elem;
                if (node.getMetadataList().size() > 0) {
                    IMetadataTable metadataTable = node.getMetadataFromConnector(testedParam.getContext());
                    testedParam.setValueToDefault(node.getElementParameters());
                    IMetadataTable newMetadataTable = (IMetadataTable) testedParam.getValue();
                    newMetadataTable.setTableName(metadataTable.getTableName());
                    newMetadataTable.setAttachedConnector(metadataTable.getAttachedConnector());
                    metadataTable.setReadOnly(newMetadataTable.isReadOnly());

                    metadataTable.setListColumns(newMetadataTable.clone(true).getListColumns());

                    changeMetadataCommand = new ChangeMetadataCommand(node, null, metadataTable, newMetadataTable);
                    changeMetadataCommand.execute(true);
                }
            }
        }
    }

    @Override
    public void undo() {
        IElementParameter currentParam = elem.getElementParameter(propName);
        if (repositoryValueWasUsed) {
            if (currentParam.getField() == EParameterFieldType.MEMO_SQL) {
                elem.setPropertyValue(EParameterName.QUERYSTORE_TYPE.getName(), EmfComponent.REPOSITORY);
            } else {
                elem.setPropertyValue(propertyTypeName, EmfComponent.REPOSITORY);
            }
            for (IElementParameter param : elem.getElementParameters()) {
                String repositoryValue = param.getRepositoryValue();
                if (param.isShow(elem.getElementParameters()) && (repositoryValue != null)
                        && param.getCategory().equals(currentParam.getCategory())) {
                    param.setRepositoryValueUsed(true);
                }
            }
        }
        elem.setPropertyValue(propName, oldValue);
        if (propName.contains(EParameterName.PROCESS_TYPE_PROCESS.getName())) {
            // oldValue is the id of the job
            ProcessItem processItem = ItemCacheManager.getProcessItem((String) oldValue);
            if (processItem != null) {
                currentParam.setLinkedRepositoryItem(processItem);
                currentParam.getParentParameter().setValue(processItem.getProperty().getLabel());
            }
        }

        for (IElementParameter param : oldElementValues.keySet()) {
            param.setValue(oldElementValues.get(param));
        }

        if (toUpdate) {
            elem.setPropertyValue(updataComponentParamName, new Boolean(true));
        }
        if (changeMetadataCommand != null) {
            changeMetadataCommand.undo();
        }
        CodeView.refreshCodeView(elem);
        ComponentSettings.switchToCurComponentSettingsView();
        JobSettings.switchToCurJobSettingsView();
        refreshTraceConnections();
        if (elem instanceof Node) {
            ((Node) elem).checkAndRefreshNode();
        }

    }

    @Override
    public void redo() {
        IElementParameter currentParam = elem.getElementParameter(propName);
        if (repositoryValueWasUsed) {
            if (currentParam.getField() == EParameterFieldType.MEMO_SQL) {
                elem.setPropertyValue(EParameterName.QUERYSTORE_TYPE.getName(), EmfComponent.BUILTIN);
            } else {
                elem.setPropertyValue(propertyTypeName, EmfComponent.BUILTIN);
            }

            for (IElementParameter param : elem.getElementParameters()) {
                boolean paramFlag = JobSettingsConstants.isExtraParameter(param.getName());
                boolean extraFlag = JobSettingsConstants.isExtraParameter(propertyTypeName);
                if (paramFlag == extraFlag) {
                    // for job settings extra.(feature 2710)
                    param.setRepositoryValueUsed(false);
                }
            }
        }

        elem.setPropertyValue(propName, newValue);
        if (propName.contains(EParameterName.PROCESS_TYPE_PROCESS.getName())) {
            // newValue is the id of the job
            ProcessItem processItem = ItemCacheManager.getProcessItem((String) newValue);
            if (processItem != null) {
                currentParam.setLinkedRepositoryItem(processItem);
                currentParam.getParentParameter().setValue(processItem.getProperty().getLabel());
            }
        }

        if (currentParam.getField().equals(EParameterFieldType.CLOSED_LIST)) {
            for (int i = 0; i < elem.getElementParameters().size(); i++) {
                IElementParameter param = elem.getElementParameters().get(i);
                if (param.getDefaultValues().size() > 0) {
                    param.setValueToDefault(elem.getElementParameters());
                }
            }
        }

        if (toUpdate) {
            elem.setPropertyValue(updataComponentParamName, new Boolean(true));
        }

        if (changeMetadataCommand != null) {
            changeMetadataCommand.redo();
        }
        CodeView.refreshCodeView(elem);
        ComponentSettings.switchToCurComponentSettingsView();
        JobSettings.switchToCurJobSettingsView();
        refreshTraceConnections();
        if (elem instanceof Node) {
            ((Node) elem).checkAndRefreshNode();
        }
    }

    private void refreshTraceConnections() {
        if (propName.equals(EParameterName.TRACES_CONNECTION_ENABLE.getName()) || this.elem instanceof Connection) {
            ((Connection) this.elem).getConnectionTrace().setPropertyValue(EParameterName.TRACES_SHOW_ENABLE.getName(), true);
        }
    }

    public String getPropName() {
        return this.propName;
    }

    public Element getElement() {
        return this.elem;
    }

    public Object getOldValue() {
        return this.oldValue;
    }

    public Object getNewValue() {
        return this.newValue;
    }

}
