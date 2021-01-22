# Puzzle Engine - Proyecto desarrollado como examen para SITRACK


Esta es una engine que genera aleatoriamente una sopa
de letras, con la posibilidad de que estas palabras estén
ubicadas horizontalmente, verticalmente y en diagonal de
izquierda a derecha o derecha a izquierda, de arriba hacia abajo
o de abajo hacia arriba. La generación de la sopa de letra puede 
ser de distintos tamaños tanto en ancho como en alto sin necesidad 
de que la misma sea cuadrada, esto dos valores (ancho y alto) no
pueden ser menores a 15 letras y mayores a 80.
Esta engine permite interactuar para indicarle toda la parametrización, 
tanto las alternativas válidas para acomodar las palabras como el 
tamaño de la sopa de letras.
Una vez generada la sopa de letras, esta puede ser visualizada de
la siguiente forma:

|h|s|d|f|g|
|a|o|d|f|g|
|a|s|l|f|g|
|a|s|d|a|g|

Una vez que se puede visualizar la sopa de letras se le pude indicar 
al sistema que se ha encontrado una palabra indicando el par
fila y columna inicial de la palabra y el par final, el sistema tiene 
que poder indicar si la palabra es correcta y si el path de la
palabra es correcto se transforma la palabra encontrada en mayusculas

Ejemplo
|H|s|d|f|g|
|a|O|d|f|g|
|a|s|L|f|g|
|a|s|d|A|g|

EndPoints:

POST http://host/alphabetSoup/
{
“w”:15,
“h”:15,
“ltr”:true,
“rtl”:true,
“ttb”:true,
“btt”:true,
“d”:true
}
w - Ancho de la sopa de letras, valor opcional por defecto debe ser 15
h - Largo de la sopa de letras, valor opcional pode defecto debe ser 15
ltr - Habilitar o deshabilitar palabras que van de izquierda a derecha, valor opcional por
defecto debe ser true
rtl - Habilitar o deshabilitar palabras que van de derecha a izquierda, valor opcional por
defecto debe ser false
ttb - Habilitar o deshabilitar palabras que van desde arriba hacia abajo, valor opcional por
defecto debe ser true
btt - Habilitar o deshabilitar palabras que van desde abajo hacia arriba, valor opcional por
defecto debe ser false
d - Habilitar o deshabilitar palabras diagonales, valor por opcional por defecto debe ser false
Este endpoint devuelve un json que indica el id
autogenerado para la sopa que se acaba de crear, el id tiene
formato UUID, en caso de que falle debe indicarse en el
código http como un error 400 y en el body un json que indique
el mensaje del error.
En caso satisfactorio:
{
“id”:”d041eaf2-0ac2-4376-812b-3e08be0bfd65”
}
En caso de error:
{
“message”:”Mensaje de error”
}

Endpoint para visualizar la lista de palabras
GET http://host/alphabetSoup/list/d041eaf2-0ac2-4376-812b-3e08be0bfd65
Este endpoint devuelve la lista de palabras que se
encuentran en la sopa de letras en formato json
Endpoint para visualizar la sopa de letras
GET http://host/alphabetSoup/view/d041eaf2-0ac2-4376-812b-3e08be0bfd65
Este endpoint debe devolver el formato de texto plano indicado
en los puntos anteriores con las palabras ya encontradas hasta
el momento.
Endpoint para indicar que hemos encontrado una palabra
PUT http://host/alphabetSoup/d041eaf2-0ac2-4376-812b-3e08be0bfd65
{
“sr”:0,
“sc”:0,
“er”:10,
“ec”:10
}
sr - Fila donde comienza la palabra encontrada
sc - Columna donde comienza la palabra encontrada
er - Fila donde termina la palabra encontrada
ec - Columna donde termina la palabra encontrada
Este endpoint debe devolver un json con un mensaje indicando
si la palabra encontrada es correcta o no y modificar el estado
de la sopa de letras


## Comenzando 🚀

Este es un proyecto maven desarrollado en JAVA, que hace uso de SpringBoot, JPA.


### Pre-requisitos 📋

Tener instalado el eclipse para desarrolladores JEE



### Instalación 🔧

Generamos el componente jar (spring boot) el cual expone servicios rest que permiten 
interactuar con el engine del juego sopa de letras

    mvn clean compile packege -PDocker

Lo anterior puede ser realizado en el mismo eclipse.
Una vez terminado el proceso anterior, nos dirigimos a la carpeta target 

    cd target

y ejecutarmos el engine de la siguiente forma.

    java -jar puzzle_engine-1.0.0.jar

Lo anterior disponibilizará en engine en el puerto local 8081.


## Despliegue 📦

Al presente tambien se agrega una shell y configuración necesaria para poder desplegar 
este componente con docker

puzzle-engine.sh: Shell que permite un rapido despliegue del componente
Dockerfile: Archivo de configuración de docker

Al ser desplegado en un container docker se podra acceder a el desde el puerto local 80, 
el cual esta bindiado al puerto 8081 del container (puerto donde estan expuestos los servicios).


## Construido con 🛠️

_Menciona las herramientas que utilizaste para crear tu proyecto_

* [Eclipse](https://www.eclipse.org/) - IDE de preferencia
* [Maven](https://maven.apache.org/) - Manejador de dependencias
* [Spring boot](https://start.spring.io/) - Generador de proyectos

