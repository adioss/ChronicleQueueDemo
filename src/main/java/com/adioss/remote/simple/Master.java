package com.adioss.remote.simple;

import java.io.*;
import java.util.logging.*;
import com.adioss.Utils;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;

import static com.adioss.Utils.*;

public class Master {
    Logger m_logger = Logger.getLogger(Master.class.getName());

    private final Chronicle m_chronicle;
    private final ExcerptAppender m_appender;
    private final Thread m_server;

    public Master() throws IOException {
        String indexPath = Utils.prepareIndexDirectory(TEST_INDEX_DIRECTORY_PATH);
        m_chronicle = ChronicleQueueBuilder.indexed(indexPath).source().bindAddress(PORT).build();
        m_appender = m_chronicle.createAppender();
        m_server = new Thread(new Runnable() {
            @Override
            public void run() {
                m_chronicle.clear();
                m_logger.info("start server");
                for (long i = 1; i <= 1000000; i++) {
                    String msg = "test" + i;
                    m_logger.info("server stock:" + msg);
                    publish(msg.getBytes());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        //
                    }
                }
                m_appender.close();
            }
        });
    }

    public void start() {
        m_server.start();
    }

    public void publish(byte[] stream) {
        m_appender.startExcerpt(stream.length + 4);
        m_appender.writeInt(stream.length);
        m_appender.write(stream);
        m_appender.finish();
    }

    public static void main(String... args) throws IOException, InterruptedException {
        Master master = new Master();
        master.start();
    }
}
