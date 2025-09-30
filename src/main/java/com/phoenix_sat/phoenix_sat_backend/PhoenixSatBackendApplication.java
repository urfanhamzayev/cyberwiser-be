package com.phoenix_sat.phoenix_sat_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PhoenixSatBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoenixSatBackendApplication.class, args);
    }
//   password 123456789
//    $2a$08$aZqWnAJRaKmNeb8cbGQsj.HC6lgvk.BCWKZd7vGJXW4bhkRdSFXuK
//{
//    "email": "urfanhamzayev@gmail.com",
//    "password": "123456789"
//}
}

// TODO:

//   BEFORE RUN APP THIS SHOULD MOVE TO APPLICATION.YML
//        #cloud:
//        #  aws:
//        #    region:
//        #      static: eu-central-1
//        #      auto: false
//        #    stack:
//        #      auto: false
//        #    credentials:
//        #      access-key: AKIAQEIP3QLAS6EYA2EI
//        #      secret-key: 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
//        #
//        #aws:
//        #  s3:
//        #    bucket-name: dev-logeaze
//        #    region: eu-central-1
//        #    access-key: AKIAQEIP3QLAS6EYA2EI
//        #    secret-key: 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
