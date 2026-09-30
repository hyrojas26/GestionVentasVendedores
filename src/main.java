import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Programa principal: lee los archivos de productos, vendedores y ventas
 * (uno o varios por vendedor) y genera dos reportes en formato CSV:
 * <ul>
 *   <li>{@code reporte_vendedores.csv}: {@code Nombre Apellidos;DineroRecaudado},
 *       ordenado de mayor a menor recaudo.</li>
 *   <li>{@code reporte_productos.csv}: {@code NombreProducto;Precio},
 *       ordenado de mayor a menor cantidad vendida.</li>
 * </ul>
 *
 * <p>Detecta archivos con formato erróneo o información incoherente (producto
 * inexistente, cantidades o precios negativos, vendedor desconocido). Las
 * líneas inválidas se omiten y se informan por consola.</p>
 *
 * <p>Este programa no solicita información al usuario.</p>
 *
 * @version 1.0
 */
public class main {

    /** Archivo de reporte de vendedores. */
    private static final String SALESMEN_REPORT_NAME = "reporte_vendedores.csv";

    /** Archivo de reporte de productos. */
    private static final String PRODUCTS_REPORT_NAME = "reporte_productos.csv";

    /** Prefijo de los archivos de ventas. */
    private static final String SALES_FILE_PREFIX = "ventas_";

    /** Extensión de los archivos de ventas. */
    private static final String SALES_FILE_EXTENSION = ".txt";

    /** Separador de campos. */
    private static final String SEPARATOR = ";";

    /** Carpeta del proyecto donde se buscan los archivos. */
    private static final String PROJECT_FOLDER = ".";

    /** Mensajes de advertencia acumulados durante el procesamiento. */
    private static final List<String> WARNINGS = new ArrayList<String>();

    /** Producto del catálogo, con su cantidad total vendida. */
    private static class Product {
        private final String name;
        private final double price;
        private long unitsSold;

        Product(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    /** Vendedor, con el dinero total recaudado. */
    private static class Salesman {
        private final String fullName;
        private double totalRevenue;

        Salesman(String fullName) {
            this.fullName = fullName;
        }
    }

    /**
     * Punto de entrada. Ejecuta todo el proceso y muestra un mensaje de éxito
     * o de error.
     *
     * @param args argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        try {
            Map<String, Product> products = loadProducts(GenerateInfoFiles.PRODUCTS_FILE_NAME);
            Map<String, Salesman> salesmen = loadSalesmen(GenerateInfoFiles.SALESMEN_FILE_NAME);
            processSalesFiles(salesmen, products);
            writeSalesmenReport(salesmen);
            writeProductsReport(products);
            for (String warning : WARNINGS) {
                System.err.println("Advertencia: " + warning);
            }
            System.out.println("Reportes generados exitosamente.");
        } catch (IOException | RuntimeException e) {
            System.err.println("Error al procesar los archivos: " + e.getMessage());
        }
    }

    /**
     * Carga el catálogo de productos. Omite líneas con formato erróneo o con
     * precio negativo.
     *
     * @param fileName nombre del archivo de productos
     * @return mapa de ID de producto a producto
     * @throws IOException si no se puede leer el archivo
     */
    private static Map<String, Product> loadProducts(String fileName) throws IOException {
        Map<String, Product> products = new HashMap<String, Product>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split(SEPARATOR);
                if (fields.length < 3 || !isNumber(fields[2]) || Double.parseDouble(fields[2]) < 0) {
                    WARNINGS.add(fileName + " línea " + lineNumber + ": producto inválido.");
                    continue;
                }
                products.put(fields[0].trim(),
                        new Product(fields[1].trim(), Double.parseDouble(fields[2])));
            }
        }
        return products;
    }

    /**
     * Carga la información de los vendedores. La clave es
     * {@code TipoDocumento;NumeroDocumento}.
     *
     * @param fileName nombre del archivo de vendedores
     * @return mapa de documento a vendedor
     * @throws IOException si no se puede leer el archivo
     */
    private static Map<String, Salesman> loadSalesmen(String fileName) throws IOException {
        Map<String, Salesman> salesmen = new HashMap<String, Salesman>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split(SEPARATOR);
                if (fields.length < 4) {
                    WARNINGS.add(fileName + " línea " + lineNumber + ": vendedor inválido.");
                    continue;
                }
                String key = fields[0].trim() + SEPARATOR + fields[1].trim();
                salesmen.put(key, new Salesman(fields[2].trim() + " " + fields[3].trim()));
            }
        }
        return salesmen;
    }

    /**
     * Procesa todos los archivos de ventas de la carpeta del proyecto. Un
     * mismo vendedor puede tener varios archivos; sus ventas se acumulan.
     *
     * @param salesmen  vendedores conocidos
     * @param products  catálogo de productos
     * @throws IOException si ocurre un error de lectura
     */
    private static void processSalesFiles(Map<String, Salesman> salesmen,
            Map<String, Product> products) throws IOException {
        File[] files = new File(PROJECT_FOLDER).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(SALES_FILE_PREFIX) && name.endsWith(SALES_FILE_EXTENSION);
            }
        });
        if (files == null) {
            throw new IOException("No se pudo leer la carpeta del proyecto.");
        }
        for (File file : files) {
            processSalesFile(file, salesmen, products);
        }
    }

    /**
     * Procesa un archivo de ventas de un vendedor.
     *
     * @param file      archivo de ventas
     * @param salesmen  vendedores conocidos
     * @param products  catálogo de productos
     * @throws IOException si ocurre un error de lectura
     */
    private static void processSalesFile(File file, Map<String, Salesman> salesmen,
            Map<String, Product> products) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            String[] headerFields = header == null ? new String[0] : header.split(SEPARATOR);
            if (headerFields.length < 2) {
                WARNINGS.add(file.getName() + ": encabezado inválido, archivo omitido.");
                return;
            }
            Salesman salesman = salesmen.get(headerFields[0].trim() + SEPARATOR + headerFields[1].trim());
            if (salesman == null) {
                WARNINGS.add(file.getName() + ": vendedor no registrado, archivo omitido.");
                return;
            }
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    registerSale(line, file.getName() + " línea " + lineNumber, salesman, products);
                }
            }
        }
    }

    /**
     * Valida y registra una venta.
     *
     * @param line     línea con formato {@code IDProducto;Cantidad;}
     * @param location descripción de la línea para los mensajes de advertencia
     * @param salesman vendedor al que se le suma el recaudo
     * @param products catálogo de productos
     */
    private static void registerSale(String line, String location, Salesman salesman,
            Map<String, Product> products) {
        String[] fields = line.split(SEPARATOR);
        if (fields.length < 2 || !isNumber(fields[1])) {
            WARNINGS.add(location + ": formato erróneo.");
            return;
        }
        Product product = products.get(fields[0].trim());
        long quantity = (long) Double.parseDouble(fields[1]);
        if (product == null) {
            WARNINGS.add(location + ": el producto " + fields[0].trim() + " no existe.");
        } else if (quantity <= 0) {
            WARNINGS.add(location + ": cantidad no válida (" + quantity + ").");
        } else {
            product.unitsSold += quantity;
            salesman.totalRevenue += quantity * product.price;
        }
    }

    /**
     * Escribe el reporte de vendedores ordenado por recaudo, de mayor a menor.
     *
     * @param salesmen vendedores con su recaudo
     * @throws IOException si no se puede escribir el archivo
     */
    private static void writeSalesmenReport(Map<String, Salesman> salesmen) throws IOException {
        List<Salesman> sorted = new ArrayList<Salesman>(salesmen.values());
        sorted.sort((a, b) -> Double.compare(b.totalRevenue, a.totalRevenue));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESMEN_REPORT_NAME))) {
            for (Salesman salesman : sorted) {
                writer.write(salesman.fullName + SEPARATOR
                        + String.format(Locale.US, "%.2f", salesman.totalRevenue));
                writer.newLine();
            }
        }
    }

    /**
     * Escribe el reporte de productos vendidos, ordenado por cantidad de
     * mayor a menor. Solo incluye productos con al menos una unidad vendida.
     *
     * @param products catálogo con cantidades vendidas
     * @throws IOException si no se puede escribir el archivo
     */
    private static void writeProductsReport(Map<String, Product> products) throws IOException {
        List<Product> sold = new ArrayList<Product>();
        for (Product product : products.values()) {
            if (product.unitsSold > 0) {
                sold.add(product);
            }
        }
        sold.sort((a, b) -> Long.compare(b.unitsSold, a.unitsSold));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_REPORT_NAME))) {
            for (Product product : sold) {
                writer.write(product.name + SEPARATOR
                        + String.format(Locale.US, "%.2f", product.price));
                writer.newLine();
            }
        }
    }

    /**
     * Indica si un texto es un número válido.
     *
     * @param text texto a evaluar
     * @return {@code true} si se puede convertir a número
     */
    private static boolean isNumber(String text) {
        try {
            Double.parseDouble(text.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
