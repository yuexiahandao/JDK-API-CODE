/*
 * Copyright (c) 2000, 2011, Oracle and/or its affiliates. All rights reserved.
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

package java.security.cert;

import java.io.ByteArrayInputStream;
import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * An immutable sequence of certificates (a certification path).
 * <p>
 * This is an abstract class that defines the methods common to all
 * <code>CertPath</code>s. Subclasses can handle different kinds of
 * certificates (X.509, PGP, etc.).
 * <p>
 * All <code>CertPath</code> objects have a type, a list of
 * <code>Certificate</code>s, and one or more supported encodings. Because the
 * <code>CertPath</code> class is immutable, a <code>CertPath</code> cannot
 * change in any externally visible way after being constructed. This
 * stipulation applies to all public fields and methods of this class and any
 * added or overridden by subclasses.
 * <p>
 * The type is a <code>String</code> that identifies the type of
 * <code>Certificate</code>s in the certification path. For each
 * certificate <code>cert</code> in a certification path <code>certPath</code>,
 * <code>cert.getType().equals(certPath.getType())</code> must be
 * <code>true</code>.
 * <p>
 * The list of <code>Certificate</code>s is an ordered <code>List</code> of
 * zero or more <code>Certificate</code>s. This <code>List</code> and all
 * of the <code>Certificate</code>s contained in it must be immutable.
 * <p>
 * Each <code>CertPath</code> object must support one or more encodings
 * so that the object can be translated into a byte array for storage or
 * transmission to other parties. Preferably, these encodings should be
 * well-documented standards (such as PKCS#7). One of the encodings supported
 * by a <code>CertPath</code> is considered the default encoding. This
 * encoding is used if no encoding is explicitly requested (for the
 * {@link #getEncoded() getEncoded()} method, for instance).
 * <p>
 * All <code>CertPath</code> objects are also <code>Serializable</code>.
 * <code>CertPath</code> objects are resolved into an alternate
 * {@link CertPathRep CertPathRep} object during serialization. This allows
 * a <code>CertPath</code> object to be serialized into an equivalent
 * representation regardless of its underlying implementation.
 * <p>
 * <code>CertPath</code> objects can be created with a
 * <code>CertificateFactory</code> or they can be returned by other classes,
 * such as a <code>CertPathBuilder</code>.
 * <p>
 * By convention, X.509 <code>CertPath</code>s (consisting of
 * <code>X509Certificate</code>s), are ordered starting with the target
 * certificate and ending with a certificate issued by the trust anchor. That
 * is, the issuer of one certificate is the subject of the following one. The
 * certificate representing the {@link TrustAnchor TrustAnchor} should not be
 * included in the certification path. Unvalidated X.509 <code>CertPath</code>s
 * may not follow these conventions. PKIX <code>CertPathValidator</code>s will
 * detect any departure from these conventions that cause the certification
 * path to be invalid and throw a <code>CertPathValidatorException</code>.
 *
 * <p> Every implementation of the Java platform is required to support the
 * following standard <code>CertPath</code> encodings:
 * <ul>
 * <li><tt>PKCS7</tt></li>
 * <li><tt>PkiPath</tt></li>
 * </ul>
 * These encodings are described in the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html#CertPathEncodings">
 * CertPath Encodings section</a> of the
 * Java Cryptography Architecture Standard Algorithm Name Documentation.
 * Consult the release documentation for your implementation to see if any
 * other encodings are supported.
 * <p>
 * <b>Concurrent Access</b>
 * <p>
 * All <code>CertPath</code> objects must be thread-safe. That is, multiple
 * threads may concurrently invoke the methods defined in this class on a
 * single <code>CertPath</code> object (or more than one) with no
 * ill effects. This is also true for the <code>List</code> returned by
 * <code>CertPath.getCertificates</code>.
 * <p>
 * Requiring <code>CertPath</code> objects to be immutable and thread-safe
 * allows them to be passed around to various pieces of code without worrying
 * about coordinating access.  Providing this thread-safety is
 * generally not difficult, since the <code>CertPath</code> and
 * <code>List</code> objects in question are immutable.
 *
 * @see CertificateFactory
 * @see CertPathBuilder
 *
 * @author      Yassir Elley
 * @since       1.4
 */

/**
 * 不可变的证书序列（证书路径）。
 * 这是一个抽象类，定义了常用于所有 CertPath 的方法。其子类可处理不同类型的证书（X.509、PGP 等等）。
 *
 * 所有 CertPath 对象都包含类型、Certificate 列表及其支持的一种或多种编码。由于 CertPath 类是不可变的，
 * 所以构造 CertPath 后无法以任何外部可见的方式更改它。此规定适用于此类的所有公共字段和方法，以及由子类添加或重写的所有公共字段和方法。
 *
 * 类型是标识证书路径中 Certificate 类型的一个 String。对于证书路径 certPath 中的每个证书 cert 而言，cert.getType().equals(certPath.getType()) 必须为 true。
 *
 * Certificate 列表是零个或多个 Certificate 的有序 List。此 List 和其中所包含的所有 Certificate 都必须是不可变的。
 *
 * 每个 CertPath 对象必须支持一种或多种编码方式，这样可将对象转换成 byte 数组进行存储，或传输给其他方。
 * 这些编码最好应该具有记录良好的标准（例如 PKCS#7）。将 CertPath 支持的某种编码视为默认编码。
 * 如果没有显式地请求编码（例如，getEncoded() 方法），则使用此编码。
 *
 * 所有 CertPath 对象都是 Serializable。在序列化期间将 CertPath 对象解析为一个替换的 CertPathRep 对象。
 * 这就允许不管 CertPath 对象的基础实现如何，都可以将该对象序列化为等效的表示形式。
 *
 * 可使用 CertificateFactory 创建 CertPath 对象，或者可通过其他类（如 CertPathBuilder）返回这些对象。
 *
 * 按照惯例，X.509 CertPath（由 X509Certificate 组成）的顺序按照从目标证书开始，从信任的定位点所发布的证书结束。
 * 也就是说，证书的发布方是以下某个主体。表示 TrustAnchor 的证书不应包括在证书路径中。
 * 未验证的 X.509 CertPath 可能不遵循这些约定。PKIX CertPathValidator 将检测任何与这些约定的偏差，
 * 这些偏差会导致证书路径无效并且抛出 CertPathValidatorException。
 *
 * 并发访问
 * 所有 CertPath 对象必须是线程安全的。也就是说，多个线程在单个 CertPath 对象（或多个对象）上并发调用此类中所定义的各种方法不会产生坏的影响。
 * 对于 CertPath.getCertificates 返回的 List 也应如此。
 *
 * 要求 CertPath 对象是不可变的并且是线程安全的，就允许将其传递到各种代码片断中，而无需担心协调访问。
 * 通常提供此种线程安全性并不难，因为相关的 CertPath 和 List 对象都是不可变的。
 *
 * 可以查看：http://www.enkichen.com/2016/02/26/digital-certificate-based/
 */
public abstract class CertPath implements Serializable {

    private static final long serialVersionUID = 6068470306649138683L;

    // 证书链的类型
    private String type;        // the type of certificates in this chain

    /**
     * Creates a <code>CertPath</code> of the specified type.
     * <p>
     * This constructor is protected because most users should use a
     * <code>CertificateFactory</code> to create <code>CertPath</code>s.
     *
     * @param type the standard name of the type of
     * <code>Certificate</code>s in this path
     */
    protected CertPath(String type) {
        this.type = type;
    }

    /**
     * Returns the type of <code>Certificate</code>s in this certification
     * path. This is the same string that would be returned by
     * {@link java.security.cert.Certificate#getType() cert.getType()}
     * for all <code>Certificate</code>s in the certification path.
     *
     * @return the type of <code>Certificate</code>s in this certification
     * path (never null)
     */
    public String getType() {
        return type;
    }

    /**
     * Returns an iteration of the encodings supported by this certification
     * path, with the default encoding first. Attempts to modify the returned
     * <code>Iterator</code> via its <code>remove</code> method result in an
     * <code>UnsupportedOperationException</code>.
     *
     * @return an <code>Iterator</code> over the names of the supported
     *         encodings (as Strings)
     */
    public abstract Iterator<String> getEncodings();

    /**
     * Compares this certification path for equality with the specified
     * object. Two <code>CertPath</code>s are equal if and only if their
     * types are equal and their certificate <code>List</code>s (and by
     * implication the <code>Certificate</code>s in those <code>List</code>s)
     * are equal. A <code>CertPath</code> is never equal to an object that is
     * not a <code>CertPath</code>.
     * <p>
     * This algorithm is implemented by this method. If it is overridden,
     * the behavior specified here must be maintained.
     *
     * @param other the object to test for equality with this certification path
     * @return true if the specified object is equal to this certification path,
     * false otherwise
     */
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (! (other instanceof CertPath))
            return false;

        CertPath otherCP = (CertPath) other;
        if (! otherCP.getType().equals(type))
            return false;

        List<? extends Certificate> thisCertList = this.getCertificates();
        List<? extends Certificate> otherCertList = otherCP.getCertificates();
        return(thisCertList.equals(otherCertList));
    }

    /**
     * Returns the hashcode for this certification path. The hash code of
     * a certification path is defined to be the result of the following
     * calculation:
     * <pre><code>
     *  hashCode = path.getType().hashCode();
     *  hashCode = 31*hashCode + path.getCertificates().hashCode();
     * </code></pre>
     * This ensures that <code>path1.equals(path2)</code> implies that
     * <code>path1.hashCode()==path2.hashCode()</code> for any two certification
     * paths, <code>path1</code> and <code>path2</code>, as required by the
     * general contract of <code>Object.hashCode</code>.
     *
     * @return the hashcode value for this certification path
     */
    public int hashCode() {
        int hashCode = type.hashCode();
        hashCode = 31*hashCode + getCertificates().hashCode();
        return hashCode;
    }

    /**
     * Returns a string representation of this certification path.
     * This calls the <code>toString</code> method on each of the
     * <code>Certificate</code>s in the path.
     *
     * @return a string representation of this certification path
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator<? extends Certificate> stringIterator =
                                        getCertificates().iterator();

        sb.append("\n" + type + " Cert Path: length = "
            + getCertificates().size() + ".\n");
        sb.append("[\n");
        int i = 1;
        while (stringIterator.hasNext()) {
            sb.append("=========================================="
                + "===============Certificate " + i + " start.\n");
            Certificate stringCert = stringIterator.next();
            sb.append(stringCert.toString());
            sb.append("\n========================================"
                + "=================Certificate " + i + " end.\n\n\n");
            i++;
        }

        sb.append("\n]");
        return sb.toString();
    }

    /**
     * Returns the encoded form of this certification path, using the default
     * encoding.
     *
     * @return the encoded bytes
     * @exception CertificateEncodingException if an encoding error occurs
     */
    public abstract byte[] getEncoded()
        throws CertificateEncodingException;

    /**
     * Returns the encoded form of this certification path, using the
     * specified encoding.
     *
     * @param encoding the name of the encoding to use
     * @return the encoded bytes
     * @exception CertificateEncodingException if an encoding error occurs or
     *   the encoding requested is not supported
     */
    public abstract byte[] getEncoded(String encoding)
        throws CertificateEncodingException;

    /**
     * Returns the list of certificates in this certification path.
     * The <code>List</code> returned must be immutable and thread-safe.
     *
     * @return an immutable <code>List</code> of <code>Certificate</code>s
     *         (may be empty, but not null)
     */
    public abstract List<? extends Certificate> getCertificates();

    /**
     * Replaces the <code>CertPath</code> to be serialized with a
     * <code>CertPathRep</code> object.
     *
     * @return the <code>CertPathRep</code> to be serialized
     *
     * @throws ObjectStreamException if a <code>CertPathRep</code> object
     * representing this certification path could not be created
     */
    protected Object writeReplace() throws ObjectStreamException {
        try {
            return new CertPathRep(type, getEncoded());
        } catch (CertificateException ce) {
            NotSerializableException nse =
                new NotSerializableException
                    ("java.security.cert.CertPath: " + type);
            nse.initCause(ce);
            throw nse;
        }
    }

    /**
     * Alternate <code>CertPath</code> class for serialization.
     * @since 1.4
     *
     * * 用于序列化的替换 CertPath 类。
     */
    protected static class CertPathRep implements Serializable {

        private static final long serialVersionUID = 3015633072427920915L;

        /** The Certificate type */
        private String type;
        /** The encoded form of the cert path */
        private byte[] data;

        /**
         * Creates a <code>CertPathRep</code> with the specified
         * type and encoded form of a certification path.
         *
         * @param type the standard name of a <code>CertPath</code> type
         * @param data the encoded form of the certification path
         */
        protected CertPathRep(String type, byte[] data) {
            this.type = type;
            this.data = data;
        }

        /**
         * Returns a <code>CertPath</code> constructed from the type and data.
         *
         * @return the resolved <code>CertPath</code> object
         *
         * @throws ObjectStreamException if a <code>CertPath</code> could not
         * be constructed
         */
        protected Object readResolve() throws ObjectStreamException {
            try {
                CertificateFactory cf = CertificateFactory.getInstance(type);
                return cf.generateCertPath(new ByteArrayInputStream(data));
            } catch (CertificateException ce) {
                NotSerializableException nse =
                    new NotSerializableException
                        ("java.security.cert.CertPath: " + type);
                nse.initCause(ce);
                throw nse;
            }
        }
    }
}
