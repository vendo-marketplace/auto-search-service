package com.vendo.auto_search_service.port.auto_search;

public interface AutoSearchEventSenderPort {

    void sendMatching(String id, String email);

    void sendRequestReady(String id, String email);

}
