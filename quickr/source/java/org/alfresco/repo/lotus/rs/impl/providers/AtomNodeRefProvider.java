/*
 * Copyright 2010-2012 Alfresco Software Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is part of an unsupported extension to Alfresco.
 *
 * [BRIEF DESCRIPTION OF FILE CONTENTS]
 */

package org.alfresco.repo.lotus.rs.impl.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.util.FileCopyUtils;

/**
 * @author PavelYur
 */
@Produces( { "application/atom+xml", "text/html", "application/xhtml+xml", "application/xml" })
@Provider
public class AtomNodeRefProvider implements MessageBodyWriter<NodeRef>
{
    public static SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

    static
    {
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private CheckOutCheckInService checkOutCheckInService;

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    public void writeTo(NodeRef nodeRef, Class<?> clazz, Type type, Annotation[] a, MediaType mt, MultivaluedMap<String, Object> headers, OutputStream os) throws IOException,
            WebApplicationException
    {
        if (nodeRef == null)
        {
            return;
        }

        Map<QName, Serializable> props = nodeService.getProperties(nodeRef);

        ContentData content = (ContentData) props.get(ContentModel.PROP_CONTENT);

        Date lastModified = (Date) props.get(ContentModel.PROP_MODIFIED);

        String fileName = (String) props.get(ContentModel.PROP_NAME);
        NodeRef checkedOutNodeRef = checkOutCheckInService.getCheckedOut(nodeRef);
        if (checkedOutNodeRef != null)
        {
            fileName = (String) nodeService.getProperty(checkedOutNodeRef, ContentModel.PROP_NAME);
        }

        String documentLanguage = content.getLocale().toString().replace('_', '-');

        headers.add(HttpHeaders.CONTENT_TYPE, content.getMimetype());
        headers.add(HttpHeaders.CACHE_CONTROL, "must-revalidate,private,max-age=0");
        headers.add(HttpHeaders.CONTENT_LENGTH, Long.toString(getSize(nodeRef, null, null, null, null)));
        headers.add(HttpHeaders.CONTENT_LANGUAGE, documentLanguage);
        headers.add(HttpHeaders.ETAG, "\"" + nodeRef.getId() + ":" + Long.toString(lastModified.getTime()) + "\"");
        headers.add(HttpHeaders.LAST_MODIFIED, format.format(lastModified));
        headers.add("Content-Disposition", "attachment; filename*=" + content.getEncoding() + "'" + documentLanguage + "'" + filenameToHexString(fileName));

        ContentReader reader = fileFolderService.getReader(nodeRef);
        FileCopyUtils.copy(reader.getContentInputStream(), os);
    }

    private String filenameToHexString(String filename)
    {
        StringBuilder sb = new StringBuilder();

        int pos = filename.lastIndexOf(".");

        String extension = "";

        if (pos != -1)
        {
            extension = filename.substring(pos);
        }

        for (int i = 0; i < (pos == -1 ? filename.length() : pos); i++)
        {
            sb.append("%" + Integer.toHexString((int) filename.charAt(i)));
        }

        sb.append(extension);

        return sb.toString();
    }

    public long getSize(NodeRef nodeRef, Class<?> type, Type genericType, Annotation[] a, MediaType mt)
    {
        ContentData content = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
        return content.getSize();
    }
    
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] a, MediaType mt)
    {
        return NodeRef.class.isAssignableFrom(type);
    }
}
