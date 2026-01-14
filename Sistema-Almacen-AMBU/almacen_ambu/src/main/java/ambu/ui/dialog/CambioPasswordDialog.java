package ambu.ui.dialog;

import ambu.ui.componentes.CustomButton;
import ambu.ui.componentes.CustomPasswordField;
import ambu.process.LoginService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CambioPasswordDialog extends JDialog {

    private CustomPasswordField passField;
    private CustomPasswordField confirmPassField;
    private int initialX;
    private int initialY;
    
    //  Variable para guardar el ID del usuario a modificar
    private final long usuarioIdTarget; 

    //  Agregamos 'long usuarioId' al constructor
    public CambioPasswordDialog(Frame owner, LoginService loginService, long usuarioId) {
        super(owner, "Cambio de Contraseña", true);
        this.usuarioIdTarget = usuarioId; // Guardamos el ID recibido

        // Estilo del diálogo
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); 
        setSize(500, 300);
        setLocationRelativeTo(owner);
        
        // Panel principal con fondo redondeado
        JPanel roundedPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fill(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        roundedPanel.setOpaque(false);
        roundedPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(roundedPanel);

        // Movimiento de la ventana
        roundedPanel.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                initialX = e.getX(); initialY = e.getY();
            }
        });
        roundedPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - initialX, e.getYOnScreen() - initialY);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo Nueva Contraseña
        JLabel passLabel = new JLabel("Nueva Contraseña:");
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0;
        roundedPanel.add(passLabel, gbc);

        passField = new CustomPasswordField(20);
        passField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        roundedPanel.add(passField, gbc);

        // Campo Confirmar
        JLabel confirmPassLabel = new JLabel("Confirmar Contraseña:");
        confirmPassLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1;
        roundedPanel.add(confirmPassLabel, gbc);

        confirmPassField = new CustomPasswordField(20);
        confirmPassField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        roundedPanel.add(confirmPassField, gbc);

        // Botón Cambiar
        CustomButton changePassButton = new CustomButton("Cambiar Contraseña");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        roundedPanel.add(changePassButton, gbc);

        // Botón Cancelar
        CustomButton cancelButton = new CustomButton("Cancelar");
        gbc.gridy = 3;
        roundedPanel.add(cancelButton, gbc);
        cancelButton.addActionListener(e -> dispose());

        // Lógica del botón Cambiar
        changePassButton.addActionListener(e -> {
            String newPassword = new String(passField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Usamos 'usuarioIdTarget' en lugar de 0
            boolean success = loginService.cambiarContraseñaUsuario(usuarioIdTarget, newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Contraseña cambiada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cambiar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
