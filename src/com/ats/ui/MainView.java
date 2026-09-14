package com.ats.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class MainView {

    private BorderPane root;

    private Button chooseButton;
    private Button analyzeButton;
    private Label fileLabel;
    private Label statusLabel;
    private ProgressBar progressBar;

    private Label nameLabel;
    private Label gpaLabel;
    private Label experienceLabel;
    private Label skillsLabel;
    private Label scoreLabel;

    public MainView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        createUI();
    }

    private void createUI() {

        Label title = new Label("AI-POWERED ATS");
        title.setFont(Font.font(28));

        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        VBox uploadBox = new VBox(12);
        uploadBox.setPadding(new Insets(25));

        chooseButton = new Button("Chọn CV");
        analyzeButton = new Button("Phân tích CV");

        fileLabel = new Label("CV: chưa chọn");
        statusLabel = new Label("Trạng thái: Chưa phân tích");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(500);

        uploadBox.getChildren().addAll(
                chooseButton,
                fileLabel,
                analyzeButton,
                statusLabel,
                progressBar
        );

        VBox resultBox = new VBox(10);
        resultBox.setPadding(new Insets(25));

        Label resultTitle = new Label("KẾT QUẢ PHÂN TÍCH");
        resultTitle.setFont(Font.font(20));

        nameLabel = new Label("Ứng viên: ---");
        gpaLabel = new Label("GPA: ---");
        experienceLabel = new Label("Kinh nghiệm: ---");
        skillsLabel = new Label("Kỹ năng: ---");
        scoreLabel = new Label("Điểm phù hợp: ---");

        resultBox.getChildren().addAll(
                resultTitle,
                nameLabel,
                gpaLabel,
                experienceLabel,
                skillsLabel,
                scoreLabel
        );

        VBox center = new VBox(15, uploadBox, resultBox);
        root.setCenter(center);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getChooseButton() {
        return chooseButton;
    }

    public Button getAnalyzeButton() {
        return analyzeButton;
    }

    public Label getFileLabel() {
        return fileLabel;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getGpaLabel() {
        return gpaLabel;
    }

    public Label getExperienceLabel() {
        return experienceLabel;
    }

    public Label getSkillsLabel() {
        return skillsLabel;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }
}