module adv.headsoccer_project {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.apache.logging.log4j;
    requires org.json;

    opens adv.headsoccer_project to javafx.fxml;
    exports adv.headsoccer_project;

}