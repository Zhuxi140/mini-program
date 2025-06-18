package com.zhuxi.utils.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( prefix = "bcrypt")
public class BCryptProperties {

    private int strength;

    public BCryptProperties(int strength) {
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
