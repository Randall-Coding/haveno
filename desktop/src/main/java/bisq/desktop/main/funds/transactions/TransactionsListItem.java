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

package bisq.desktop.main.funds.transactions;

import bisq.desktop.components.indicator.TxConfidenceIndicator;

import bisq.core.btc.listeners.TxConfidenceListener;
import bisq.core.btc.wallet.XmrWalletService;
import bisq.core.trade.Tradable;
import bisq.core.util.coin.CoinFormatter;

import org.bitcoinj.core.Coin;

import com.google.common.base.Suppliers;

import javafx.scene.control.Tooltip;

import java.util.Date;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;



import monero.wallet.model.MoneroTxWallet;

@Slf4j
class TransactionsListItem {
    private final XmrWalletService xmrWalletService;
    private final CoinFormatter formatter;
    private String dateString;
    private final Date date;
    private final String txId;
    @Nullable
    private Tradable tradable;
    private String details = "";
    private String addressString = "";
    private String direction = "";
    private TxConfidenceListener txConfidenceListener;
    private boolean received;
    private boolean detailsAvailable;
    private Coin amountAsCoin = Coin.ZERO;
    private String memo = "";
    private int confirmations = 0;
    @Getter
    private final boolean isDustAttackTx;
    private boolean initialTxConfidenceVisibility = true;
    private final Supplier<LazyFields> lazyFieldsSupplier;

    private static class LazyFields {
        TxConfidenceIndicator txConfidenceIndicator;
        Tooltip tooltip;
    }

    private LazyFields lazy() {
        return lazyFieldsSupplier.get();
    }

    // used at exportCSV
    TransactionsListItem() {
        date = null;
        xmrWalletService = null;
        txId = null;
        formatter = null;
        isDustAttackTx = false;
        lazyFieldsSupplier = null;
    }

    TransactionsListItem(MoneroTxWallet transaction,
                         XmrWalletService xmrWalletService,
                         TransactionAwareTradable transactionAwareTradable,
                         CoinFormatter formatter,
                         long ignoreDustThreshold) {
        throw new RuntimeException("TransactionsListItem needs updated to use XMR wallet");
//        this.btcWalletService = btcWalletService;
//        this.formatter = formatter;
//        this.memo = transaction.getMemo();
//
//        txId = transaction.getTxId().toString();
//
//        Optional<Tradable> optionalTradable = Optional.ofNullable(transactionAwareTradable)
//                .map(TransactionAwareTradable::asTradable);
//
//        Coin valueSentToMe = btcWalletService.getValueSentToMeForTransaction(transaction);
//        Coin valueSentFromMe = btcWalletService.getValueSentFromMeForTransaction(transaction);
//
//        // TODO check and refactor
//        if (valueSentToMe.isZero()) {
//            amountAsCoin = valueSentFromMe.multiply(-1);
//            for (TransactionOutput output : transaction.getOutputs()) {
//                if (!btcWalletService.isTransactionOutputMine(output)) {
//                    received = false;
//                    if (WalletService.isOutputScriptConvertibleToAddress(output)) {
//                        addressString = WalletService.getAddressStringFromOutput(output);
//                        direction = Res.get("funds.tx.direction.sentTo");
//                        break;
//                    }
//                }
//            }
//        } else if (valueSentFromMe.isZero()) {
//            amountAsCoin = valueSentToMe;
//            direction = Res.get("funds.tx.direction.receivedWith");
//            received = true;
//            for (TransactionOutput output : transaction.getOutputs()) {
//                if (btcWalletService.isTransactionOutputMine(output) &&
//                        WalletService.isOutputScriptConvertibleToAddress(output)) {
//                    addressString = WalletService.getAddressStringFromOutput(output);
//                    break;
//                }
//            }
//        } else {
//            amountAsCoin = valueSentToMe.subtract(valueSentFromMe);
//            boolean outgoing = false;
//            for (TransactionOutput output : transaction.getOutputs()) {
//                if (!btcWalletService.isTransactionOutputMine(output)) {
//                    if (WalletService.isOutputScriptConvertibleToAddress(output)) {
//                        addressString = WalletService.getAddressStringFromOutput(output);
//                        outgoing = true;
//                        break;
//                    }
//                } else {
//                    addressString = WalletService.getAddressStringFromOutput(output);
//                    outgoing = (valueSentToMe.getValue() < valueSentFromMe.getValue());
//                    if (!outgoing) {
//                        direction = Res.get("funds.tx.direction.receivedWith");
//                        received = true;
//                    }
//                }
//            }
//
//            if (outgoing) {
//                direction = Res.get("funds.tx.direction.sentTo");
//                received = false;
//            }
//        }
//
//
//        if (optionalTradable.isPresent()) {
//            tradable = optionalTradable.get();
//            detailsAvailable = true;
//            String tradeId = tradable.getShortId();
//            if (tradable instanceof OpenOffer) {
//                details = Res.get("funds.tx.createOfferFee", tradeId);
//            } else if (tradable instanceof Trade) {
//                Trade trade = (Trade) tradable;
//                TransactionAwareTrade transactionAwareTrade = (TransactionAwareTrade) transactionAwareTradable;
//                if (trade.getTakerFeeTxId() != null && trade.getTakerFeeTxId().equals(txId)) {
//                    details = Res.get("funds.tx.takeOfferFee", tradeId);
//                } else {
//                    Offer offer = trade.getOffer();
//                    String offerFeePaymentTxID = offer.getOfferFeePaymentTxId();
//                    if (offerFeePaymentTxID != null && offerFeePaymentTxID.equals(txId)) {
//                        details = Res.get("funds.tx.createOfferFee", tradeId);
//                    } else if (trade.getDepositTx() != null &&
//                            trade.getDepositTx().getTxId().equals(Sha256Hash.wrap(txId))) {
//                        details = Res.get("funds.tx.multiSigDeposit", tradeId);
//                    } else if (trade.getPayoutTx() != null &&
//                            trade.getPayoutTx().getTxId().equals(Sha256Hash.wrap(txId))) {
//                        details = Res.get("funds.tx.multiSigPayout", tradeId);
//
//                        if (amountAsCoin.isZero()) {
//                            initialTxConfidenceVisibility = false;
//                        }
//                    } else {
//                        Trade.DisputeState disputeState = trade.getDisputeState();
//                        if (disputeState == Trade.DisputeState.DISPUTE_CLOSED) {
//                            if (valueSentToMe.isPositive()) {
//                                details = Res.get("funds.tx.disputePayout", tradeId);
//                            } else {
//                                details = Res.get("funds.tx.disputeLost", tradeId);
//                                initialTxConfidenceVisibility = false;
//                            }
//                        } else if (disputeState == Trade.DisputeState.REFUND_REQUEST_CLOSED ||
//                                disputeState == Trade.DisputeState.REFUND_REQUESTED ||
//                                disputeState == Trade.DisputeState.REFUND_REQUEST_STARTED_BY_PEER) {
//                            if (valueSentToMe.isPositive()) {
//                                details = Res.get("funds.tx.refund", tradeId);
//                            } else {
//                                // We have spent the deposit tx outputs to the Bisq donation address to enable
//                                // the refund process (refund agent -> reimbursement). As the funds have left our wallet
//                                // already when funding the deposit tx we show 0 BTC as amount.
//                                // Confirmation is not known from the BitcoinJ side (not 100% clear why) as no funds
//                                // left our wallet nor we received funds. So we set indicator invisible.
//                                amountAsCoin = Coin.ZERO;
//                                details = Res.get("funds.tx.collateralForRefund", tradeId);
//                                initialTxConfidenceVisibility = false;
//                            }
//                        } else {
//                            if (transactionAwareTrade.isDelayedPayoutTx(txId)) {
//                                details = Res.get("funds.tx.timeLockedPayoutTx", tradeId);
//                                initialTxConfidenceVisibility = false;
//                            } else {
//                                details = Res.get("funds.tx.unknown", tradeId);
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            if (amountAsCoin.isZero()) {
//                details = Res.get("funds.tx.noFundsFromDispute");
//                initialTxConfidenceVisibility = false;
//        }
//        // Use tx.getIncludedInBestChainAt() when available, otherwise use tx.getUpdateTime()
//        date = transaction.getIncludedInBestChainAt() != null ? transaction.getIncludedInBestChainAt() : transaction.getUpdateTime();
//        dateString = DisplayUtils.formatDateTime(date);
//
//        isDustAttackTx = received && valueSentToMe.value < ignoreDustThreshold;
//        if (isDustAttackTx) {
//            details = Res.get("funds.tx.dustAttackTx");
//        }
//
//        // confidence
//        lazyFieldsSupplier = Suppliers.memoize(() -> new LazyFields() {{
//            txConfidenceIndicator = new TxConfidenceIndicator();
//            txConfidenceIndicator.setId("funds-confidence");
//            tooltip = new Tooltip(Res.get("shared.notUsedYet"));
//            txConfidenceIndicator.setProgress(0);
//            txConfidenceIndicator.setTooltip(tooltip);
//            txConfidenceIndicator.setVisible(initialTxConfidenceVisibility);
//
//            TransactionConfidence confidence = transaction.getConfidence();
//            GUIUtil.updateConfidence(confidence, tooltip, txConfidenceIndicator);
//            confirmations = confidence.getDepthInBlocks();
//        }});
//
//        txConfidenceListener = new TxConfidenceListener(txId) {
//            @Override
//            public void onTransactionConfidenceChanged(TransactionConfidence confidence) {
//                GUIUtil.updateConfidence(confidence, lazy().tooltip, lazy().txConfidenceIndicator);
//                confirmations = confidence.getDepthInBlocks();
//            }
//        };
//        btcWalletService.addTxConfidenceListener(txConfidenceListener);
    }

    public void cleanup() {
        // TODO (woodser): remove wallet listener
        //xmrWalletService.removeTxConfidenceListener(txConfidenceListener);
    }


    public TxConfidenceIndicator getTxConfidenceIndicator() {
        return lazy().txConfidenceIndicator;
    }

    public final String getDateString() {
        return dateString;
    }


    public String getAmount() {
        return formatter.formatCoin(amountAsCoin);
    }

    public Coin getAmountAsCoin() {
        return amountAsCoin;
    }


    public String getAddressString() {
        return addressString;
    }

    public String getDirection() {
        return direction;
    }

    public String getTxId() {
        return txId;
    }

    public boolean getReceived() {
        return received;
    }

    public String getDetails() {
        return details;
    }

    public boolean getDetailsAvailable() {
        return detailsAvailable;
    }

    public Date getDate() {
        return date;
    }

    @Nullable
    public Tradable getTradable() {
        return tradable;
    }

    public String getNumConfirmations() {
        return String.valueOf(confirmations);
    }

    public String getMemo() {
        return memo;
    }
}
