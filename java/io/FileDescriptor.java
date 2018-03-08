/*
 * Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.io;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Instances of the file descriptor class serve as an opaque handle
 * to the underlying machine-specific structure representing an
 * open file, an open socket, or another source or sink of bytes.
 * The main practical use for a file descriptor is to create a
 * {@link FileInputStream} or {@link FileOutputStream} to contain it.
 *
 * <p>Applications should not create their own file descriptors.
 *
 * @author  Pavani Diwanji
 * @since   JDK1.0
 *
 * 文件描述符类的实例用作底层机器特定结构的不透明句柄，表示打开的文件，打开的套接字或字节的另一个源或接收器。
 * 文件描述符的主要实际用途是创建一个FileInputStream或FileOutputStream来包含它。
 *
 * 应用不应该自己的FileDescriptor类。
 */
public final class FileDescriptor {

    private int fd;

    private long handle;

    /**
     * A use counter for tracking the FIS/FOS/RAF instances that
     * use this FileDescriptor. The FIS/FOS.finalize() will not release
     * the FileDescriptor if it is still under use by any stream.
     *
     * useCount是为了追踪使用FileDescriptor的FIS/FOS/RAF实例数。
     * 如果还有任何流使用，FIS/FOS.finalize()就不会释放FileDescriptor。
     */
    private AtomicInteger useCount;


    /**
     * Constructs an (invalid) FileDescriptor
     * object.
     *
     * 创建一个不合法的FileDescriptor
     */
    public /**/ FileDescriptor() {
        fd = -1;
        handle = -1;
        useCount = new AtomicInteger();
    }

    static {
        initIDs();
    }

    // Set up JavaIOFileDescriptorAccess in SharedSecrets
    static {
        // 设置setJavaIOFileDescriptorAccess，有意思
        // 这个类设置了JavaIOFileDescriptorAccess，这个里面是文件句柄，我们应该可以使用这个类来获取进程的句柄
        sun.misc.SharedSecrets.setJavaIOFileDescriptorAccess(
                // 但是都需要FileDescriptor类
            new sun.misc.JavaIOFileDescriptorAccess() {
                public void set(FileDescriptor obj, int fd) {
                    obj.fd = fd;
                }

                public int get(FileDescriptor obj) {
                    return obj.fd;
                }

                public void setHandle(FileDescriptor obj, long handle) {
                    obj.handle = handle;
                }

                public long getHandle(FileDescriptor obj) {
                    return obj.handle;
                }
            }
        );
    }

    /**
     * A handle to the standard input stream. Usually, this file
     * descriptor is not used directly, but rather via the input stream
     * known as {@code System.in}.
     *
     * @see     java.lang.System#in
     */
    public static final FileDescriptor in = standardStream(0);

    /**
     * A handle to the standard output stream. Usually, this file
     * descriptor is not used directly, but rather via the output stream
     * known as {@code System.out}.
     * @see     java.lang.System#out
     */
    public static final FileDescriptor out = standardStream(1);

    /**
     * A handle to the standard error stream. Usually, this file
     * descriptor is not used directly, but rather via the output stream
     * known as {@code System.err}.
     *
     * @see     java.lang.System#err
     */
    public static final FileDescriptor err = standardStream(2);

    /**
     * Tests if this file descriptor object is valid.
     *
     * 测试文件描述符是不是合法的
     *
     * @return  {@code true} if the file descriptor object represents a
     *          valid, open file, socket, or other active I/O connection;
     *          {@code false} otherwise.
     */
    public boolean valid() {
        return ((handle != -1) || (fd != -1));
    }

    /**
     * Force all system buffers to synchronize with the underlying
     * device.  This method returns after all modified data and
     * attributes of this FileDescriptor have been written to the
     * relevant device(s).  In particular, if this FileDescriptor
     * refers to a physical storage medium, such as a file in a file
     * system, sync will not return until all in-memory modified copies
     * of buffers associated with this FileDesecriptor have been
     * written to the physical medium.
     *
     * sync is meant to be used by code that requires physical
     * storage (such as a file) to be in a known state  For
     * example, a class that provided a simple transaction facility
     * might use sync to ensure that all changes to a file caused
     * by a given transaction were recorded on a storage medium.
     *
     * sync only affects buffers downstream of this FileDescriptor.  If
     * any in-memory buffering is being done by the application (for
     * example, by a BufferedOutputStream object), those buffers must
     * be flushed into the FileDescriptor (for example, by invoking
     * OutputStream.flush) before that data will be affected by sync.
     *
     * @exception SyncFailedException
     *        Thrown when the buffers cannot be flushed,
     *        or because the system cannot guarantee that all the
     *        buffers have been synchronized with physical media.
     * @since     JDK1.1
     *
     * 内存到文件的同步
     */
    public native void sync() throws SyncFailedException;

    /* This routine initializes JNI field offsets for the class */
    // 本地方法，此例程初始化该类的JNI字段偏移量
    private static native void initIDs();

    // 本地方法
    private static native long set(int d);

    /**
     * 文件标准的流，这个会实例化FileDescriptor，设置fd的值
     * @param fd
     * @return
     */
    private static FileDescriptor standardStream(int fd) {
        FileDescriptor desc = new FileDescriptor();
        // 这里就是setId的返回
        desc.handle = set(fd);
        return desc;
    }

    // package private methods used by FIS, FOS and RAF.
    // Atomic操作
    int incrementAndGetUseCount() {
        return useCount.incrementAndGet();
    }

    int decrementAndGetUseCount() {
        return useCount.decrementAndGet();
    }
}
