package io.github.easybill;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import java.nio.charset.StandardCharsets;

@QuarkusMain
public class Main {

    public static void main(String[] args) throws Exception {
        Main.printBannerText();
        Quarkus.run(args);
    }

    private static void printBannerText() throws Exception {
        try (var bannerStream = Main.class.getResourceAsStream("/banner.txt")) {
            if (bannerStream == null) {
                throw new Exception("could not read banner file");
            }

            System.out.println(
                new String(bannerStream.readAllBytes(), StandardCharsets.UTF_8)
            );
        }
    }
}
