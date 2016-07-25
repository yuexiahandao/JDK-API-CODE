/*
 * Copyright (c) 1998, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java.security;


/**
 * A computation to be performed with privileges enabled.  The computation is
 * performed by invoking <code>AccessController.doPrivileged</code> on the
 * <code>PrivilegedAction</code> object.  This interface is used only for
 * computations that do not throw checked exceptions; computations that
 * throw checked exceptions must use <code>PrivilegedExceptionAction</code>
 * instead.
 *
 * 启用特权的情况下要执行的计算。通过在 PrivilegedAction 对象上调用 AccessController.doPrivileged 执行该计算。
 * 此接口只用于那些不抛出经过检查的异常的计算；抛出经过检查的异常的计算必须使用 PrivilegedExceptionAction。
 *
 * @see AccessController
 * @see AccessController#doPrivileged(PrivilegedAction)
 * @see PrivilegedExceptionAction
 */

public interface PrivilegedAction<T> {
    /**
     * Performs the computation.  This method will be called by
     * <code>AccessController.doPrivileged</code> after enabling privileges.
     *
     * 执行计算。此方法将在启动优先级后由 AccessController.doPrivileged 调用。
     *
     * @return a class-dependent value that may represent the results of the
     *         computation. Each class that implements
     *         <code>PrivilegedAction</code>
     *         should document what (if anything) this value represents.
     * @see AccessController#doPrivileged(PrivilegedAction)
     * @see AccessController#doPrivileged(PrivilegedAction,
     *                                     AccessControlContext)
     */
    T run();
}
