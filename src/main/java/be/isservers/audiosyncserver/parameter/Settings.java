package be.isservers.audiosyncserver.parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @version Provient du Dossier Java LCR 2018-2019
 * @author Reusch Colin
 * @author Hanse Steven
 */
public class Settings {

    private static Logger logger = LoggerFactory.getLogger(Settings.class);

    private static String nameApplication = "audiosync_server";
    private static String nameFileSettings = System.getProperty("user.home") + System.getProperty("file.separator") + Settings.nameApplication + System.getProperty("file.separator") + "settings.properties";

    private static Properties getFile(){
        Properties prop = new Properties();
        InputStream input = null;
        try{
            input = new FileInputStream(nameFileSettings);
            prop.load(input);
            input.close();

            return prop;
        }
        catch (IOException e) {
            OutputStream output = null;
            try {
                new File(System.getProperty("user.home") + System.getProperty("file.separator") + Settings.nameApplication).mkdir();
                prop.setProperty("workdirectory", Settings.nameApplication);
                prop.setProperty("serverPort", "9090");

                output = new FileOutputStream(nameFileSettings);
                prop.store(output, null);
                output.close();

                logger.info("Save - settings.properties -  Succès");
            }
            catch (final IOException ex) {
                logger.error("Save - settings.properties -  Echec");
            }
            finally{
                return prop;
            }
        }
    }

    private static String getDirectory(){
        Properties prop = getFile();
        if(prop != null){
            String directory = prop.getProperty("workdirectory");
            String home = System.getProperty("user.home");
            String separator = System.getProperty("file.separator");

            if(!home.isEmpty() && !directory.isEmpty() && !separator.isEmpty())
                return home + separator + directory + separator;
            return null;
        }
        return null;
    }

    private static String getPropertyFile(String name){
        Properties prop = getFile();
        if(prop != null){
            String directory = Settings.getDirectory();
            String file = prop.getProperty(name);

            if(!directory.isEmpty() && !file.isEmpty())
                return directory + file;
            return null;
        }
        return null;
    }

    private static String getPropertyNumber(String name){
        Properties prop = getFile();
        if(prop != null){
            String file = prop.getProperty(name);

            if(!file.isEmpty())
                return file;
            return null;
        }
        return null;
    }



    public static String getWorkDirectory(){
        return Settings.getDirectory();
    }

    public static String getServerPort(){
        return Settings.getPropertyNumber("serverPort");
    }


}
