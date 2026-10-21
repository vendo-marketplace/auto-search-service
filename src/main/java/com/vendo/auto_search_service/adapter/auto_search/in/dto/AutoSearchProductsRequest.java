package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.application.auto_search.command.ProductPageRequest;
import com.vendo.auto_search_service.application.search.command.ProductSortField;
import com.vendo.auto_search_service.application.search.command.SortCommand;
import com.vendo.auto_search_service.application.search.command.SortDirection;
import com.vendo.core_lib.utils.ObjectUtils;
import jakarta.validation.constraints.Min;

public record AutoSearchProductsRequest(

        @Min(value = 1, message = "Page must not be less than one.")
        Integer page,

        @Min(value = 1, message = "Page size must not be less than one.")
        Integer size,

        ProductSortField sortBy,
        SortDirection direction

) {

    public ProductPageRequest toPageRequest() {
        SortCommand sort = ObjectUtils.isAnyNonNull(sortBy, direction) ? new SortCommand(sortBy, direction) : null;
        return new ProductPageRequest(page, size, sort);
    }

}
