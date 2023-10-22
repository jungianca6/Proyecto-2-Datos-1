import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

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
            String cadena;
            double solucionEnviar;
            paqueteDatos operacionRecibida;

            while(true) {
                /**
                 *Permite que acepte las conexiones del exterior
                 */
                Socket misocket = servidor.accept();

                ObjectInputStream operacionEntrante = new ObjectInputStream(misocket.getInputStream());
                operacionRecibida = (paqueteDatos) operacionEntrante.readObject();

                cadena = operacionRecibida.getCadena();
                ArbolBinarioExp ABE = new ArbolBinarioExp(cadena);
                solucionEnviar = ABE.evaluaExpresion();
                operacionRecibida.setCadena(" "+ solucionEnviar);

                int port;
                port = 9091;
                for (int i =port; i<9100;i++){
                    try {
                        Socket enviaDestinatario = new Socket("localhost",i);

                        ObjectOutputStream reenvioSolucion = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                        reenvioSolucion.writeObject(operacionRecibida);

                    }
                    catch (IOException e) {}
                }
                /**cierra el flujo de datos*/
                misocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

class NodeArbol {
    protected Object data;
    protected NodeArbol left;
    protected NodeArbol right;

    public NodeArbol(Object data) {
        this.data = data;
        left = right = null;
    }
}

class NodePila{
    protected NodeArbol data;
    protected NodePila next;

    public NodePila(NodeArbol x){
        this.data = x;
        next = null;
    }
}

class PilaArbolExp{
    private NodePila tope;

    public PilaArbolExp(){
        tope = null;
    }

    public void insertar (NodeArbol elemento){
        NodePila nuevo;
        nuevo = new NodePila(elemento);
        nuevo.next = tope;
        tope = nuevo;
    }

    public boolean pilaVacia(){
        return tope == null;
    }

    public NodeArbol topePila(){
        return tope.data;
    }
    public void ReiniciarPila(){
        tope = null;
    }
    public NodeArbol quitar(){
        NodeArbol aux = null;
        if (!pilaVacia()){
            aux = tope.data;
            tope = tope.next;
        }
        return aux;
    }
}

class ArbolBinarioExp{
    NodeArbol raiz;

    public ArbolBinarioExp(){
        raiz= null;
    }

    /**
     * crea el arbol de expresiones a partir de la cadena
     */
    public ArbolBinarioExp(String cadena){
        raiz = creaArbolBE(cadena);
    }

    public void reiniciaArbol(){
        raiz = null;
    }

    public void creaNodo(Object data){
        raiz = new NodeArbol (data);
    }
    public NodeArbol creaSubArbol(NodeArbol dato2,NodeArbol dato1, NodeArbol operador){
        operador.left= dato1;
        operador.right= dato2;
        return operador;
    }

    public boolean arbolVacio(){
        return raiz == null;
    }

    private String inOrden(NodeArbol subArbol,String c){
        String cadena;
        cadena = "";
        if (subArbol != null){
            cadena = c + inOrden(subArbol.left,c) + subArbol.data.toString() +"\n"+
                    inOrden(subArbol.right,c);
        }
        return cadena;
    }

    public String toString(){
        String cadena = "";
        cadena = inOrden(raiz,cadena);
        return cadena;
    }

    private int prioridad(char c){
        int p=100;
        switch (c){
            case '^':
                p=30;
                break;
            case '%':
                p=25;
                break;
            case '*':
            case '/':
                p=20;
                break;
            case '+':
            case '-':
            case '&':
            case '|':
            case '~':
            case '#':
                p=10;
                break;
            default:
                p=0;
        }
        return p;
    }

    private boolean esOperador(char c){
        boolean resultado;
        switch(c){
            case '(':
            case ')':
            case '^': //potencia
            case '*':
            case '/':
            case '+':
            case '-':
            case '&': //operador logico AND
            case '|': //operador logico OR
            case '~': //operador logico NOT
            case '?': //operador logico XOR
                resultado = true;
                break;
            default:
                resultado = false;
        }
        return resultado;
    }

    private NodeArbol creaArbolBE (String cadena){
        PilaArbolExp pilaOperadores;
        PilaArbolExp pilaExpresiones;
        NodeArbol token;
        NodeArbol op1;
        NodeArbol op2;
        NodeArbol op;

        pilaOperadores = new PilaArbolExp();
        pilaExpresiones = new PilaArbolExp();
        char caracterEvaluado;
        for (int i=0; i<cadena.length();i++){
            caracterEvaluado = cadena.charAt(i);
            token = new NodeArbol(caracterEvaluado);
            if (Character.isDigit(caracterEvaluado)){
                StringBuilder numero = new StringBuilder();
                while (i<cadena.length() && Character.isDigit(cadena.charAt(i))){
                    numero.append(cadena.charAt(i));
                    i++;
                }
                i--;
                NodeArbol numeroNodo = new NodeArbol(numero);
                pilaExpresiones.insertar(numeroNodo);
            }
            else if (!esOperador(caracterEvaluado)){

            }else{ /**es un operador*/
                switch(caracterEvaluado){
                    case '(':
                        pilaOperadores.insertar(token);
                        break;
                    case ')':
                        while (!pilaOperadores.pilaVacia() &&
                                !pilaOperadores.topePila().data.equals('(')){
                            op2 = pilaExpresiones.quitar();
                            op1 = pilaExpresiones.quitar();
                            op = pilaOperadores.quitar();
                            op = creaSubArbol(op2,op1,op);
                            pilaExpresiones.insertar(op);
                        }
                        /**
                         * Lo quitamos de la pila de operadores, porque
                         * el parentesis no forma parte de nuestra expresión
                         */
                        pilaOperadores.quitar();
                        break;
                    default:
                        while(!pilaOperadores.pilaVacia() && prioridad(caracterEvaluado)
                                <= prioridad(pilaOperadores.topePila().data.toString().charAt(0))){
                            op2 = pilaExpresiones.quitar();
                            op1 = pilaExpresiones.quitar();
                            op = pilaOperadores.quitar();
                            op = creaSubArbol(op2,op1,op);
                            pilaExpresiones.insertar(op);
                        }
                        pilaOperadores.insertar(token);

                }
            }

        }
        /**
         * En el caso que la pila de operadores no este vacia
         */
        while (!pilaOperadores.pilaVacia()){
            op2 = pilaExpresiones.quitar();
            op1 = pilaExpresiones.quitar();
            op= pilaOperadores.quitar();
            op= creaSubArbol(op2,op1,op);
            pilaExpresiones.insertar(op);
        }
        /**
         * Al final se retorna el arbol completo de expresiones
         */
        op = pilaExpresiones.quitar();
        return op;
    }

    public double evaluaExpresion(){
        return evalua(raiz);
    }

    private double evalua(NodeArbol subarbol){
        double acum=0;
        if (!esOperador(subarbol.data.toString().charAt(0))){
            return Double.parseDouble(subarbol.data.toString());
        }else{
            switch(subarbol.data.toString().charAt(0)){
                case '^':
                    acum = acum + Math.pow(evalua(subarbol.left),evalua(subarbol.right));
                    break;
                case '*':
                    acum = acum + evalua(subarbol.left) * evalua(subarbol.right);
                    break;
                case '/':
                    acum = acum + evalua(subarbol.left) / evalua(subarbol.right);
                    break;
                case '+':
                    acum = acum + evalua(subarbol.left) + evalua(subarbol.right);
                    break;
                case '-':
                    acum = acum + evalua(subarbol.left) - evalua(subarbol.right);
                    break;
                case '%':
                    acum= acum + (evalua(subarbol.left) * evalua(subarbol.right))/ 100;
                    break;
            }
        }
        return acum;
    }

    private boolean evalualogico(NodeArbol subarbol){
        boolean logico= false;
        if (!esOperador(subarbol.data.toString().charAt(0))){
            return Boolean.parseBoolean(subarbol.data.toString());
        }else{
            switch(subarbol.data.toString().charAt(0)){
                case '&':
                    logico = evalualogico(subarbol.left) && evalualogico(subarbol.right);
                    break;
                case '|':
                    logico = evalualogico(subarbol.left) || evalualogico(subarbol.right);
                    break;
                case '~':
                    logico = !evalualogico(subarbol.left);
                    break;
                case '?':
                    logico = evalualogico(subarbol.left) ^ evalualogico(subarbol.right);
                    break;

            }
        }
        return logico;
    }
}


public class Servidor {
    public static void main(String[] args) {
        System.out.println("hola");

        VentanaServer servidor = new VentanaServer();
        servidor.setVisible(true);

    }
}
