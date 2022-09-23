package io.trzcinski.hikvision;

import okhttp3.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * @author Jakub Trzcinski jakub@trzcinski.io
 * @since 22-09-2022
 */
class Main {
    public static void main(String[] args) throws Exception {
        new HikvisionCamera("192.168.0.276", new HikvisionCameraAuthImpl(
                "login", "password"
        )).listen(event->{
            System.out.println(event);
        });
    }

}
