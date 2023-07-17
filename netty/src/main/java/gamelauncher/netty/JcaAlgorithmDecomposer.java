/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class JcaAlgorithmDecomposer {
    static final JcaAlgorithmDecomposer INSTANCE_JCA = new JcaAlgorithmDecomposer();
    private static final Pattern PATTERN = Pattern.compile("with|and|(?<!padd)in", Pattern.CASE_INSENSITIVE);

    private static void ensureBothIfEither(Set<String> elements, String a, String b) {
        boolean hasA = elements.contains(a), hasB = elements.contains(b);
        if (hasA ^ hasB) {
            elements.add(hasA ? b : a);
        }
    }

    public Set<String> decompose(String algorithm) {
        if (algorithm.indexOf('/') < 0) {
            return java.util.Collections.emptySet();
        }

        Set<String> result = new HashSet<String>();

        for (String section : algorithm.split("/")) {
            if (section.length() > 0) {
                for (String part : PATTERN.split(section)) {
                    if (part.length() > 0) {
                        result.add(part);
                    }
                }
            }
        }

        ensureBothIfEither(result, "SHA1", "SHA-1");
        ensureBothIfEither(result, "SHA224", "SHA-224");
        ensureBothIfEither(result, "SHA256", "SHA-256");
        ensureBothIfEither(result, "SHA384", "SHA-384");
        ensureBothIfEither(result, "SHA512", "SHA-512");

        return result;
    }
}
