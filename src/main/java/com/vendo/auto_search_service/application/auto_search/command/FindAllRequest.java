package com.vendo.auto_search_service.application.auto_search.command;

import com.vendo.auto_search_service.shared.Address;

import java.math.BigDecimal;

public record FindAllRequest(String categoryId, Address address, BigDecimal price) {

    public static FindAllRequest from(String categoryId, Address address, BigDecimal price) {
        return new FindAllRequest(categoryId, address, price);
    }

}
