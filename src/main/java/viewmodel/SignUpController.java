package viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class SignUpController {
    public void createNewAccount(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Signed up!");
        alert.showAndWait();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
