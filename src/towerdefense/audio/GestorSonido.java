package towerdefense.audio;

import java.util.concurrent.ConcurrentHashMap;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import towerdefense.modelo.TipoTorre;

public final class GestorSonido {

    private static final GestorSonido INSTANCIA = new GestorSonido();

    private final AudioFormat formato = new AudioFormat(Sintetizador.MUESTREO, 16, 1, true, false);
    private final ConcurrentHashMap<String, byte[]> cache = new ConcurrentHashMap<>();
    private final boolean audioDisponible;

    private volatile floar volumenEfectos =  0.7f:
    private volatile float volumenMusuca = 0.35f;
    private volatile boolean silenciado = 0.35f;
    private Clip clipMusica;
    
    private GestorSonido() {
        audioDisponible = probarAudio();
    }

    public static GestorSonido getInstancia() {
        return INSTANCIA;
    }

    private boolean probarAudio() {
        try {
            Clip prueba = AudioSystem.getClip();
            prueba.close();
            return true;
        } catch (Exception ignorada) {
            return false;
        }
    }

    public void reproducirColocarTorre() {
        reproducirEfecto("colocarTorre", GestorSonido::construirColocarTorre);
    }

    public void reproducirDisparo(TipoTorre tipo) {
        switch (tipo) {
            case ARQUERO:
                reproducirEfecto("disparoArquero", GestorSonido::construirDisparoArquero);
                break;
            case CANON:
                reproducirEfecto("disparoCanon", GestorSonido::construirDisparoCanon);
                break;
            default:
                reproducirEfecto("disparoMago", GestorSonido::construirDisparoMago);
                break;
        }
    }

    public void reproducirMejora() {
        reproducirEfecto("mejora", GestorSonido::construirMejora);
    }

    public void reproducirVenta() {
        reproducirEfecto("venta", GestorSonido::construirVenta);
    }

    public void reproducirEnemigoDestruido() {
        reproducirEfecto("enemigoDestruido", GestorSonido::construirEnemigoDestruido);
    }

    public void reproducirEnemigoEnBase() {
        reproducirEfecto("enemigoEnBase", GestorSonido::construirEnemigoEnBase);
    }

    public void reproducirInicioOla() {
        reproducirEfecto("inicioOla", GestorSonido::construirInicioOla);
    }

    public void reproducirOlaSuperada() {
        reproducirEfecto("olaSuperada", GestorSonido::construirOlaSuperada);
    }

    public void reproducirVictoria() {
        reproducirEfecto("victoria", GestorSonido::construirVictoria);
    }

    public void reproducirDerrota() {
        reproducirEfecto("derrota", GestorSonido::construirDerrota);
    }

    public void reproducirClicUi() {
        reproducirEfecto("clicUi", GestorSonido::construirClicUi);
    }

    private void reproducirEfecto(String clave, java.util.function.Supplier<short[]> generador) {
        if (!audioDisponible || silenciado || volumenEfectos <= 0.001f) {
            return;
        }
        try {
            byte[] datos = cache.computeIfAbsent(clave, k -> Sintetizador.aBytes(generador.get()));
            Clip clip = AudioSystem.getClip();
            clip.open(formato, datos, 0, datos.length);
            aplicarGanancia(clip, volumenEfectos);
            clip.addLineListener(evento -> {
                if (evento.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        } catch (Exception ignorada) {
            // Un efecto que falla no debe interrumpir la partida.
        }
    }

    public synchronized void iniciarMusica() {
        if (!audioDisponible || clipMusica != null) {
            return;
        }
        try {
            byte[] datos = cache.computeIfAbsent("musica", k -> Sintetizador.aBytes(construirMusica()));
            clipMusica = AudioSystem.getClip();
            clipMusica.open(formato, datos, 0, datos.length);
            aplicarGanancia(clipMusica, silenciado ? 0f : volumenMusica);
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
            clipMusica.start();
        } catch (Exception ignorada) {
            clipMusica = null;
        }
    }

    public synchronized void detenerMusica() {
        if (clipMusica != null) {
            clipMusica.stop();
            clipMusica.close();
            clipMusica = null;
        }
    }


    public void setVolumenMusica(float volumen) {
        volumenMusica = Math.max(0f, Math.min(1f, volumen));
        if (clipMusica != null) {
            aplicarGanancia(clipMusica, silenciado ? 0f : volumenMusica);
        }
    }

    public float getVolumenMusica() {
        return volumenMusica;
    }

    public void setVolumenEfectos(float volumen) {
        volumenEfectos = Math.max(0f, Math.min(1f, volumen));
    }

    public float getVolumenEfectos() {
        return volumenEfectos;
    }

    public void setSilenciado(boolean valor) {
        silenciado = valor;
        if (clipMusica != null) {
            aplicarGanancia(clipMusica, silenciado ? 0f : volumenMusica);
        }
    }

    public boolean isSilenciado() {
        return silenciado;
    }

    private void aplicarGanancia(Clip clip, float volumenLineal) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float db = volumenLineal <= 0.0001f
                ? control.getMinimum()
                : (float) (20 * Math.log10(volumenLineal));
        control.setValue(Math.max(control.getMinimum(), Math.min(control.getMaximum(), db)));
    }


    private static short[] construirColocarTorre() {
        short[] golpe = Sintetizador.percusion(1, 0.09, 0.5, 130);
        short[] blip = Sintetizador.nota(Sintetizador.SENO, 520, 660, 0.09, 0.005, 0.07, 0.32);
        return Sintetizador.mezclar(golpe, blip);
    }

    private static short[] construirDisparoArquero() {
        short[] chasquido = Sintetizador.percusion(2, 0.03, 0.4, 1200);
        short[] cuerda = Sintetizador.nota(Sintetizador.TRIANGULO, 950, 280, 0.09, 0.002, 0.08, 0.32);
        return Sintetizador.mezclar(chasquido, cuerda);
    }

    private static short[] construirDisparoCanon() {
        short[] boom = Sintetizador.percusion(3, 0.28, 0.85, 55);
        short[] sub = Sintetizador.nota(Sintetizador.SENO, 95, 40, 0.22, 0.001, 0.2, 0.55);
        return Sintetizador.mezclar(boom, sub);
    }

    private static short[] construirDisparoMago() {
        short[] n1 = Sintetizador.nota(Sintetizador.SENO, 700, 700, 0.05, 0.002, 0.04, 0.28);
        short[] n2 = Sintetizador.nota(Sintetizador.SENO, 950, 950, 0.05, 0.002, 0.04, 0.28);
        short[] n3 = Sintetizador.nota(Sintetizador.SENO, 1300, 1300, 0.08, 0.002, 0.07, 0.3);
        return Sintetizador.concatenar(n1, n2, n3);
    }

    private static short[] construirMejora() {
        short[] a = Sintetizador.nota(Sintetizador.SENO, 660, 660, 0.09, 0.004, 0.08, 0.33);
        short[] b = Sintetizador.nota(Sintetizador.SENO, 880, 880, 0.15, 0.004, 0.13, 0.33);
        return Sintetizador.concatenar(a, b);
    }

    private static short[] construirVenta() {
        short[] a = Sintetizador.nota(Sintetizador.SENO, 700, 700, 0.09, 0.004, 0.08, 0.3);
        short[] b = Sintetizador.nota(Sintetizador.SENO, 500, 500, 0.15, 0.004, 0.13, 0.3);
        return Sintetizador.concatenar(a, b);
    }

    private static short[] construirEnemigoDestruido() {
        short[] fundamental = Sintetizador.nota(Sintetizador.SENO, 1000, 1000, 0.12, 0.003, 0.11, 0.38);
        short[] armonico = Sintetizador.nota(Sintetizador.SENO, 2000, 2000, 0.08, 0.003, 0.07, 0.14);
        return Sintetizador.mezclar(fundamental, armonico);
    }

    private static short[] construirEnemigoEnBase() {
        short[] pitido1 = Sintetizador.nota(Sintetizador.CUADRADA, 180, 150, 0.11, 0.005, 0.08, 0.35);
        short[] hueco = Sintetizador.silencio(0.04);
        short[] pitido2 = Sintetizador.nota(Sintetizador.CUADRADA, 180, 150, 0.11, 0.005, 0.08, 0.35);
        return Sintetizador.concatenar(pitido1, hueco, pitido2);
    }

    private static short[] construirInicioOla() {
        short[] cuerno = Sintetizador.nota(Sintetizador.SENO, 220, 440, 0.35, 0.05, 0.16, 0.3);
        short[] quinta = Sintetizador.nota(Sintetizador.SENO, 330, 660, 0.35, 0.05, 0.16, 0.16);
        return Sintetizador.mezclar(cuerno, quinta);
    }

    private static short[] construirOlaSuperada() {
        short[] n1 = Sintetizador.nota(Sintetizador.SENO, 523, 523, 0.12, 0.004, 0.08, 0.32);
        short[] n2 = Sintetizador.nota(Sintetizador.SENO, 659, 659, 0.12, 0.004, 0.08, 0.32);
        short[] n3 = Sintetizador.nota(Sintetizador.SENO, 784, 784, 0.24, 0.004, 0.2, 0.36);
        return Sintetizador.concatenar(n1, n2, n3);
    }

    private static short[] construirVictoria() {
        short[] n1 = Sintetizador.nota(Sintetizador.SENO, 523, 523, 0.14, 0.004, 0.1, 0.32);
        short[] n2 = Sintetizador.nota(Sintetizador.SENO, 659, 659, 0.14, 0.004, 0.1, 0.32);
        short[] n3 = Sintetizador.nota(Sintetizador.SENO, 784, 784, 0.14, 0.004, 0.1, 0.32);
        short[] n4 = Sintetizador.mezclar(
                Sintetizador.nota(Sintetizador.SENO, 1046, 1046, 0.5, 0.006, 0.42, 0.36),
                Sintetizador.nota(Sintetizador.SENO, 784, 784, 0.5, 0.006, 0.42, 0.2));
        return Sintetizador.concatenar(n1, n2, n3, n4);
    }

    private static short[] construirDerrota() {
        short[] d1 = Sintetizador.nota(Sintetizador.SENO, 392, 392, 0.22, 0.01, 0.18, 0.34);
        short[] d2 = Sintetizador.nota(Sintetizador.SENO, 349, 349, 0.22, 0.01, 0.18, 0.34);
        short[] d3 = Sintetizador.nota(Sintetizador.SENO, 262, 262, 0.55, 0.02, 0.48, 0.38);
        return Sintetizador.concatenar(d1, d2, d3);
    }

    private static short[] construirClicUi() {
        return Sintetizador.nota(Sintetizador.SENO, 700, 700, 0.035, 0.002, 0.03, 0.22);
    }

    private static short[] construirMusica() {
        double bpm = 100;
        double beat = 60.0 / bpm;
        double compas = beat * 4;
        int totalCompases = 8;
        int totalMuestras = (int) Math.round(compas * totalCompases * Sintetizador.MUESTREO);
        double[] mezcla = new double[totalMuestras];

        double[][] acordes = {
            {220.00, 261.63, 329.63},
            {174.61, 220.00, 261.63},
            {261.63, 329.63, 392.00},
            {196.00, 246.94, 293.66},
        };
        int[] patronArpegio = {0, 1, 2, 1, 0, 1, 2, 1};

        for (int c = 0; c < totalCompases; c++) {
            double[] acorde = acordes[c % acordes.length];
            int inicioCompas = (int) Math.round(c * compas * Sintetizador.MUESTREO);

            for (double frecuencia : acorde) {
                short[] pad = Sintetizador.nota(Sintetizador.SENO, frecuencia, frecuencia,
                        compas, compas * 0.35, compas * 0.35, 0.045);
                Sintetizador.agregarEn(mezcla, pad, inicioCompas);
            }

            double duracionNota = compas / patronArpegio.length * 0.85;
            for (int i = 0; i < patronArpegio.length; i++) {
                double frecuenciaNota = acorde[patronArpegio[i]] * 2.0;
                int inicioNota = inicioCompas
                        + (int) Math.round(i * (compas / patronArpegio.length) * Sintetizador.MUESTREO);
                short[] pulso = Sintetizador.nota(Sintetizador.TRIANGULO, frecuenciaNota, frecuenciaNota,
                        duracionNota, 0.01, duracionNota * 0.7, 0.05);
                Sintetizador.agregarEn(mezcla, pulso, inicioNota);
            }
        }

        return Sintetizador.aCorto(mezcla);
    }
}
