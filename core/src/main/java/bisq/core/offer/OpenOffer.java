/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.offer;

import bisq.core.trade.Tradable;

import bisq.network.p2p.NodeAddress;

import bisq.common.Timer;
import bisq.common.UserThread;
import bisq.common.proto.ProtoUtil;
import java.util.Date;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

@EqualsAndHashCode
@Slf4j
public final class OpenOffer implements Tradable {
    // Timeout for offer reservation during takeoffer process. If deposit tx is not completed in that time we reset the offer to AVAILABLE state.
    private static final long TIMEOUT = 60;
    transient private Timer timeoutTimer;

    public enum State {
        AVAILABLE,
        RESERVED,
        CLOSED,
        CANCELED,
        DEACTIVATED
    }

    @Getter
    private final Offer offer;
    @Getter
    private State state;
    @Getter
    @Setter
    @Nullable
    private NodeAddress backupArbitrator;
    @Setter
    @Getter
    private String reserveTxHash;
    @Setter
    @Getter
    private String reserveTxHex;
    @Setter
    @Getter
    private String reserveTxKey;
    

    // Added in v1.5.3.
    // If market price reaches that trigger price the offer gets deactivated
    @Getter
    private final long triggerPrice;
    @Getter
    @Setter
    transient private long mempoolStatus = -1;

    public OpenOffer(Offer offer) {
        this(offer, 0);
    }

    public OpenOffer(Offer offer, long triggerPrice) {
        this.offer = offer;
        this.triggerPrice = triggerPrice;
        state = State.AVAILABLE;
    }
    
    public OpenOffer(Offer offer,
                     long triggerPrice,
                     String reserveTxHash,
                     String reserveTxHex,
                     String reserveTxKey) {
        this.offer = offer;
        this.triggerPrice = triggerPrice;
        state = State.AVAILABLE;
        this.reserveTxHash = reserveTxHash;
        this.reserveTxHex = reserveTxHex;
        this.reserveTxKey = reserveTxKey;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private OpenOffer(Offer offer,
                      State state,
                      @Nullable NodeAddress backupArbitrator,
                      long triggerPrice,
                      String reserveTxHash,
                      String reserveTxHex,
                      String reserveTxKey) {
        this.offer = offer;
        this.state = state;
        this.backupArbitrator = backupArbitrator;
        this.triggerPrice = triggerPrice;
        this.reserveTxHash = reserveTxHash;
        this.reserveTxHex = reserveTxHex;
        this.reserveTxKey = reserveTxKey;

        if (this.state == State.RESERVED)
            setState(State.AVAILABLE);
    }

    @Override
    public protobuf.Tradable toProtoMessage() {
        protobuf.OpenOffer.Builder builder = protobuf.OpenOffer.newBuilder()
                .setOffer(offer.toProtoMessage())
                .setTriggerPrice(triggerPrice)
                .setState(protobuf.OpenOffer.State.valueOf(state.name()))
                .setReserveTxHash(reserveTxHash)
                .setReserveTxHex(reserveTxHex)
                .setReserveTxKey(reserveTxKey);

        Optional.ofNullable(backupArbitrator).ifPresent(nodeAddress -> builder.setBackupArbitrator(nodeAddress.toProtoMessage()));

        return protobuf.Tradable.newBuilder().setOpenOffer(builder).build();
    }

    public static Tradable fromProto(protobuf.OpenOffer proto) {
        OpenOffer openOffer = new OpenOffer(Offer.fromProto(proto.getOffer()),
                ProtoUtil.enumFromProto(OpenOffer.State.class, proto.getState().name()),
                proto.hasBackupArbitrator() ? NodeAddress.fromProto(proto.getBackupArbitrator()) : null,
                proto.getTriggerPrice(),
                proto.getReserveTxHash(),
                proto.getReserveTxHex(),
                proto.getReserveTxKey());
        return openOffer;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Date getDate() {
        return offer.getDate();
    }

    @Override
    public String getId() {
        return offer.getId();
    }

    @Override
    public String getShortId() {
        return offer.getShortId();
    }

    public void setState(State state) {
        this.state = state;

        // We keep it reserved for a limited time, if trade preparation fails we revert to available state
        if (this.state == State.RESERVED) {
            startTimeout();
        } else {
            stopTimeout();
        }
    }

    public boolean isDeactivated() {
        return state == State.DEACTIVATED;
    }

    private void startTimeout() {
        stopTimeout();

        timeoutTimer = UserThread.runAfter(() -> {
            log.debug("Timeout for resetting State.RESERVED reached");
            if (state == State.RESERVED) {
                // we do not need to persist that as at startup any RESERVED state would be reset to AVAILABLE anyway
                setState(State.AVAILABLE);
            }
        }, TIMEOUT);
    }

    private void stopTimeout() {
        if (timeoutTimer != null) {
            timeoutTimer.stop();
            timeoutTimer = null;
        }
    }


    @Override
    public String toString() {
        return "OpenOffer{" +
                ",\n     offer=" + offer +
                ",\n     state=" + state +
                ",\n     arbitratorNodeAddress=" + backupArbitrator +
                ",\n     triggerPrice=" + triggerPrice +
                "\n}";
    }
}

