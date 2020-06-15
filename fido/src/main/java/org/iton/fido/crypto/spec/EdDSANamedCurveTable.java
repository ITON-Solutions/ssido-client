/*
 *
 *  The MIT License
 *
 *  Copyright 2019 ITON Solutions.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */
package org.iton.fido.crypto.spec;

import org.iton.fido.crypto.math.Curve;
import org.iton.fido.crypto.math.Field;
import org.iton.fido.crypto.math.ed25519.Ed25519LittleEndianEncoding;
import org.iton.fido.crypto.math.ed25519.Ed25519ScalarOps;
import org.iton.fido.util.CryptoUtil;

import java.util.HashMap;
import java.util.Locale;


/**
 * The named EdDSA curves.
 *
 * @author str4d
 */
public class EdDSANamedCurveTable {
    public static final String ED_25519 = "Ed25519";

    private static final Field ed25519field = new Field(
            256, // b
            CryptoUtil.fromHex("edffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff7f"), // q
            new Ed25519LittleEndianEncoding());

    private static final Curve ed25519curve = new Curve(ed25519field,
            CryptoUtil.fromHex("a3785913ca4deb75abd841414d0a700098e879777940c78c73fe6f2bee6c0352"), // d
            ed25519field.fromByteArray(CryptoUtil.fromHex("b0a00e4a271beec478e42fad0618432fa7d7fb3d99004d2b0bdfc14f8024832b"))); // I

    public static final EdDSANamedCurveSpec ED_25519_CURVE_SPEC = new EdDSANamedCurveSpec(
            ED_25519,
            ed25519curve,
            "SHA-512", // H
            new Ed25519ScalarOps(), // l
            ed25519curve.createPoint( // B
                    CryptoUtil.fromHex("5866666666666666666666666666666666666666666666666666666666666666"),
                    true)); // Precompute tables for B

    private static volatile HashMap<String, EdDSANamedCurveSpec> curves = new HashMap<>();

    private static synchronized void putCurve(String name, EdDSANamedCurveSpec curve) {
        HashMap<String, EdDSANamedCurveSpec> newCurves = new HashMap<>(curves);
        newCurves.put(name, curve);
        curves = newCurves;
    }

    public static void defineCurve(EdDSANamedCurveSpec curve) {
        putCurve(curve.getName().toLowerCase(Locale.ENGLISH), curve);
    }

    static void defineCurveAlias(String name, String alias) {
        EdDSANamedCurveSpec curve = curves.get(name.toLowerCase(Locale.ENGLISH));
        if (curve == null) {
            throw new IllegalStateException();
        }
        putCurve(alias.toLowerCase(Locale.ENGLISH), curve);
    }

    static {
        // RFC 8032
        defineCurve(ED_25519_CURVE_SPEC);
    }

    public static EdDSANamedCurveSpec getByName(String name) {
        return curves.get(name.toLowerCase(Locale.ENGLISH));
    }
}
