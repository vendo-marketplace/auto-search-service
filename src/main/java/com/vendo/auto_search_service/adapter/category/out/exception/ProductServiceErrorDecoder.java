package com.vendo.auto_search_service.adapter.category.out.exception;

import com.vendo.auto_search_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.core_lib.type.ServiceName;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class ProductServiceErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        if (status == HttpStatus.NOT_FOUND) {
            return new CategoryNotFoundException("Category not found.");
        }

        if (status.is5xxServerError()) {
            return new ProductServiceUnavailableException(ServiceName.PRODUCT_SERVICE + " is unavailable.");
        }

        log.error(response.toString());
        return new IllegalArgumentException("Unhandled product exception.");
    }

}
