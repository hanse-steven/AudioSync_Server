package be.isservers.be.audiosyncserver;

import be.isservers.be.audiosyncserver.convert.Music;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class AudiosyncserverApplication {

    private static ArrayList<Music> musicTab;

    public static void main(String[] args) {
        SpringApplication.run(AudiosyncserverApplication.class, args);

        musicTab = new ArrayList<>();
        File directory = new File(Music.PathToMusic);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().contains(".mp3"))
                    musicTab.add(new Music(file.getName()));
            }
            Collections.sort(musicTab);
        }
    }

    @RequestMapping("/")
    String home() {
        return new ToStringBuilder(this).append("musicTab",musicTab).toString();
    }



    @RequestMapping("/music/{hash}")
    public void downloadMusic(HttpServletResponse response,@PathVariable String hash) throws IOException {
            String filename = hash;

            File file = new File(filename);
            InputStream inputStream = new FileInputStream(file);

            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.setContentLength((int) file.length());

            FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

}
