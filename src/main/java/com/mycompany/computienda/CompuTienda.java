

package com.mycompany.computienda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
/**
 *
 * @author gomez
 */
public class CompuTienda {
    
     private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/computienda";
        String usuario = "root";
        String contraseña = "admin";
        return DriverManager.getConnection(url, usuario, contraseña);
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n--- Menú de Gestión de Productos ---");
            System.out.println("1. Crear nuevo producto");
            System.out.println("2. Consultar productos");
            System.out.println("3. Vender Producto");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea después de ingresar la opción

            switch (opcion) {
                case 1:
                    crearProducto(scanner);
                    break;
                case 2:
                    consultarProductos();
                    break;
                case 3:
                    venderProducto(scanner);
                    break;
                case 4:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
                    break;
            }
        } while (opcion != 3);

        scanner.close();
    }

    // Método para crear un nuevo producto en la base de datos
    private static void crearProducto(Scanner scanner) {
        System.out.println("\n--- Crear Nuevo Producto ---");
        
        try (Connection conn = getConnection()) {
            // Solicitar datos del producto al usuario
            System.out.print("Ingrese el código del producto: ");
            int codigoProducto = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea
            
            System.out.print("Ingrese el nombre del producto: ");
            String nombre = scanner.nextLine();
            
            System.out.print("Ingrese la descripción del producto: ");
            String descripcion = scanner.nextLine();
            
            System.out.print("Ingrese el precio base: ");
            double precioBase = scanner.nextDouble();
            
            System.out.print("Ingrese el precio de venta: ");
            double precioVenta = scanner.nextDouble();
            scanner.nextLine();
            
            System.out.print("Ingrese la categoria del producto: ");
            String categoria = scanner.nextLine();
            
            System.out.print("Ingrese la cantidad disponible: ");
            int cantidadDisponible = scanner.nextInt();
            
            // Crear la sentencia SQL para insertar el nuevo producto
            String sqlInsertProducto = "INSERT INTO Productos (codigo_producto, nombre, descripcion, precio_base, precio_venta, categoria, cantidad_disponible ) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psInsertProducto = conn.prepareStatement(sqlInsertProducto);
            
            // Configurar los parámetros de la consulta
            psInsertProducto.setInt(1, codigoProducto);
            psInsertProducto.setString(2, nombre);
            psInsertProducto.setString(3, descripcion);
            psInsertProducto.setDouble(4, precioBase);
            psInsertProducto.setDouble(5, precioVenta);
            psInsertProducto.setString(6, categoria);
            psInsertProducto.setInt(7, cantidadDisponible);
            
            // Ejecutar la inserción
            psInsertProducto.executeUpdate();
            System.out.println("Producto creado exitosamente.");
            
        } catch (SQLException e) {
            System.out.println("Error al crear el producto: " + e.getMessage());
        }
    }

    // Método para consultar los productos existentes en la base de datos
    private static void consultarProductos() {
        System.out.println("\n--- Consulta de Productos ---");

        try (Connection conn = getConnection()) {
            // Crear la sentencia SQL para consultar los productos
            String sqlConsultarProductos = "SELECT * FROM Productos";
            PreparedStatement psConsultarProductos = conn.prepareStatement(sqlConsultarProductos);
            
            // Ejecutar la consulta y obtener los resultados
            ResultSet rs = psConsultarProductos.executeQuery();
            
            // Mostrar los resultados
            System.out.println("Código | Nombre | Descripción | Precio_Base | Precio_Venta | Categoria | Cantidad_Disponible");
            System.out.println("-----------------------------------------------");
            
            while (rs.next()) {
                int codigoProducto = rs.getInt("codigo_producto");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                double precioBase  = rs.getDouble("precio_base");
                double precioVenta = rs.getDouble("precio_venta");
                String categoria   = rs.getString("categoria");
                int cantidadDisponible = rs.getInt("cantidad_disponible");
                
                
                System.out.println(codigoProducto + " | " + nombre + " | " + descripcion + " | " + precioBase +" | "+ precioVenta + " | " + categoria +" | "+cantidadDisponible);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al consultar los productos: " + e.getMessage());
        }
    }
    
    private static void venderProducto(Scanner scanner) {
    System.out.println("\n--- Vender Producto ---");
    
    try (Connection conn = getConnection()) {
        // Desactivar el auto-commit para manejar la transacción manualmente
        conn.setAutoCommit(false);
        
        // Solicitar al usuario el ID del producto y la cantidad a vender
        System.out.print("Ingrese el ID del producto: ");
        int codigoProducto = scanner.nextInt();
        scanner.nextLine();  // Consumir el salto de línea
            
            System.out.print("Ingrese el nombre del producto: ");
            String nombre = scanner.nextLine();
            
            System.out.print("Ingrese la descripción del producto: ");
            String descripcion = scanner.nextLine();
            
          
           
            
            System.out.print("Ingrese la categoria del producto: ");
            String categoria = scanner.nextLine();
            
        
        System.out.print("Ingrese la cantidad a vender: ");
        int cantidadVendida = scanner.nextInt();
        
        // Consultar el inventario del producto
        String sqlSelectProducto = "SELECT cantidad_disponible, precio_venta FROM Productos WHERE codigo_producto = ?";
        PreparedStatement psSelectProducto = conn.prepareStatement(sqlSelectProducto);
        psSelectProducto.setInt(1, codigoProducto);
        
        ResultSet rsProducto = psSelectProducto.executeQuery();
        
        if (rsProducto.next()) {
            int cantidadDisponible = rsProducto.getInt("cantidad_disponible");
            double precioVenta = rsProducto.getDouble("precio_venta");
            
            //  Verificar si hay suficiente inventario
            if (cantidadVendida > cantidadDisponible) {
                System.out.println("Error: No hay suficiente inventario disponible.");
                return;
            }
            
            // Actualizar el inventario del producto
            String sqlUpdateInventario = "UPDATE Productos SET cantidad_disponible = ? WHERE codigo_producto = ?";
            PreparedStatement psUpdateInventario = conn.prepareStatement(sqlUpdateInventario);
            psUpdateInventario.setInt(1, cantidadDisponible - cantidadVendida);
            psUpdateInventario.setInt(2, codigoProducto);
            psUpdateInventario.executeUpdate();
            
            //  Registrar la venta en la tabla Ventas
            String sqlInsertVenta = "INSERT INTO productos_vendidos (codigo_producto, nombre, descripcion,  categoria, cantidad_vendida) VALUES (?, ?, ?, ?, ? )";
            PreparedStatement psInsertVenta = conn.prepareStatement(sqlInsertVenta);
            psInsertVenta.setInt(1, codigoProducto);
            psInsertVenta.setString(2, nombre);
            psInsertVenta.setString(3, descripcion);
           
            psInsertVenta.setString(4, categoria);
            psInsertVenta.setInt(5, cantidadVendida);
            
            psInsertVenta.executeUpdate();
            
            // 5. Confirmar la transacción
            conn.commit();
            System.out.println("Producto vendido exitosamente.");
        } else {
            System.out.println("Error: Producto no encontrado.");
        }
        
    } catch (SQLException e) {
      //  try {
            // En caso de error, revertir la transacción
            System.out.println("Error durante la venta: " + e.getMessage());
            //conn.rollback();
      //  } catch (SQLException rollbackEx) {
      //      System.out.println("Error al hacer rollback: " + rollbackEx.getMessage());
       // }
    }
}

    }

