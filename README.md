# IoT Dev (Talento Futuro)

El objetivo de este proyecto es la implementaci&oacute;n de una API capaz de recibir, procesar y almacenar datos transmitidos por dispositivos IoT.  
Desarrollada en Java y dise&ntilde;ada para ser desplegada utilizando Docker y Fly.io. 

## 🚀 Tecnolog&iacute;as Utilizadas

- **Java 21** 
- **SpringBoot**
- **Docker**
- **Maven**
- **GitHub Actions** (para CI/CD)
- **Fly.io** (configuraci&oacute;n mediante `fly.toml`)

## 📁 Estructura del Proyecto

- `src/`: C&oacute;digo fuente principal de la aplicaci&oacute;n.
- `.env.example`: Archivo de ejemplo para variables de entorno.
- `Dockerfile`: Configuraci&oacute;n para construir la imagen Docker.
- `compose.yaml`: Definici&oacute;n de servicios de base de datos PostgreSQL para Docker Compose.
- `.github/workflows/`: Flujos de trabajo para integraci&oacute;n continua.
- `fly.toml`: Configuraci&oacute;n para despliegue en Fly.io.


## ⚙️ Configuraci&oacute;n y Despliegue

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/CristobalNPE/iot-api-dev.git
   cd iot-api-dev
   ```

2. **Configurar variables de entorno:**

   Configurar las variables de entorno indicadas en el archivo `.env.example` seg&uacute;n el sistema operativo correspondiente.  

   ```bash
   #Windows
   set DOCKER_ENABLED=false
   #Linux
   export DOCKER_ENABLED='false'
   ```

3. **Configurar contenedor conteniendo base de datos PostgreSQL con Docker Compose:**

   ```bash
   docker-compose up --build
   ```

4. **Desplegar en Fly.io:**

   Asegurarse de tener la CLI de Fly.io instalada y configurada. Luego, ejecutar:

   ```bash
   fly deploy
   ```

## 🧪 Pruebas

Para ejecutar las pruebas, utiliza Maven:

```bash
./mvnw test
```
## 📚 Documentaci&oacute;n

La documentaci&oacute;n con el detalle de endpoints de esta API se encuentra disponible en el siguiente enlace:

🔗 [cristobalnpe.github.io/iot-api-dev/](https://cristobalnpe.github.io/iot-api-dev/)

