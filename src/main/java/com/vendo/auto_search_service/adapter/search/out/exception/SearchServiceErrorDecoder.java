package com.vendo.auto_search_service.adapter.search.out.exception;

import com.vendo.core_lib.types.ServiceName;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class SearchServiceErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        if (status.is5xxServerError()) {
            return new SearchServiceUnavailableException(ServiceName.SEARCH_SERVICE + " is unavailable.");
        }

        log.error(response.toString());
        return new IllegalArgumentException("Unhandled search exception.");
    }

}
