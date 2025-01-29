import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Serveur");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("En attente d'une image...", SwingConstants.CENTER);
        frame.add(label, BorderLayout.NORTH);

        JLabel imageLabel = new JLabel();
        frame.add(imageLabel, BorderLayout.CENTER);

        frame.setVisible(true);

        new Thread(() -> {
            try {
                receiveImage(imageLabel, label);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Erreur du serveur : " + e.getMessage());
            }
        }).start();
    }

    private static void receiveImage(JLabel imageLabel, JLabel statusLabel) throws IOException {
        DatagramSocket socket = new DatagramSocket(9876);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[65000];

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String data = new String(packet.getData(), 0, packet.getLength());
            if (data.equals("END")) {
                // Terminer la réception et afficher l'image
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                ImageIcon imageIcon = new ImageIcon(imageBytes);
                imageLabel.setIcon(imageIcon);
                statusLabel.setText("Image reçue !");
                byteArrayOutputStream.reset();
            } else {
                // Ajouter les données reçues au flux
                byteArrayOutputStream.write(packet.getData(), 0, packet.getLength());
            }
        }
    }
}
