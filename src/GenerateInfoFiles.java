import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Genera archivos planos pseudoaleatorios que sirven como entrada para el
 * programa principal de procesamiento de ventas.
 *
 * <p>Archivos generados (en la carpeta de trabajo del proyecto):</p>
 * <ul>
 *   <li>{@code productos.txt}: {@code IDProducto;NombreProducto;Precio}</li>
 *   <li>{@code vendedores.txt}:
 *       {@code TipoDocumento;NumeroDocumento;Nombres;Apellidos}</li>
 *   <li>{@code ventas_<tipo>_<id>.txt}: un archivo por vendedor, con la
 *       primera línea {@code TipoDocumento;NumeroDocumento} y luego una venta
 *       por línea {@code IDProducto;Cantidad;}</li>
 * </ul>
 *
 * <p>Este programa no solicita información al usuario.</p>
 *
 * @version 1.0
 */
public class GenerateInfoFiles {

    /** Nombre del archivo de productos. */
    public static final String PRODUCTS_FILE_NAME = "productos.txt";

    /** Nombre del archivo de información de vendedores. */
    public static final String SALESMEN_FILE_NAME = "vendedores.txt";

    /** Tipo de documento usado para los vendedores generados. */
    private static final String DOCUMENT_TYPE = "CC";

    /** Separador de campos de los archivos planos. */
    private static final String SEPARATOR = ";";

    /** Cantidad de productos generados por defecto. */
    private static final int DEFAULT_PRODUCTS_COUNT = 20;

    /** Cantidad de vendedores generados por defecto. */
    private static final int DEFAULT_SALESMEN_COUNT = 10;

    /** Cantidad máxima de ventas por vendedor. */
    private static final int MAX_SALES_PER_SALESMAN = 15;

    /** Cantidad máxima de unidades vendidas en una sola venta. */
    private static final int MAX_UNITS_PER_SALE = 10;

    /** Precio mínimo de un producto. */
    private static final int MIN_PRICE = 1000;

    /** Precio máximo de un producto. */
    private static final int MAX_PRICE = 500000;

    /** Nombres reales de persona. */
    private static final String[] FIRST_NAMES = {
        "Juan", "Maria", "Carlos", "Ana", "Luis", "Laura", "Andres",
        "Camila", "Jorge", "Valentina", "Felipe", "Sofia", "Diego",
        "Daniela", "Santiago", "Paula", "Miguel", "Natalia", "David", "Juliana"
    };

    /** Apellidos reales de persona. */
    private static final String[] LAST_NAMES = {
        "Garcia", "Rodriguez", "Martinez", "Lopez", "Gonzalez", "Perez",
        "Sanchez", "Ramirez", "Torres", "Diaz", "Vargas", "Castro",
        "Moreno", "Rojas", "Ortiz", "Gomez", "Herrera", "Medina", "Silva", "Cruz"
    };

    /** Nombres base de productos. */
    private static final String[] PRODUCT_NAMES = {
        "Cafe", "Azucar", "Arroz", "Leche", "Pan", "Aceite", "Huevos",
        "Queso", "Chocolate", "Galletas", "Jugo", "Agua", "Cereal",
        "Pasta", "Atun", "Frijoles", "Mantequilla", "Jabon", "Detergente",
        "Papel higienico", "Yogurt", "Sal", "Harina", "Lentejas", "Te"
    };

    /** Generador de números pseudoaleatorios compartido. */
    private static final Random RANDOM = new Random();

    /** Cantidad de productos existentes; los IDs van de 1 a este valor. */
    private static int productsCount = DEFAULT_PRODUCTS_COUNT;

    /**
     * Punto de entrada. Genera productos, vendedores y un archivo de ventas
     * por cada vendedor. Muestra un mensaje de éxito o de error.
     *
     * @param args argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        try {
            createProductsFile(DEFAULT_PRODUCTS_COUNT);
            createSalesManInfoFile(DEFAULT_SALESMEN_COUNT);
            createSalesFilesFromSalesmenFile();
            System.out.println("Archivos de prueba generados exitosamente.");
        } catch (IOException | RuntimeException e) {
            System.err.println("Error al generar los archivos: " + e.getMessage());
        }
    }

    /**
     * Crea un archivo pseudoaleatorio de ventas para un vendedor.
     *
     * @param randomSalesCount cantidad de ventas (líneas) a generar
     * @param name             nombre del vendedor (usado en el nombre del archivo)
     * @param id               número de documento del vendedor
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id)
            throws IOException {
        String fileName = "ventas_" + DOCUMENT_TYPE + "_" + id + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(DOCUMENT_TYPE + SEPARATOR + id);
            writer.newLine();
            for (int i = 0; i < randomSalesCount; i++) {
                int productId = RANDOM.nextInt(productsCount) + 1;
                int quantity = RANDOM.nextInt(MAX_UNITS_PER_SALE) + 1;
                writer.write(productId + SEPARATOR + quantity + SEPARATOR);
                writer.newLine();
            }
        }
    }

    /**
     * Crea un archivo con información pseudoaleatoria de productos.
     * Formato de cada línea: {@code IDProducto;NombreProducto;Precio}.
     *
     * @param productsCount cantidad de productos a generar (mayor que cero)
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createProductsFile(int productsCount) throws IOException {
        if (productsCount <= 0) {
            throw new IllegalArgumentException("La cantidad de productos debe ser positiva.");
        }
        GenerateInfoFiles.productsCount = productsCount;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE_NAME))) {
            for (int productId = 1; productId <= productsCount; productId++) {
                String productName = PRODUCT_NAMES[RANDOM.nextInt(PRODUCT_NAMES.length)]
                        + " " + productId;
                int price = MIN_PRICE + RANDOM.nextInt(MAX_PRICE - MIN_PRICE + 1);
                writer.write(productId + SEPARATOR + productName + SEPARATOR + price);
                writer.newLine();
            }
        }
    }

    /**
     * Crea un archivo con información pseudoaleatoria de vendedores.
     * Formato de cada línea:
     * {@code TipoDocumento;NumeroDocumento;Nombres;Apellidos}.
     *
     * @param salesmanCount cantidad de vendedores a generar (mayor que cero)
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        if (salesmanCount <= 0) {
            throw new IllegalArgumentException("La cantidad de vendedores debe ser positiva.");
        }
        Set<Long> usedIds = new HashSet<Long>();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESMEN_FILE_NAME))) {
            while (usedIds.size() < salesmanCount) {
                long id = 10000000L + (long) (RANDOM.nextDouble() * 90000000L);
                if (usedIds.add(id)) {
                    String firstName = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
                    String lastName = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)]
                            + " " + LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
                    writer.write(DOCUMENT_TYPE + SEPARATOR + id + SEPARATOR
                            + firstName + SEPARATOR + lastName);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Lee el archivo de vendedores y genera un archivo de ventas para cada
     * uno, de modo que los documentos sean coherentes entre archivos.
     *
     * @throws IOException si ocurre un error al leer o escribir archivos
     */
    private static void createSalesFilesFromSalesmenFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(SALESMEN_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(SEPARATOR);
                long id = Long.parseLong(fields[1]);
                int salesCount = RANDOM.nextInt(MAX_SALES_PER_SALESMAN) + 1;
                createSalesMenFile(salesCount, fields[2], id);
            }
        }
    }
}