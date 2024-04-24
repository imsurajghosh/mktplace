package com.supaki.mktplace;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


@Configuration
@EnableAsync
public class MarketPlaceConfig {

    @Bean
    public ModelMapper modelMapper()
    {
        return new ModelMapper();
    }

}
