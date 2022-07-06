package com.peratonlabs.closure.eop2;

public class VideoServerTiny implements Runnable
{
    private static VideoServerTiny instance;
    private String webroot;
    
    public void serve() {
        @SuppressWarnings("serial")
        class MyServ extends Acme.Serve.Serve {
            // Overriding method for public access
            public void setMappingTable(PathTreeDictionary mappingtable) { 
                super.setMappingTable(mappingtable);
            }
            // add the method below when .war deployment is needed
            public void addWarDeployer(String deployerFactory, String throttles) {
                super.addWarDeployer(deployerFactory, throttles);
            }
            public void addWebsocketProvider() { // add if plan to deploy websocket endpoints
                addWebsocketProvider(null); // list of class path file components can be provided here
            }

        };

        final MyServ srv = new MyServ();
        // setting aliases, for an optional file servlet
        Acme.Serve.Serve.PathTreeDictionary aliases = new Acme.Serve.Serve.PathTreeDictionary();
        aliases.put("/*", new java.io.File(webroot));
        //  note cast name will depend on the class name, since it is anonymous class
        srv.setMappingTable(aliases);
        // setting properties for the server, and exchangeable Acceptors
        java.util.Properties properties = new java.util.Properties();
        properties.put("port", 8080);
        properties.setProperty(Acme.Serve.Serve.ARG_NOHUP, "nohup");
        properties.setProperty("acceptorImpl", "Acme.Serve.SelectorAcceptor"); // this acceptor is requireed for websocket support
        srv.arguments = properties;
        srv.addDefaultServlets(null); // optional file servlet
        srv.addWebsocketProvider();  // enable websocket
        srv.addServlet("/request", new VideoServlet()); // optional
        // the pattern above is exact match, use /myservlet/* for mapping any path startting with /myservlet (Since 1.93)
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                srv.notifyStop();
                srv.destroyAllServlets();
            }
        }));
        srv.serve();
    }
    
    public static VideoServerTiny getInstance() {
        if (instance == null) {
            instance = new VideoServerTiny();
        }
        return instance;
    }
    
    public void start(String webroot) {
        VideoServerTiny server = getInstance();
        server.webroot = webroot;
        
        Thread thread = new Thread(server);
        thread.start();
    }

    @Override
    public void run() {
        serve();
    }
}
