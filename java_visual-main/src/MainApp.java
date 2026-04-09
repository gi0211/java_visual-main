import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

    // Karakter sebagai object (integrasi OOP dari Character.java)
    private Character steve;
    private Character zombie;

    // Components UI
    private ProgressBar playerHealthBar;
    private Label playerHealthText;
    private ProgressBar enemyHealthBar;
    private Label enemyHealthText;
    private Label statusLabel;
    private Label playerLevelLabel;
    private Label enemyLevelLabel;
    private Button fightBtn;

    private void updateHealthDisplays() {
        // Update Steve
        double playerProg = (double) steve.getHealth() / steve.getMaxHealth(); // asumsi tambah getMaxHealth di Character
        playerHealthBar.setProgress(playerProg);
        playerHealthText.setText("Health: " + steve.getHealth() + "/" + steve.getMaxHealth() + " ❤️");
        playerHealthBar.setStyle(steve.getHealth() <= 8 ? "-fx-accent: red;" :
                steve.getHealth() <= 12 ? "-fx-accent: orange;" : "-fx-accent: lime;");

        // Update Zombie
        double enemyProg = (double) zombie.getHealth() / zombie.getMaxHealth();
        enemyHealthBar.setProgress(enemyProg);
        enemyHealthText.setText("Health: " + zombie.getHealth() + "/" + zombie.getMaxHealth() + " 💀");
        enemyHealthBar.setStyle(zombie.getHealth() <= 12 ? "-fx-accent: red;" :
                zombie.getHealth() <= 18 ? "-fx-accent: orange;" : "-fx-accent: lime;");

        // Cek winner
        if (steve.getHealth() <= 0) {
            statusLabel.setText("Zombie Wins! Steve kalah 😭");
            zombie.levelUp(); // Zombie naik level
            enemyLevelLabel.setText("Level: " + zombie.getLevel());
            fightBtn.setDisable(true);
        } else if (zombie.getHealth() <= 0) {
            statusLabel.setText("Steve Wins! Level up! 🎉");
            steve.levelUp();
            playerLevelLabel.setText("Level: " + steve.getLevel());
            fightBtn.setDisable(true);
        }
    }

    private void playDamageSound() {
        AudioClip damageSound = new AudioClip(getClass().getResource("/sounds/beep-10.mp3").toExternalForm());
        damageSound.play();
    }

    private void animateCriticalHit(ImageView target) {
        // Efek glow + scale pas critical
        Glow glow = new Glow(0.8);
        target.setEffect(glow);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), target);
        scale.setToX(1.15);
        scale.setToY(1.15);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();

        FadeTransition fade = new FadeTransition(Duration.millis(800), target);
        fade.setFromValue(1.0);
        fade.setToValue(0.6);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);
        fade.play();

        // Reset effect setelah animasi
        scale.setOnFinished(e -> target.setEffect(new DropShadow(15, Color.BLACK)));
    }

    @Override
    public void start(Stage primaryStage) {
        // Init karakter pake class Character (extend sedikit buat maxHealth)
        steve = new Character("Steve", 20, 1); // health awal = max
        zombie = new Character("Zombie", 30, 1);

        // Tambah maxHealth (kita extend logic di sini atau modifikasi class nanti)
        // Untuk sekarang, kita simpan manual atau tambah field di Character.java nanti

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(40);
        grid.setVgap(20);
        grid.setPadding(new Insets(30));

        // MenuBar
        MenuBar menuBar = new MenuBar();
        Menu gameMenu = new Menu("Game");

        MenuItem resetItem = new MenuItem("Reset Battle");
        resetItem.setOnAction(e -> {
            steve = new Character("Steve", 20, 1);
            zombie = new Character("Zombie", 30, 1);
            statusLabel.setText("Fight!");
            statusLabel.setTextFill(Color.BLACK);
            fightBtn.setDisable(false);
            updateHealthDisplays();
            playerLevelLabel.setText("Level: 1");
            enemyLevelLabel.setText("Level: 1");
        });

        MenuItem healSteveItem = new MenuItem("Heal Steve (+10)");
        healSteveItem.setOnAction(e -> {
            steve.heal(10); // tambah method heal di Character nanti
            updateHealthDisplays();
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());

        gameMenu.getItems().addAll(resetItem, healSteveItem, new SeparatorMenuItem(), exitItem);
        menuBar.getMenus().add(gameMenu);

        // Gambar & VBox untuk Steve & Zombie
        ImageView steveView = new ImageView(new Image(getClass().getResource("/images/steve.jpg").toExternalForm()));
        steveView.setFitHeight(280);
        steveView.setPreserveRatio(true);
        steveView.setEffect(new DropShadow(15, Color.BLACK));

        ImageView zombieView = new ImageView(new Image(getClass().getResource("/images/zombie.jpg").toExternalForm()));
        zombieView.setFitHeight(280);
        zombieView.setPreserveRatio(true);
        zombieView.setEffect(new DropShadow(15, Color.BLACK));

        VBox steveBox = new VBox(10, new Label("Steve 🧑"), steveView,
                playerLevelLabel = new Label("Level: " + steve.getLevel()),
                playerHealthBar = new ProgressBar(1.0),
                playerHealthText = new Label("Health: 20/20 ❤️"));
        steveBox.setAlignment(Pos.CENTER);

        VBox zombieBox = new VBox(10, new Label("Zombie 🧟"), zombieView,
                enemyLevelLabel = new Label("Level: " + zombie.getLevel()),
                enemyHealthBar = new ProgressBar(1.0),
                enemyHealthText = new Label("Health: 30/30 💀"));
        zombieBox.setAlignment(Pos.CENTER);

        grid.add(steveBox, 0, 0);
        grid.add(zombieBox, 2, 0);

        statusLabel = new Label("Fight!");
        statusLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        fightBtn = new Button("Fight! ⚔️");
        fightBtn.setStyle("-fx-font-size: 18px; -fx-padding: 10 30;");

        grid.add(statusLabel, 1, 1, 1, 1);
        grid.add(fightBtn, 1, 2);

        VBox root = new VBox(menuBar, grid);
        root.setSpacing(10);

        // Event Fight
        fightBtn.setOnAction(e -> {
            if (steve.getHealth() <= 0 || zombie.getHealth() <= 0) return;

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Konfirmasi Serangan");
            confirm.setHeaderText("Yakin mau fight?");
            confirm.setContentText("Steve dan Zombie bakal saling serang! Critical hit mungkin terjadi.");

            confirm.showAndWait().ifPresent(response -> {
                if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    double chance = Math.random();
                    int damageToZombie = 5;
                    boolean isCritical = false;

                    if (chance > 0.8) { // 20% critical
                        damageToZombie = 10;
                        statusLabel.setText("CRITICAL HIT! 🔥 Steve nge-damage besar!");
                        statusLabel.setTextFill(Color.ORANGE);
                        isCritical = true;
                        animateCriticalHit(zombieView);
                    } else {
                        statusLabel.setText("Steve menyerang Zombie!");
                        statusLabel.setTextFill(Color.BLACK);
                    }

                    zombie.takeDamage(damageToZombie);
                    playDamageSound();

                    // Zombie balas jika masih hidup
                    if (zombie.getHealth() > 0) {
                        int damageToSteve = 4 + (zombie.getLevel() - 1); // tambah damage per level zombie
                        steve.takeDamage(damageToSteve);
                        playDamageSound();
                        statusLabel.setText(statusLabel.getText() + " Zombie balas!");
                        animateCriticalHit(steveView); // efek kena damage
                    }

                    updateHealthDisplays();
                }
            });
        });

        Scene scene = new Scene(root, 950, 750);
        primaryStage.setTitle("Minecraft Character Battle - Level Up Edition");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}