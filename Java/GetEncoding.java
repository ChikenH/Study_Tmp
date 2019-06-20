import java.nio.charset.Charset;

public class Main {

    // プラットフォームのデフォルトの文字コードを知る
    public static void main(String[] args) {

        /** public static String getProperty​(String key)
         指定されたキーによって示されるシステム・プロパティを取得 */
        String localEncoding = System.getProperty("file.encoding");
        System.out.println(localEncoding);

        /** public static Charset defaultCharset()
         このJava仮想マシンのデフォルトの文字セットを返す */
        String defCharset = Charset.defaultCharset().displayName();
        System.out.println(defCharset);
    }
}
