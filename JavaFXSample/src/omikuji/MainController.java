/**
 * 
 */
package omikuji;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import omikuji.RandamInt;

/**
 * @author chick
 * @see OmikujiFXMain.java, RandamInt.java
 * @version 1.00
 * @since 2018/02/22
 */
public class MainController implements Initializable {
	@FXML private Button button1;
	@FXML private Label label1;

	private RandamInt rmd = new RandamInt();

	@FXML
	public void onButtonClicked(ActionEvent event) {
		this.label1.setText(rmd.getOmikuji());
	}

	@Override
	public void initialize(URL url, ResourceBundle bundel) {
	}
}
