import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) {
        Gui screen = new Gui();
        try{
            screen.setUpIntro(stage);
        }catch(Exception e){
            e.printStackTrace();
        }
        screen.setUpPlay();
//        stage.setTitle("Nine Men's Morris Project");
//        stage.setScene(screen.introScene);
        stage.setScene(screen.introScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}