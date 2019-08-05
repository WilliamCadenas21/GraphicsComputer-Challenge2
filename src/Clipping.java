
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JFrame;

public class Clipping extends JPanel {

    private final int numberOfPoints = 6;
    private final double angulo = 10;

    private static final int INSIDE = 0; //0000
    private static final int LEFT = 1; //0001
    private static final int RIGHT = 2; //0010
    private static final int BOTTOM = 4; //0100
    private static final int TOP = 8; //1000

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // size es el tamaÃ±o de la ventana.
        Dimension size = getSize();
        // Insets son los bordes y los tÃ­tulos de la ventana.
        Insets insets = getInsets();

        int w = size.width - insets.left - insets.right;
        int h = size.height - insets.top - insets.bottom;
        
        g2d.setColor(Color.WHITE);
        g2d.drawLine(0, h / 2, w, h / 2);// eje x
        g2d.drawLine(w / 2, 0, w / 2, h);// eje y
        
        int x = 100;
        int y = 100;

        Point[] boundary = new Point[4];
        boundary[0] = new Point(-x, -y);
        boundary[1] = new Point(-x, y);
        boundary[2] = new Point(x, y);
        boundary[3] = new Point(x, -y);
        
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 4; i++) {
            drawJava(boundary[i].x, boundary[i].y, boundary[(i + 1) % 4].x, boundary[(i + 1) % 4].y, g2d, w, h); //eje x
        }

        Point[] points = new Point[numberOfPoints];
        //one line
        points[0] = new Point(-150, -150);
        points[1] = new Point(150, 150);
        //second line
        points[2] = new Point(-200, 4);
        points[3] = new Point(200, 4);
        //tree line
        points[4] = new Point(4, 200);
        points[5] = new Point(-4, -200);

        CohenSutherLand(points, boundary, g2d, w, h);
    }

    /**
     * this is a method created for help to draw lines in Cartesian coordinates
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param g2d
     * @param w
     * @param h
     */
    public static void drawJava(int x1, int y1, int x2, int y2, Graphics2D g2d, int w, int h) {
        int xj1 = w / 2 + x1;
        int yj1 = h / 2 - y1;
        int xj2 = w / 2 + x2;
        int yj2 = h / 2 - y2;
        g2d.drawLine(xj1, yj1, xj2, yj2);
    }

    public static void main(String[] args) {
        // Crear un nuevo Frame
        JFrame frame = new JFrame("Points");
        // Al cerrar el frame, termina la ejecuciÃ³n de este programa
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Agregar un JPanel que se llama Points (esta clase)
        frame.add(new Clipping());
        // Asignarle tamaÃ±o
        frame.setSize(500, 500);
        // Poner el frame en el centro de la pantalla
        frame.setLocationRelativeTo(null);
        // Mostrar el frame
        frame.setVisible(true);
    }

    /**
     * this is the main method to calculate if a line or segment is inside or
     * outside of a clipping area
     *
     * @param points the points that make the segments
     * @param boundary the array that contains the limits points
     * @param g2d
     * @param w the weight of the window
     * @param h the high of the window
     */
    private void CohenSutherLand(Point[] points, Point[] boundary, Graphics2D g2d, int w, int h) {
        boolean extraSegment = false;
        for (int i = 0; i < points.length; i = i + 2) {

            int startCode = getCode(points[i], boundary);
            int endCode = getCode(points[i + 1], boundary);
            Point pi = new Point(points[i].x, points[i].y);
            Point pf = new Point(points[i + 1].x, points[i + 1].y);

            while (true) {
                if ((startCode | endCode) == 0) {
                    System.out.println("Adentro");
                    break;
                } else if ((startCode & endCode) != 0) {
                    System.out.println("afuera");
                    break;
                } else {
                    System.out.println("Crea un nuevo segmento");
                    int x = 0, y = 0;
                    extraSegment = true;

                    int x0 = pi.x;
                    int y0 = pi.y;
                    int x1 = pf.x;
                    int y1 = pf.y;
                    int ymax = boundary[2].y;
                    int ymin = boundary[0].y;
                    int xmax = boundary[2].x;
                    int xmin = boundary[0].x;

                    int outCode = (startCode != 0) ? startCode : endCode;

                    if ((outCode & TOP) != 0) {
                        x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
                        y = ymax;
                    } else if ((outCode & BOTTOM) !=  0) {
                        x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
                        y = ymin;
                    } else if ((outCode & RIGHT) != 0) {
                        y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
                        x = xmax;
                    } else if ((outCode & LEFT) != 0) {
                        y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
                        x = xmin;
                    }

                    if (outCode == startCode) {
                        pi.x = x;
                        pi.y = y;
                        startCode = getCode(pi, boundary);
                    } else {
                        pf.x = x;
                        pf.y = y;
                        endCode = getCode(pf, boundary);
                    }
                }
            }
            
            g2d.setColor(Color.red);
            drawJava(points[i].x, points[i].y, points[i + 1].x, points[i + 1].y, g2d, w, h);

            if (extraSegment) {
                g2d.setColor(Color.green);// se encuentra dentro
                drawJava(pi.x, pi.y, pf.x, pf.y, g2d, w, h);
                extraSegment = false;
            }
        }
    }

    //funcion necesaria para la implementacion de CohenSutherland
    private int getCode(Point points, Point[] boundary) {
        int result = INSIDE; // 0000

        if (points.x < boundary[0].x) {
            result = LEFT; // 0001
        } else if (points.x > boundary[2].x) {
            result = RIGHT; // 0010
        }

        if (points.y < boundary[0].y) {
            result = BOTTOM; // 0100
        } else if (points.y > boundary[2].y) {
            result = TOP; // 1000
        }
        return result;
    }

}
