/*
 * Copyright 2019-2021 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.ringrtc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 *
 * Represents the currently joined users and other transient state in a group call.
 */
public final class PeekInfo {
  // These "synthetic" status codes match up with Rust's lite::http::ResponseStatus.

  /**
   * As a peek result, indicates that a call link has expired or been revoked.
   */
  public static final short EXPIRED_CALL_LINK_STATUS = 703;

  /**
   * As a peek result, indicates that a call link is invalid.
   *
   * It may have expired a long time ago.
   */
  public static final short INVALID_CALL_LINK_STATUS = 704;

  @NonNull
  private static final String TAG = PeekInfo.class.getSimpleName();

  @NonNull
  private final Collection<UUID> joinedMembers;
  @Nullable
  private final UUID             creator;
  @Nullable
  private final String           eraId;
  @Nullable
  private final Long             maxDevices;

  private final long             deviceCount;

  public PeekInfo(
    @NonNull  Collection<UUID> joinedMembers,
    @Nullable UUID             creator,
    @Nullable String           eraId,
    @Nullable Long             maxDevices,
              long             deviceCount
  ) {
    this.joinedMembers = joinedMembers;
    this.creator = creator;
    this.eraId = eraId;
    this.maxDevices = maxDevices;
    this.deviceCount = deviceCount;
  }

  @CalledByNative
  private static PeekInfo fromNative(
    @NonNull  List<byte[]> joinedMembers,
    @Nullable byte[]       creator,
    @Nullable String       eraId,
    @Nullable Long         maxDevices,
              long         deviceCount
  ) {
    Log.i(TAG, "fromNative(): joinedMembers.size = " + joinedMembers.size());

    // Create the collection, converting each provided byte[] to a UUID.
    Collection<UUID> joinedGroupMembers = new ArrayList<UUID>(joinedMembers.size());
    for (byte[] joinedMember : joinedMembers) {
        joinedGroupMembers.add(Util.getUuidFromBytes(joinedMember));
    }

    return new PeekInfo(joinedGroupMembers, creator == null ? null : Util.getUuidFromBytes(creator), eraId, maxDevices, deviceCount);
  }

  @NonNull
  public Collection<UUID> getJoinedMembers() {
    return joinedMembers;
  }

  @Nullable
  public UUID getCreator() {
    return creator;
  }

  @Nullable
  public String getEraId() {
    return eraId;
  }

  @Nullable
  public Long getMaxDevices() {
    return maxDevices;
  }

  public long getDeviceCount() {
    return deviceCount;
  }
}
