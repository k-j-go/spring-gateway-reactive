package com.azunitech.search;

import com.azunitech.search.filters.FilterConfig;
import com.azunitech.search.web.WebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({WebConfig.class, FilterConfig.class})
public class Wire {
    private Object WebConfig;
}
