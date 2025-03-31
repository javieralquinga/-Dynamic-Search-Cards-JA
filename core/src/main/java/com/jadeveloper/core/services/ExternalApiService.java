package com.jadeveloper.core.services;

public interface ExternalApiService {
    String getEndpointUrl();
    int getTimeout();
    String fetchData();
}
