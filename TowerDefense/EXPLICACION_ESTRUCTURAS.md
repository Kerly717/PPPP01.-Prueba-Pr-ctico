# Explicación del uso de las estructuras de datos — Tower Defense

## 1. Lista secuencial (`ListaSecuencialTorres`) — Torres defensivas

Se implementó con un **arreglo fijo** `Torre[] torres` de tamaño 20 y un
contador `contador` que indica cuántas posiciones están realmente ocupadas.
No se usa `ArrayList` ni ninguna colección de `java.util`.

- **Insertar**: se coloca la nueva torre en `torres[contador]` y se
  incrementa el contador (inserción al final, O(1)).
- **Eliminar por id**: se busca el índice de la torre y se desplazan
  todos los elementos posteriores una posición a la izquierda, para no
  dejar huecos en el arreglo (O(n)).
- **Buscar por id**: recorrido lineal desde el índice 0 hasta `contador`.
- **Mostrar / contar**: recorrido lineal simple sobre las posiciones
  ocupadas del arreglo.

Se eligió una lista secuencial para las torres porque son el elemento
que menos cambia durante la partida (se agregan o eliminan con poca
frecuencia), por lo que el costo de desplazar elementos al eliminar es
aceptable.

## 2. Lista doblemente enlazada (`ListaDobleEnemigos`) — Enemigos activos

Se implementó con nodos `NodoEnemigo`, cada uno con una referencia
`anterior` y una `siguiente`, más las referencias globales `primero` y
`ultimo`. Cada nodo envuelve un objeto `Enemigo` con sus datos.

- **Insertar al final**: se crea el nodo y se enlaza como nuevo `ultimo`,
  ajustando el `siguiente` del antiguo último y el `anterior` del nuevo
  nodo (O(1)).
- **Eliminar por id**: se busca el nodo y se reconectan sus vecinos
  (`anterior.siguiente = siguiente` y `siguiente.anterior = anterior`),
  cuidando los casos borde donde el nodo eliminado es `primero` o
  `ultimo`.
- **Recorrido adelante**: desde `primero`, siguiendo `siguiente`.
- **Recorrido atrás**: desde `ultimo`, siguiendo `anterior`.
- **Actualizar posición**: recorrido simple que suma la velocidad de
  cada enemigo a su posición, en cada turno.

Se eligió una lista doblemente enlazada porque los enemigos se insertan
y eliminan constantemente durante la partida (cada turno pueden morir
varios a la vez), y el enlace bidireccional facilita mostrar el estado
del campo de batalla en ambos sentidos sin recorrer todo el arreglo.

## 3. Lista simplemente enlazada circular (`ListaCircularOleadas`) — Oleadas

Se implementó con nodos `NodoOleada`, cada uno con una única referencia
`siguiente`. Se mantiene solamente la referencia `ultimo`; el primer
nodo siempre se obtiene como `ultimo.getSiguiente()`. El `siguiente` del
último nodo siempre apunta al primero, formando el círculo.

- **Registrar oleada**: se inserta el nuevo nodo justo después de
  `ultimo` y se reubica `ultimo` al nuevo nodo, conservando la
  circularidad.
- **Mostrar oleadas**: se recorre el círculo exactamente `contador`
  veces empezando en `ultimo.getSiguiente()` (el primero), para no
  entrar en un bucle infinito.
- **Avanzar siguiente oleada**: el puntero `oleadaActual` avanza un
  nodo (`oleadaActual.getSiguiente()`).
- **Reiniciar ciclo**: `oleadaActual` vuelve a apuntar al primero
  (`ultimo.getSiguiente()`), permitiendo recorrer las oleadas otra vez
  desde el inicio si el jugador decide continuar jugando tras completar
  todas las oleadas definidas.

Se eligió una lista circular porque el concepto de "oleadas" es
naturalmente cíclico: tiene sentido poder volver a la primera oleada
después de la última sin necesitar una estructura aparte.

## 4. Clases de datos simples

`Torre`, `Enemigo` y `Oleada` son clases de datos (atributos privados +
constructor + getters) independientes de los nodos que las enlazan.
Esto separa la información del juego (qué es una torre, un enemigo, una
oleada) de la mecánica de la estructura de datos que la organiza
(cómo se enlazan o se guardan en el arreglo), siguiendo el principio de
modularidad pedido en el objetivo académico.

`Jugador` es una clase auxiliar simple que guarda las vidas restantes.
