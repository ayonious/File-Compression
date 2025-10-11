package prog;

import javax.swing.*;
import java.awt.*;

class PanelContainer {
    final JPanel totalGUI;
    final JPanel mainPanel;

    PanelContainer() {
        totalGUI = new JPanel();
        totalGUI.setLayout(new BorderLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        totalGUI.add(mainPanel, BorderLayout.CENTER);
        totalGUI.setOpaque(true);
    }
}
