package com.nhnacademy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CurlClient {
    @Parameter(names = {"curl"})
    static
    URL url;

    @Parameter(names = {"op"})
    static
    String op;

    @Parameter(names = {"method"})
    static
    String method;

    @Parameter(names = {"-H"})
    static
    List<String> head = new ArrayList<>();

    @Parameter(names = {"-d"})
    static
    List<String> data = new ArrayList<>();

    private boolean isV = false;
    private boolean isL = false;
    private static boolean isEnd = false;
    private static int count = 0;

    public static void main(String[] args) throws IOException {
        CurlClient client = new CurlClient();
        JCommander.newBuilder()
            .addObject(client)
            .build()
            .parse(args);

        if (!isEnd) {
            client.connect(InetAddress.getByName(url.getHost()));
        }
    }

    private void connect(InetAddress serverHost) throws IOException {
        try (Socket socket = new Socket(serverHost.getHostName(), 80)) {
            if ("-XGET".equals(method) || "-X".equals(method)) {
                printWriter(socket);
            } else if ("-v".equals(op)) {
                isV = true;
                vConnect(socket);
            } else if ("-L".equals(op)) {
                isV = true;
                isL = true;
                printWriterOpV(socket, "GET");
            }  else {
                printWriter(socket);
            }
        }
    }

    private void vConnect(Socket socket) throws IOException {
        if ("-XPOST".equals(method)) {
            printWriterOpV(socket, "POST");
        } else {
            printWriterOpV(socket, "GET");
        }
    }

    private void printWriter(Socket socket) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            subPrinter(socket, "GET", br);
        }
    }

    private void printWriterOpV(Socket socket, String msg) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            subPrinter(socket, msg, br);
        }
    }

    private void subPrinter(Socket socket, String msg, BufferedReader br) throws IOException {
        String line = null;
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.println(msg + " " + url.getPath() + " HTTP/1.1"); // POST /get HTTP/1.1
        writer.println("Host: " + url.getHost());
        if ((data.size()) != 0) {
            head.add("Content-Length: " + data.get(0).length());
        }
        for (var i = 1; i < head.size(); i++) {
            writer.println(head.get(i));
        }
        writer.println();
        for (String datum : data) {
            writer.println(datum);
        }
        if (isV) {
            writer.flush();
        }try {
            while ((line = br.readLine()) != null) {
                if (isL) {
                    writer.close();
                    fiveCount(line);
                }
                System.out.println(line);
            }
        } catch (SocketException e) {
            isEnd = true;
            System.out.println("끝");
        }
    }

    private void fiveCount(String line) throws IOException {
        if (count == 5) {
            throw new IOException("redirection 그만하세요");
        }
        if (line.contains("location:") || line.contains("Location:")) {
            String[] location = line.split(":");
            count++;
            URL nextURL = new URL("http://httpbin.org" + location[1].strip());
            String[] operator = {"curl", String.valueOf(nextURL)};
            main(operator);
        }
    }
}