/*
 * Copyright (c) 1997, 1998, Oracle and/or its affiliates. All rights reserved.
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
 * <p> This interface represents a guard, which is an object that is used
 * to protect access to another object.
 *
 * <p>This interface contains a single method, <code>checkGuard</code>,
 * with a single <code>object</code> argument. <code>checkGuard</code> is
 * invoked (by the GuardedObject <code>getObject</code> method)
 * to determine whether or not to allow access to the object.
 *
 * @see GuardedObject
 *
 * @author Roland Schemers
 * @author Li Gong
 *
 * 这个接口代表一个守卫，守卫是一个对象，用来保护对其他对象的访问。
 */

public interface Guard {

    /**
     * Determines whether or not to allow access to the guarded object
     * <code>object</code>. Returns silently if access is allowed.
     * Otherwise, throws a SecurityException.
     *
     * @param object the object being protected by the guard.
     *
     * @exception SecurityException if access is denied.
     *
     * 通过object去判断是否被允许访问guard对象。如果可以，沉默返回，否则抛出异常。
     *
     */
    void checkGuard(Object object) throws SecurityException;
}
