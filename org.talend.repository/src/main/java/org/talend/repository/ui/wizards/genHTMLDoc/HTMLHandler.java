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
package org.talend.repository.ui.wizards.genHTMLDoc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.talend.commons.exception.ExceptionHandler;

/**
 * This class is used for transfering XML file to HTML file.
 * 
 * $Id: HTMLGenerator.java 2007-3-7,下午04:42:22 ftang $
 * 
 */
public class HTMLHandler {

	private static final String XMLFILESUFFIX = ".xml";

	private static final String HTMLFILESUFFIX = ".html";

	/**
	 * This method is used for generating HTML file base on given folder, job
	 * name and xsl file name.
	 * 
	 * @param tempFolderPath
	 *            a string
	 * @param jobName
	 *            a string
	 * @param xslFileName
	 *            a string
	 */
	public static void generate(String tempFolderPath, String jobName,
			String xslFilePath) throws Exception {
		FileOutputStream output = null;
		Writer writer = null;
		try {
			File xmlFile = new File(tempFolderPath + File.separatorChar
					+ jobName + XMLFILESUFFIX);
			File xsltFile = new File(xslFilePath);

			javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(
					xmlFile);
			javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(
					xsltFile);

			output = new FileOutputStream(tempFolderPath + File.separatorChar
					+ jobName + HTMLFILESUFFIX);

			// Note that if the are chinese in the file, should set the encoding
			// type to "UTF-8", this is caused by DOM4J.
			writer = new OutputStreamWriter(output, "UTF-8");

			javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(
					writer);

			// create an instance of TransformerFactory
			javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory
					.newInstance();

			javax.xml.transform.Transformer trans = transFact
					.newTransformer(xsltSource);

			trans.transform(xmlSource, result);

		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				ExceptionHandler.process(e);
			}
			try {
				if (writer != null) {
					writer.close();
				}

			} catch (Exception e1) {
				ExceptionHandler.process(e1);
			}

		}
	}
}
