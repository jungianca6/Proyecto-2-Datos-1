import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

class VentanaServer extends JFrame{
    private JPanel panelSV;
    private JLabel serverLabel;

    /**
     * constructor de la ventana
     */

    public VentanaServer() {
        this.setBounds(500, 200, 700, 700);
        setTitle("Servidor");

        /**
         * construimos el hilo para que siempre escuche
         */

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

public class Servidor {
    public static void main(String[] args) {
        System.out.println("hola");

        VentanaServer servidor = new VentanaServer();
        servidor.setVisible(true);
    }
}
