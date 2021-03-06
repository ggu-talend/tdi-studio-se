// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.generic.model;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.Trigger;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.INode;
import org.talend.designer.core.generic.constants.IGenericConstants;
import org.talend.designer.core.generic.i18n.Messages;
import org.talend.designer.core.model.components.NodeConnector;

// TUP-4153
public class ComponentUtils {

    /**
     * Check if the current connector contains correct information to be translated to a NodeConnector. There is
     * currently no case where a connector can be invalid.
     *
     * @param connector a Connector generated by the new component architecture
     * @param componentName the name of the current component
     * @return a boolean if the connector is valid
     */
    public static boolean isAValidConnector(Connector connector, String componentName) {
        // Currently a connector can only be a FLOW, a MAIN or a REJECT. There is nothing to check for the moment.
        return true;
    }

    /**
     * Transform a Connector to a NodeConnector.
     *
     * @param connector a Connector generated by the new component architecture
     * @param parentNode The parent node current connector
     * @return a NodeConnector compatible with the Studio.
     */
    public static NodeConnector generateNodeConnectorFromConnector(Connector connector, INode parentNode) {
        String originalConnectorName = connector.getType().name();
        boolean isFlow_Sub_ConnectorName = IGenericConstants.MAIN_CONNECTOR_NAME.equals(originalConnectorName)
                || IGenericConstants.REJECT_CONNECTOR_NAME.equals(originalConnectorName);
        EConnectionType currentType = EConnectionType.FLOW_MAIN;

        NodeConnector nodeConnector = new NodeConnector(parentNode);
        nodeConnector.setDefaultConnectionType(currentType);
        // set the default values
        nodeConnector.setLinkName(currentType.getDefaultLinkName());
        nodeConnector.setMenuName(currentType.getDefaultMenuName());

        // set input
        if (!isFlow_Sub_ConnectorName) {
            nodeConnector.setMaxLinkInput(connector.getMaxInput());
        }

        // set output
        nodeConnector.setMaxLinkOutput(connector.getMaxOutput());

        if (nodeConnector.getName() == null) {
            nodeConnector.setName(originalConnectorName);
            nodeConnector.setBaseSchema(currentType.getName());
            if (IGenericConstants.REJECT_CONNECTOR_NAME.equals(originalConnectorName)) {
                nodeConnector.setMenuName("Reject"); //$NON-NLS-1$
                nodeConnector.setLinkName("Reject"); //$NON-NLS-1$
            }
        }
        setConnectionProperty(currentType, nodeConnector);

        // if kind is "flow" (main type), then add the same for the lookup and merge.
        setConnectionProperty(EConnectionType.FLOW_REF, nodeConnector);
        setConnectionProperty(EConnectionType.FLOW_MERGE, nodeConnector);
        return nodeConnector;
    }

    /**
     * Check if the current trigger contains correct information to be translated to a NodeConnector. For example, we
     * currently do not support LOOKUP or MERGE trigger.
     *
     * @param trigger a Trigger generated by the new component architecture
     * @param componentName the name of the current component
     * @return a boolean if the trigger is valid
     */
    public static boolean isAValidTrigger(Trigger trigger, String componentName) {
        String triggerName = trigger.getType().name();
        EConnectionType currentType = EConnectionType.getTypeFromName(triggerName);
        if (currentType == null || ("LOOKUP").equals(triggerName) || ("MERGE").equals(triggerName)) {//$NON-NLS-1$//$NON-NLS-2$
            if (currentType == null) {
                Component.getLog().warn(Messages.getString("Component.componentNotExist", componentName //$NON-NLS-1$
                        , triggerName));
            }
            return false;
        }
        return true;
    }

    /**
     * Transform a Trigger to a NodeConnector.
     *
     * @param trigger a Trigger generated by the new component architecture
     * @param parentNode The parent node current trigger
     * @return a NodeConnector compatible with the Studio.
     */
    public static NodeConnector generateNodeConnectorFromTrigger(Trigger trigger, INode parentNode) {
        String triggerName = trigger.getType().name();
        EConnectionType currentType = EConnectionType.getTypeFromName(triggerName);

        NodeConnector nodeConnector = new NodeConnector(parentNode);

        nodeConnector.setDefaultConnectionType(currentType);
        // set the default values
        nodeConnector.setLinkName(currentType.getDefaultLinkName());
        nodeConnector.setMenuName(currentType.getDefaultMenuName());

        // set input
        nodeConnector.setMaxLinkInput(trigger.getMaxInput());

        // set output
        if ("ITERATE".equals(triggerName)) {//$NON-NLS-1$
            nodeConnector.setMaxLinkOutput(trigger.getMaxOutput());
        }

        if (nodeConnector.getName() == null) {
            nodeConnector.setName(triggerName);
            nodeConnector.setBaseSchema(currentType.getName());
        }

        setConnectionProperty(currentType, nodeConnector);
        return nodeConnector;
    }

    /**
     * Utilitary function to set a connectionProperty of a given type to a node.
     *
     * @param currentType type of the connection
     * @param node currentNode
     */
    private static void setConnectionProperty(EConnectionType currentType, NodeConnector node) {
        // One line method that factorize a lot of code.
        node.addConnectionProperty(currentType, currentType.getRGB(), currentType.getDefaultLineStyle());
    }

}
