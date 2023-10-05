import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

class VentanaCliente extends JFrame{

    /**
     * constructor de la ventana
     */

    public VentanaCliente() {
        this.setBounds(500, 200, 300, 300);
        setTitle("Cliente");

        /**
         * construimos el hilo para que siempre escuche
         */

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}

public class Cliente {
    public static void main(String[] args) {
        VentanaCliente cliente = new VentanaCliente();
        cliente.setVisible(true);
    }
}
