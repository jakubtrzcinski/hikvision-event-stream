package io.trzcinski.hikvision;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jakub Trzcinski jakub@trzcinski.io
 * @since 23-09-2022
 */
@RequiredArgsConstructor
class HikvisionCameraAuthImpl implements HikvisionCameraAuth {

    private final String login;

    private final String password;
    @Override
    @SneakyThrows
    public String getCookies(String url) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("http://"+login+":"+password+"@"+url+"/ISAPI/Security/sessionLogin/capabilities?username=admin")
                .method("GET", null)
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8")
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", "language=en; _wnd_size_mode=4; sdMarkTab_1_0=0%3AsettingBasic; sdMarkTab_7_0=0%3AplanCapture; sdMarkTab_1_2=0%3AsecurityAuth; sdMarkTab_2_0=0%3AbasicTcpIp; sdMarkTab_3=0%3Avideo; sdMarkTab_6_0=0%3AeventMotion; sdMarkTab_1_4=1%3AuserOnlineUser; sdMarkTab_4=0%3Adisplay; sdMarkTab_2_1=4%3AadvancedQos; sdMarkMenu=1_0%3Asystem; szLastPageName=system%3Csetting; WebSession_c852853da7=5ec1e915497efdbd97aea80e701aa5623cd580a68fd8c86f74692ed2278891bc")
                .addHeader("If-Modified-Since", "0")
                .addHeader("Referer", "http://192.168.0.130/doc/page/login.asp")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36 OPR/90.0.4480.117")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();
        Response response = client.newCall(request).execute();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        var doc = dbf.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(response.body().bytes())));
        doc.getDocumentElement().normalize();

        var session = doc.getDocumentElement().getElementsByTagName("sessionID").item(0).getTextContent();
        var password = encodePwd(
                "jakub340A",
                doc.getDocumentElement().getElementsByTagName("challenge").item(0).getTextContent(),
                "admin",
                doc.getDocumentElement().getElementsByTagName("salt").item(0).getTextContent(),
                100
        );

        MediaType mediaType = MediaType.parse("application/xml");
        RequestBody body = RequestBody.create(mediaType, "<SessionLogin><userName>admin</userName><password>"+password+"</password><sessionID>"+session+"</sessionID><isSessionIDValidLongTerm>false</isSessionIDValidLongTerm><sessionIDVersion>2</sessionIDVersion></SessionLogin>");
        Request request2 = new Request.Builder()
                .url("http://192.168.0.130/ISAPI/Security/sessionLogin?timeStamp="+System.currentTimeMillis())
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8")
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("Connection", "keep-alive")
                .addHeader("If-Modified-Since", "0")
                .addHeader("Origin", "http://192.168.0.130")
                .addHeader("Referer", "http://192.168.0.130/doc/page/login.asp")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36 OPR/90.0.4480.117")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Content-Type", "application/xml")
                .build();
        Response response2 = client.newCall(request2).execute();
        var cookie = response2.headers("set-cookie").get(0);
        return cookie.substring(0, cookie.indexOf(";"));
    }

    public static String encodePwd(String password, String challenge, String userName, String salt, int iterate) throws NoSuchAlgorithmException {

        var i = sha256(userName + salt + password);
        i = sha256(i + challenge);

        for (var n = 2; iterate > n; n++) {
            i = sha256(i);
        }

        return i;
    }

    public static String sha256(final String base) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
