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

package org.iton.fido.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CollectionUtil {

    /**
     * Make an unmodifiable shallow copy of the argument.
     *
     * @return A shallow copy of <code>m</code> which cannot be modified
     */
    public static <K, V> Map<K, V> immutableMap(Map<K, V> m) {
        return Collections.unmodifiableMap(new HashMap<>(m));
    }

    /**
     * Make an unmodifiable shallow copy of the argument.
     *
     * @return A shallow copy of <code>l</code> which cannot be modified
     */
    public static <T> List<T> immutableList(List<T> l) {
        return Collections.unmodifiableList(new ArrayList<>(l));
    }

    /**
     * Make an unmodifiable shallow copy of the argument.
     *
     * @return A shallow copy of <code>s</code> which cannot be modified
     */
    public static <T> Set<T> immutableSet(Set<T> s) {
        return Collections.unmodifiableSet(new HashSet<>(s));
    }

    /**
     * Make an unmodifiable shallow copy of the argument.
     *
     * @return A shallow copy of <code>s</code> which cannot be modified
     */
    public static <T> Set<T> immutableSortedSet(SortedSet<T> s) {
        return Collections.unmodifiableSortedSet(new TreeSet<>(s));
    }

}
