package viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class HelpController {
    @FXML
    private Button backButton;

    @FXML
    private BorderPane borderPane;

    @FXML
    private StackPane loginBox;

    @FXML
    private TextFlow words;

    @FXML
    void backButtonAction(ActionEvent actionEvent) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/db_interface_gui.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void load(){
        Text paragraph = new Text("""
                                        How do you use connect? The data base is already 
                                        configured to connect.
                                        
                                        How to use the ChatGPT function? Make sure the data is already
                                        in the table view and press the button. 
                                        
                                        How to use insert? Put every single field into the textfields and 
                                        press insert.
                                        
                                        How to use query ID? Just enter the ID of the person and click on the 
                                        button. 
                                        
                                        How to use delete id? Enter the ID of the person and click 
                                        on the button. Or you can click on a record in the table. 
                                        
                                        How to use edit ID? Enter the ID of the person and click on the 
                                        button.
                                      """);
        paragraph.setFont(Font.font("Segoe UI", 14));
        words.getChildren().add(paragraph);
    }
}
