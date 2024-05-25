import javax.swing.SwingUtilities;
import ui.MainFrame;

public class TableEditorApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
