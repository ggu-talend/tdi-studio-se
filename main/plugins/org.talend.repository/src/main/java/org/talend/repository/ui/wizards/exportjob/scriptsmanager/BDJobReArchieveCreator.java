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
package org.talend.repository.ui.wizards.exportjob.scriptsmanager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.osgi.framework.Bundle;
import org.talend.commons.CommonsPlugin;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.utils.generation.JavaUtils;
import org.talend.commons.utils.resource.FileExtensions;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.hadoop.HadoopConstants;
import org.talend.core.hadoop.version.EHadoopDistributions;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.runprocess.LastGenerationInfo;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.designer.core.IDesignerCoreService;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.maven.utils.PomUtil;
import org.talend.repository.ui.utils.ZipToFile;
import org.talend.repository.ui.wizards.exportjob.JavaJobExportReArchieveCreator;
import org.talend.utils.io.FilesUtils;

/**
 * DOC ggu class global comment. Detailled comment
 */
public class BDJobReArchieveCreator {

    private static final String PLUGIN_ID = "org.talend.libraries.apache.storm"; //$NON-NLS-1$

    private ProcessItem processItem;

    private Boolean isMRWithHDInsight, isStormJob, isSparkWithHDInsight;

    public BDJobReArchieveCreator(ProcessItem processItem) {
        this.processItem = processItem;
    }

    /**
     * copied from MapReduceJobJavaScriptsManager.
     */
    public boolean isMRWithHDInsight() {
        if (isMRWithHDInsight == null && processItem != null) {
            isMRWithHDInsight = false;
            if (isBDJobWithFramework(ERepositoryObjectType.PROCESS_MR, HadoopConstants.FRAMEWORK_MAPREDUCE)) {
                EList<ElementParameterType> parameters = processItem.getProcess().getParameters().getElementParameter();
                for (ElementParameterType pt : parameters) {
                    if (pt.getName().equals("DISTRIBUTION") //$NON-NLS-1$
                            && EHadoopDistributions.MICROSOFT_HD_INSIGHT.getName().equals(pt.getValue())) {
                        isMRWithHDInsight = true;
                        break;
                    }
                }
            }
        }
        return isMRWithHDInsight;
    }

    public boolean isFatJar() {
        if (isStormJob == null) {
            isStormJob = isBDJobWithFramework(ERepositoryObjectType.PROCESS_STORM, HadoopConstants.FRAMEWORK_STORM);
        }

        if (isSparkWithHDInsight == null) {
            isSparkWithHDInsight = false;
            if (isBDJobWithFramework(ERepositoryObjectType.PROCESS_MR, HadoopConstants.FRAMEWORK_SPARK)
                    || isBDJobWithFramework(ERepositoryObjectType.PROCESS_STORM, HadoopConstants.FRAMEWORK_SPARKSTREAMING)) {
                EList<ElementParameterType> parameters = processItem.getProcess().getParameters().getElementParameter();
                boolean modeParameterVisited = false;
                for (ElementParameterType pt : parameters) {
                    if (pt.getName().equals("SPARK_LOCAL_MODE")) { //$NON-NLS-1$
                        modeParameterVisited = true;
                        if ("true".equals(pt.getValue())) { //$NON-NLS-1$
                            isSparkWithHDInsight = false;
                            break;
                        }
                    }
                    if (pt.getName().equals("DISTRIBUTION") //$NON-NLS-1$
                            && EHadoopDistributions.MICROSOFT_HD_INSIGHT.getName().equals(pt.getValue())) {
                        isSparkWithHDInsight = true;
                        // If the SPARK_LOCAL_MODE parameter already have been processed and if we continue to loop,
                        // that means we are not in a LOCAL mode context. We can break the loop.
                        if (modeParameterVisited) {
                            break;
                        }
                    }
                }
            }
        }
        return isStormJob || isSparkWithHDInsight;
    }

    private boolean isBDJobWithFramework(ERepositoryObjectType objectType, String frameworkName) {
        if (processItem != null) {
            // Storm/SparkStreaming(PROCESS_STORM), MR/Spark(PROCESS_MR)
            if (ERepositoryObjectType.getItemType(processItem).equals(objectType)) { // have same type
                Property property = processItem.getProperty();
                if (property != null && property.getAdditionalProperties() != null
                        && frameworkName.equals(property.getAdditionalProperties().get(HadoopConstants.FRAMEWORK))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void create(File zipFile) {
        if (zipFile == null || !zipFile.exists() || !zipFile.isFile()) {
            return;
        }

        // check
        if (!isMRWithHDInsight() && !isFatJar()) {
            return;
        }
        Property property = processItem.getProperty();
        String label = property.getLabel();
        String version = property.getVersion();

        JavaJobExportReArchieveCreator creator = new JavaJobExportReArchieveCreator(zipFile.getAbsolutePath(), label);
        try {
            // create temp folders.
            creator.deleteTempFiles(); // clean temp folder
            File zipTmpFolder = new File(creator.getTmpFolder(), "zip-" + label + "_" + version); //$NON-NLS-1$ //$NON-NLS-2$
            File jarTmpFolder = new File(creator.getTmpFolder(), "jar-" + label + "_" + version); //$NON-NLS-1$ //$NON-NLS-2$
            zipTmpFolder.mkdirs();
            jarTmpFolder.mkdirs();

            // unzip the files.
            FilesUtils.unzip(zipFile.getAbsolutePath(), zipTmpFolder.getAbsolutePath());

            String jobJarName = JavaResourcesHelper.getJobJarName(property.getLabel(), property.getVersion())
                    + FileExtensions.JAR_FILE_SUFFIX;
            // same the the job pom assembly for package.
            File originalJarFile = new File(zipTmpFolder, label + '/' + jobJarName);
            if (!originalJarFile.exists()) { // can't find the job jar.
                return;
            }
            FilesUtils.unzip(originalJarFile.getAbsolutePath(), jarTmpFolder.getAbsolutePath());
            // re-build the job jar with lib.
            File newJarFile = new File(creator.getTmpFolder(), jobJarName);
            FilesUtils.copyFile(originalJarFile, newJarFile); // make sure enable to package the libs.
            JarBuilder jarbuilder = new JarBuilder(jarTmpFolder, newJarFile);
            String jobClassPackageFolder = JavaResourcesHelper.getJobClassPackageFolder(property.getItem());
            jarbuilder.setIncludeDir(Collections.singleton(jobClassPackageFolder));
            jarbuilder.setExcludeDir(null);
            if (isMRWithHDInsight()) {
                jarbuilder.setLibPath(getLibPath(zipTmpFolder, true));
            } else if (isFatJar()) {
                jarbuilder.setLibPath(getLibPath(zipTmpFolder, false));
                jarbuilder.setFatJar(true);
            }
            jarbuilder.buildJar();

            // use new jar to overwrite old one.
            if (originalJarFile.exists()) {
                originalJarFile.delete();
            }
            FilesUtils.copyFile(newJarFile, originalJarFile);

            // zip
            ZipToFile.zipFile(zipTmpFolder.getAbsolutePath(), zipFile.getAbsolutePath());
        } catch (Exception e) {
            ExceptionHandler.process(e);
        } finally {
            // when debug, keep the files first. because when build again. will do clean also.
            if (!CommonsPlugin.isDebugMode()) {
                creator.deleteTempFiles();
            }
        }
    }

    /**
     * 
     * maybe, should be same result with JobJavaScriptsManager.getLibPath
     */
    public List<File> getLibPath(File zipTmpFolder, boolean isSpecialMR) {
        List<File> neededLibFiles = new ArrayList<File>();
        File libFolder = new File(zipTmpFolder, JavaUtils.JAVA_LIB_DIRECTORY);

        Set<String> compiledModulesSet = new HashSet<String>(100);

        Set<ModuleNeeded> neededModules = LastGenerationInfo.getInstance().getModulesNeededWithSubjobPerJob(
                processItem.getProperty().getId(), processItem.getProperty().getVersion());
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IDesignerCoreService.class)) {
            IDesignerCoreService designerCoreService = (IDesignerCoreService) GlobalServiceRegister.getDefault().getService(
                    IDesignerCoreService.class);
            IProcess process = designerCoreService.getProcessFromProcessItem(processItem);
            compiledModulesSet.addAll(PomUtil.getCodesExportJars(process));
            if (neededModules.isEmpty()) {
                neededModules = process.getNeededModules(true);
            }
        }

        for (ModuleNeeded module : neededModules) {
            if (((isSpecialMR || isSparkWithHDInsight) && module.isMrRequired()) || (!isSpecialMR && !isSparkWithHDInsight)) {
                compiledModulesSet.add(module.getModuleName());
            }
        }

        Set<String> jarNames = new HashSet<String>();
        try {
            // from org.talend.libraries.apache.storm/lib
            Bundle bundle = Platform.getBundle(PLUGIN_ID);
            if (bundle != null) {
                URL stormLibUrl = FileLocator.toFileURL(FileLocator.find(bundle, new Path("lib"), null)); //$NON-NLS-1$
                if (stormLibUrl != null) {
                    File file = new File(stormLibUrl.getFile());
                    File[] jars = file.listFiles();
                    for (File f : jars) {
                        jarNames.add(f.getName());
                    }
                }
            }
        } catch (IOException e) {
            ExceptionHandler.process(e);
        }
        for (String jarName : compiledModulesSet) {
            File jarFile = new File(libFolder, jarName);
            if (jarFile.exists()) {
                if (!isSpecialMR && !jarNames.isEmpty()) {
                    // for storm not include the jar from libraries.apache.strom
                    if (!jarNames.contains(jarName)) {
                        neededLibFiles.add(jarFile);
                    }
                } else {
                    neededLibFiles.add(jarFile);
                }
            }
        }
        return neededLibFiles;
    }

}
