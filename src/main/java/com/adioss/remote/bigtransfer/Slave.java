package com.adioss.remote.bigtransfer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.logging.*;
import com.adioss.Utils;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptTailer;

import static com.adioss.Utils.*;

public class Slave {
    Logger m_logger = Logger.getLogger(Slave.class.getName());

    private final Chronicle m_chronicle;
    private final ExcerptTailer m_tailer;
    private final Thread m_client;
    private boolean m_running;

    public Slave() throws IOException, InterruptedException {
        String indexPath = Utils.waitForIndexPath(TEST_INDEX_DIRECTORY_PATH);
        m_chronicle = ChronicleQueueBuilder.remoteTailer().connectAddress(new InetSocketAddress(HOST, PORT)).build();
        m_tailer = m_chronicle.createTailer();
        m_client = new Thread(new Runnable() {
            @Override
            public void run() {
                m_logger.info("start client");
                m_running = true;
                while (m_running) {
                    if (!m_tailer.nextIndex()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            //
                        }
                        continue;
                    }
                    int size = m_tailer.readInt();
                    byte[] stream = new byte[size];
                    m_tailer.read(stream);
                    m_logger.info("client:" + stream.length + " for index " + m_tailer.index());
                }
            }
        });
    }

    private void start() {
        m_client.start();
    }

    public static void main(String... args) throws IOException, InterruptedException {
        Slave slave = new Slave();
        slave.start();
    }
}

