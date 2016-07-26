/*
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
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

/**
 * 字符数据类，主要是常见的字符集
 *
 * CharacterData 接口使用属性集合和用于访问 DOM 中字符数据的方法扩展节点。
 * 为了清楚起见，在这里定义此集合，而不是在使用这些属性和方法的每个对象上定义。
 * 任何 DOM 对象都不会直接对应于 CharacterData，尽管 Text 和其他的对象是从它继承该接口的。此接口中的所有 offset 都从 0 开始。
 *
 * 如 DOMString 接口中所述，DOM 中的文本字符串以 UTF-16（即 16 位单元序列）表示。
 * 在下述情况下，每当需要指示以 16 位单元在 CharacterData 上进行索引时就使用术语 16 位单元。
 */
abstract class CharacterData {
    abstract int getProperties(int ch);
    abstract int getType(int ch);
    abstract boolean isWhitespace(int ch);
    abstract boolean isMirrored(int ch);
    abstract boolean isJavaIdentifierStart(int ch);
    abstract boolean isJavaIdentifierPart(int ch);
    abstract boolean isUnicodeIdentifierStart(int ch);
    abstract boolean isUnicodeIdentifierPart(int ch);
    abstract boolean isIdentifierIgnorable(int ch);
    abstract int toLowerCase(int ch);
    abstract int toUpperCase(int ch);
    abstract int toTitleCase(int ch);
    abstract int digit(int ch, int radix);
    abstract int getNumericValue(int ch);
    abstract byte getDirectionality(int ch);

    //need to implement for JSR204
    int toUpperCaseEx(int ch) {
        return toUpperCase(ch);
    }

    char[] toUpperCaseCharArray(int ch) {
        return null;
    }

    boolean isOtherLowercase(int ch) {
        return false;
    }

    boolean isOtherUppercase(int ch) {
        return false;
    }

    boolean isOtherAlphabetic(int ch) {
        return false;
    }

    boolean isIdeographic(int ch) {
        return false;
    }

    // Character <= 0xff (basic latin) is handled by internal fast-path
    // to avoid initializing large tables.
    // Note: performance of this "fast-path" code may be sub-optimal
    // in negative cases for some accessors due to complicated ranges.
    // Should revisit after optimization of table initialization.

    /**
     * 这是获取实现类之一
     * @param ch
     * @return
     */
    static final CharacterData of(int ch) {
        if (ch >>> 8 == 0) {     // fast-path
            // 获取CharacterDataLatin1实例
            return CharacterDataLatin1.instance;
        } else {
            switch(ch >>> 16) {  //plane 00-16
            case(0):
                return CharacterData00.instance;
            case(1):
                return CharacterData01.instance;
            case(2):
                return CharacterData02.instance;
            case(14):
                return CharacterData0E.instance;
            case(15):   // Private Use
            case(16):   // Private Use
                return CharacterDataPrivateUse.instance;
            default:
                return CharacterDataUndefined.instance;
            }
        }
    }
}
