package io.trzcinski.hikvision;

import io.trzcinski.hikvision.dto.CameraEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Jakub Trzcinski jakub@trzcinski.io
 * @since 23-09-2022
 */
@Getter
@RequiredArgsConstructor
class HikvisionCamera {

    private final String url;

    private final HikvisionCameraAuth auth;

    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    @SneakyThrows
    public void listen(HikvisionEventListener listener){
        while(true) {
            try {
                URL website = new URL("http://" + this.url + "/ISAPI/Event/notification/alertStream");
                URLConnection connection = website.openConnection();
                connection.addRequestProperty("Cookie", this.auth.getCookies(this.url));

                BufferedReader in;
                String inputLine;
                var buffer = "";
                for (in = new BufferedReader(new InputStreamReader(connection.getInputStream())); (inputLine = in.readLine()) != null; ) {
                    if (inputLine.startsWith("<")) {
                        buffer += inputLine;
                    }
                    if (inputLine.startsWith("</EventNotificationAlert>")) {
                        listener.listen(map(buffer));
                        buffer = "";
                    }
                }
                in.close();
            } catch (Exception ex) {

            }
        }
    }

    @SneakyThrows
    private CameraEvent map(String buffer) {
        var doc = dbf.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(buffer.getBytes())));
        doc.getDocumentElement().normalize();

        return new CameraEvent(
                doc.getDocumentElement().getElementsByTagName("ipAddress").item(0).getTextContent(),
                doc.getDocumentElement().getElementsByTagName("portNo").item(0).getTextContent(),
                doc.getDocumentElement().getElementsByTagName("protocol").item(0).getTextContent(),
                doc.getDocumentElement().getElementsByTagName("macAddress").item(0).getTextContent(),
                doc.getDocumentElement().getElementsByTagName("channelID").item(0).getTextContent(),
                doc.getDocumentElement().getElementsByTagName("dateTime").item(0).getTextContent(),
                doc.getDocumentElement().getElementsByTagName("activePostCount").item(0).getTextContent(),
                doc.getDocumentElement().getElementsByTagName("eventType").item(0).getTextContent(),
                doc.getDocumentElement().getElementsByTagName("eventState").item(0).getTextContent(),
                doc.getDocumentElement().getElementsByTagName("eventDescription").item(0).getTextContent()
        );
    }
}
