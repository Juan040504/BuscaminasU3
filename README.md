# 🎮 Buscaminas JavaFX

Un juego de buscaminas clásico implementado en JavaFX con principios de Clean Code y SOLID, utilizando SQLite para la persistencia de datos.

## 🚀 Características

- **Tablero 20x20**: Juego de tamaño intermedio con 60 minas
- **Interfaz moderna**: Diseño limpio y responsive
- **Cronómetro**: Seguimiento del tiempo de juego
- **Contador de minas**: Muestra las minas restantes
- **Marcado de minas**: Clic derecho para marcar/desmarcar
- **Revelado automático**: Algoritmo de inundación para celdas vacías
- **Colores diferenciados**: Números con colores según el estándar del buscaminas
- **Persistencia de datos**: Guardado y carga de partidas con SQLite
- **Múltiples dificultades**: Fácil, Intermedio y Difícil
- **Gestión de partidas**: Guardar, cargar y eliminar partidas

## 🎯 Cómo Jugar

1. **Clic izquierdo**: Revelar una celda
2. **Clic derecho**: Marcar/desmarcar una mina
3. **Objetivo**: Revelar todas las celdas sin minas
4. **Victoria**: Revelar todas las celdas seguras
5. **Derrota**: Hacer clic en una mina

## 🏗️ Arquitectura del Proyecto

### Principios SOLID Aplicados

- **SRP (Single Responsibility Principle)**: Cada clase tiene una responsabilidad única
- **OCP (Open/Closed Principle)**: El código está abierto para extensión, cerrado para modificación
- **DIP (Dependency Inversion Principle)**: Dependencias a través de abstracciones

### Estructura del Código

```
src/main/java/
├── aplication/
│   └── App.java                    # Punto de entrada de la aplicación
├── controller/
│   ├── BuscaminasController.java   # Controlador principal
│   └── EstilosUI.java             # Gestión centralizada de estilos
└── celda/
    ├── Celda.java                 # Representa una celda individual
    └── TableroBuscaminas.java     # Lógica del juego
```

### Clean Code Implementado

- **Nombres descriptivos**: Variables y métodos con nombres claros
- **Funciones pequeñas**: Cada método tiene una responsabilidad específica
- **Comentarios útiles**: Documentación donde es necesaria
- **Eliminación de duplicación**: Estilos centralizados en `EstilosUI`
- **Separación de responsabilidades**: Modelo, Vista y Controlador claramente separados

## 🎨 Características de la Interfaz

- **Botones pequeños**: 25x25 píxeles para optimizar espacio
- **Colores intuitivos**: 
  - Gris: Celdas no reveladas
  - Amarillo: Celdas marcadas
  - Rojo: Minas
  - Números: Colores según cantidad de minas adyacentes
- **Información en tiempo real**: Contador de minas y cronómetro
- **Instrucciones integradas**: Guía visual en la interfaz

## 🚀 Cómo Ejecutar

1. **Requisitos**: Java 21+ y Maven
2. **Compilar**: `mvn clean compile`
3. **Ejecutar**: `mvn javafx:run`

### Base de Datos SQLite
- **Automática**: La base de datos se crea automáticamente al ejecutar
- **Archivo local**: `buscaminas.db` se crea en el directorio del proyecto
- **Sin configuración**: No requiere servidor ni configuración adicional

## 🎯 Funcionalidades Técnicas

### Algoritmo de Inundación
- Revela automáticamente celdas adyacentes cuando se hace clic en una celda vacía
- Utiliza una pila para el recorrido eficiente
- Evita bucles infinitos con verificaciones de estado

### Generación de Minas
- Las minas se colocan aleatoriamente después del primer clic
- Garantiza que el primer clic nunca sea una mina
- Calcula automáticamente los números de minas adyacentes

### Gestión de Estado
- Control de victoria/derrota
- Reinicio completo del juego
- Persistencia del estado durante la sesión
- Guardado automático de partidas
- Carga y eliminación de partidas guardadas

## 🔧 Personalización

### Cambiar Dificultad
El juego incluye tres niveles de dificultad configurables:
- **Fácil**: 30 minas
- **Intermedio**: 60 minas (por defecto)
- **Difícil**: 90 minas

Para modificar las dificultades, edita el enum `Dificultad` en `BuscaminasController.java`.

### Cambiar Estilos
Modifica los métodos en `EstilosUI.java` para personalizar colores y apariencia.

## 📝 Notas de Desarrollo

- **Thread Safety**: El cronómetro se ejecuta en un hilo separado
- **Memory Management**: Uso eficiente de recursos con botones reutilizables
- **Error Handling**: Manejo robusto de excepciones
- **Performance**: Algoritmos optimizados para tableros grandes

## 🎉 Créditos

Desarrollado siguiendo principios de Clean Code y SOLID para crear un código mantenible, extensible y legible. 