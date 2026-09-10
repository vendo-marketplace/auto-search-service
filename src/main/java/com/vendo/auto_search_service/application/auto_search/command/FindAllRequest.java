package com.vendo.auto_search_service.application.auto_search.command;

import java.math.BigDecimal;

public record FindAllRequest(String categoryId, String address, BigDecimal price) {

    public static FindAllRequest from(String categoryId, String address, BigDecimal price) {
        return new FindAllRequest(categoryId, address, price);
    }

}
