package com.ringme.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {
    @Bean
    public DefaultKaptcha captchaProducer() {
        DefaultKaptcha captcha = new DefaultKaptcha();
        Properties props = new Properties();

        props.setProperty("kaptcha.border", "no");

        props.setProperty("kaptcha.textproducer.font.color", "magenta");
        props.setProperty("kaptcha.textproducer.font.size", "48");
        props.setProperty("kaptcha.textproducer.font.names", "Arial,Courier,Tahoma");

        props.setProperty("kaptcha.image.width", "250");
        props.setProperty("kaptcha.image.height", "80");

        props.setProperty("kaptcha.textproducer.char.length", "6");

        props.setProperty("kaptcha.textproducer.char.string", "ABCDEFGHJKLMNPQRSTUVWXYZ23456789");

        props.setProperty("kaptcha.textproducer.char.space", "6");

//        props.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise"); // Không nhiễu

//        props.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple"); // Ripple mềm mại

        props.setProperty("kaptcha.background.impl", "com.google.code.kaptcha.impl.DefaultBackground");
        props.setProperty("kaptcha.background.clear.from", "255,255,255"); // White
        props.setProperty("kaptcha.background.clear.to", "255,255,255");   // White

        Config config = new Config(props);
        captcha.setConfig(config);
        return captcha;
    }
}
