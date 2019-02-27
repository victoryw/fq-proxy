/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.victoryw.fq.proxy;

import com.victoryw.fq.proxy.demo.DiscardServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        try {
            new DiscardServer(port).run();
        } catch (InterruptedException e) {
            logger.error("......",e);
        }
    }
}
