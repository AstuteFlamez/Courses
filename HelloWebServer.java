import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import java.io.OutputStream;

public class HelloWebServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000),0);
        HttpContext context = server.createContext("/");
        context.setHandler(exchange -> {
		System.out.println("Server Received HTTP Request");
		exchange.sendResponseHeaders(204,-1);
	    });

        server.start();
	System.out.println("Hello Web Server Running...");
    }
}

