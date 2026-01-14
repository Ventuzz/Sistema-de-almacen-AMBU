***Ctrl + shift + V* para preview**

# **INSTALACIÓN Y CONFIGURACIÓN DEL SISTEMA AMBU INVENTARIO**

Esta parte del documento describe el proceso completo para instalar y configurar el entorno necesario para ejecutar el sistema Ambu Inventario, utilizando MySQL y Java (JDK).

## **INSTALACIÓN DE MYSQL**
1. Abrir MySQL Installer.
2. Seleccionar los componentes:
   - MySQL Workbench
   - MySQL Server
3. Instalar los componentes siguiendo las instrucciones del instalador.

## **CONFIGURACIÓN DEL SERVIDOR MYSQL**
1. Configurar el servidor con las siguientes credenciales:
   Usuario: root
   Contraseña: admin
2. Asegurarse de que el puerto sea 3306 (por defecto).

## **CONEXIÓN DESDE MYSQL WORKBENCH**
1. Abrir MySQL Workbench.
2. Crear una nueva conexión con los siguientes datos:
   Hostname: localhost
   Port: 3306
   Username: root
   Password: admin
3. Verificar que la conexión sea exitosa antes de continuar.

## **IMPORTAR LA BASE DE DATOS**
1. Abrir los archivos de scripts SQL:
   - Query_AmbuDB.sql
   - Almacen.sql
2. En MySQL Workbench, ejecutar primero Query_AmbuDB.sql y luego Almacen.sql (usando el ícono del rayo).
3. Confirmar que las tablas se hayan creado correctamente.

## **VERIFICAR LA CONEXIÓN AL SERVIDOR**

1. En el panel de administración de MySQL Workbench, confirmar que el servidor esté activo.
2. Si hay errores, revisar el puerto, las credenciales o el estado del servicio.

## **INSTALACIÓN DE JAVA (JDK)**
1. Localizar el instalador JDK en la carpeta del proyecto.
2. Ejecutarlo y seguir los pasos de instalación.
3. Verificar la instalación con el comando:
   java -version

## **EJECUCIÓN DEL SISTEMA AMBU INVENTARIO**
1. Abrir la carpeta del proyecto.
2. Ejecutar el archivo correspondiente para iniciar Ambu Inventario.
3. Verificar que el sistema se conecte correctamente a la base de datos.

### **NOTA**
Si no hay conexión en el servidor, es posible que la contraseña configurada sea incorrecta
o que el puerto seleccionado no sea el adecuado. En caso de presentarse este problema,
repetir únicamente los pasos 1 y 2, realizando la configuración nuevamente solo del servidor MySQL.

____________________________________________________________________________________________________

# Funcionamiento del sistema
# -Inicio
Al empezar el programa nos encontraremos con la pantalla de login donde ingresaremos, ya sea como como un administrador o como un usuario general

<img width="609" height="404" alt="image" src="https://github.com/user-attachments/assets/37c03459-2bc8-40d3-ac9f-f4f0fcdab177" />

# -Paneles de Administrador
Ya dentro del perfil de administrador nos encontraremos con distindos paneles, los cuales podremos dividirlos en las siguientes secciones

## Tickets
En los paneles de tickets "**Ticket Insumos**", "**Ticket Combustible**" y "**Ticket Fluidos**", es donde podremos generar cualquier tipo de pedido dentro del almacen

1. En cualquier tipo de ticket, el sistema siempre requerirá el nombre del solicitante (Siendo este, un solicitante que fué fisicamente a generar un ticket)
2. Posteriormente deberemos seleccionar el producto a solicitar, ya sea un simple insumo, combustible o fluido
3. Después simplemente rellenaremos todos los requisitos para la entrega (Este paso es diferente en cada tipo de ticket, según sean los requerimientos necesarios de cada reporte)
4. Por último enviaremos la solicitud con el botón del mismo nombre "Enviar Solicitud"

<img width="911" height="1140" alt="image" src="https://github.com/user-attachments/assets/888fbdbb-5e5f-49cc-8a0c-ec133520e3f7" />

## General
En esta sección estarían los paneles "**Usuarios**" e "**Inventario**", los cuales nos ayudarán a gestionar que usuarios existen dentro del sistema y que artículos existen dentro del almacen

En el panel de Usuarios, nos encontraremos todos los usuarios registrados, tendremos la posibilidad de desactivar o activar algun usuario con la finalidad de gestionar quien puede entrar al sistema; además podemos asignarles un rol general o de administrador, añadir un nuevo socio al sistema y refrescar la misma lista

<img width="920" height="1140" alt="image" src="https://github.com/user-attachments/assets/b6500e53-823e-4ae8-bebb-ef95fec08d2d" />

En el panel de inventario será el lugar donde gestionaremos todo lo que se encuentra en el almacen. Tendremos la posibilidad de añadir o modificar cualquier artículo, además de poder eliminarlos del sistema siempre y cuando este no haya sido prestado anteriormente

<img width="920" height="1140" alt="image" src="https://github.com/user-attachments/assets/4e57cb1d-54c1-4265-969d-e0f214d3bf44" />

## Aprobaciones
En esta sección encontraremos los paneles "**Aprobaciones Insumos**", "**Aprobaciones Gasolina**" y "**Aprobaciones Fluidos**"

Todos estos apartados son la conexión directa con la sección de tickets, y tendremos la posibilidad de rechazar o aprobar cualquier ticket correspondiente y ver los detalles de cada solicitud

Con la excepción de **Aprobaciones Insumos** donde también podremos añadir insumos a un ticket ya creado anteiormente, con la intensión de minimizar la sobre carga de tickets generados en un mismo día

<img width="920" height="1140" alt="image" src="https://github.com/user-attachments/assets/7108933f-20a2-4406-aeb5-aa48ee1eac77" />

## Historial
Esta sección encontraremos los paneles "**Historial Insumos**", "**Historial Gasolina**" e "**Historial Fluidos**" los cuales nos otorgarán un resumen de todos los movimientos hechos tanto por usuarios generales como por el administrador del sistema. Ya sean solicitudes, entregas o devoluciones, todas tendran un espacio dentro del historial.

En estos paneles tendremos las opciones de: devolución de artículos, que nos permitiran generar devoluciones parciales de cada articulo "en prestamo" y la posibilidad de exportar los datos del historial a un excel, donde solamente deberemos elegir la ruta en donde guardaremos nuestro documento y asignarle un nombre

<img width="920" height="1140" alt="image" src="https://github.com/user-attachments/assets/77f3c37c-afd7-4d3e-bab9-9935cfe5774c" />

# -Paneles de Usuarios
## Solicitudes
En esta sección, encontraremos los paneles "**Solicitud Insumos**", "**Solicitud Combustible**" y "**Solicitud Fluidos**", donde como usuario general podremos generar los tickets de cualquier artículo, donde solamente deberemos poner los detalles necesarios para completar la solicitud, la cual posteriormente aparecerá dentro del **Panel de Administrador** para su futuro seguimiento

<img width="916" height="1137" alt="image" src="https://github.com/user-attachments/assets/c69f6142-196a-4703-bbf2-0ca0d4f07b19" />

## Historiales
En esta parte veremos los paneles "**Historial Insumos**", "**Historial Gasolina**" e "**Historial Fluidos**", estos al igual que en el panel de administrador, serán un resumen de los registros de las solicitudes, entregas y devoluciones, con la posibilidad de exportar los datos a un excel. Sin embargo estos registros serán completamente personales, al contrario de los historiales de administrador donde aparecen los movimientos de todos los usuarios

<img width="920" height="1140" alt="image" src="https://github.com/user-attachments/assets/52d2724e-b632-419d-9b75-aedb87c3f293" />

____________________________________________________________________________________________________

# **Backups**

El primer paso para realizar una copia de seguridad es abrir una terminal como administrador, tienes que ir a la ruta donde tienes instalado el MySQL server y dirigirte a la carpeta bin
como se observa en la siguiente imagen. 

<img width="557" height="136" alt="image" src="https://github.com/user-attachments/assets/9d91ba21-4e65-4f08-8b33-007a8ac9749c" />

Dentro de nuestra carpeta bin vamos a ir a nuestra terminar y adaptar el siguiente comando;

mysqldump -u (Ingresa tu usuario) -p (Ingresa el nombre de tu base de datos) > amudb_backup.sql (nombre que le quieres dar al backup)

-u = se remplaza por el usuario

-p = este parametro es para que te solicite tu contraseña

-ambudb = El nombre de tu database

<img width="691" height="76" alt="image" src="https://github.com/user-attachments/assets/759cb9ca-803d-45f8-94df-3a2858f249ea" />

**Comando**

" mysqldump -u admin -p ambudb > backup.sql "

Una vez que corras el comando se te pedirá tu contraseña, despues de ingresarla y presionar ENTER se generará el backup en la carpeta bin

<img width="618" height="85" alt="image" src="https://github.com/user-attachments/assets/08b1ea9f-77dd-4917-a2d8-bf3730f2351e" />

# **Cargar un backup**

Para cargar un backup se necesita abrir una terminal como administrador en la misma ruta bin dentro de la carpeta de MySQL server

<img width="549" height="104" alt="image" src="https://github.com/user-attachments/assets/183f0eb6-df67-4310-b1d0-d59b53078afa" />


Una vez ahí se tendrá que adaptar el siguiente comando;

mysql -u (Ingresa tu usuario) -p ambudb(Ingresa el nombre de tu base de datos)  < amudb_backup.sql (Nombre del archivo que quieres cargar)

-u = Se remplaza por tu usuario

-p = Se te solicitará tu contraseña

-ambudb = El nombre de tu database

ambudb_backup.sql = El nombre de tu archivo que quieres cargar


<img width="654" height="48" alt="image" src="https://github.com/user-attachments/assets/be90e571-0912-4c70-9564-11aaae106483" />

**Comando**

" mysql -u -p ambudb < amudb_backup.sql "


Una vez hecho este comando la base de datos se actualizará por la del archivo especificado.
