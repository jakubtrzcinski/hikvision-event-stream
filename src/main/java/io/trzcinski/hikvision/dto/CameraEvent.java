package io.trzcinski.hikvision.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Jakub Trzcinski jakub@trzcinski.io
 * @since 23-09-2022
 */
@Getter
@RequiredArgsConstructor
@ToString
public class CameraEvent {
    private final String ipAddress;
    private final String portNo;
    private final String protocol;
    private final String macAddress;
    private final String channelID;
    private final String dateTime;
    private final String activePostCount;
    private final String eventType;
    private final String eventState;
    private final String eventDescription;
}
