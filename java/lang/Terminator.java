/*
 * Copyright (c) 1999, 2001, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

import sun.misc.Signal;
import sun.misc.SignalHandler;


/**
 * Package-private utility class for setting up and tearing down
 * platform-specific support for termination-triggered shutdowns.
 *
 * 包内访问的类。为了设置和拆除平台相关的终止触发关闭支持。
 *
 * @author   Mark Reinhold
 * @since    1.3
 */

class Terminator {

    // SignalHandler是终止触发事件的管理
    private static SignalHandler handler = null;

    /* Invocations of setup and teardown are already synchronized
     * on the shutdown lock, so no further synchronization is needed here
     */

    static void setup() {
        if (handler != null) return;
        // 默认的接口实现
        SignalHandler sh = new SignalHandler() {
            // 所以这个接口不需要实现，直接使用即可了
            public void handle(Signal sig) {
                // 终止号
                Shutdown.exit(sig.getNumber() + 0200);
            }
        };
        handler = sh;
        try {
            // 加入信号处理
            Signal.handle(new Signal("INT"), sh);
            Signal.handle(new Signal("TERM"), sh);
        } catch (IllegalArgumentException e) {
            // When -Xrs is specified the user is responsible for
            // ensuring that shutdown hooks are run by calling
            // System.exit()
        }
    }

    static void teardown() {
        /* The current sun.misc.Signal class does not support
         * the cancellation of handlers
         */
    }

}
