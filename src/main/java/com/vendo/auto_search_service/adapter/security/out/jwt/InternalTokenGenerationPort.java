package com.vendo.auto_search_service.adapter.security.out.jwt;

import com.vendo.core_lib.type.ServiceName;

public interface InternalTokenGenerationPort {

    String generate(ServiceName audience);

}
