/**
 * おみくじ
 */
package omikuji;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author chick
 * @since   1.00 :2018/02/22
 * @version 1.01 :2018/02/23
 */
public class OmikujiFXMain extends Application {

	public static void main(String[] args) {
		// JavaFXの実行
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Window Titleをセット
		stage.setTitle("Omikuji");

		try {
			// fxmlファイルの読み込み
			Parent root = FXMLLoader.load(getClass().getResource("OmikujiFX.fxml"));

			// Sceneの作成・登録
			Scene scene = new Scene(root);
			stage.setScene(scene);
			// 表示
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {}
	}
}
