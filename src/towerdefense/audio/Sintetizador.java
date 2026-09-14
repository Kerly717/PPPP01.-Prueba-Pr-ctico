package towerdefense.audio;

import java.util.Random;


final class Sintetizador {

    static final float MUESTREO = 44100f;

    private Sintetizador() {
    }

    interface Onda {
        double valor(double faseCiclos);
    }

    static final Onda SENO = fase -> Math.sin(2 * Math.PI * fase);
    static final Onda TRIANGULO = fase -> 2 * Math.abs(2 * (fase - Math.floor(fase + 0.5))) - 1;
    static final Onda CUADRADA = fase -> Math.signum(Math.sin(2 * Math.PI * fase));

    static short[] nota(Onda onda, double frecuenciaInicial, double frecuenciaFinal,
            double duracionSeg, double ataqueSeg, double caidaSeg, double amplitud) {
        int total = Math.max(1, (int) (duracionSeg * MUESTREO));
        short[] datos = new short[total];
        double fase = 0;
        for (int i = 0; i < total; i++) {
            double t = i / MUESTREO;
            double frecuencia = frecuenciaInicial + (frecuenciaFinal - frecuenciaInicial) * (t / duracionSeg);
            fase += frecuencia / MUESTREO;
            double sobre = envolvente(t, duracionSeg, ataqueSeg, caidaSeg);
            datos[i] = recortar(onda.valor(fase) * amplitud * sobre);
        }
        return datos;
    }
    static short[] percusion(long semilla, double duracionSeg, double amplitud, double tonoBase) {
        Random azar = new Random(semilla);
        int total = Math.max(1, (int) (duracionSeg * MUESTREO));
        short[] datos = new short[total];
        double fase = 0;
        for (int i = 0; i < total; i++) {
            double t = i / MUESTREO;
            double sobre = Math.exp(-t * (6.0 / duracionSeg));
            fase += tonoBase / MUESTREO;
            double mezcla = 0.55 * (azar.nextDouble() * 2 - 1) + 0.45 * Math.sin(2 * Math.PI * fase);
            datos[i] = recortar(mezcla * amplitud * sobre);
        }
        return datos;
    }

    static short[] silencio(double duracionSeg) {
        return new short[Math.max(0, (int) (duracionSeg * MUESTREO))];
    }

    private static double envolvente(double t, double duracion, double ataque, double caida) {
        if (ataque > 0 && t < ataque) {
            return t / ataque;
        }
        double restante = duracion - t;
        if (caida > 0 && restante < caida) {
            return Math.max(0, restante / caida);
        }
        return 1.0;
    }

    private static short recortar(double valor) {
        int v = (int) Math.round(valor * Short.MAX_VALUE);
        return (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, v));
    }

    static short[] concatenar(short[]... fragmentos) {
        int total = 0;
        for (short[] f : fragmentos) {
            total += f.length;
        }
        short[] resultado = new short[total];
        int pos = 0;
        for (short[] f : fragmentos) {
            System.arraycopy(f, 0, resultado, pos, f.length);
            pos += f.length;
        }
        return resultado;
    }

    static short[] mezclar(short[]... pistas) {
        int total = 0;
        for (short[] p : pistas) {
            total = Math.max(total, p.length);
        }
        short[] resultado = new short[total];
        for (short[] p : pistas) {
            for (int i = 0; i < p.length; i++) {
                int suma = resultado[i] + p[i];
                resultado[i] = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, suma));
            }
        }
        return resultado;
    }

    static void agregarEn(double[] acumulador, short[] fragmento, int desdeMuestra) {
        for (int i = 0; i < fragmento.length; i++) {
            int destino = desdeMuestra + i;
            if (destino >= 0 && destino < acumulador.length) {
                acumulador[destino] += fragmento[i] / (double) Short.MAX_VALUE;
            }
        }
    }

    static short[] aCorto(double[] acumulador) {
        short[] resultado = new short[acumulador.length];
        for (int i = 0; i < acumulador.length; i++) {
            resultado[i] = recortar(acumulador[i]);
        }
        return resultado;
    }

    static byte[] aBytes(short[] muestras) {
        byte[] datos = new byte[muestras.length * 2];
        for (int i = 0; i < muestras.length; i++) {
            datos[i * 2] = (byte) (muestras[i] & 0xFF);
            datos[i * 2 + 1] = (byte) ((muestras[i] >> 8) & 0xFF);
        }
        return datos;
    }
}
