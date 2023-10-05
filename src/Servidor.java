import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

class VentanaServer extends JFrame implements Runnable{
    private JPanel panelServidor;
    private JLabel Servidor;

    /**
     * constructor de la ventana
     */

    public VentanaServer() {
        this.setBounds(500, 200, 300, 300);
        setTitle("Servidor");

        componentesServer();

        /**
         * construimos el hilo para que siempre escuche
         */
        Thread mihilo = new Thread(this);
        mihilo.start();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void componentesServer(){
        panelServidor();
        etiquetaServer();
    }

    private void panelServidor(){
        panelServidor = new JPanel();
        panelServidor.setLayout(null);
        this.getContentPane().add(panelServidor);
    }

    private void etiquetaServer(){
        Servidor = new JLabel("Servidor",SwingConstants.CENTER);
        panelServidor.add(Servidor);
        Servidor.setBounds(50,15,100,25);
        Servidor.setForeground(Color.WHITE);
        Servidor.setBackground(Color.BLACK);
        Servidor.setFont(new Font("times new roman", Font.PLAIN,20));
        Servidor.setOpaque(true);
    }

    @Override
    public void run() {
        try {
            ServerSocket servidor = new ServerSocket(9090);

            while(true) {
                /**
                 *Permite que acepte las conexiones del exterior
                 */
                Socket misocket = servidor.accept();
                /**cierra el flujo de datos*/
                misocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Servidor {
    public static void main(String[] args) {
        System.out.println("hola");

        VentanaServer servidor = new VentanaServer();
        servidor.setVisible(true);
    }
}
