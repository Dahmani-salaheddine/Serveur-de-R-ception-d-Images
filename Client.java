import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Client");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("Sélectionnez une image à envoyer :");
        JButton button = new JButton("Choisir une image");

        frame.add(label);
        frame.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        sendImage(file);
                        JOptionPane.showMessageDialog(frame, "Image envoyée avec succès !");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Erreur lors de l'envoi de l'image : " + ex.getMessage());
                    }
                }
            }
        });

        frame.setVisible(true);
    }

    private static void sendImage(File file) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");
        int port = 9876;

        // Lire le fichier image en bytes
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        
        // Diviser les données en segments UDP
        int maxPacketSize = 65000; // Limite UDP
        for (int i = 0; i < fileBytes.length; i += maxPacketSize) {
            int length = Math.min(maxPacketSize, fileBytes.length - i);
            byte[] segment = new byte[length];
            System.arraycopy(fileBytes, i, segment, 0, length);

            DatagramPacket packet = new DatagramPacket(segment, segment.length, serverAddress, port);
            socket.send(packet);
        }

        // Envoyer un paquet vide pour signaler la fin
        byte[] endSignal = "END".getBytes();
        DatagramPacket endPacket = new DatagramPacket(endSignal, endSignal.length, serverAddress, port);
        socket.send(endPacket);

        socket.close();
    }
}
