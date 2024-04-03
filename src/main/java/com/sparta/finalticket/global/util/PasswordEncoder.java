package com.sparta.finalticket.global.util;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordEncoder {

    public String encode(String rawPassword){
       return DigestUtils.sha256Hex(rawPassword);
    }

    public boolean matches(String rawPassword , String hashedPassword){
        return hashedPassword.equals(DigestUtils.sha256Hex(rawPassword));
    }

}
