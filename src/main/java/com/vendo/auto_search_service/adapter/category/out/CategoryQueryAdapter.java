package com.vendo.auto_search_service.adapter.category.out;

import com.vendo.auto_search_service.port.category.CategoryQueryPort;
import org.springframework.stereotype.Component;

@Component
public class CategoryQueryAdapter implements CategoryQueryPort {

    @Override
    public Object findById(String id) {
        // TODO Create category entity with minimal required fields (check if all are necessary)
        // TODO Retrieve category: if missing throw else return

        // TODO Add validation method in category entity that checks if category has desired type
        // TODO Category as entity itself should be in domain package
        return null;
    }
}
