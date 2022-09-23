# Hikvision camera event listener

This little piece of bad written code allows you to listen for motion events from your Hikvision cameras.

# Usage
This bad boy will print all events from camera. Please be aware most of those events ate heartbeat and can be ignored, 
what you really looking for is event with `eventState=active`
```java
new HikvisionCamera("192.168.0.276", new HikvisionCameraAuthImpl(
        "login", "password"
)).listen(event->{
    System.out.println(event);
});
```

![](https://github.com/jakubtrzcinski/hikvision-event-stream/blob/master/Screenshot%202022-09-23%20at%2019.41.39.png)

# Tested cameras
* Hikvision HWI-B121H(C)
