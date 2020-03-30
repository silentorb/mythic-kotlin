/*
 * (C) Copyright 2015-2018 JOML

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package silentorb.mythic.spatial

import org.joml.internal.Options
import java.lang.Math

/**
 * Contains fast approximations of some [java.lang.Math] operations.
 *
 *
 * By default, [java.lang.Math] methods will be used by all other JOML classes. In order to use the approximations in this class, start the JVM with the parameter <tt>-Djoml.fastmath</tt>.
 *
 *
 * There are two algorithms for approximating sin/cos:
 *
 *  1. arithmetic [polynomial approximation](http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361815/view.html#msg361815) contributed by roquendm
 *  1. theagentd's [linear interpolation](http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/msg/346213/view.html#msg346213) variant of Riven's algorithm from
 * [http://www.java-gaming.org/](http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/view.html)
 *
 * By default, the first algorithm is being used. In order to use the second one, start the JVM with <tt>-Djoml.sinLookup</tt>. The lookup table bit length of the second algorithm can also be adjusted
 * for improved accuracy via <tt>-Djoml.sinLookup.bits=&lt;n&gt;</tt>, where &lt;n&gt; is the number of bits of the lookup table.
 *
 * @author Kai Burjack
 */
object Math {
    /*
     * The following implementation of an approximation of sine and cosine was
     * thankfully donated by Riven from http://java-gaming.org/.
     *
     * The code for linear interpolation was gratefully donated by theagentd
     * from the same site.
     */
    const val PI = Math.PI
    const val PI2 = PI * 2.0
    const val PIHalf = PI * 0.5
    const val PI_4 = PI * 0.25
    const val PI_INV = 1.0 / PI
    private val lookupBits = Options.SIN_LOOKUP_BITS
    private val lookupTableSize = 1 shl lookupBits
    private val lookupTableSizeMinus1 = lookupTableSize - 1
    private val lookupTableSizeWithMargin = lookupTableSize + 1
    private val pi2OverLookupSize =
        PI2 / lookupTableSize
    private val lookupSizeOverPi2 =
        lookupTableSize / PI2
    private val sinTable: FloatArray?
    private val c1 = java.lang.Double.longBitsToDouble(-4628199217061079772L)
    private val c2 = java.lang.Double.longBitsToDouble(4575957461383582011L)
    private val c3 = java.lang.Double.longBitsToDouble(-4671919876300759001L)
    private val c4 = java.lang.Double.longBitsToDouble(4523617214285661942L)
    private val c5 = java.lang.Double.longBitsToDouble(-4730215272828025532L)
    private val c6 = java.lang.Double.longBitsToDouble(4460272573143870633L)
    private val c7 = java.lang.Double.longBitsToDouble(-4797767418267846529L)

    /**
     * @author theagentd
     */
    fun sin_theagentd_arith(x: Double): Double {
        val xi =
            floor((x + PI_4) * PI_INV)
        val x_ = x - xi * PI
        val sign = (xi.toInt() and 1) * -2 + 1.toDouble()
        val x2 = x_ * x_
        var sin = x_
        var tx = x_ * x2
        sin += tx * c1
        tx *= x2
        sin += tx * c2
        tx *= x2
        sin += tx * c3
        tx *= x2
        sin += tx * c4
        tx *= x2
        sin += tx * c5
        tx *= x2
        sin += tx * c6
        tx *= x2
        sin += tx * c7
        return sign * sin
    }

    /**
     * Reference: [http://www.java-gaming.org/](http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361718/view.html#msg361718)
     */
    fun sin_roquen_arith(x: Double): Double {
        val xi =
            floor((x + PI_4) * PI_INV)
        var x_ = x - xi * PI
        val sign = (xi.toInt() and 1) * -2 + 1.toDouble()
        val x2 = x_ * x_

        // code from sin_theagentd_arith:
        // double sin = x_;
        // double tx = x_ * x2;
        // sin += tx * c1; tx *= x2;
        // sin += tx * c2; tx *= x2;
        // sin += tx * c3; tx *= x2;
        // sin += tx * c4; tx *= x2;
        // sin += tx * c5; tx *= x2;
        // sin += tx * c6; tx *= x2;
        // sin += tx * c7;
        // return sign * sin;
        var sin: Double
        x_ = sign * x_
        sin = c7
        sin = sin * x2 + c6
        sin = sin * x2 + c5
        sin = sin * x2 + c4
        sin = sin * x2 + c3
        sin = sin * x2 + c2
        sin = sin * x2 + c1
        return x_ + x_ * x2 * sin
    }

    private val s5 = java.lang.Double.longBitsToDouble(4523227044276562163L)
    private val s4 = java.lang.Double.longBitsToDouble(-4671934770969572232L)
    private val s3 = java.lang.Double.longBitsToDouble(4575957211482072852L)
    private val s2 = java.lang.Double.longBitsToDouble(-4628199223918090387L)
    private val s1 = java.lang.Double.longBitsToDouble(4607182418589157889L)

    /**
     * Reference: [http://www.java-gaming.org/](http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361815/view.html#msg361815)
     */
    fun sin_roquen_9(v: Double): Double {
        val i = Math.rint(v * PI_INV)
        var x = v - i * PI
        val qs = 1 - 2 * (i.toInt() and 1).toDouble()
        val x2 = x * x
        var r: Double
        x = qs * x
        r = s5
        r = r * x2 + s4
        r = r * x2 + s3
        r = r * x2 + s2
        r = r * x2 + s1
        return x * r
    }

    private val k1 = java.lang.Double.longBitsToDouble(-4628199217061079959L)
    private val k2 = java.lang.Double.longBitsToDouble(4575957461383549981L)
    private val k3 = java.lang.Double.longBitsToDouble(-4671919876307284301L)
    private val k4 = java.lang.Double.longBitsToDouble(4523617213632129738L)
    private val k5 = java.lang.Double.longBitsToDouble(-4730215344060517252L)
    private val k6 = java.lang.Double.longBitsToDouble(4460268259291226124L)
    private val k7 = java.lang.Double.longBitsToDouble(-4798040743777455072L)

    /**
     * Reference: [http://www.java-gaming.org/](http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361815/view.html#msg361815)
     */
    fun sin_roquen_newk(v: Double): Double {
        val i = Math.rint(v * PI_INV)
        var x = v - i * PI
        val qs = 1 - 2 * (i.toInt() and 1).toDouble()
        val x2 = x * x
        var r: Double
        x = qs * x
        r = k7
        r = r * x2 + k6
        r = r * x2 + k5
        r = r * x2 + k4
        r = r * x2 + k3
        r = r * x2 + k2
        r = r * x2 + k1
        return x + x * x2 * r
    }

    /**
     * Reference: [http://www.java-gaming.org/](http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/msg/349515/view.html#msg349515)
     */
    fun sin_theagentd_lookup(rad: Double): Double {
        val index = (rad * lookupSizeOverPi2).toFloat()
        val ii = Math.floor(index.toDouble()).toInt()
        val alpha = index - ii
        val i = ii and lookupTableSizeMinus1
        val sin1 = sinTable!![i]
        val sin2 = sinTable[i + 1]
        return (sin1 + (sin2 - sin1) * alpha).toDouble()
    }

    fun sin(rad: Double): Double {
        return if (Options.FASTMATH) {
            if (Options.SIN_LOOKUP) sin_theagentd_lookup(rad) else sin_roquen_newk(rad)
        } else Math.sin(rad)
    }

    fun cos(rad: Double): Double {
        return if (Options.FASTMATH) sin(rad + PIHalf) else Math.cos(rad)
    }

    fun cosFromSin(sin: Double, angle: Double): Double {
        if (Options.FASTMATH) return sin(angle + PIHalf)
        // sin(x)^2 + cos(x)^2 = 1
        val cos = sqrt(1.0 - sin * sin)
        val a = angle + PIHalf
        var b =
            a - (a / PI2).toInt() * PI2
        if (b < 0.0) b = PI2 + b
        return if (b >= PI) -cos else cos
    }

    /* Other math functions not yet approximated */
    fun sqrt(r: Double): Double {
        return Math.sqrt(r)
    }

    fun tan(r: Double): Double {
        return Math.tan(r)
    }

    fun acos(r: Double): Double {
        return Math.acos(r)
    }

    fun atan2(y: Double, x: Double): Double {
        return Math.atan2(y, x)
    }

    fun asin(r: Double): Double {
        return Math.asin(r)
    }

    fun abs(r: Double): Double {
        return Math.abs(r)
    }

    fun abs(r: Float): Float {
        return Math.abs(r)
    }

    fun max(x: Int, y: Int): Int {
        return Math.max(x, y)
    }

    fun min(x: Int, y: Int): Int {
        return Math.min(x, y)
    }

    fun min(a: Float, b: Float): Float {
        return if (a < b) a else b
    }

    fun max(a: Float, b: Float): Float {
        return if (a > b) a else b
    }

    fun min(a: Double, b: Double): Double {
        return if (a < b) a else b
    }

    fun max(a: Double, b: Double): Double {
        return if (a > b) a else b
    }

    fun toRadians(angles: Double): Double {
        return Math.toRadians(angles)
    }

    fun toDegrees(angles: Double): Double {
        return Math.toDegrees(angles)
    }

    fun floor(v: Double): Double {
        return Math.floor(v)
    }

    fun exp(a: Double): Double {
        return Math.exp(a)
    }

    init {
        if (Options.FASTMATH && Options.SIN_LOOKUP) {
            sinTable = FloatArray(lookupTableSizeWithMargin)
            for (i in 0 until lookupTableSizeWithMargin) {
                val d = i * pi2OverLookupSize
                sinTable[i] =
                    Math.sin(d).toFloat()
            }
        } else {
            sinTable = null
        }
    }
}
