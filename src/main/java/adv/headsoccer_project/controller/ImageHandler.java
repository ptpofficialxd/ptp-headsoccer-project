package adv.headsoccer_project.controller;

import java.io.InputStream;

import adv.headsoccer_project.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageHandler {
    private static Logger logger = LogManager.getLogger(ImageHandler.class);

    public static String getImage(String URL) {
        if (Launcher.class.getResource(URL) == null) {
            logger.info(String.format("Image not found, Requested: %s", URL));
            return Launcher.class.getResource("/assets/img/blank.png").toExternalForm();
        }
        return Launcher.class.getResource(URL).toExternalForm();
    }

    public static InputStream getImageAsStream(String URL) {
        if (Launcher.class.getResource(URL).toExternalForm() == null) {
            logger.info(String.format("Image not found, Requested: %s", URL));
            return Launcher.class.getResourceAsStream("/assets/img/blank.png");
        }
        return Launcher.class.getResourceAsStream(URL);
    }
}