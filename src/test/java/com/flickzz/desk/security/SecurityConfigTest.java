package com.flickzz.desk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.CorsFilter;

class SecurityConfigTest {

    @Test
    void corsFilterAllowsOptionsPreflightForBpEndpoints() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();
        CorsFilter corsFilter = securityConfig.corsFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/bp/config/category/1");
        request.addHeader(HttpHeaders.ORIGIN, "http://localhost:4200");
        request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET");

        MockHttpServletResponse response = new MockHttpServletResponse();
        corsFilter.doFilter(request, response, (req, res) -> {
            // no-op; preflight should be handled by the filter itself
        });

        assertEquals(200, response.getStatus());
        assertNotNull(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
