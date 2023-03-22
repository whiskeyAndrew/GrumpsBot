package com.grumps.GrumpsWeb.entity;

import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@NoArgsConstructor
public class Roles implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return "ADMIN";
    }
}
