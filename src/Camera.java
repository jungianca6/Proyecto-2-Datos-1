import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

/**
 * La clase Camera representa una aplicación Java que permite capturar imágenes
 * desde una cámara de video utilizando OpenCV y guardarlas en archivos JPEG.
 * Proporciona una interfaz de usuario simple con un botón "Capture" para
 * capturar imágenes y mostrar el flujo de la cámara en una ventana.
 */
public class Camera extends JFrame {

    /**
     * Vista de la camara
     */
    private JLabel cameraScreen;
    /**
     * Boton que captura la foto
     */
    private JButton btnCapture;
    /**
     * Captura video desde una camara
     */
    private VideoCapture capture;
    /**
     * Matriz para almacenar la imagen capturada
     */
    private Mat image;
    /**
     * Indica si se ha hecho click al boton de captura
     */
    private boolean clicked = false;

    /**
     * Constructor de la clase Camera. Inicializa la interfaz de usuario y
     * configura los componentes como la pantalla de la cámara y el botón de
     * captura.
     */
    public Camera() {
        setLayout(null);

        cameraScreen = new JLabel();
        cameraScreen.setBounds(0, 0, 640, 480);
        add(cameraScreen);

        btnCapture = new JButton("capture");
        btnCapture.setBounds(300, 480, 80, 40);
        add(btnCapture);

        btnCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clicked = true;
            }
        });

        setSize(new Dimension(640, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Inicia la cámara, captura imágenes continuamente y permite al usuario
     * guardar las imágenes capturadas en archivos JPEG con nombres personalizados.
     */
    public void startCamera() {
        capture = new VideoCapture(0);
        image = new Mat();
        byte[] imageData;

        ImageIcon icon;
        while (true) {
            capture.read(image);

            final MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, buf);

            imageData = buf.toArray();

            icon = new ImageIcon(imageData);
            cameraScreen.setIcon(icon);

            if (clicked) {
                String name = JOptionPane.showInputDialog(
                        this, "Enter image name");
                if (name == null) {
                    name = new SimpleDateFormat(
                            "yyyy-MM-dd-HH-mm-ss")
                            .format(new Date(System.currentTimeMillis()));
                }

                String imagePath = System.getProperty("user.dir") + "/" + name + ".jpg";
                Imgcodecs.imwrite(imagePath, image);
                clicked = false;
            }
        }
    }

    /**
     * Método principal que carga la biblioteca nativa de OpenCV, crea una
     * instancia de la clase Camera y ejecuta la cámara en un hilo separado.
     *
     * @param args Argumentos de línea de comandos (no utilizados en este caso).
     */
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final Camera camera = new Camera();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        camera.startCamera();
                    }
                }).start();
            }
        });
    }
}
