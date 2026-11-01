package com.vendo.auto_search_service.port.auto_search.usecase;

import com.vendo.auto_search_service.application.auto_search.command.ProductPageRequest;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;

public interface AutoSearchProductUseCase {

    SearchResponseCommand findAll(String autoSearchId, ProductPageRequest pageRequest);

}
