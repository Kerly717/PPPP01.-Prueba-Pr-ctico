# Diagrama simple de clases — Tower Defense

## Clases de datos (independientes de las estructuras)

```
+------------------+   +---------------------+   +---------------------+
|      Torre       |   |       Enemigo        |   |        Oleada        |
+------------------+   +---------------------+   +---------------------+
| - id: int        |   | - id: int            |   | - idOleada: int       |
| - nombre: String |   | - tipo: String        |   | - cantidadEnemigos:   |
| - tipo: String   |   | - vida: int           |   |   int                 |
| - posicion: int  |   | - velocidad: int      |   | - tipoEnemigo: String |
| - danio: int     |   | - posicion: int       |   | - vidaBase: int       |
| - rango: int     |   | - recompensa: int     |   | - velocidadBase: int  |
| - costo: int     |   +---------------------+   +---------------------+
+------------------+   | + recibirDanio()      |
| + estaEnRango()  |   | + estaDestruido()     |
+------------------+   +---------------------+

+------------------+
|      Jugador      |
+------------------+
| - vidas: int      |
+------------------+
| + perderVida()    |
| + estaDerrotado() |
+------------------+
```

## Nodos y estructuras enlazadas

```
+------------------------+        +---------------------------+
|      NodoEnemigo        |        |        NodoOleada          |
+------------------------+        +---------------------------+
| - enemigo: Enemigo      |        | - oleada: Oleada            |
| - anterior: NodoEnemigo |        | - siguiente: NodoOleada     |
| - siguiente: NodoEnemigo|        +---------------------------+
+------------------------+
        ^      ^                           ^
        | 1  n |                           | n
        |      |                           |
+------------------------+        +---------------------------+
|   ListaDobleEnemigos    |        |   ListaCircularOleadas      |
+------------------------+        +---------------------------+
| - primero: NodoEnemigo  |        | - ultimo: NodoOleada         |
| - ultimo: NodoEnemigo   |        | - oleadaActual: NodoOleada   |
| - contador: int         |        | - contador: int              |
+------------------------+        +---------------------------+
| + insertarAlFinal()     |        | + registrarOleada()          |
| + eliminarPorId()       |        | + mostrarOleadas()            |
| + buscarPorId()         |        | + avanzarSiguienteOleada()    |
| + mostrarAdelante()     |        | + reiniciarCiclo()            |
| + mostrarAtras()        |        +---------------------------+
| + actualizarPosiciones()|
+------------------------+

+---------------------------+
|   ListaSecuencialTorres    |
+---------------------------+
| - torres: Torre[]          |
| - contador: int            |
+---------------------------+
| + insertarTorre()          |
| + eliminarTorrePorId()     |
| + buscarTorrePorId()       |
| + mostrarTorres()          |
| + contarActivas()          |
+---------------------------+
```

## Relación con la clase principal

```
+-------------------------------------------------------------+
|                        TowerDefenseApp                       |
|  (contiene el metodo main y el menu)                         |
+-------------------------------------------------------------+
| - listaTorres:   ListaSecuencialTorres                       |
| - listaEnemigos: ListaDobleEnemigos                          |
| - listaOleadas:  ListaCircularOleadas                        |
| - jugador:       Jugador                                     |
+-------------------------------------------------------------+
```

`TowerDefenseApp` es dueño de una instancia de cada una de las tres
estructuras y del jugador. En cada turno consulta `ListaSecuencialTorres`
para saber qué torres disparan, recorre `ListaDobleEnemigos` para mover
y dañar enemigos, y usa `ListaCircularOleadas` para saber qué oleada
enviar a continuación.
