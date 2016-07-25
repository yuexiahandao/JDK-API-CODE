/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
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

import java.io.File;

class ClassLoaderHelper {

    private ClassLoaderHelper() {}

    /**
     * Returns an alternate path name for the given file
     * such that if the original pathname did not exist, then the
     * file may be located at the alternate location.
     * For most platforms, this behavior is not supported and returns null.
     *
     * 为给定的文件返回一个可选的路径名，这样的话，如果源路径名不存在，那么这个文件可能被放在可选的路径中。
     * 对于绝大多数的平台，不支持这种操作并且返回null。
     *
     * 这个应该是平台相关的。
     */
    static File mapAlternativeName(File lib) {
        return null;
    }
}
