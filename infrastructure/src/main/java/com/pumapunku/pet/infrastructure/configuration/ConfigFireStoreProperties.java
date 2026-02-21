package com.pumapunku.pet.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:firebase.properties")
public class ConfigFireStoreProperties
{

}
