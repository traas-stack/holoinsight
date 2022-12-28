/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.auth;

import io.holoinsight.server.common.auth.ApikeyAuthService;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.common.web.InternalWebApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>created at 2022/10/12
 *
 * @author sw1136562366
 */
@RestController
@InternalWebApi
public class ApikeyWebController {
    @Autowired
    private ApikeyAuthService apikeyAuthService;

    /**
     * <p>checkApikey.</p>
     *
     * @param apikey
     */
    @Deprecated
    @GetMapping({"/api/apikey/check", "/internal/api/gateway/apikey/check"})
    public AuthInfo checkApikey(@RequestParam String apikey) {
        return apikeyAuthService.getFromCache(apikey);
    }

}
