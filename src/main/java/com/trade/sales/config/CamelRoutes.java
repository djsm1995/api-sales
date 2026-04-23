package com.trade.sales.config;

import com.trade.sales.features.inventory.application.InventoryService;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/***
 * Para que el sistema sea SOLID,
 * el ProductService no debería saber que existe el InventoryService.
 * Usaremos Camel para "escuchar" cuando se crea un producto.
 * ¿Para qué sirve Camel aquí? Actúa como un pegamento.
 * Si mañana quieres que al crear un producto también se envíe un correo
 * o se notifique a otro sistema, solo agregas una ruta en Camel sin tocar tu código de negocio.
 * ***/
@Component
public class CamelRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // CONFIGURACIÓN DE REINTENTOS Y ERROR HANDLING
        // Si falla, reintenta 3 veces cada 2000ms (2 seg)
        errorHandler(deadLetterChannel("direct:deadLetter")
                .maximumRedeliveries(3)
                .redeliveryDelay(2000)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .log("Error procesando mensaje, reintentando..."));

        // Ruta: Cuando llegue un mensaje al canal "direct:newProduct"
        from("direct:newProduct")
                .routeId("inventorySyncRoute")
                .log("Camel: Procesando producto ${body.name} (ID: ${body.id})")
                .bean(InventoryService.class, "initializeInventoryFromCamel(${body.id})")
                .log("Camel: Mensaje enviado a InventoryService exitosamente");
        // Llamamos al servicio de inventario para crear el registro con 0 stock
        // NUEVA RUTA: Descontar stock tras una venta
        from("direct:decreaseStock")
                .split(body()) // Divide la lista de SaleItems para procesar uno por uno
                .log("Descontando stock para producto: ${body.productId}")
                .bean("inventoryService", "decreaseStock");
        // RUTA DE MANEJO DE FALLOS (Dead Letter Channel)
        from("direct:deadLetter")
                .routeId("deadLetterRoute")
                .log(LoggingLevel.ERROR, "El mensaje falló tras los reintentos: ${body}")
                .to("file:errors/inventory?fileName=failed-products-${date:now:yyyyMMdd}.txt&fileExist=Append");
    }
}

/***
 * from("direct:decreaseStock")  // "DE: este origen"
 *     .split(body())            // "DIVIDE: el contenido"
 *     .log("Procesando...")     // "REGISTRA: un mensaje"
 *     .bean("service", "method")// "USA: este servicio"
 *
 */
