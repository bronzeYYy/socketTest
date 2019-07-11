import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8000));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
                while (selectionKeyIterator.hasNext()) {
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    //System.out.println(selectionKey.channel());
                    SocketChannel socketChannel = null;
                    if (selectionKey.channel() instanceof ServerSocketChannel) {
                        socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
                    } else {
                        socketChannel = (SocketChannel) selectionKey.channel();
                    }
                    if (selectionKey.isAcceptable()) {
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    if (selectionKey.isReadable()) {
                        byteBuffer.clear();
                        System.out.print(socketChannel + ": ");
                        socketChannel.read(byteBuffer);
                        byteBuffer.flip();
                        while (byteBuffer.hasRemaining()) {
                            System.out.print((char) byteBuffer.get());
                        }
                        byteBuffer.position(0);
                        socketChannel.write(byteBuffer);
                        //byteBuffer.flip();
                        System.out.println();
                    }
                    selectionKeyIterator.remove();
                }
            }
            /*System.out.println(socketChannel.read(byteBuffer));
            //byteBuffer.clear();
            System.out.println(socketChannel.read(byteBuffer));
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                System.out.println(Integer.toHexString(byteBuffer.get()));
            }*/
            //serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
