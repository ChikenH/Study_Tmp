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
 * @see OmikujiFX.fxml
 * @version 1.00
 * @since 2018/02/22
 */
public class OmikujiFXMain extends Application {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// JavaFXの実行
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		try {
			// fxmlファイルの読み込み
			Parent root = FXMLLoader.load(getClass().getResource("OmikujiFX.fxml"));

			// Sceneの作成・登録
			Scene scene = new Scene(root);
			stage.setScene(scene);

			// 表示
			stage.show();
		} catch (Exception e) {
			System.out.println(e);
		} finally {}
	}
}
