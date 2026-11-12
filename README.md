# Carva-X — Backend

API REST en **Java 25 + Spring Boot 4.1.1** con **PostgreSQL**, autenticación por **JWT** (HS512) y documentación con **Swagger / Scalar**.

**Documentación de la API desplegada:** https://carva-x.onrender.com/scalar.html

**Frontend desplegado (probar toda la funcionalidad):** https://amazing-nougat-a719ff.netlify.app/#/catalogo

Módulos actuales:

| Módulo | Ruta base | Descripción |
|---|---|---|
| Autenticación | `/api/auth` | Registro e inicio de sesión (devuelve token JWT) |
| Productos | `/api/products` | CRUD de productos (crear/editar/eliminar solo rol `admin`) |
| Lista de deseos | `/api/wishlist` | Productos guardados por el usuario autenticado |

---

## 1. Requisitos previos

| Herramienta | Versión | Uso |
|---|---|---|
| JDK | 25 | Compilar y ejecutar |
| Maven | Incluido (`mvnw` / `mvnw.cmd`) | No hace falta instalarlo |
| PostgreSQL | 14 o superior | Base de datos (local o en la nube, ej. Render) |
| Docker + Docker Compose | Opcional | Despliegue en contenedor |
| Git | Cualquiera | Clonar el repositorio |

Verificar:

```bash
java -version
docker --version
```

---

## 2. Variables de entorno (`.env`)

La aplicación lee un archivo `.env` en la raíz del proyecto. `CarvaXApplication.main` lo carga con **dotenv-java antes de arrancar Spring** y `application.properties` resuelve cada valor como `${VARIABLE}`.

| Variable | Obligatoria | Descripción | Ejemplo |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | Sí | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/carva_x` |
| `SPRING_DATASOURCE_USERNAME` | Sí | Usuario de la BD | `carva_x_user` |
| `SPRING_DATASOURCE_PASSWORD` | Sí | Contraseña de la BD | `********` |
| `JWT_SECRET` | Sí | Clave de firma HS512. **Mínimo 64 caracteres** (512 bits) | cadena aleatoria larga |
| `JWT_EXPIRATION` | Sí | Vida del token en **milisegundos** | `3000000` (= 50 min) |
| `CORS_ALLOWED_ORIGINS` | No | Orígenes del frontend permitidos, separados por coma. Por defecto `http://localhost:3000` | `https://mi-front.netlify.app,http://localhost:5173` |

Plantilla para un entorno nuevo:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/carva_x
SPRING_DATASOURCE_USERNAME=carva_x_user
SPRING_DATASOURCE_PASSWORD=cambia_esta_clave
JWT_SECRET=pon-aqui-una-cadena-aleatoria-de-al-menos-64-caracteres-para-hs512-0123456789
JWT_EXPIRATION=3000000
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

### 2.1 Credenciales del entorno actual (producción en Render)

Valores vigentes del archivo `.env` del repositorio:

| Dato | Valor |
|---|---|
| Proveedor BD | Render — PostgreSQL (región Oregon) |
| Host | `dpg-dbcli30473hc739f3ni0-a.oregon-postgres.render.com` |
| Puerto | `5432` |
| Base de datos | `carva_x` |
| Usuario | `carva_x_user` |
| Contraseña | `j5LeGyh0v6DpkDszyAJ9pDzfdO9Kzc4T` |
| URL JDBC | `jdbc:postgresql://dpg-dbcli30473hc739f3ni0-a.oregon-postgres.render.com/carva_x` |
| JWT secret | `clave-secreta-super-larga-de-al-menos-256-bits-cambiala-en-produccion-1234567890` |
| JWT expiración | `3000000` ms (50 minutos) |
| Frontend permitido (CORS) | `https://amazing-nougat-a719ff.netlify.app` |

Conexión directa desde consola (útil para ejecutar los scripts de la sección 3):

```bash
psql "postgresql://carva_x_user:j5LeGyh0v6DpkDszyAJ9pDzfdO9Kzc4T@dpg-dbcli30473hc739f3ni0-a.oregon-postgres.render.com:5432/carva_x?sslmode=require"
```

> **Advertencia de seguridad.** El archivo `.env` está versionado en Git y este README publica las credenciales. Cualquiera con acceso al repositorio puede entrar a la base de datos y firmar tokens válidos. Recomendado antes de un despliegue real:
> 1. Rotar la contraseña de la BD desde el panel de Render y generar un `JWT_SECRET` nuevo.
> 2. Agregar `.env` a `.gitignore` y sacarlo del índice con `git rm --cached .env`.
> 3. Configurar las variables directamente en el panel del proveedor (Render, Railway, etc.) en lugar de subir el archivo.

---

## 3. Base de datos

### 3.1 Cómo se crea el esquema

El proyecto **no usa migraciones**. Hibernate genera y actualiza las tablas a partir de las entidades al arrancar (`spring.jpa.hibernate.ddl-auto=update`). Basta con que la base de datos y el usuario existan: las tablas se crean solas en el primer arranque.

Los scripts siguientes sirven para crear todo a mano (servidor propio, revisión del esquema o entornos donde se quiera desactivar `ddl-auto`).

### 3.2 Script 1 — Crear usuario y base de datos

Ejecutar conectado como superusuario (`postgres`). En Render este paso no aplica: la BD y el usuario los crea el proveedor.

```sql
-- 01_create_database.sql
CREATE USER carva_x_user WITH PASSWORD 'cambia_esta_clave';

CREATE DATABASE carva_x
    WITH OWNER = carva_x_user
         ENCODING = 'UTF8'
         TEMPLATE = template0;

GRANT ALL PRIVILEGES ON DATABASE carva_x TO carva_x_user;
```

Luego conectarse a la nueva base (`\c carva_x`) y dar permisos sobre el esquema:

```sql
GRANT ALL ON SCHEMA public TO carva_x_user;
```

### 3.3 Script 2 — Crear tablas

Equivalente al esquema que genera Hibernate desde `entity/User.java`, `entity/Product.java` y `entity/WishlistItem.java`:

```sql
-- 02_create_tables.sql
CREATE TABLE IF NOT EXISTS users (
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL,      -- hash BCrypt, nunca texto plano
    addres    VARCHAR(255) NOT NULL,      -- (sic) asi se llama el campo en la entidad
    role      VARCHAR(255) NOT NULL,      -- 'admin' o 'user'
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS products (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255)     NOT NULL,
    price       DOUBLE PRECISION NOT NULL,
    amount      INTEGER          NOT NULL,   -- cantidad en inventario
    description VARCHAR(255)     NOT NULL,
    tax         DOUBLE PRECISION NOT NULL,   -- impuesto
    CONSTRAINT uk_products_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS wishlist_items (
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    CONSTRAINT uk_wishlist_user_product UNIQUE (user_id, product_id),
    CONSTRAINT fk_wishlist_user    FOREIGN KEY (user_id)    REFERENCES users (id),
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products (id)
);
```

> Si se crean las tablas con este script y se deja `ddl-auto=update`, Hibernate no las duplica: solo agrega columnas que falten. Para que Hibernate solo verifique el esquema, cambiar a `spring.jpa.hibernate.ddl-auto=validate`.

### 3.4 Script 3 — Datos iniciales (opcional)

```sql
-- 03_seed_products.sql
INSERT INTO products (name, price, amount, description, tax) VALUES
    ('Chevrolet Spark GT', 120000, 3, 'Compacto urbano, 5 puertas, mecanico', 0.19),
    ('Mazda 3 Touring',    210000, 2, 'Sedan automatico, 2.0L',               0.19),
    ('Toyota Fortuner',    350000, 1, 'SUV 4x4, 7 puestos, diesel',           0.19)
ON CONFLICT (name) DO NOTHING;
```

**Usuario administrador.** Las contraseñas se guardan con BCrypt, así que no se insertan por SQL en texto plano. Crear el admin con el endpoint de registro (sección 6):

```bash
curl -X POST http://localhost:8085/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","email":"admin@carvax.com","password":"Admin123*","addres":"Sede principal","role":"admin"}'
```

Comprobar el resultado:

```sql
SELECT id, name, email, role FROM users;
```

---

## 4. Despliegue local (sin Docker)

1. Clonar el repositorio y entrar a la carpeta:

   ```bash
   git clone <url-del-repositorio>
   ```

   ```bash
   cd carva-x
   ```

2. Crear el archivo `.env` en la raíz (ver sección 2) apuntando a una BD accesible.
3. Compilar y ejecutar:

   ```bash
   ./mvnw spring-boot:run
   ```

   En Windows (PowerShell / CMD):

   ```bash
   mvnw.cmd spring-boot:run
   ```

4. La API queda en **http://localhost:8085**.

### Generar el JAR para un servidor

```bash
./mvnw clean package -DskipTests
```

```bash
java -jar target/carva-x-0.0.1-SNAPSHOT.jar
```

El `.env` debe estar en el **directorio desde el que se ejecuta** `java -jar`. Si no existe, dotenv lo ignora (`ignoreIfMissing`) y las variables se toman del sistema operativo, lo que permite configurarlas en el panel del proveedor.

---

## 5. Despliegue con Docker

El `Dockerfile` es multi-etapa: compila con `maven:3.9-eclipse-temurin-25` y ejecuta con `eclipse-temurin:25-jre-alpine` bajo un usuario sin privilegios (`spring`).

```bash
docker compose -f Docker-compose.yaml up --build -d
```

- El archivo se llama `Docker-compose.yaml` (con D mayúscula), por eso se pasa con `-f`.
- `env_file: .env` inyecta las variables al contenedor.
- Publica el puerto `8085:8085`.

Comandos útiles:

```bash
docker compose -f Docker-compose.yaml logs -f app
```

```bash
docker compose -f Docker-compose.yaml down
```

Sin Compose:

```bash
docker build -t carva-x .
```

```bash
docker run -d --name carva-x -p 8085:8085 --env-file .env carva-x
```

### Despliegue en Render (Web Service)

1. New → **Web Service** → conectar el repositorio. Runtime: **Docker**.
2. En **Environment** cargar las variables de la sección 2 (no subir `.env`).
3. Agregar `PORT=8085` o bien ajustar `server.port` al puerto que asigne Render.
4. Usar la **Internal Database URL** de Render en `SPRING_DATASOURCE_URL` si la API y la BD están en la misma región (más rápido y sin salir a internet).
5. Incluir la URL del frontend en `CORS_ALLOWED_ORIGINS`.

---

## 6. Verificación del despliegue

| Recurso | Local | Producción (Render) |
|---|---|---|
| Scalar | http://localhost:8085/scalar.html | https://carva-x.onrender.com/scalar.html |
| Swagger UI | http://localhost:8085/swagger-ui.html | https://carva-x.onrender.com/swagger-ui.html |
| OpenAPI JSON | http://localhost:8085/v3/api-docs | https://carva-x.onrender.com/v3/api-docs |
| Frontend (catálogo) | http://localhost:3000 | https://amazing-nougat-a719ff.netlify.app/#/catalogo |

El frontend en Netlify consume esta API y permite probar el flujo completo: registro, login, catálogo de productos, administración (rol `admin`) y lista de deseos. Su origen ya está en `CORS_ALLOWED_ORIGINS`.

> En el plan gratuito de Render el servicio se suspende tras ~15 minutos sin tráfico; la primera petición puede tardar 30–60 s mientras arranca.

Prueba rápida de punta a punta:

```bash
# 1. Login -> copiar el token de data.accessToken
curl -X POST http://localhost:8085/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@carvax.com","password":"Admin123*"}'
```

```bash
# 2. Consultar productos con el token
curl http://localhost:8085/api/products -H "Authorization: Bearer <TOKEN>"
```

### Endpoints

Todos los endpoints de productos y wishlist exigen el header `Authorization: Bearer <token>`.

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | público | Registrar usuario |
| POST | `/api/auth/login` | público | Iniciar sesión |
| GET | `/api/products` | autenticado | Listar productos |
| GET | `/api/products/{productId}` | autenticado | Detalle de producto |
| POST | `/api/products` | `admin` | Crear producto |
| PUT | `/api/products/{productId}` | `admin` | Actualizar producto |
| DELETE | `/api/products/{productId}` | `admin` | Eliminar producto |
| GET | `/api/wishlist` | autenticado | Ver mi lista de deseos |
| POST | `/api/wishlist/products/{productId}` | autenticado | Agregar producto |
| DELETE | `/api/wishlist/products/{productId}` | autenticado | Quitar producto |
| PUT | `/api/wishlist/products/{productId}/replace/{replacementProductId}` | autenticado | Reemplazar un producto por otro |

Formato de respuesta (éxito y error):

```json
{ "result": true,  "data": { } }
{ "result": false, "data": { "message": "Producto no encontrado", "statusCode": 404 } }
```

---

## 7. Comentarios adicionales y problemas conocidos

- **Puerto inconsistente.** `application.properties` y `Docker-compose.yaml` usan **8085**, pero el `Dockerfile` declara `EXPOSE 8084`. No rompe nada (`EXPOSE` es informativo), pero conviene cambiarlo a `8085`.
- **Healthcheck sin Actuator.** El compose consulta `/actuator/health`, pero `spring-boot-starter-actuator` no está en el `pom.xml`, así que el contenedor se marcará `unhealthy`. Solución: agregar la dependencia o quitar el bloque `healthcheck`.
- **Rol libre en el registro.** `/api/auth/register` acepta el campo `role` del cliente, por lo que cualquiera puede registrarse como `admin`. Para producción, forzar `role = "user"` en el registro y crear los administradores por otra vía.
- **`JWT_SECRET` y HS512.** La librería JJWT exige una clave de al menos 512 bits (64 bytes) para HS512; con una más corta el login falla con `WeakKeyException`.
- **`JWT_EXPIRATION` va en milisegundos**, no en segundos.
- **SSL con Render.** Si la conexión externa a la BD de Render falla, agregar `?sslmode=require` al final de `SPRING_DATASOURCE_URL`.
- **`spring.jpa.show-sql=true`** imprime cada consulta SQL en el log. Útil en desarrollo; desactivarlo en producción.
- **Tests.** `./mvnw test` levanta el contexto completo y necesita una BD accesible con las variables del `.env`.
- **CORS con credenciales.** Como `allowCredentials(true)` está activo, `CORS_ALLOWED_ORIGINS` no admite `*`: hay que listar cada origen exacto.
