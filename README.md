EpubSearcherAndroidApp
======================

A small Android App to seek for epub files in our Dropbox account

Dificultades:

* Al ser mi primera aplicación Android, me ha costado un poco enterarme del flujo entre actividades, y sobre todo cuando saber
  que había que utilizar una tarea asíncrona para poder llevar a cabo ciertas funciones. Eso me ha ralentizado mucho.
  
* No saber como colocar listener en los objetos del todo bien, en especial en un GridView que contiene un RelativeLayout

Ha quedado por implementar:

Punto 4: Mostrar carátula con doble click. He visto que la manera de implementarlo más correcta es con un Listener que he implementado,
pero no he conseguido que funcione, aunque capturaba el evento onTouch, el gestureDetector devolvía siempre false.

Posibles mejoras:

* Interfaz poco amigable.

* Guardar un xml protegido por cada usuario que guardase los paths y los epubs y las carátulas, para que poder acceder de un modo
  "offline".

* Poder añadir epubs desde la aplicación.

* Poder editar los atributos de un epub.

