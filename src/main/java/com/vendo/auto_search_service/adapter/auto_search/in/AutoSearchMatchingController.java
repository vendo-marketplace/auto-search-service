package com.vendo.auto_search_service.adapter.auto_search.in;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auto-search")
public class AutoSearchMatchingController {

    @GetMapping("/{id}/products")
    public ResponseEntity<?> search(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

}
