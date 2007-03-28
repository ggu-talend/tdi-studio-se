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
package org.talend.repository.preview;

import org.eclipse.core.runtime.CoreException;


/**
 * DOC amaumont  class global comment. Detailled comment
 * <br/>
 *
 * $Id$
 *
 * @param <R> result of preview
 */
public class PreviewHandlerEvent<R> {

    /**
     * Type of the preview handler event.
     * <br/>
     *
     * $Id$
     *
     */
    public enum TYPE {
        PREVIEW_STARTED,
        PREVIEW_ENDED,
        PREVIEW_INTERRUPTED,
        PREVIEW_IN_ERROR,
    }

    private TYPE type;
    private AsynchronousPreviewHandler<R> source;
    private CoreException exception;

    /**
     * DOC amaumont PreviewHandlerEvent constructor comment.
     */
    public PreviewHandlerEvent(TYPE type, AsynchronousPreviewHandler<R> source) {
        super();
        this.type = type;
        this.source = source;
    }

    
    /**
     * DOC amaumont PreviewHandlerEvent constructor comment.
     * @param preview_in_error
     * @param name
     * @param e
     */
    public PreviewHandlerEvent(TYPE type, AsynchronousPreviewHandler<R> source, CoreException e) {
        this(type, source);
        this.exception = e;
    }


    /**
     * Getter for type.
     * @return the type
     */
    public TYPE getType() {
        return this.type;
    }


    
    /**
     * Getter for source.
     * @return the source
     */
    public AsynchronousPreviewHandler<R> getSource() {
        return this.source;
    }


    
    /**
     * Getter for exception.
     * @return the exception
     */
    public CoreException getException() {
        return this.exception;
    }
    
    
    
    
}
