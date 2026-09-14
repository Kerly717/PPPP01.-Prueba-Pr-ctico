package towerdefense.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import towerdefense.logica.Evento;
import towerdefense.modelo.TipoEnemigo;
import towerdefense.modelo.TipoTorre;

/**
 * Identidad visual del juego: colores, tipografias y utilidades de dibujo.
 *
 * Todo el estilo vive aqui, de modo que cambiar el aspecto del juego entero es
 * cuestion de tocar un solo archivo (DRY).
 */
public final class Paleta {

    

    private Paleta() {
        // Clase utilitaria.
    }

    /** Color propio de cada tipo de torre. */
    public static Color colorDe(TipoTorre tipo) {
        switch (tipo) {
            case ARQUERO: return new Color(0x4FA3D1);
            case CANON:   return new Color(0xE0872F);
            default:      return new Color(0xC77DFF);
        }
    }

    /** Color propio de cada tipo de enemigo. */
    public static Color colorDe(TipoEnemigo tipo) {
        switch (tipo) {
            case BASICO:   return new Color(0x6FBF73);
            case RAPIDO:   return new Color(0x62C7E8);
            case BLINDADO: return new Color(0x9A8FB5);
            default:       return new Color(0xC2456B);
        }
    }

    /** Color con el que se muestra cada nivel de evento en la bitacora. */
    public static Color colorDe(Evento.Nivel nivel) {
        switch (nivel) {
            case EXITO:   return VIDA;
            case AVISO:   return AVISO;
            case PELIGRO: return PELIGRO;
            default:      return TEXTO_TENUE;
        }
    }

    public static Color con(Color color, int alfa) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(),
                Math.max(0, Math.min(255, alfa)));
    }

    public static Color aclarar(Color color, int cantidad) {
        return new Color(Math.min(255, color.getRed() + cantidad),
                Math.min(255, color.getGreen() + cantidad),
                Math.min(255, color.getBlue() + cantidad));
    }

    public static Color oscurecer(Color color, int cantidad) {
        return new Color(Math.max(0, color.getRed() - cantidad),
                Math.max(0, color.getGreen() - cantidad),
                Math.max(0, color.getBlue() - cantidad));
    }

    /** Activa el suavizado de bordes y de texto. */
    public static void calidad(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /** Recuadro redondeado con degradado y borde. */
    public static void tarjeta(Graphics2D g, int x, int y, int ancho, int alto,
                               int radio, Color fondo, Color borde) {
        RoundRectangle2D forma = new RoundRectangle2D.Double(x, y, ancho, alto, radio, radio);
        g.setPaint(new GradientPaint(x, y, aclarar(fondo, 8), x, y + alto, oscurecer(fondo, 6)));
        g.fill(forma);
        if (borde != null) {
            g.setColor(borde);
            g.setStroke(new BasicStroke(1.2f));
            g.draw(forma);
        }
    }

    /** Barra de progreso redondeada (vida, avance, etc.). */
    public static void barra(Graphics2D g, int x, int y, int ancho, int alto,
                             double porcentaje, Color color) {
        double p = Math.max(0, Math.min(1, porcentaje));
        g.setColor(con(Color.BLACK, 150));
        g.fillRoundRect(x, y, ancho, alto, alto, alto);
        if (p > 0) {
            g.setColor(color);
            g.fillRoundRect(x, y, Math.max(alto, (int) (ancho * p)), alto, alto, alto);
        }
    }

    public static void textoCentrado(Graphics2D g, String texto, int centroX, int lineaBase) {
        g.drawString(texto, centroX - g.getFontMetrics().stringWidth(texto) / 2, lineaBase);
    }

    /** Resplandor circular suave, para destacar torres y enemigos. */
    public static void halo(Graphics2D g, double x, double y, double radio, Color color, int alfa) {
        for (int i = 4; i >= 1; i--) {
            double r = radio * i / 4;
            g.setColor(con(color, alfa / i));
            g.fillOval((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2));
        }
    }

    /** Recorta un texto midiendo con la fuente real para que no se desborde. */
    public static String recortar(Graphics2D g, String texto, int anchoDisponible) {
        java.awt.FontMetrics metricas = g.getFontMetrics();
        if (metricas.stringWidth(texto) <= anchoDisponible) {
            return texto;
        }
        int anchoPuntos = metricas.stringWidth("...");
        StringBuilder recortado = new StringBuilder();
        int acumulado = 0;
        for (char caracter : texto.toCharArray()) {
            int ancho = metricas.charWidth(caracter);
            if (acumulado + ancho + anchoPuntos > anchoDisponible) {
                break;
            }
            recortado.append(caracter);
            acumulado += ancho;
        }
        return recortado.toString().trim() + "...";
    }
}
