/*
 * Copyright (c) 1999, 2006, Oracle and/or its affiliates. All rights reserved.
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
 * A <code>DomainCombiner</code> provides a means to dynamically
 * update the ProtectionDomains associated with the current
 * <code>AccessControlContext</code>.
 *
 * <p> A <code>DomainCombiner</code> is passed as a parameter to the
 * appropriate constructor for <code>AccessControlContext</code>.
 * The newly constructed context is then passed to the
 * <code>AccessController.doPrivileged(..., context)</code> method
 * to bind the provided context (and associated <code>DomainCombiner</code>)
 * with the current execution Thread.  Subsequent calls to
 * <code>AccessController.getContext</code> or
 * <code>AccessController.checkPermission</code>
 * cause the <code>DomainCombiner.combine</code> to get invoked.
 *
 * <p> The combine method takes two arguments.  The first argument represents
 * an array of ProtectionDomains from the current execution Thread,
 * since the most recent call to <code>AccessController.doPrivileged</code>.
 * If no call to doPrivileged was made, then the first argument will contain
 * all the ProtectionDomains from the current execution Thread.
 * The second argument represents an array of inherited ProtectionDomains,
 * which may be <code>null</code>.  ProtectionDomains may be inherited
 * from a parent Thread, or from a privileged context.  If no call to
 * doPrivileged was made, then the second argument will contain the
 * ProtectionDomains inherited from the parent Thread.  If one or more calls
 * to doPrivileged were made, and the most recent call was to
 * doPrivileged(action, context), then the second argument will contain the
 * ProtectionDomains from the privileged context.  If the most recent call
 * was to doPrivileged(action), then there is no privileged context,
 * and the second argument will be <code>null</code>.
 *
 * <p> The <code>combine</code> method investigates the two input arrays
 * of ProtectionDomains and returns a single array containing the updated
 * ProtectionDomains.  In the simplest case, the <code>combine</code>
 * method merges the two stacks into one.  In more complex cases,
 * the <code>combine</code> method returns a modified
 * stack of ProtectionDomains.  The modification may have added new
 * ProtectionDomains, removed certain ProtectionDomains, or simply
 * updated existing ProtectionDomains.  Re-ordering and other optimizations
 * to the ProtectionDomains are also permitted.  Typically the
 * <code>combine</code> method bases its updates on the information
 * encapsulated in the <code>DomainCombiner</code>.
 *
 * <p> After the <code>AccessController.getContext</code> method
 * receives the combined stack of ProtectionDomains back from
 * the <code>DomainCombiner</code>, it returns a new
 * AccessControlContext that has both the combined ProtectionDomains
 * as well as the <code>DomainCombiner</code>.
 *
 * @see AccessController
 * @see AccessControlContext
 * @since 1.3
 */

/**
 * DomainCombiner 提供一个动态更新与当前 AccessControlContext 关联的 ProtectionDomain 的方法。
 *
 * 将 DomainCombiner 作为参数传递给 AccessControlContext 的适当的构造方法。然后，将新构造的上下文传递给
 * AccessController.doPrivileged(..., context) 方法来将提供的上下文（以及关联的 DomainCombiner）与当前的执行线程绑定在一起。
 * 对 AccessController.getContext 或 AccessController.checkPermission 的后续调用将导致对 DomainCombiner.combine 的调用。
 *
 * 这只是一个接口规范，ProtectionDomain应该实现了这个接口
 */
public interface DomainCombiner {

    /**
     * Modify or update the provided ProtectionDomains.
     * ProtectionDomains may be added to or removed from the given
     * ProtectionDomains.  The ProtectionDomains may be re-ordered.
     * Individual ProtectionDomains may be modified (with a new
     * set of Permissions, for example).
     *
     * <p>
     *
     * @param currentDomains the ProtectionDomains associated with the
     *          current execution Thread, up to the most recent
     *          privileged <code>ProtectionDomain</code>.
     *          The ProtectionDomains are are listed in order of execution,
     *          with the most recently executing <code>ProtectionDomain</code>
     *          residing at the beginning of the array. This parameter may
     *          be <code>null</code> if the current execution Thread
     *          has no associated ProtectionDomains.<p>
     *
     * @param assignedDomains an array of inherited ProtectionDomains.
     *          ProtectionDomains may be inherited from a parent Thread,
     *          or from a privileged <code>AccessControlContext</code>.
     *          This parameter may be <code>null</code>
     *          if there are no inherited ProtectionDomains.
     *
     * @return a new array consisting of the updated ProtectionDomains,
     *          or <code>null</code>.
     *
     * 修改或更新提供的 ProtectionDomain。可以将 ProtectionDomain 添加到给定的 ProtectionDomain 中，也可以从中移除。
     * 可以重新排列 ProtectionDomain 的顺序。可以修改个别 ProtectionDomain（例如，利用新的 Permission 的集合）。
     */
    ProtectionDomain[] combine(ProtectionDomain[] currentDomains,
                                ProtectionDomain[] assignedDomains);
}
