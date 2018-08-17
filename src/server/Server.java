package server;

import listener_interfaces.ServerListener;
import model.Corridor;
import model.SimulationHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static model.SimulationHandler.SimulationStatus.*;

public class Server extends Thread {
    private boolean isServerOn;
    private ServerSocket serverSocket;
    private Corridor corridor;
    private List<ServerListener> serverListeners = new ArrayList<ServerListener>();
    public SimulationHandler simulationHandler;


    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public boolean isServerOn() {
        return isServerOn;
    }

    public void setIsServerOn(boolean serverOn) {
        isServerOn = serverOn;
    }

    public Corridor getCorridor() {
        return corridor;
    }

    public Server(){
        super();
        this.corridor = new Corridor(240,400, 180);
        this.isServerOn = true;
        try {
            serverSocket = new ServerSocket(11111);
            simulationHandler = new SimulationHandler(corridor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(Corridor corridor){
        this.corridor = corridor;
        this.isServerOn = true;
        try {
            serverSocket = new ServerSocket(11111);
            simulationHandler = new SimulationHandler(corridor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        fireServerIsAliveEvent();
        while(isServerOn){
            System.out.println("Waiting for connection");
            try {
                Socket clientSocket;
                if(simulationHandler.getSimulationStatus() != OFF) {
                    clientSocket = serverSocket.accept();
                    simulationHandler.addConnection(clientSocket);
                    System.out.println("A new client has connected to the server");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server: run should be closed now");
        try {
            serverSocket.close();
            fireServerDisconnectedEvent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addServerListener(ServerListener serverListener){
        serverListeners.add(serverListener);
    }

    private void fireServerIsAliveEvent() {
        for (ServerListener listener: serverListeners) {
            listener.onServerIsAlive();
        }
    }

    private void fireServerDisconnectedEvent() {
        for (ServerListener listener: serverListeners) {
            listener.onServerDisconnected();
        }
    }

}
