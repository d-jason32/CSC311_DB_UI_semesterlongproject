package viewmodel;

import dao.DbConnectivityClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.opencsv.CSVWriter;
import java.nio.file.*;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DB_GUI_Controller implements Initializable {
    @FXML
    private TextArea chatGPTArea;
    @FXML
    private TextField statusBox;
    @FXML
    TextField first_name, last_name, department, email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    private ComboBox<Major> comboBox;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    Pattern firstNamePattern = Pattern.compile("^[a-zA-Z]{2,25}$");
    Pattern lastNamePattern = Pattern.compile("^[a-zA-Z]{2,25}$");
    Pattern departmentPattern = Pattern.compile("^[a-zA-Z]{2,25}$");
    Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]{1,25}@farmingdale\\.edu$");

    boolean firstNameBool, lastNameBool, departmentBool, emailBool = false;
    boolean canAdd = false;
    String report;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        comboBox.getItems().addAll(Major.values());
        TextField[] fields = { first_name, last_name, department, email};

        /*
        After each text field is clicked on, every
        field will be checked if it is correct.
         */
        for (TextField field : fields) {
            field.setOnMouseClicked(e -> {
                checkIfCorrect();
                setAddButton();
            });
        }
        // Disables the edit button unless the table view is clicked on.
        editButton.disableProperty().bind(
                tv.getSelectionModel().selectedItemProperty().isNull());

        // Disables the delete button unless the table view is clicked on.
        deleteButton.disableProperty().bind(
                tv.getSelectionModel().selectedItemProperty().isNull());

    }

    /**
     * If every field is valid, the canAdd is set to true.
     * If not, it is set to false.
     */
    void setAddButton(){
        if (firstNameBool == true
                && lastNameBool == true
                && departmentBool == true
                && emailBool == true){
            canAdd = true;
            addBtn.setStyle("-fx-background-color:#6dff7c; -fx-border-width:2px;");
        }
        else {
            canAdd = false;
            addBtn.setStyle("-fx-background-color:red; -fx-border-width:2px;");
            statusBox.setText("Fill in missing info!");
        }
    }

    /**
     * Checks if every text field is valid.
     */
    void checkIfCorrect(){
        if (firstNamePattern.matcher(first_name.getText()).matches()){
            first_name.setStyle("-fx-border-color:#6dff7c; -fx-border-width:2px;");
            firstNameBool = true;
        }
        else {
            first_name.setStyle("-fx-border-color:red; -fx-border-width:2px;");
            firstNameBool = false;
        }
        if (lastNamePattern.matcher(last_name.getText()).matches()){
            last_name.setStyle("-fx-border-color:#6dff7c; -fx-border-width:2px;");
            lastNameBool = true;
        }
        else {
            last_name.setStyle("-fx-border-color:red; -fx-border-width:2px;");
            lastNameBool = false;
        }
        if (departmentPattern.matcher(department.getText()).matches()){
            department.setStyle("-fx-border-color:#6dff7c; -fx-border-width:2px;");
            departmentBool = true;
        }
        else {
            department.setStyle("-fx-border-color:red; -fx-border-width:2px;");
            departmentBool = false;
        }
        if (emailPattern.matcher(email.getText()).matches()){
            email.setStyle("-fx-border-color:#6dff7c; -fx-border-width:2px;");
            emailBool = true;
        }
        else {
            email.setStyle("-fx-border-color:red; -fx-border-width:2px;");
            emailBool = false;
        }
    }


    @FXML
    protected void addNewRecord() {
        if (canAdd){
            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    comboBox.getValue().toString(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
            statusBox.setText("Added Record!");
        }


    }

    protected void addNewRecord(String firstName, String lastName, String department, String major, String email) {

        Person p = new Person(firstName, lastName, department,
                major, email, "");
        cnUtil.insertUser(p);
        cnUtil.retrieveId(p);
        p.setId(cnUtil.retrieveId(p));
        data.add(p);
        clearForm();
        statusBox.setText("Added Record!");

    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        comboBox.setValue(null);
        email.setText("");
        imageURL.setText("");
        statusBox.setText("Cleared form!");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                comboBox.getValue().toString(), email.getText(),  imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
        statusBox.setText("Edited Record!");


    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
        statusBox.setText("Deleted Record!");
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();

        if (p != null){
            first_name.setText(p.getFirstName());
            last_name.setText(p.getLastName());
            department.setText(p.getDepartment());
            comboBox.setValue(Major.valueOf(p.getMajor()));
            email.setText(p.getEmail());
            imageURL.setText(p.getImageURL());
        }

    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    private static enum Major {Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }



    @FXML
    void importCSVButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                String firstName;
                String lastName;
                String department;
                String major;
                String email;
                while ((line = br.readLine()) != null) {
                    String[] section = line.split(",");

                    firstName = section[0].trim();
                    lastName = section[1].trim();
                    department = section[2].trim();
                    major = section[3].trim();
                    email = section[4].trim();

                    addNewRecord(firstName, lastName, department, major, email);
                    Person p = new Person(firstName, lastName, department,
                            major, email, " ");
                    cnUtil.insertUser(p);
                    cnUtil.retrieveId(p);
                    p.setId(cnUtil.retrieveId(p));
                    data.add(p);
                    clearForm();
                    statusBox.setText("Added Record!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        statusBox.setText("Added CSV!");
    }


    @FXML
    void exportCSVButton(ActionEvent event) throws IOException {

    }

    public void chatGPT(){
        // The message sent to the OpenAI API model.
        String messageToChatbot = "Summarize this table" + data.toString();

        System.out.println(messageToChatbot);
        String apiKey = "";

        var openAI = SimpleOpenAI.builder()
                .apiKey(apiKey)
                .build();

        var chatRequest = ChatRequest.builder()
                .model("gpt-4o")
                .message(ChatMessage.SystemMessage.of("Please check your answer."))
                .message(ChatMessage.UserMessage.of(messageToChatbot))
                .temperature(0.0)
                .maxCompletionTokens(300)
                .build();

        var futureChat = openAI.chatCompletions().create(chatRequest);
        var chatResponse = futureChat.join();
        report = chatResponse.firstContent();
        chatGPTArea.setText(chatResponse.firstContent());
    }

    @FXML
    void runChatGPT(ActionEvent event) {
        chatGPT();
    }

    @FXML
    void helpButtonAction(ActionEvent actionEvent) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/help.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses itext7 api to generate a pdf file into the root directory.
     * @param event
     * @throws IOException
     */
    @FXML
    void pdfGeneration(ActionEvent event) throws IOException {
        try (PdfWriter w = new PdfWriter("generated_file.pdf");
             PdfDocument pdf = new PdfDocument(w);
             Document doc = new Document(pdf)) {
            chatGPT();
            doc.add(new Paragraph(report));
        }
    }

}