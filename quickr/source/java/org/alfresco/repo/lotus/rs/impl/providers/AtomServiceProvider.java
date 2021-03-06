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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Service;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author Eugene Zheleznyakov
 */
@Produces( { "application/atom+xml", "application/atom+xml;type=feed", "application/json" })
@Consumes( { "application/atom+xml", "application/atom+xml;type=feed" })
@Provider
public class AtomServiceProvider implements MessageBodyWriter<Service>, MessageBodyReader<Service>
{
    private static final Abdera ATOM_ENGINE = new Abdera();

    private static final String WORKSPACE = "workspace";
    private static final String COLLECTION = "collection";

    private static final String TEMPLATE_NAME = "getLibraries";
    private static final String TEMPLATE_FILE_NAME = "getLibraries.ftl";

    private Template template = null;

    public void writeTo(Service service, Class<?> clazz, Type type, Annotation[] a, MediaType mt, MultivaluedMap<String, Object> headers, OutputStream os) throws IOException,
            WebApplicationException
    {
        Map<String, Object> freeMarkerMap = new HashMap<String, Object>();
        freeMarkerMap.put(WORKSPACE, service.getWorkspaces().get(0));
        freeMarkerMap.put(COLLECTION, service.getWorkspaces().get(0).getCollections().get(0));

        OutputStreamWriter osw = null;
        try
        {
            if (template == null)
            {
                template = new Template(TEMPLATE_NAME, new InputStreamReader(getClass().getResourceAsStream(TEMPLATE_FILE_NAME)), null, AtomConstants.TEMPLATE_ENCODING);
            }
            osw = new OutputStreamWriter(os);
            Environment env = template.createProcessingEnvironment(freeMarkerMap, osw);
            env.setOutputEncoding(AtomConstants.TEMPLATE_ENCODING);
            env.process();
        }
        catch (TemplateException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (osw != null)
            {
                osw.flush();
                osw.close();
            }
        }
    }

    public Service readFrom(Class<Service> clazz, Type t, Annotation[] a, MediaType mt, MultivaluedMap<String, String> headers, InputStream is) throws IOException,
            WebApplicationException
    {
        Document<Service> doc = ATOM_ENGINE.getParser().parse(is);
        return doc.getRoot();
    }

    public long getSize(Service arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4)
    {
        return -1;
    }
    
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] a, MediaType mt)
    {
        return Service.class.isAssignableFrom(type);
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] a, MediaType mt)
    {
        return Service.class.isAssignableFrom(type);
    }
}
