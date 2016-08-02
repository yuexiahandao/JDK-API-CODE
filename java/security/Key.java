/*
 * Copyright (c) 1996, 2006, Oracle and/or its affiliates. All rights reserved.
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
 * The Key interface is the top-level interface for all keys. It
 * defines the functionality shared by all key objects. All keys
 * have three characteristics:
 *
 * <UL>
 *
 * <LI>An Algorithm
 *
 * <P>This is the key algorithm for that key. The key algorithm is usually
 * an encryption or asymmetric operation algorithm (such as DSA or
 * RSA), which will work with those algorithms and with related
 * algorithms (such as MD5 with RSA, SHA-1 with RSA, Raw DSA, etc.)
 * The name of the algorithm of a key is obtained using the
 * {@link #getAlgorithm() getAlgorithm} method.<P>
 *
 * <LI>An Encoded Form
 *
 * <P>This is an external encoded form for the key used when a standard
 * representation of the key is needed outside the Java Virtual Machine,
 * as when transmitting the key to some other party. The key
 * is encoded according to a standard format (such as
 * X.509 <code>SubjectPublicKeyInfo</code> or PKCS#8), and
 * is returned using the {@link #getEncoded() getEncoded} method.
 * Note: The syntax of the ASN.1 type <code>SubjectPublicKeyInfo</code>
 * is defined as follows:
 *
 * <pre>
 * SubjectPublicKeyInfo ::= SEQUENCE {
 *   algorithm AlgorithmIdentifier,
 *   subjectPublicKey BIT STRING }
 *
 * AlgorithmIdentifier ::= SEQUENCE {
 *   algorithm OBJECT IDENTIFIER,
 *   parameters ANY DEFINED BY algorithm OPTIONAL }
 * </pre>
 *
 * For more information, see
 * <a href="http://www.ietf.org/rfc/rfc3280.txt">RFC 3280:
 * Internet X.509 Public Key Infrastructure Certificate and CRL Profile</a>.
 * <P>
 *
 * <LI>A Format
 *
 * <P>This is the name of the format of the encoded key. It is returned
 * by the {@link #getFormat() getFormat} method.<P>
 *
 * </UL>
 *
 * Keys are generally obtained through key generators, certificates,
 * or various Identity classes used to manage keys.
 * Keys may also be obtained from key specifications (transparent
 * representations of the underlying key material) through the use of a key
 * factory (see {@link KeyFactory}).
 *
 * <p> A Key should use KeyRep as its serialized representation.
 * Note that a serialized Key may contain sensitive information
 * which should not be exposed in untrusted environments.  See the
 * <a href="../../../platform/serialization/spec/security.html">
 * Security Appendix</a>
 * of the Serialization Specification for more information.
 *
 * @see PublicKey
 * @see PrivateKey
 * @see KeyPair
 * @see KeyPairGenerator
 * @see KeyFactory
 * @see KeyRep
 * @see java.security.spec.KeySpec
 * @see Identity
 * @see Signer
 *
 * @author Benjamin Renaud
 *
 * Key 是所有密钥的顶层接口。它定义了供所有密钥对象共享的功能。所有的密钥都具有三个特征：
 * 算法 ：
 * 这是该密钥的密钥算法。密钥算法通常是加密或不对称操作算法（如 DSA 或 RSA），它们将和那些算法及相关的算法（例如 MD5 和 RSA，SHA-1 和 RSA、Raw DSA 等等）一起使用。用 getAlgorithm 方法获取密钥算法的名称。
 *
 * 编码形式 ：这是密钥的外部编码形式，在 Java 虚拟机之外需要密钥的标准表示形式时以及将密钥传输到其他某些部分时使用。密钥根据标准格式（如 X.509 SubjectPublicKeyInfo 或 PKCS#8）编码，使用 getEncoded 方法返回。
 *
 * 格式：这是已编码密钥的格式的名称。它由 getFormat 方法返回。
 *
 * 密钥通常通过密钥生成器、证书或用来管理密钥的各种 Identity 类来获取。密钥也可以通过使用密钥工厂（请参见 KeyFactory）从密钥规范（基础密钥材料的透明表示形式）获取。
 *
 * 密钥应该使用 KeyRep 作为其序列化的表示形式。注意，序列化的 Key 可能包含不应该在不可信任的环境中显示的敏感信息。有关更多信息，请参见序列化规范的安全附录。
 */

public interface Key extends java.io.Serializable {

    // Declare serialVersionUID to be compatible with JDK1.1

   /**
    * The class fingerprint that is set to indicate
    * serialization compatibility with a previous
    * version of the class.
    */
    static final long serialVersionUID = 6603384152749567654L;

    /**
     * Returns the standard algorithm name for this key. For
     * example, "DSA" would indicate that this key is a DSA key.
     * See Appendix A in the <a href=
     * "../../../technotes/guides/security/crypto/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a>
     * for information about standard algorithm names.
     *
     * @return the name of the algorithm associated with this key.
     */
    public String getAlgorithm();

    /**
     * Returns the name of the primary encoding format of this key,
     * or null if this key does not support encoding.
     * The primary encoding format is
     * named in terms of the appropriate ASN.1 data format, if an
     * ASN.1 specification for this key exists.
     * For example, the name of the ASN.1 data format for public
     * keys is <I>SubjectPublicKeyInfo</I>, as
     * defined by the X.509 standard; in this case, the returned format is
     * <code>"X.509"</code>. Similarly,
     * the name of the ASN.1 data format for private keys is
     * <I>PrivateKeyInfo</I>,
     * as defined by the PKCS #8 standard; in this case, the returned format is
     * <code>"PKCS#8"</code>.
     *
     * @return the primary encoding format of the key.
     */
    public String getFormat();

    /**
     * Returns the key in its primary encoding format, or null
     * if this key does not support encoding.
     *
     * @return the encoded key, or null if the key does not support
     * encoding.
     */
    public byte[] getEncoded();
}
