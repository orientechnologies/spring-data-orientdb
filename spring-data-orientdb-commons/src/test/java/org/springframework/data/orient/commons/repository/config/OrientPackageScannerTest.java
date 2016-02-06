package org.springframework.data.orient.commons.repository.config;

import org.springframework.data.orient.commons.repository.config.scanner.test.EntitiesMarker;
import org.springframework.data.orient.commons.repository.config.scanner.test.NoEntity;
import org.springframework.data.orient.commons.repository.config.scanner.test.SimpleEntity;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author saljuama
 * @since 6th February 2016
 */
public class OrientPackageScannerTest {

    private final String testBasePackage = "org.springframework.data.orient.commons.repository.config.scanner.test";
    private final Class testBasePackageClass = EntitiesMarker.class;

    @Test
    public void testScanBasePackagesForClasses_withString() throws Exception {

        Set<? extends Class<?>> result = OrientPackageScanner.scanBasePackagesForClasses(testBasePackage);

        assertTrue( result.size() == 1 );
        assertTrue( result.contains( SimpleEntity.class ));
        assertFalse( result.contains( NoEntity.class ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScanBasePackagesForClasses_withClass() throws Exception {

        Set<? extends Class<?>> result = OrientPackageScanner.scanBasePackagesForClasses(testBasePackageClass);

        assertTrue( result.size() == 1 );
        assertTrue( result.contains( SimpleEntity.class ));
        assertFalse( result.contains( NoEntity.class ));
    }
}