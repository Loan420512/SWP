//package com.evswap;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication(scanBasePackages = "com.evswap")
//public class SwpApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(SwpApplication.class, args);
//    }
//
//}

package com.evswap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.evswap")
@EnableScheduling

public class SwpApplication {
    public static void main(String[] args) {
        SpringApplication.run(SwpApplication.class, args);
    }
}

