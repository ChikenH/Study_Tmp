/**
 * 
 */
package omikuji;

import java.util.Random;

/**
 * @author chick
 * @see MainController.java
 * @version 1.00
 * @since 2018/02/22
 */
public class RandamInt {
	private String result = "";

	public String getOmikuji() {
		// 乱数
		Random rnd = new Random();
		int opt = rnd.nextInt(7);

		// 大吉、中吉、小吉、吉、末吉、凶、大凶
		if (opt == 0) {
			result = "大吉";
		} else if (opt == 1) {
			result = "中吉";
		} else if (opt == 2) {
			result = "小吉";
		} else if (opt == 3) {
			result = "吉";
		} else if (opt == 4) {
			result = "末吉";
		} else if (opt == 5) {
			result = "凶";
		} else {
			result = "大凶";
		}

		return result;
	}
}
