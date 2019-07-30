import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JFrame;

public class Clipping extends JPanel {
    
    
    public int numberOfPoints = 6;
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);

        // size es el tamaÃ±o de la ventana.
        Dimension size = getSize();
        // Insets son los bordes y los tÃ­tulos de la ventana.
        Insets insets = getInsets();

        int w = size.width - insets.left - insets.right;
        int h = size.height - insets.top - insets.bottom;

        int x = 100;
        int y = 100;

        Point[] boundary = new Point[4];
        boundary[0] = new Point(-x, -y);
        boundary[1] = new Point(-x, y);
        boundary[2] = new Point(x, y);
        boundary[3] = new Point(x, -y);

        for (int i = 0; i < 4; i++) {
            drawJava(boundary[i].x, boundary[i].y, boundary[(i + 1) % 4].x, boundary[(i + 1) % 4].y, g2d, w, h); //eje x
        }
        
        Point[] points = new Point[numberOfPoints];
        points[0] = new Point(-150, -150);
        points[1] = new Point(50, 50);
        points[2] = new Point(-50, 150);
        points[3] = new Point(50, 150);
        points[4] = new Point(110, 50);
        points[5] = new Point(160, 80);

        CohenSutherLand(points, boundary, g2d, w, h);
    }
    
    /**
     * this is a method created for help to draw lines in Cartesian coordinates 
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
     * this is the main method to calculate if a line or segment is 
     * inside or outside of a clipping area
     * @param points the points that make the segments
     * @param boundary the array that contains the limits points
     * @param g2d 
     * @param w the weight of the window
     * @param h the high of the window 
     */
    private void CohenSutherLand(Point[] points, Point[] boundary,Graphics2D g2d, int w, int h) {
        for (int i = 0; i < points.length; i=i+2) {
            
            int startCode = getCode(points[i], boundary);
            int endCode = getCode(points[i+1], boundary);
            
            System.out.println("s:"+startCode+"e:"+endCode);
            if ((startCode | endCode) == 0) {
                g2d.setColor(Color.blue);// se encuentra dentro
                System.out.println("Adentro");
            }else if((startCode & endCode) !=0){
                g2d.setColor(Color.red);//a fuera
                System.out.println("afuera");
            }else{
                g2d.setColor(Color.green);//a fuera
                System.out.println("No se puede deteminar por las dos pruebas de trivialidad");
            }
            
            drawJava(points[i].x, points[i].y, points[i+1].x, points[i+1].y, g2d, w, h);
        }
    }
    
    //funcion necesaria para la implementacion de CohenSutherland
    private int getCode(Point points, Point[] boundary) {
        int result = 0; // Inside 0000

        if (points.x < boundary[0].x) {
            result = 1; // Left 0001
        } else if (points.x > boundary[2].x) {
            result = 2; // Right 0010
        }

        if (points.y < boundary[0].y) {
            result = 4; // Bottom 0100
        } else if (points.y > boundary[2].y) {
            result = 8; // Top
        }
        return result;
    }

}
