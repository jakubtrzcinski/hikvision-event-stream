package io.trzcinski.hikvision;

import io.trzcinski.hikvision.dto.CameraEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author Jakub Trzcinski jakub@trzcinski.io
 * @since 22-09-2022
 */
interface HikvisionEventListener {
    void listen(CameraEvent event);
}
