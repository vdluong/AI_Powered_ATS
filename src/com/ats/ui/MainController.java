package com.ats.ui;

import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainController implements ProgressObserver {

    private MainView view;
    private File selectedFile;
    private List<ProgressObserver> observers = new ArrayList<>();

    public MainController(MainView view) {
        this.view = view;

        observers.add(this);

        view.getChooseButton().setOnAction(e -> chooseCV());
        view.getAnalyzeButton().setOnAction(e -> analyzeCV());
    }

    private void chooseCV() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn CV");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        selectedFile = chooser.showOpenDialog(
                view.getRoot().getScene().getWindow()
        );

        if (selectedFile != null) {
            view.getFileLabel().setText(
                    "CV: " + selectedFile.getName()
            );

            view.getStatusLabel().setText("Đã chọn CV");
        }
    }

    private void analyzeCV() {

        if (selectedFile == null) {
            view.getStatusLabel().setText(
                    "Vui lòng chọn CV trước!"
            );
            return;
        }

        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {

                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(30);
                    notifyObservers(i / 100.0);
                }

                return null;
            }
        };

        task.setOnRunning(e ->
                view.getStatusLabel().setText(
                        "Đang phân tích CV..."
                )
        );

        task.setOnSucceeded(e -> {

            view.getStatusLabel().setText(
                    "Phân tích hoàn tất"
            );

            // Dữ liệu mẫu
            view.getNameLabel().setText(
                    "Ứng viên: Nguyễn Văn A"
            );

            view.getGpaLabel().setText(
                    "GPA: 3.5"
            );

            view.getExperienceLabel().setText(
                    "Kinh nghiệm: 2 năm"
            );

            view.getSkillsLabel().setText(
                    "Kỹ năng: Java, SQL, Python"
            );

            view.getScoreLabel().setText(
                    "Điểm phù hợp: 85%"
            );
        });

        new Thread(task).start();
    }

    private void notifyObservers(double progress) {

        for (ProgressObserver observer : observers) {
            observer.update(progress);
        }
    }

    @Override
    public void update(double progress) {

        view.getProgressBar().setProgress(progress);
    }
}
