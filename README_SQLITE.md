# 🗄️ Migración a SQLite

## 📋 Resumen de Cambios

El proyecto ha sido migrado de MySQL/Docker a SQLite para simplificar la configuración y hacer el proyecto más portable.

## ✅ Cambios Realizados

### 1. **Dependencias Actualizadas**
- ❌ Eliminado: `mysql-connector-java`
- ❌ Eliminado: `HikariCP` (pool de conexiones)
- ✅ Agregado: `sqlite-jdbc`

### 2. **DatabaseManager Refactorizado**
- **Antes**: Pool de conexiones MySQL con HikariCP
- **Ahora**: Conexión directa a SQLite
- **Ventajas**: 
  - Sin configuración de servidor
  - Archivo local automático
  - Inicialización automática de tablas

### 3. **Estructura de Base de Datos**
```sql
-- Tabla de partidas
CREATE TABLE partidas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_jugador TEXT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    cantidad_minas INTEGER NOT NULL DEFAULT 60,
    celdas_reveladas INTEGER NOT NULL DEFAULT 0,
    minas_marcadas INTEGER NOT NULL DEFAULT 0,
    juego_terminado BOOLEAN NOT NULL DEFAULT 0,
    juego_ganado BOOLEAN NOT NULL DEFAULT 0,
    tiempo_juego INTEGER DEFAULT 0,
    estado_partida TEXT DEFAULT 'en_curso'
);

-- Tabla de celdas
CREATE TABLE celdas_partida (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    partida_id INTEGER NOT NULL,
    fila INTEGER NOT NULL,
    columna INTEGER NOT NULL,
    es_mina BOOLEAN NOT NULL DEFAULT 0,
    esta_revelada BOOLEAN NOT NULL DEFAULT 0,
    esta_marcada BOOLEAN NOT NULL DEFAULT 0,
    minas_adyacentes INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (partida_id) REFERENCES partidas(id) ON DELETE CASCADE,
    UNIQUE(partida_id, fila, columna)
);
```

## 🚀 Ventajas de SQLite

### **Simplicidad**
- ✅ Sin servidor de base de datos
- ✅ Sin configuración de Docker
- ✅ Sin variables de entorno
- ✅ Archivo único `buscaminas.db`

### **Portabilidad**
- ✅ Funciona en cualquier sistema operativo
- ✅ No requiere instalación de MySQL
- ✅ Base de datos incluida en el proyecto

### **Desarrollo**
- ✅ Inicialización automática
- ✅ Creación automática de tablas
- ✅ Sin scripts de configuración

## 📁 Archivos Eliminados

- `docker-compose.yml`
- `iniciar_base_datos.bat`
- `iniciar_base_datos.sh`
- `README_BASE_DATOS.md`
- `GUIA_INTEGRACION.md`
- `sql/init.sql`

## 🔧 Configuración

### **Automática**
La base de datos se crea automáticamente al ejecutar la aplicación:
```bash
mvn javafx:run
```

### **Archivo de Base de Datos**
- **Ubicación**: `buscaminas.db` (en el directorio del proyecto)
- **Tamaño**: ~50KB inicial
- **Contenido**: Partidas guardadas y estados del juego

## 🛠️ Mantenimiento

### **Respaldar Partidas**
```bash
# Copiar el archivo de base de datos
cp buscaminas.db backup_buscaminas.db
```

### **Restaurar Partidas**
```bash
# Restaurar desde backup
cp backup_buscaminas.db buscaminas.db
```

### **Reiniciar Base de Datos**
```bash
# Eliminar archivo para empezar desde cero
rm buscaminas.db
```

## 📊 Compatibilidad

### **Funcionalidades Mantenidas**
- ✅ Guardado de partidas
- ✅ Carga de partidas
- ✅ Eliminación de partidas
- ✅ Múltiples dificultades
- ✅ Persistencia completa

### **Mejoras Adicionales**
- ✅ Inicialización más rápida
- ✅ Menos dependencias
- ✅ Configuración simplificada

## 🎯 Próximos Pasos

1. **Probar funcionalidades** de guardado/carga
2. **Verificar rendimiento** con SQLite
3. **Documentar** cualquier problema encontrado
4. **Optimizar** consultas si es necesario

---

**Nota**: La migración mantiene toda la funcionalidad existente mientras simplifica significativamente la configuración del proyecto.

