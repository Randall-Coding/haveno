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

package bisq.core.trade.protocol;

import bisq.core.account.witness.AccountAgeWitnessService;
import bisq.core.btc.wallet.BtcWalletService;
import bisq.core.btc.wallet.TradeWalletService;
import bisq.core.btc.wallet.XmrWalletService;
import bisq.core.filter.FilterManager;
import bisq.core.offer.OpenOfferManager;
import bisq.core.support.dispute.arbitration.arbitrator.ArbitratorManager;
import bisq.core.support.dispute.mediation.mediator.MediatorManager;
import bisq.core.support.dispute.refund.refundagent.RefundAgentManager;
import bisq.core.trade.statistics.ReferralIdService;
import bisq.core.trade.statistics.TradeStatisticsManager;
import bisq.core.user.User;

import bisq.network.p2p.P2PService;

import bisq.common.crypto.KeyRing;

import javax.inject.Inject;

import lombok.Getter;

@Getter
public class ProcessModelServiceProvider {
    private final OpenOfferManager openOfferManager;
    private final P2PService p2PService;
    private final BtcWalletService btcWalletService;
    private final XmrWalletService xmrWalletService;
    private final TradeWalletService tradeWalletService;
    private final ReferralIdService referralIdService;
    private final User user;
    private final FilterManager filterManager;
    private final AccountAgeWitnessService accountAgeWitnessService;
    private final TradeStatisticsManager tradeStatisticsManager;
    private final ArbitratorManager arbitratorManager;
    private final MediatorManager mediatorManager;
    private final RefundAgentManager refundAgentManager;
    private final KeyRing keyRing;

    @Inject
    public ProcessModelServiceProvider(OpenOfferManager openOfferManager,
                                       P2PService p2PService,
                                       BtcWalletService btcWalletService,
                                       XmrWalletService xmrWalletService,
                                       TradeWalletService tradeWalletService,
                                       ReferralIdService referralIdService,
                                       User user,
                                       FilterManager filterManager,
                                       AccountAgeWitnessService accountAgeWitnessService,
                                       TradeStatisticsManager tradeStatisticsManager,
                                       ArbitratorManager arbitratorManager,
                                       MediatorManager mediatorManager,
                                       RefundAgentManager refundAgentManager,
                                       KeyRing keyRing) {
        this.openOfferManager = openOfferManager;
        this.p2PService = p2PService;
        this.btcWalletService = btcWalletService;
        this.xmrWalletService = xmrWalletService;
        this.tradeWalletService = tradeWalletService;
        this.referralIdService = referralIdService;
        this.user = user;
        this.filterManager = filterManager;
        this.accountAgeWitnessService = accountAgeWitnessService;
        this.tradeStatisticsManager = tradeStatisticsManager;
        this.arbitratorManager = arbitratorManager;
        this.mediatorManager = mediatorManager;
        this.refundAgentManager = refundAgentManager;
        this.keyRing = keyRing;
    }
}
