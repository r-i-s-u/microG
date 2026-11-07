/*
 * SPDX-FileCopyrightText: 2026 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.location.manager

import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import com.google.android.gms.location.Granularity.GRANULARITY_COARSE
import com.google.android.gms.location.Granularity.GRANULARITY_FINE
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LastLocationCapsuleTest {
    @Test
    fun fixedLocationIsAvailableWithoutADeviceFixAndIsFresh() {
        val capsule = LastLocationCapsule(RuntimeEnvironment.getApplication())
        val before = SystemClock.elapsedRealtimeNanos()
        val location = requireNotNull(capsule.getLocation(GRANULARITY_FINE, 0))
        assertEquals(28.347657, location.latitude, 0.0)
        assertEquals(79.429645, location.longitude, 0.0)
        assertTrue(location.elapsedRealtimeNanos >= before)
        assertEquals("fused", location.provider)
    }

    @Test
    fun deviceFixAndItsMetadataAreNotReturned() {
        val capsule = LastLocationCapsule(RuntimeEnvironment.getApplication())
        val deviceFix = Location("gps").apply {
            latitude = 40.7128
            longitude = -74.0060
            accuracy = 3f
            altitude = 100.0
            speed = 10f
            extras = Bundle().apply { putString("source", "device") }
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
        capsule.updateFineLocation(deviceFix)
        for (granularity in listOf(GRANULARITY_FINE, GRANULARITY_COARSE)) {
            val location = requireNotNull(capsule.getLocation(granularity))
            assertEquals(28.347657, location.latitude, 0.0)
            assertEquals(79.429645, location.longitude, 0.0)
            assertFalse(location.hasAltitude())
            assertFalse(location.hasSpeed())
            assertNull(location.extras)
        }
    }
}
