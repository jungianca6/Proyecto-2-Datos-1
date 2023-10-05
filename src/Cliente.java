import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

class VentanaCliente extends JFrame implements Runnable{
    private JPanel panelCliente;
    private JLabel Cliente;
    private JTextField operacion;
    private JTextArea resultado;
    private JButton solucion;

    /**
     * constructor de la ventana
     */

    public VentanaCliente() {
        this.setBounds(600, 200, 450, 450);
        setTitle("Cliente");

        ComponentesCliente();

        /**
         * construimos el hilo para que siempre escuche
         */
        Thread mihilo = new Thread(this);
        mihilo.start();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void ComponentesCliente(){
        panelCliente();
        etiquetaCliente();
        colocarCajadeTexto();
        colocarAreaText();
        colocarBoton();
        /*colocarCajadeTexto();
        colocarBoton();
        colocarAreaText();
        */
    }

    private void panelCliente(){
        panelCliente = new JPanel();
        panelCliente.setLayout(null);
        this.getContentPane().add(panelCliente);
    }

    private void etiquetaCliente(){
        Cliente = new JLabel("Cliente",SwingConstants.CENTER);
        panelCliente.add(Cliente);
        Cliente.setBounds(140,20,100,25);
        Cliente.setForeground(Color.WHITE);
        Cliente.setBackground(Color.BLACK);
        Cliente.setFont(new Font("times new roman", Font.PLAIN,20));
        Cliente.setOpaque(true);
    }

    private void colocarCajadeTexto(){
        operacion = new JTextField();
        operacion.setBounds(60,325,250,20);
        panelCliente.add(operacion);
    }

    private void colocarAreaText(){
        resultado = new JTextArea();
        resultado.setBounds(0,100,450,200);
        panelCliente.add(resultado);
        resultado.setEditable(true);
    }

    private void colocarBoton() {
        solucion = new JButton("Solución");
        solucion.setBounds(140, 370, 100, 30);
        panelCliente.add(solucion);
        solucion.setEnabled(true);
    }

    @Override
    public void run() {
        int port;
        port = 9091;
        for (int i = port; i<9100;i++){
            try {
                ServerSocket servidorcliente = new ServerSocket(i);
                Socket cliente;

                while(true){
                    cliente = servidorcliente.accept();
                }
            } catch (IOException e) {
                //throw new RuntimeException(e);
            }
        }
    }
}

public class Cliente {
    public static void main(String[] args) {
        VentanaCliente cliente = new VentanaCliente();
        cliente.setVisible(true);
    }
}
