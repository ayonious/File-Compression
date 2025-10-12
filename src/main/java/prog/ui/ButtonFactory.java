package prog.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Factory class for creating styled buttons with consistent appearance and behavior.
 */
public class ButtonFactory {

    /**
     * Creates a styled button with 3D appearance, hover effects, and press animations.
     *
     * @param text The button text
     * @param bgColor The background color
     * @param listener The action listener for button clicks
     * @return A configured JButton with styling and event handlers
     */
    public static JButton createStyledButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setPreferredSize(new Dimension(140, 40));
        button.addActionListener(listener);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Add hover and press effects
        addButtonEffects(button, bgColor);

        return button;
    }

    /**
     * Adds mouse hover and press effects to a button.
     *
     * @param button The button to add effects to
     * @param baseColor The button's base background color
     */
    private static void addButtonEffects(JButton button, Color baseColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });
    }
}
