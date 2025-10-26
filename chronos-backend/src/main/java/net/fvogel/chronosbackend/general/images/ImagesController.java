package net.fvogel.chronosbackend.general.images;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

@RestController
@RequestMapping("/api/images")
public class ImagesController {

    @Value("${app.image.path}")
    private String imagePath;

    @GetMapping(path = "/jumbotron.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getRandomJumbotronImage() throws IOException {
        String[] files = new File(imagePath).list((dir, name) -> name.toLowerCase().endsWith(".jpg"));
        assert files != null;
        Random random = new Random();
        String randomFile = files[random.nextInt(files.length)];
        Path path = Path.of(imagePath, randomFile);
        return Files.readAllBytes(path);
    }
}
