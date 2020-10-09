package be.isservers.audiosyncserver;

import be.isservers.audiosyncserver.convert.ListingMusic;
import be.isservers.audiosyncserver.convert.Music;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import org.springframework.web.bind.annotation.*;

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
    public String home() {
        return new ToStringBuilder(this).append("musicTab",musicTab).toString();
    }

    @RequestMapping("/music/{hash}")
    public void downloadMusic(HttpServletResponse response,@PathVariable String hash) throws IOException {
        String filename = null;

        for (Music music : musicTab) {
            if (music.getHash().equals(hash)) filename = music.getName();
        }

        if (filename != null){
            File file = new File(filename);
            InputStream inputStream = new FileInputStream(file);

            response.setContentType("audio/mpeg");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.setContentLength((int) file.length());

            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
        else{
            String message = "BAD_HASH";

            response.setContentType("text/plain");
            response.setContentLength((int) message.length());

            FileCopyUtils.copy(message.getBytes(),response.getOutputStream());
        }
    }

    @RequestMapping(value="/synchronization", method= RequestMethod.POST)
    public String synchronization(@RequestBody String payload) {

        ArrayList<String> dataFromPhone = new Gson().fromJson(payload,new TypeToken<ArrayList<String>>(){}.getType());
        ArrayList<Music> dataFromPhoneFormated = new ArrayList<>();
        ListingMusic listingMusic = new ListingMusic();

        for (String o : dataFromPhone) {
            dataFromPhoneFormated.add(new Music().withSerializedString(o));
        }

        for (Music music : dataFromPhoneFormated) {
            if (searchInArray(music.getHash(),musicTab))
                listingMusic.getToKeep().add(music);
            else
                listingMusic.getToDelete().add(music);
        }

        for (Music music : musicTab) {
            if (!searchInArray(music.getHash(),dataFromPhoneFormated))
                listingMusic.getToDownload().add(music);
        }

        return new Gson().toJson(listingMusic);
    }

    private boolean searchInArray(String val,ArrayList<Music> data){
        for (Music music : data) {
            if (music.getHash().equals(val)) return true;
        }
        return false;
    }

}
