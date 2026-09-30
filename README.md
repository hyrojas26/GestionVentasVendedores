# GestionVentasVendedores

Proyecto del Módulo: procesamiento de archivos planos de ventas de vendedores.
**Versión: entrega 2 (versión preliminar del proyecto completo).**

## Estructura

Dos clases con método `main` (Java 8, Eclipse):

| Clase | Función |
|-------|---------|
| `GenerateInfoFiles` | Genera los archivos de prueba pseudoaleatorios: `productos.txt`, `vendedores.txt` y un `ventas_CC_<id>.txt` por vendedor. |
| `main` | Lee esos archivos y genera `reporte_vendedores.csv` y `reporte_productos.csv`. |

## Cómo ejecutar

1. Ejecutar `GenerateInfoFiles` (Run As > Java Application). Crea los archivos de entrada en la raíz del proyecto.
2. Ejecutar `main`. Crea los dos reportes en la raíz del proyecto.

Ninguno de los dos programas solicita datos al usuario; ambos muestran un mensaje de éxito o de error.

## Reportes generados

- `reporte_vendedores.csv`: `Nombres Apellidos;DineroRecaudado`, de mayor a menor recaudo.
- `reporte_productos.csv`: `NombreProducto;Precio`, de mayor a menor cantidad vendida (solo productos con ventas).

## Requisitos cubiertos

- [x] Documentación Javadoc en clases, métodos y atributos.
- [x] Nombres de variables y espaciado según buenas prácticas.
- [x] Reporte de vendedores ordenado por recaudo (punto 3).
- [x] Reporte de productos ordenado por cantidad vendida (punto 4).
- [x] Métodos de generación: `createSalesMenFile`, `createProductsFile`, `createSalesManInfoFile` (punto 5).
- [x] Extra a: más de un archivo de ventas por vendedor (las ventas se acumulan por documento).
- [x] Extra c: detección de formato erróneo e información incoherente (producto inexistente, cantidad no válida o negativa, vendedor no registrado, encabezado inválido, precio negativo). Las líneas inválidas se omiten y se informan por consola.

## Partes que faltan (pendientes de esta entrega)

1. **Archivos serializados (extra b):** aún no se puede trabajar con archivos serializados.
2. **Pruebas automatizadas:** faltan pruebas unitarias (por ejemplo, JUnit) para las validaciones y el ordenamiento. Hasta ahora solo se probó ejecutando los programas con datos generados y con datos erróneos puestos a mano.
3. **Prueba con gran volumen:** falta medir el rendimiento con muchos vendedores y muchos archivos.
4. **Rediseño en más clases:** la lógica de `main` está en una sola clase; falta separarla (por ejemplo, lectores de archivos, modelo de datos y escritores de reportes) para mejorar el diseño.
5. **Advertencias en archivo:** las advertencias de datos inválidos solo se muestran por consola; falta guardarlas en un archivo de registro.
6. **Precios decimales y formato regional:** se asume punto como separador decimal; falta contemplar otros formatos.
7. **Revisión final de documentación:** falta generar el Javadoc en HTML y hacer una revisión final del estilo del código.
8. **Datos de prueba:** falta ampliar las listas de nombres y productos, y permitir otros tipos de documento además de `CC`.
