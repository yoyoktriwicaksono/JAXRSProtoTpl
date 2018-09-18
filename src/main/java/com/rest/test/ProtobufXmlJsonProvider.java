package com.rest.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.wink.common.annotations.Scope;

import com.google.common.base.Charsets;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.XmlFormat;

/**
 * Created by Yoyok_T on 14/09/2018.
 */
@Scope(Scope.ScopeType.SINGLETON)
@Provider
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ProtobufXmlJsonProvider<T extends Message> implements MessageBodyWriter<T>, MessageBodyReader<T> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isReadableOrWriteable(type, mediaType);
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> arg4httpHeaders,
                      InputStream entityStream) throws IOException {
        String data = IOUtils.toString(entityStream, Charsets.UTF_8.name());

        Message.Builder builder;
        try {
            Method newBuilder = type.getDeclaredMethod("newBuilder");
            builder = (Message.Builder) newBuilder.invoke(null);
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessError("Failed to find builder method on Protobuf message object.");
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalAccessError("Failed to invoke builder method on Protobuf message object.");
        }

        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            JsonFormat.merge(data, builder);
        } else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
            XmlFormat.merge(data, builder);
        }
        return (T) builder.build();
    }

    @Override
    public long getSize(T message, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        long size;
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            size = getMessageAsJSON(message).length;
        } else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
            size = getMessageAsXML(message).length;
        } else {
            size = 0;
        }
        return size;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isReadableOrWriteable(type, mediaType);
    }

    @Override
    public void writeTo(T message, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            entityStream.write(getMessageAsJSON(message));
        } else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
            entityStream.write(getMessageAsXML(message));
        }
    }

    public static boolean isReadableOrWriteable(Class<?> type, MediaType mediaType) {
        final boolean assignableFromGeneratedMessage = GeneratedMessage.class.isAssignableFrom(type);
        return assignableFromGeneratedMessage;
    }

    private byte[] getMessageAsXML(Message generatedMessage) {
        return XmlFormat.printToString(generatedMessage).getBytes(Charsets.UTF_8);
    }

    private byte[] getMessageAsJSON(Message generatedMessage) {
        return JsonFormat.printToString(generatedMessage).getBytes(Charsets.UTF_8);
    }

}
