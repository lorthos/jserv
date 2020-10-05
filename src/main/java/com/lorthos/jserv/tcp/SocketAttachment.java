package com.lorthos.jserv.tcp;

import com.lorthos.jserv.http.Response;
import com.lorthos.jserv.http.client.Method;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * Attachment object to the selection key
 * Represents the state for serving the request
 * Encapsulates some I/O operations
 */
public class SocketAttachment {
    private Response response;
    private final SocketChannel channel;
    private final ByteBuffer readBuffer;
    private int reuseCount = 0;

    public SocketAttachment(SocketChannel channel, ByteBuffer readBuffer) {
        this.channel = channel;
        this.readBuffer = readBuffer;
    }

    /**
     * read from the socket after its selected for reads
     *
     * @return
     * @throws IOException
     */
    public boolean read() throws IOException {
        reuseCount++;
        if (channel.read(readBuffer) < 0) {
            return false;
        } else {
            readBuffer.flip();
            return true;
        }
    }

    /**
     * writes the response in the state to socket channel
     *
     * @throws IOException
     */
    public void write() throws IOException {

        ByteBuffer srcBuffer = ByteBuffer.wrap(response.renderBody());
        while (srcBuffer.hasRemaining()) {
            channel.write(srcBuffer);
        }

        if (response.getFileChannel().isPresent() &&
                response.getRequest().getParsedRequest().isPresent() &&
                response.getRequest().getParsedRequest().get().getMethod() == Method.GET) {
            FileChannel fs = response.getFileChannel().get();
            long position = 0;
            long size = fs.size();

            while (0 < size) {
                long count = fs.transferTo(position, size, channel);
                if (count > 0) {
                    position += count;
                    size -= count;
                }
            }
            fs.close();
        }

    }


    public static SocketAttachment build(SocketChannel channel) {
        ByteBuffer buffer;
        buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        return new SocketAttachment(channel, buffer);
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public int getReuseCount() {
        return reuseCount;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
