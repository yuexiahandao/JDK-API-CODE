/*
 * Copyright (c) 1997, 2012, Oracle and/or its affiliates. All rights reserved.
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

import sun.security.util.Debug;

/**
 *
 * AccessController 类用于与访问控制相关的操作和决定。
 *
 * 更确切地说，AccessController 类用于以下三个目的：
 *
 * 基于当前生效的安全策略决定是允许还是拒绝对关键系统资源的访问
 * 将代码标记为享有“特权”，从而影响后续访问决定，以及
 * 获取当前调用上下文的“快照”，这样便可以相对于已保存的上下文作出其他上下文的访问控制决定。
 *
 * <p> The {@link #checkPermission(Permission) checkPermission} method
 * determines whether the access request indicated by a specified
 * permission should be granted or denied. A sample call appears
 * below. In this example, <code>checkPermission</code> will determine
 * whether or not to grant "read" access to the file named "testFile" in
 * the "/temp" directory.
 *
 * <pre>
 *
 * FilePermission perm = new FilePermission("/temp/testFile", "read");
 * AccessController.checkPermission(perm);
 *
 * </pre>
 *
 * <p> If a requested access is allowed,
 * <code>checkPermission</code> returns quietly. If denied, an
 * AccessControlException is
 * thrown. AccessControlException can also be thrown if the requested
 * permission is of an incorrect type or contains an invalid value.
 * Such information is given whenever possible.
 *
 * Suppose the current thread traversed m callers, in the order of caller 1
 * to caller 2 to caller m. Then caller m invoked the
 * <code>checkPermission</code> method.
 * The <code>checkPermission </code>method determines whether access
 * is granted or denied based on the following algorithm:
 *
 *  <pre> {@code
 * for (int i = m; i > 0; i--) {
 *
 *     if (caller i's domain does not have the permission)
 *         throw AccessControlException
 *
 *     else if (caller i is marked as privileged) {
 *         if (a context was specified in the call to doPrivileged)
 *             context.checkPermission(permission)
 *         return;
 *     }
 * };
 *
 * // Next, check the context inherited when the thread was created.
 * // Whenever a new thread is created, the AccessControlContext at
 * // that time is stored and associated with the new thread, as the
 * // "inherited" context.
 *
 * inheritedContext.checkPermission(permission);
 * }</pre>
 *
 * <p> A caller can be marked as being "privileged"
 * (see {@link #doPrivileged(PrivilegedAction) doPrivileged} and below).
 * When making access control decisions, the <code>checkPermission</code>
 * method stops checking if it reaches a caller that
 * was marked as "privileged" via a <code>doPrivileged</code>
 * call without a context argument (see below for information about a
 * context argument). If that caller's domain has the
 * specified permission, no further checking is done and
 * <code>checkPermission</code>
 * returns quietly, indicating that the requested access is allowed.
 * If that domain does not have the specified permission, an exception
 * is thrown, as usual.
 *
 * <p> The normal use of the "privileged" feature is as follows. If you
 * don't need to return a value from within the "privileged" block, do
 * the following:
 *
 *  <pre> {@code
 * somemethod() {
 *     ...normal code here...
 *     AccessController.doPrivileged(new PrivilegedAction<Void>() {
 *         public Void run() {
 *             // privileged code goes here, for example:
 *             System.loadLibrary("awt");
 *             return null; // nothing to return
 *         }
 *     });
 *     ...normal code here...
 * }}</pre>
 *
 * <p>
 * PrivilegedAction is an interface with a single method, named
 * <code>run</code>.
 * The above example shows creation of an implementation
 * of that interface; a concrete implementation of the
 * <code>run</code> method is supplied.
 * When the call to <code>doPrivileged</code> is made, an
 * instance of the PrivilegedAction implementation is passed
 * to it. The <code>doPrivileged</code> method calls the
 * <code>run</code> method from the PrivilegedAction
 * implementation after enabling privileges, and returns the
 * <code>run</code> method's return value as the
 * <code>doPrivileged</code> return value (which is
 * ignored in this example).
 *
 * <p> If you need to return a value, you can do something like the following:
 *
 *  <pre> {@code
 * somemethod() {
 *     ...normal code here...
 *     String user = AccessController.doPrivileged(
 *         new PrivilegedAction<String>() {
 *         public String run() {
 *             return System.getProperty("user.name");
 *             }
 *         });
 *     ...normal code here...
 * }}</pre>
 *
 * <p>If the action performed in your <code>run</code> method could
 * throw a "checked" exception (those listed in the <code>throws</code> clause
 * of a method), then you need to use the
 * <code>PrivilegedExceptionAction</code> interface instead of the
 * <code>PrivilegedAction</code> interface:
 *
 *  <pre> {@code
 * somemethod() throws FileNotFoundException {
 *     ...normal code here...
 *     try {
 *         FileInputStream fis = AccessController.doPrivileged(
 *         new PrivilegedExceptionAction<FileInputStream>() {
 *             public FileInputStream run() throws FileNotFoundException {
 *                 return new FileInputStream("someFile");
 *             }
 *         });
 *     } catch (PrivilegedActionException e) {
 *         // e.getException() should be an instance of FileNotFoundException,
 *         // as only "checked" exceptions will be "wrapped" in a
 *         // PrivilegedActionException.
 *         throw (FileNotFoundException) e.getException();
 *     }
 *     ...normal code here...
 *  }}</pre>
 *
 * <p> Be *very* careful in your use of the "privileged" construct, and
 * always remember to make the privileged code section as small as possible.
 *
 * <p> Note that <code>checkPermission</code> always performs security checks
 * within the context of the currently executing thread.
 * Sometimes a security check that should be made within a given context
 * will actually need to be done from within a
 * <i>different</i> context (for example, from within a worker thread).
 * The {@link #getContext() getContext} method and
 * AccessControlContext class are provided
 * for this situation. The <code>getContext</code> method takes a "snapshot"
 * of the current calling context, and places
 * it in an AccessControlContext object, which it returns. A sample call is
 * the following:
 *
 * <pre>
 *
 * AccessControlContext acc = AccessController.getContext()
 *
 * </pre>
 *
 * <p>
 * AccessControlContext itself has a <code>checkPermission</code> method
 * that makes access decisions based on the context <i>it</i> encapsulates,
 * rather than that of the current execution thread.
 * Code within a different context can thus call that method on the
 * previously-saved AccessControlContext object. A sample call is the
 * following:
 *
 * <pre>
 *
 * acc.checkPermission(permission)
 *
 * </pre>
 *
 * <p> There are also times where you don't know a priori which permissions
 * to check the context against. In these cases you can use the
 * doPrivileged method that takes a context:
 *
 *  <pre> {@code
 * somemethod() {
 *     AccessController.doPrivileged(new PrivilegedAction<Object>() {
 *         public Object run() {
 *             // Code goes here. Any permission checks within this
 *             // run method will require that the intersection of the
 *             // callers protection domain and the snapshot's
 *             // context have the desired permission.
 *         }
 *     }, acc);
 *     ...normal code here...
 * }}</pre>
 *
 * @see AccessControlContext
 *
 * @author Li Gong
 * @author Roland Schemers
 */

public final class AccessController {

    /**
     * Don't allow anyone to instantiate an AccessController
     */
    private AccessController() { }

    /**
     * Performs the specified <code>PrivilegedAction</code> with privileges
     * enabled. The action is performed with <i>all</i> of the permissions
     * possessed by the caller's protection domain.
     *
     * 启用特权，执行指定的 PrivilegedAction。该操作在调用者保护域所拥有的全部权限下执行。
     *
     * <p> If the action's <code>run</code> method throws an (unchecked)
     * exception, it will propagate through this method.
     * 如果该操作的 run 方法抛出（未经过检查的）异常，则该异常将通过此方法传播。
     *
     * <p> Note that any DomainCombiner associated with the current
     * AccessControlContext will be ignored while the action is performed.
     * 注意，执行该操作时，任何与当前 AccessControlContext 关联的 DomainCombiner 都将被忽略。
     *
     * @param action the action to be performed.
     *               action为要执行的操作
     *
     * @return the value returned by the action's <code>run</code> method.
     * 操作的 run 方法返回的值。
     *
     * @exception NullPointerException if the action is <code>null</code>
     *
     * @see #doPrivileged(PrivilegedAction,AccessControlContext)
     * @see #doPrivileged(PrivilegedExceptionAction)
     * @see #doPrivilegedWithCombiner(PrivilegedAction)
     * @see java.security.DomainCombiner
     *
     * AccessController.doPrivileged意思是这个是特别的,不用做权限检查.
     * http://huangyunbin.iteye.com/blog/1942509
     */

    // 做一些特权操作，但是这个是native方法
    public static native <T> T doPrivileged(PrivilegedAction<T> action);

    /**
     * Performs the specified <code>PrivilegedAction</code> with privileges
     * enabled. The action is performed with <i>all</i> of the permissions
     * possessed by the caller's protection domain.
     *
     * <p> If the action's <code>run</code> method throws an (unchecked)
     * exception, it will propagate through this method.
     *
     * <p> This method preserves the current AccessControlContext's
     * DomainCombiner (which may be null) while the action is performed.
     *
     * @param action the action to be performed.
     *
     * @return the value returned by the action's <code>run</code> method.
     *
     * @exception NullPointerException if the action is <code>null</code>
     *
     * @see #doPrivileged(PrivilegedAction)
     * @see java.security.DomainCombiner
     *
     * @since 1.6
     */
    public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> action) {

        AccessControlContext acc = getStackAccessControlContext();
        if (acc == null) {
            // 没有上下文的话，直接执行特权
            return AccessController.doPrivileged(action);
        }
        // 聚合Domain进行访问
        DomainCombiner dc = acc.getAssignedCombiner();
        return AccessController.doPrivileged(action, preserveCombiner(dc));
    }


    /**
     * Performs the specified <code>PrivilegedAction</code> with privileges
     * enabled and restricted by the specified
     * <code>AccessControlContext</code>.
     * The action is performed with the intersection of the permissions
     * possessed by the caller's protection domain, and those possessed
     * by the domains represented by the specified
     * <code>AccessControlContext</code>.
     * <p>
     * If the action's <code>run</code> method throws an (unchecked) exception,
     * it will propagate through this method.
     *
     * @param action the action to be performed.
     * @param context an <i>access control context</i>
     *                representing the restriction to be applied to the
     *                caller's domain's privileges before performing
     *                the specified action.  If the context is
     *                <code>null</code>,
     *                then no additional restriction is applied.
     *
     * @return the value returned by the action's <code>run</code> method.
     *
     * @exception NullPointerException if the action is <code>null</code>
     *
     * @see #doPrivileged(PrivilegedAction)
     * @see #doPrivileged(PrivilegedExceptionAction,AccessControlContext)
     */
    public static native <T> T doPrivileged(PrivilegedAction<T> action,
                                            AccessControlContext context);

    /**
     * Performs the specified <code>PrivilegedExceptionAction</code> with
     * privileges enabled.  The action is performed with <i>all</i> of the
     * permissions possessed by the caller's protection domain.
     *
     * <p> If the action's <code>run</code> method throws an <i>unchecked</i>
     * exception, it will propagate through this method.
     *
     * <p> Note that any DomainCombiner associated with the current
     * AccessControlContext will be ignored while the action is performed.
     *
     * @param action the action to be performed
     *
     * @return the value returned by the action's <code>run</code> method
     *
     * @exception PrivilegedActionException if the specified action's
     *         <code>run</code> method threw a <i>checked</i> exception
     * @exception NullPointerException if the action is <code>null</code>
     *
     * @see #doPrivileged(PrivilegedAction)
     * @see #doPrivileged(PrivilegedExceptionAction,AccessControlContext)
     * @see #doPrivilegedWithCombiner(PrivilegedExceptionAction)
     * @see java.security.DomainCombiner
     */
    public static native <T> T
        doPrivileged(PrivilegedExceptionAction<T> action)
        throws PrivilegedActionException;


    /**
     * Performs the specified <code>PrivilegedExceptionAction</code> with
     * privileges enabled.  The action is performed with <i>all</i> of the
     * permissions possessed by the caller's protection domain.
     *
     * <p> If the action's <code>run</code> method throws an <i>unchecked</i>
     * exception, it will propagate through this method.
     *
     * <p> This method preserves the current AccessControlContext's
     * DomainCombiner (which may be null) while the action is performed.
     *
     * @param action the action to be performed.
     *
     * @return the value returned by the action's <code>run</code> method
     *
     * @exception PrivilegedActionException if the specified action's
     *         <code>run</code> method threw a <i>checked</i> exception
     * @exception NullPointerException if the action is <code>null</code>
     *
     * @see #doPrivileged(PrivilegedAction)
     * @see #doPrivileged(PrivilegedExceptionAction,AccessControlContext)
     * @see java.security.DomainCombiner
     *
     * @since 1.6
     */
    public static <T> T doPrivilegedWithCombiner
        (PrivilegedExceptionAction<T> action) throws PrivilegedActionException {

        AccessControlContext acc = getStackAccessControlContext();
        if (acc == null) {
            return AccessController.doPrivileged(action);
        }
        DomainCombiner dc = acc.getAssignedCombiner();
        return AccessController.doPrivileged(action, preserveCombiner(dc));
    }

    /**
     * preserve the combiner across the doPrivileged call
     */
    private static AccessControlContext preserveCombiner
                                        (DomainCombiner combiner) {

        /**
         * callerClass[0] = Reflection.getCallerClass
         * callerClass[1] = AccessController.preserveCombiner
         * callerClass[2] = AccessController.doPrivileged
         * callerClass[3] = caller
         */
        final Class callerClass = sun.reflect.Reflection.getCallerClass(3);
        ProtectionDomain callerPd = doPrivileged
            (new PrivilegedAction<ProtectionDomain>() {
            public ProtectionDomain run() {
                return callerClass.getProtectionDomain();
            }
        });

        // perform 'combine' on the caller of doPrivileged,
        // even if the caller is from the bootclasspath
        ProtectionDomain[] pds = new ProtectionDomain[] {callerPd};
        if (combiner == null) {
            return new AccessControlContext(pds);
        } else {
            return new AccessControlContext(combiner.combine(pds, null),
                                            combiner);
        }
    }


    /**
     * Performs the specified <code>PrivilegedExceptionAction</code> with
     * privileges enabled and restricted by the specified
     * <code>AccessControlContext</code>.  The action is performed with the
     * intersection of the permissions possessed by the caller's
     * protection domain, and those possessed by the domains represented by the
     * specified <code>AccessControlContext</code>.
     * <p>
     * If the action's <code>run</code> method throws an <i>unchecked</i>
     * exception, it will propagate through this method.
     *
     * @param action the action to be performed
     * @param context an <i>access control context</i>
     *                representing the restriction to be applied to the
     *                caller's domain's privileges before performing
     *                the specified action.  If the context is
     *                <code>null</code>,
     *                then no additional restriction is applied.
     *
     * @return the value returned by the action's <code>run</code> method
     *
     * @exception PrivilegedActionException if the specified action's
     *         <code>run</code> method
     *         threw a <i>checked</i> exception
     * @exception NullPointerException if the action is <code>null</code>
     *
     * @see #doPrivileged(PrivilegedAction)
     * @see #doPrivileged(PrivilegedExceptionAction,AccessControlContext)
     */
    public static native <T> T
        doPrivileged(PrivilegedExceptionAction<T> action,
                     AccessControlContext context)
        throws PrivilegedActionException;

    /**
     * Returns the AccessControl context. i.e., it gets
     * the protection domains of all the callers on the stack,
     * starting at the first class with a non-null
     * ProtectionDomain.
     *
     * @return the access control context based on the current stack or
     *         null if there was only privileged system code.
     */

    private static native AccessControlContext getStackAccessControlContext();

    /**
     * Returns the "inherited" AccessControl context. This is the context
     * that existed when the thread was created. Package private so
     * AccessControlContext can use it.
     */

    static native AccessControlContext getInheritedAccessControlContext();

    /**
     * This method takes a "snapshot" of the current calling context, which
     * includes the current Thread's inherited AccessControlContext,
     * and places it in an AccessControlContext object. This context may then
     * be checked at a later point, possibly in another thread.
     *
     * @see AccessControlContext
     *
     * @return the AccessControlContext based on the current context.
     */

    public static AccessControlContext getContext()
    {
        AccessControlContext acc = getStackAccessControlContext();
        if (acc == null) {
            // all we had was privileged system code. We don't want
            // to return null though, so we construct a real ACC.
            return new AccessControlContext(null, true);
        } else {
            return acc.optimize();
        }
    }

    /**
     * Determines whether the access request indicated by the
     * specified permission should be allowed or denied, based on
     * the current AccessControlContext and security policy.
     * This method quietly returns if the access request
     * is permitted, or throws an AccessControlException otherwise. The
     * getPermission method of the AccessControlException returns the
     * <code>perm</code> Permission object instance.
     *
     * 检查参数给出的permssion是不是被接收或者拒绝，这是基于当前的AccessControlContext和安全策略。
     * 如果被接收，这个方法正常返回，否则返回异常。
     *
     * @param perm the requested permission.
     *
     * @exception AccessControlException if the specified permission
     *            is not permitted, based on the current security policy.
     * @exception NullPointerException if the specified permission
     *            is <code>null</code> and is checked based on the
     *            security policy currently in effect.
     */

    public static void checkPermission(Permission perm)
                 throws AccessControlException
    {
        //System.err.println("checkPermission "+perm);
        //Thread.currentThread().dumpStack();
        // perm参数不能为空
        if (perm == null) {
            throw new NullPointerException("permission can't be null");
        }

        // 取得当前权限信息的上下文
        AccessControlContext stack = getStackAccessControlContext();
        // if context is null, we had privileged system code on the stack.
        if (stack == null) {
            // 检查是不是要打印信息
            Debug debug = AccessControlContext.getDebug();
            boolean dumpDebug = false;
            if (debug != null) {
                // java.security.debug属性不包括"codebase="
                dumpDebug = !Debug.isOn("codebase=");
                dumpDebug &= !Debug.isOn("permission=") ||
                    Debug.isOn("permission=" + perm.getClass().getCanonicalName());
            }

            // 是否dumpStack？
            if (dumpDebug && Debug.isOn("stack")) {
                Thread.currentThread().dumpStack();
            }

            // 是否打印domain，这里打不打都无所谓吧！
            if (dumpDebug && Debug.isOn("domain")) {
                debug.println("domain (context is null)");
            }

            // 打印拥有的权限信息
            if (dumpDebug) {
                debug.println("access allowed "+perm);
            }
            return;
        }

        AccessControlContext acc = stack.optimize();
        acc.checkPermission(perm);
    }
}
